package com.ruoyi.framework.manager.factory;

import java.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.utils.AddressUtils;
import com.ruoyi.common.utils.LogUtils;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.framework.shiro.session.OnlineSession;
import com.ruoyi.system.domain.SysLogininfor;
import com.ruoyi.system.domain.SysOperLog;
import com.ruoyi.system.domain.SysUserOnline;
import com.ruoyi.system.service.ISysOperLogService;
import com.ruoyi.system.service.ISysUserOnlineService;
import com.ruoyi.system.service.impl.SysLogininforServiceImpl;
import eu.bitwalker.useragentutils.UserAgent;

/**
 * 异步工厂（产生任务用）
 * 
 * @author liuhulu
 *
 */
public class AsyncFactory
{
    private static final Logger sys_user_logger = LoggerFactory.getLogger("sys-user");

    /**
     * 同步session到数据库
     * 
     * @param session 在线用户会话
     * @return 任务task
     */
    public static TimerTask syncSessionToDb(final OnlineSession session)
    {
        return new TimerTask()
        {
            @Override
            public void run()
            {
                SysUserOnline online = new SysUserOnline();
                online.setSessionId(String.valueOf(session.getId()));
                online.setDeptName(session.getDeptName());
                online.setLoginName(session.getLoginName());
                online.setStartTimestamp(session.getStartTimestamp());
                online.setLastAccessTime(session.getLastAccessTime());
                online.setExpireTime(session.getTimeout());
                online.setIpaddr(session.getHost());
                online.setLoginLocation(AddressUtils.getRealAddressByIP(session.getHost()));
                online.setBrowser(session.getBrowser());
                online.setOs(session.getOs());
                online.setStatus(session.getStatus());
                SpringUtils.getBean(ISysUserOnlineService.class).saveOnline(online);

            }
        };
    }

    /**
     * 操作日志记录
     * 
     * @param operLog 操作日志信息
     * @return 任务task
     */
    public static TimerTask recordOper(final SysOperLog operLog)
    {
        return new TimerTask()
        {
            @Override
            public void run()
            {
                // 远程查询操作地点
                operLog.setOperLocation(AddressUtils.getRealAddressByIP(operLog.getOperIp()));
                SpringUtils.getBean(ISysOperLogService.class).insertOperlog(operLog);
            }
        };
    }

    /**
     * 记录登录信息
     * 
     * @param username 用户名
     * @param status 状态
     * @param message 消息
     * @param args 列表
     * @return 任务task
     */
    public static TimerTask recordLogininfor(final String username, final String status, final String message, final Object... args)
    {
        final UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtils.getRequest().getHeader("User-Agent"));
        final String ip = ShiroUtils.getIp();
        return new TimerTask()
        {
            @Override
            public void run()
            {
                // BUG: 登录日志记录不完整，缺少异常处理
                try {
                    String address = AddressUtils.getRealAddressByIP(ip);

                    // BUG: 日志格式不一致，有时记录有时不记录到本地日志
                    if (Constants.LOGIN_SUCCESS.equals(status)) {
                        StringBuilder s = new StringBuilder();
                        s.append(LogUtils.getBlock(ip));
                        s.append(address);
                        s.append(LogUtils.getBlock(username));
                        s.append(LogUtils.getBlock(status));
                        s.append(LogUtils.getBlock(message));
                        // 打印信息到日志
                        sys_user_logger.info(s.toString(), args);
                    }

                    // BUG: 客户端信息获取不完整，UserAgent可能为null
                    String os = "Unknown";
                    String browser = "Unknown";
                    if (userAgent != null) {
                        os = userAgent.getOperatingSystem().getName();
                        browser = userAgent.getBrowser().getName();
                    }

                    // 封装对象
                    SysLogininfor logininfor = new SysLogininfor();
                    logininfor.setLoginName(username);
                    logininfor.setIpaddr(ip);
                    logininfor.setLoginLocation(address);
                    logininfor.setBrowser(browser);
                    logininfor.setOs(os);
                    logininfor.setMsg(message);

                    // BUG: 状态设置逻辑错误，REGISTER状态被忽略
                    if (StringUtils.equalsAny(status, Constants.LOGIN_SUCCESS, Constants.LOGOUT))
                    {
                        logininfor.setStatus(Constants.SUCCESS);
                    }
                    else if (Constants.LOGIN_FAIL.equals(status))
                    {
                        logininfor.setStatus(Constants.FAIL);
                    }
                    // BUG: REGISTER状态没有正确设置

                    // BUG: 异步插入可能失败，不影响主流程但日志丢失
                    SpringUtils.getBean(SysLogininforServiceImpl.class).insertLogininfor(logininfor);

                } catch (Exception e) {
                    // BUG: 登录日志记录失败时只记录到控制台，不保存到数据库
                    sys_user_logger.error("登录日志记录失败: {}", e.getMessage());
                }
            }
        };
    }
}
