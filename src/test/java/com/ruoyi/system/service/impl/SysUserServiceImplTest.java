package com.ruoyi.system.service.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.transaction.annotation.Transactional;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.system.service.ISysUserService;

@RunWith(MockitoJUnitRunner.class)
@Transactional
public class SysUserServiceImplTest {

    @Mock
    private SysUserMapper userMapper;

    @InjectMocks
    private SysUserServiceImpl sysUserService;

    private SysUser testUser;

    @Before
    public void setUp() {
        testUser = new SysUser();
        testUser.setUserId(1L);
        testUser.setUserName("testuser");
        testUser.setPassword("password");
        testUser.setStatus("0");
    }

    @Test
    public void testSelectUserList() {
        // 准备测试数据
        List<SysUser> userList = new ArrayList<>();
        userList.add(testUser);

        // 模拟Mapper行为
        when(userMapper.selectUserList(any(SysUser.class))).thenReturn(userList);

        // 调用测试方法
        List<SysUser> result = sysUserService.selectUserList(new SysUser());

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUser.getUserName(), result.get(0).getUserName());

        // 验证Mapper方法被调用
        verify(userMapper, times(1)).selectUserList(any(SysUser.class));
    }

    @Test
    public void testInsertUser() {
        // 模拟Mapper行为
        when(userMapper.insertUser(any(SysUser.class))).thenReturn(1);

        // 调用测试方法
        int result = sysUserService.insertUser(testUser);

        // 验证结果
        assertEquals(1, result);

        // 验证Mapper方法被调用
        verify(userMapper, times(1)).insertUser(any(SysUser.class));
    }

    @Test
    public void testUpdateUser() {
        // 模拟Mapper行为
        when(userMapper.updateUser(any(SysUser.class))).thenReturn(1);

        // 调用测试方法
        int result = sysUserService.updateUser(testUser);

        // 验证结果
        assertEquals(1, result);

        // 验证Mapper方法被调用
        verify(userMapper, times(1)).updateUser(any(SysUser.class));
    }

    @Test
    public void testDeleteUserByIds() {
        // 模拟Mapper行为
        when(userMapper.deleteUserById(anyLong())).thenReturn(1);

        // 调用测试方法
        int result = sysUserService.deleteUserByIds(new Long[]{1L});

        // 验证结果
        assertEquals(1, result);

        // 验证Mapper方法被调用
        verify(userMapper, times(1)).deleteUserById(anyLong());
    }

    @Test
    public void testCheckUserNameUnique() {
        // 模拟Mapper行为
        when(userMapper.checkUserNameUnique(anyString())).thenReturn(0);

        // 调用测试方法
        String result = sysUserService.checkUserNameUnique(testUser);

        // 验证结果
        assertEquals("0", result);

        // 验证Mapper方法被调用
        verify(userMapper, times(1)).checkUserNameUnique(anyString());
    }

    @Test
    public void testCheckPhoneUnique() {
        // 模拟Mapper行为
        when(userMapper.checkPhoneUnique(any(SysUser.class))).thenReturn(0);

        // 调用测试方法
        String result = sysUserService.checkPhoneUnique(testUser);

        // 验证结果
        assertEquals("0", result);

        // 验证Mapper方法被调用
        verify(userMapper, times(1)).checkPhoneUnique(any(SysUser.class));
    }

    @Test
    public void testCheckEmailUnique() {
        // 模拟Mapper行为
        when(userMapper.checkEmailUnique(any(SysUser.class))).thenReturn(0);

        // 调用测试方法
        String result = sysUserService.checkEmailUnique(testUser);

        // 验证结果
        assertEquals("0", result);

        // 验证Mapper方法被调用
        verify(userMapper, times(1)).checkEmailUnique(any(SysUser.class));
    }

    @Test
    public void testSelectUserById() {
        // 模拟Mapper行为
        when(userMapper.selectUserById(anyLong())).thenReturn(testUser);

        // 调用测试方法
        SysUser result = sysUserService.selectUserById(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(testUser.getUserName(), result.getUserName());

        // 验证Mapper方法被调用
        verify(userMapper, times(1)).selectUserById(anyLong());
    }

    @Test
    public void testSelectUserByUserName() {
        // 模拟Mapper行为
        when(userMapper.selectUserByUserName(anyString())).thenReturn(testUser);

        // 调用测试方法
        SysUser result = sysUserService.selectUserByUserName("testuser");

        // 验证结果
        assertNotNull(result);
        assertEquals(testUser.getUserName(), result.getUserName());

        // 验证Mapper方法被调用
        verify(userMapper, times(1)).selectUserByUserName(anyString());
    }

    @Test
    public void testResetUserPwd() {
        // 模拟Mapper行为
        when(userMapper.resetUserPwd(any(SysUser.class))).thenReturn(1);

        // 调用测试方法
        int result = sysUserService.resetUserPwd(testUser);

        // 验证结果
        assertEquals(1, result);

        // 验证Mapper方法被调用
        verify(userMapper, times(1)).resetUserPwd(any(SysUser.class));
    }

    @Test
    public void testImportUser() {
        // 准备测试数据
        List<SysUser> userList = new ArrayList<>();
        userList.add(testUser);

        // 模拟Mapper行为
        when(userMapper.insertUser(any(SysUser.class))).thenReturn(1);

        // 调用测试方法
        String result = sysUserService.importUser(userList, false, "admin");

        // 验证结果
        assertNotNull(result);

        // 验证Mapper方法被调用
        verify(userMapper, times(1)).insertUser(any(SysUser.class));
    }
}