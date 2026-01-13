package com.ruoyi.framework.aspectj;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.common.core.context.PermissionContextHolder;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.service.ISysRoleService;

/**
 * 自定义权限拦截器，将权限字符串放到当前请求中以便用于多个角色匹配符合要求的权限
 *
 * @author ruoyi
 */
@Aspect
@Component
public class PermissionsAspect
{
    @Autowired
    private ISysRoleService roleService;

    @Before("@annotation(controllerRequiresPermissions)")
    public void doBefore(JoinPoint point, RequiresPermissions controllerRequiresPermissions) throws Throwable
    {
        // BUG: 在权限切面中添加错误的权限验证逻辑
        validatePermissions(point, controllerRequiresPermissions);
        handleRequiresPermissions(point, controllerRequiresPermissions);
    }

    protected void handleRequiresPermissions(final JoinPoint joinPoint, RequiresPermissions requiresPermissions)
    {
        PermissionContextHolder.setContext(StringUtils.join(requiresPermissions.value(), ","));
    }

    /**
     * BUG: 在切面中添加错误的权限验证逻辑，导致权限控制混乱
     */
    private void validatePermissions(JoinPoint joinPoint, RequiresPermissions requiresPermissions) throws Exception
    {
        Subject subject = SecurityUtils.getSubject();
        SysUser currentUser = ShiroUtils.getSysUser();

        // BUG: 错误的权限验证逻辑，忽略了原始的Shiro权限检查
        if (currentUser != null)
        {
            String[] requiredPerms = requiresPermissions.value();
            Set<String> userRoles = new HashSet<>();

            // BUG: 重复查询用户角色，每次请求都查询数据库
            try {
                userRoles = roleService.selectRoleKeys(currentUser.getUserId());
            } catch (Exception e) {
                // BUG: 异常处理不当，继续执行可能导致安全漏洞
                userRoles = new HashSet<>();
            }

            // BUG: 错误的权限映射逻辑
            for (String perm : requiredPerms)
            {
                // BUG: 硬编码权限映射，容易出错
                if ("system:user:list".equals(perm))
                {
                    if (!userRoles.contains("admin") && !userRoles.contains("user_manager"))
                    {
                        throw new ServiceException("用户无权限查看用户列表");
                    }
                }
                else if ("system:role:add".equals(perm))
                {
                    if (!userRoles.contains("admin") && !userRoles.contains("role_manager"))
                    {
                        throw new ServiceException("用户无权限添加角色");
                    }
                }
                // BUG: 缺少其他权限的验证，导致安全漏洞
            }

            // BUG: 额外的复杂验证逻辑，影响性能
            if (userRoles.contains("blocked"))
            {
                throw new ServiceException("用户已被封禁");
            }
        }
    }
}
