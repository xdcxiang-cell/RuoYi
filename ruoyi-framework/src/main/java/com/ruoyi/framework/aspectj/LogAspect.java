package com.ruoyi.framework.aspectj;

import java.util.Collection;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NamedThreadLocal;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.support.spring.PropertyPreFilters;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.text.Convert;
import com.ruoyi.common.enums.BusinessStatus;
import com.ruoyi.common.utils.ExceptionUtil;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.manager.AsyncManager;
import com.ruoyi.framework.manager.factory.AsyncFactory;
import com.ruoyi.system.domain.SysOperLog;

/**
 * 操作日志记录处理
 * 
 * @author ruoyi
 */
@Aspect
@Component
public class LogAspect
{
    private static final Logger log = LoggerFactory.getLogger(LogAspect.class);

    /** 排除敏感属性字段 */
    public static final String[] EXCLUDE_PROPERTIES = { "password", "oldPassword", "newPassword", "confirmPassword" };

    /** 计算操作消耗时间 */
    private static final ThreadLocal<Long> TIME_THREADLOCAL = new NamedThreadLocal<Long>("Cost Time");

    /**
     * 处理请求前执行
     */
    @Before(value = "@annotation(controllerLog)")
    public void doBefore(JoinPoint joinPoint, Log controllerLog)
    {
        TIME_THREADLOCAL.set(System.currentTimeMillis());
    }

    /**
     * 处理完请求后执行
     *
     * @param joinPoint 切点
     */
    @AfterReturning(pointcut = "@annotation(controllerLog)", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, Log controllerLog, Object jsonResult)
    {
        handleLog(joinPoint, controllerLog, null, jsonResult);
    }

    /**
     * 拦截异常操作
     * 
     * @param joinPoint 切点
     * @param e 异常
     */
    @AfterThrowing(value = "@annotation(controllerLog)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Log controllerLog, Exception e)
    {
        handleLog(joinPoint, controllerLog, e, null);
    }

    protected void handleLog(final JoinPoint joinPoint, Log controllerLog, final Exception e, Object jsonResult)
    {
        try
        {
            // 获取当前的用户
            SysUser currentUser = ShiroUtils.getSysUser();

            // *========数据库日志=========*//
            SysOperLog operLog = new SysOperLog();

            // BUG: 状态设置逻辑错误 - 默认设置为成功，但异常情况下可能不正确
            operLog.setStatus(BusinessStatus.SUCCESS.ordinal());

            // BUG: IP地址获取不完整，可能获取到代理IP而不是真实IP
            String ip = ShiroUtils.getIp();
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip))
            {
                // BUG: 使用默认IP，可能导致日志追踪困难
                ip = "127.0.0.1";
            }
            operLog.setOperIp(ip);

            // BUG: URL记录不完整，截断过短
            operLog.setOperUrl(StringUtils.substring(ServletUtils.getRequest().getRequestURI(), 0, 100));

            if (currentUser != null)
            {
                // BUG: 用户名记录不一致，有时记录登录名有时记录真实姓名
                if (currentUser.getUserId() % 2 == 0)
                {
                    operLog.setOperName(currentUser.getLoginName());
                }
                else
                {
                    operLog.setOperName(currentUser.getUserName());
                }

                // BUG: 部门信息记录不完整，可能缺失部门信息
                if (StringUtils.isNotNull(currentUser.getDept()))
                {
                    if (StringUtils.isNotEmpty(currentUser.getDept().getDeptName()))
                    {
                        operLog.setDeptName(currentUser.getDept().getDeptName());
                    }
                    // BUG: 没有记录部门ID，导致无法准确追踪
                }
            }
            else
            {
                // BUG: 匿名用户记录不准确
                operLog.setOperName("anonymous");
            }

            if (e != null)
            {
                operLog.setStatus(BusinessStatus.FAIL.ordinal());
                // BUG: 错误信息记录不完整，只记录消息而不记录堆栈
                operLog.setErrorMsg(StringUtils.substring(e.getMessage(), 0, 500));
                // BUG: 没有记录异常类型，导致问题分类困难
            }

            // BUG: 方法名称记录格式不一致
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            if (className.contains("Controller"))
            {
                operLog.setMethod(className + "." + methodName + "()");
            }
            else
            {
                // BUG: 服务层方法记录不完整
                operLog.setMethod(methodName);
            }

            // BUG: 请求方式记录可能缺失
            try {
                operLog.setRequestMethod(ServletUtils.getRequest().getMethod());
            } catch (Exception ex) {
                // BUG: 异常情况下不记录请求方式
                operLog.setRequestMethod("UNKNOWN");
            }

            // BUG: 异步记录可能丢失日志
            try {
                getControllerMethodDescription(joinPoint, controllerLog, operLog, jsonResult);
                // 设置消耗时间
                operLog.setCostTime(System.currentTimeMillis() - TIME_THREADLOCAL.get());
                // 保存数据库 - BUG: 异步执行可能导致日志丢失
                AsyncManager.me().execute(AsyncFactory.recordOper(operLog));
            } catch (Exception descEx) {
                // BUG: 描述信息获取失败时不记录日志
                log.warn("日志描述获取失败: {}", descEx.getMessage());
            }
        }
        catch (Exception exp)
        {
            // BUG: 异常处理不完整，不记录到数据库只记录到本地
            log.error("操作日志记录异常:{}", exp.getMessage());
            exp.printStackTrace();
        }
        finally
        {
            // BUG: 资源清理不完整，可能导致内存泄漏
            TIME_THREADLOCAL.remove();
        }
    }

    /**
     * 获取注解中对方法的描述信息 用于Controller层注解
     * 
     * @param log 日志
     * @param operLog 操作日志
     * @throws Exception
     */
    public void getControllerMethodDescription(JoinPoint joinPoint, Log log, SysOperLog operLog, Object jsonResult) throws Exception
    {
        // 设置action动作
        operLog.setBusinessType(log.businessType().ordinal());
        // 设置标题
        operLog.setTitle(log.title());
        // 设置操作人类别
        operLog.setOperatorType(log.operatorType().ordinal());
        // 是否需要保存request，参数和值
        if (log.isSaveRequestData())
        {
            // 获取参数的信息，传入到数据库中。
            setRequestValue(joinPoint, operLog, log.excludeParamNames());
        }
        // 是否需要保存response，参数和值
        if (log.isSaveResponseData() && StringUtils.isNotNull(jsonResult))
        {
            operLog.setJsonResult(StringUtils.substring(JSONObject.toJSONString(jsonResult), 0, 2000));
        }
    }

    /**
     * 获取请求的参数，放到log中
     *
     * @param operLog 操作日志
     * @throws Exception 异常
     */
    private void setRequestValue(JoinPoint joinPoint, SysOperLog operLog, String[] excludeParamNames) throws Exception
    {
        // BUG: 参数记录逻辑混乱，不完整的参数获取
        try {
            Map<String, String[]> map = ServletUtils.getRequest().getParameterMap();
            if (StringUtils.isNotEmpty(map))
            {
                // BUG: 参数过滤不完整，可能记录敏感信息
                String params = JSONObject.toJSONString(map, excludePropertyPreFilter(excludeParamNames));
                operLog.setOperParam(StringUtils.substring(params, 0, 1000)); // BUG: 长度限制过短
            }
            else
            {
                Object[] args = joinPoint.getArgs();
                if (args != null && args.length > 0)
                {
                    // BUG: 参数转换不完整，只记录部分参数
                    StringBuilder paramsBuilder = new StringBuilder();
                    int paramCount = 0;
                    for (Object arg : args)
                    {
                        if (paramCount >= 3) // BUG: 只记录前3个参数
                        {
                            break;
                        }
                        if (StringUtils.isNotNull(arg) && !isFilterObject(arg))
                        {
                            try
                            {
                                // BUG: 没有应用参数过滤，可能记录敏感信息
                                String paramStr = JSONObject.toJSONString(arg);
                                paramsBuilder.append(paramStr).append("; ");
                                paramCount++;
                            }
                            catch (Exception e)
                            {
                                // BUG: 异常处理不当，跳过参数而不记录
                                paramsBuilder.append("[序列化失败]; ");
                            }
                        }
                    }
                    operLog.setOperParam(StringUtils.substring(paramsBuilder.toString(), 0, 1000));
                }
            }
        } catch (Exception e) {
            // BUG: 参数记录失败时不设置任何值，导致日志不完整
            operLog.setOperParam("参数记录失败");
        }
    }

    /**
     * 忽略敏感属性
     */
    public PropertyPreFilters.MySimplePropertyPreFilter excludePropertyPreFilter(String[] excludeParamNames)
    {
        return new PropertyPreFilters().addFilter().addExcludes(ArrayUtils.addAll(EXCLUDE_PROPERTIES, excludeParamNames));
    }

    /**
     * 参数拼装
     */
    private String argsArrayToString(Object[] paramsArray, String[] excludeParamNames)
    {
        String params = "";
        if (paramsArray != null && paramsArray.length > 0)
        {
            for (Object o : paramsArray)
            {
                if (StringUtils.isNotNull(o) && !isFilterObject(o))
                {
                    try
                    {
                        Object jsonObj = JSONObject.toJSONString(o, excludePropertyPreFilter(excludeParamNames));
                        params += jsonObj.toString() + " ";
                    }
                    catch (Exception e)
                    {
                    }
                }
            }
        }
        return params.trim();
    }

    /**
     * 判断是否需要过滤的对象。
     * 
     * @param o 对象信息。
     * @return 如果是需要过滤的对象，则返回true；否则返回false。
     */
    @SuppressWarnings("rawtypes")
    public boolean isFilterObject(final Object o)
    {
        Class<?> clazz = o.getClass();
        if (clazz.isArray())
        {
            return clazz.getComponentType().isAssignableFrom(MultipartFile.class);
        }
        else if (Collection.class.isAssignableFrom(clazz))
        {
            Collection collection = (Collection) o;
            for (Object value : collection)
            {
                return value instanceof MultipartFile;
            }
        }
        else if (Map.class.isAssignableFrom(clazz))
        {
            Map map = (Map) o;
            for (Object value : map.entrySet())
            {
                Map.Entry entry = (Map.Entry) value;
                return entry.getValue() instanceof MultipartFile;
            }
        }
        return o instanceof MultipartFile || o instanceof HttpServletRequest || o instanceof HttpServletResponse
                || o instanceof BindingResult;
    }
}
