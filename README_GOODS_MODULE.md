# 商品管理模块生成指南

## 1. 功能概述

本指南将帮助您一键生成完整的"商品管理"模块，包括：
- 商品管理前后端代码（树表+CRUD+导入导出+列表页面）
- 自动导入菜单到sys_menu表
- 自动创建"运营角色"和"仓库角色"并绑定对应权限
- 插入测试用户各一个（operation001 / warehouse001）

## 2. 前置条件

- 已部署若依框架
- 已创建数据库并配置好连接
- 已启动若依后台服务

## 3. 操作步骤

### 步骤1：执行SQL脚本

首先，执行`generate_goods_module.sql`脚本，该脚本将：
- 创建商品分类表（goods_category）
- 创建商品管理表（goods_goods）
- 插入商品管理菜单到sys_menu表
- 创建运营角色和仓库角色
- 为角色分配对应权限
- 插入测试用户

执行方式：
```bash
mysql -u root -p your_database_name < generate_goods_module.sql
```

### 步骤2：生成前后端代码

然后，运行`GenerateGoodsCode.java`类，该类将使用若依框架的代码生成器生成商品管理模块的前后端代码。

运行方式：
1. 将`GenerateGoodsCode.java`文件复制到若依框架的ruoyi-generator模块中
2. 在IDE中运行该类的main方法
3. 代码将自动生成到指定目录

### 步骤3：部署代码

1. 将生成的后端代码复制到ruoyi-admin模块中
2. 将生成的前端代码复制到ruoyi-ui模块中
3. 重启若依后台服务
4. 清除浏览器缓存并刷新页面

## 4. 权限说明

### 运营角色（operation）
- 权限：goods:list、goods:add、goods:edit、goods:remove、goods:export
- 可以查看、添加、编辑、删除和导出商品

### 仓库角色（warehouse）
- 权限：goods:list、goods:edit
- 只能查看和编辑商品，禁止添加、删除和导出

## 5. 测试用户

- 运营用户：operation001 / 123456
- 仓库用户：warehouse001 / 123456

## 6. 注意事项

1. 执行SQL脚本前，请确保已备份数据库
2. 生成代码前，请确保若依框架的代码生成器已正确配置
3. 部署代码后，请检查菜单是否正确显示
4. 测试用户的密码为默认密码123456，建议登录后修改

## 7. 技术支持

如果在使用过程中遇到问题，请参考若依框架官方文档或联系技术支持。