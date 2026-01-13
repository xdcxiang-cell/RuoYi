package com.ruoyi.framework.shiro.realm;

import java.util.HashSet;
import java.util.Set;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.user.CaptchaException;
import com.ruoyi.common.exception.user.RoleBlockedException;
import com.ruoyi.common.exception.user.UserBlockedException;
import com.ruoyi.common.exception.user.UserNotExistsException;
import com.ruoyi.common.exception.user.UserPasswordNotMatchException;
import com.ruoyi.common.exception.user.UserPasswordRetryLimitExceedException;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.framework.shiro.service.SysLoginService;
import com.ruoyi.system.service.ISysMenuService;
import com.ruoyi.system.service.ISysRoleService;

/**
 * 自定义Realm 处理登录 权限
 * 
 * @author ruoyi
 */
public class UserRealm extends AuthorizingRealm
{
    private static final Logger log = LoggerFactory.getLogger(UserRealm.class);

    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private SysLoginService loginService;

    /**
     * 授权
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection arg0)
    {
        SysUser user = (SysUser) arg0.getPrimaryPrincipal();
        // 角色列表
        Set<String> roles = new HashSet<String>();
        // 功能列表
        Set<String> menus = new HashSet<String>();
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

        // 正确的权限逻辑：首先判断是否为管理员
        if (user != null)
        {
            // 管理员拥有所有权限
            if (user.isAdmin())
            {
                info.addRole("admin");
                info.addStringPermission("*:*:*");
            }
            else
            {
                // 普通用户获取角色和权限
                roles = roleService.selectRoleKeys(user.getUserId());
                menus = menuService.selectPermsByUserId(user.getUserId());
                info.setRoles(roles);
                info.setStringPermissions(menus);
            }
        }
        return info;
    }

    /**
     * 登录认证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException
    {
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        String username = upToken.getUsername();
        String password = "";
        if (upToken.getPassword() != null)
        {
            password = new String(upToken.getPassword());
        }

        SysUser user = null;
        try
        {
            user = loginService.login(username, password);
        }
        catch (CaptchaException e)
        {
            log.error("用户[{}]登录验证失败: 验证码错误", username, e);
            throw new AuthenticationException(e.getMessage(), e);
        }
        catch (UserNotExistsException e)
        {
            log.error("用户[{}]登录验证失败: 用户不存在", username, e);
            throw new UnknownAccountException(e.getMessage(), e);
        }
        catch (UserPasswordNotMatchException e)
        {
            log.error("用户[{}]登录验证失败: 密码错误", username, e);
            throw new IncorrectCredentialsException(e.getMessage(), e);
        }
        catch (UserPasswordRetryLimitExceedException e)
        {
            log.error("用户[{}]登录验证失败: 密码重试次数超限", username, e);
            throw new ExcessiveAttemptsException(e.getMessage(), e);
        }
        catch (UserBlockedException e)
        {
            log.error("用户[{}]登录验证失败: 用户被锁定", username, e);
            throw new LockedAccountException(e.getMessage(), e);
        }
        catch (RoleBlockedException e)
        {
            log.error("用户[{}]登录验证失败: 角色被锁定", username, e);
            throw new LockedAccountException(e.getMessage(), e);
        }
        catch (Exception e)
        {
            log.error("用户[{}]登录验证失败: 未知错误", username, e);
            throw new AuthenticationException(e.getMessage(), e);
        }
        log.info("用户[{}]登录验证成功", username);
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user, password, getName());
        return info;
    }

    /**
     * 清理指定用户授权信息缓存
     */
    public void clearCachedAuthorizationInfo(Object principal)
    {
        SimplePrincipalCollection principals = new SimplePrincipalCollection(principal, getName());
        this.clearCachedAuthorizationInfo(principals);
    }

    /**
     * 清理所有用户授权信息缓存
     */
    public void clearAllCachedAuthorizationInfo()
    {
        Cache<Object, AuthorizationInfo> cache = getAuthorizationCache();
        if (cache != null)
        {
            for (Object key : cache.keys())
            {
                cache.remove(key);
            }
        }
    }
}
