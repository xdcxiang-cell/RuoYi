-- 创建商品分类表
CREATE TABLE `goods_category` (
    `category_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `category_name` varchar(100) NOT NULL COMMENT '分类名称',
    `parent_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '父分类ID',
    `order_num` int(4) NOT NULL DEFAULT '0' COMMENT '显示顺序',
    `status` char(1) NOT NULL DEFAULT '1' COMMENT '状态（0停用/1正常）',
    `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- 创建商品管理表
CREATE TABLE `goods_goods` (
    `goods_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    `goods_name` varchar(100) NOT NULL COMMENT '商品名称',
    `goods_code` varchar(50) NOT NULL COMMENT '商品编号',
    `category_id` bigint(20) NOT NULL COMMENT '分类ID',
    `price` decimal(10,2) NOT NULL COMMENT '价格',
    `stock` int(11) NOT NULL COMMENT '库存',
    `status` char(1) NOT NULL DEFAULT '1' COMMENT '状态（0停用/1正常）',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`goods_id`),
    UNIQUE KEY `uk_goods_code` (`goods_code`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='商品管理表';

-- 插入商品管理菜单
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `url`, `target`, `menu_type`, `visible`, `is_refresh`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES
('商品管理', 0, 10, '/goods/goods', '', 'C', '0', '1', 'goods:goods:view', 'fa fa-shopping-cart', 'admin', NOW(), '', null, '商品管理目录'),
('商品列表', LAST_INSERT_ID(), 10, '/goods/goods/list', '', 'C', '0', '1', 'goods:goods:list', 'fa fa-list', 'admin', NOW(), '', null, '商品列表菜单'),
('商品添加', LAST_INSERT_ID(), 20, '', '', 'F', '0', '0', 'goods:goods:add', 'fa fa-plus', 'admin', NOW(), '', null, ''),
('商品编辑', LAST_INSERT_ID(), 30, '', '', 'F', '0', '0', 'goods:goods:edit', 'fa fa-edit', 'admin', NOW(), '', null, ''),
('商品删除', LAST_INSERT_ID(), 40, '', '', 'F', '0', '0', 'goods:goods:remove', 'fa fa-remove', 'admin', NOW(), '', null, ''),
('商品导出', LAST_INSERT_ID(), 50, '', '', 'F', '0', '0', 'goods:goods:export', 'fa fa-download', 'admin', NOW(), '', null, '');

-- 获取商品管理菜单ID
SET @goods_menu_id = (SELECT menu_id FROM sys_menu WHERE menu_name = '商品管理' AND parent_id = 0);

-- 插入运营角色
INSERT INTO `sys_role` (`role_name`, `role_key`, `role_sort`, `data_scope`, `status`, `del_flag`, `create_by`, `create_time`) VALUES
('运营', 'operation', '10', '1', '0', '0', 'admin', NOW());

-- 获取运营角色ID
SET @operation_role_id = LAST_INSERT_ID();

-- 为运营角色分配权限
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) SELECT @operation_role_id, menu_id FROM sys_menu WHERE menu_id = @goods_menu_id OR parent_id = @goods_menu_id;

-- 插入仓库角色
INSERT INTO `sys_role` (`role_name`, `role_key`, `role_sort`, `data_scope`, `status`, `del_flag`, `create_by`, `create_time`) VALUES
('仓库', 'warehouse', '20', '1', '0', '0', 'admin', NOW());

-- 获取仓库角色ID
SET @warehouse_role_id = LAST_INSERT_ID();

-- 为仓库角色分配权限（仅list和edit）
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) SELECT @warehouse_role_id, menu_id FROM sys_menu WHERE (menu_id = @goods_menu_id OR parent_id = @goods_menu_id) AND perms IN ('goods:goods:list', 'goods:goods:edit');

-- 插入运营测试用户
INSERT INTO `sys_user` (`dept_id`, `login_name`, `user_name`, `email`, `phonenumber`, `sex`, `password`, `salt`, `status`, `del_flag`, `create_by`, `create_time`) VALUES
(103, 'operation001', '运营用户', 'operation001@example.com', '13800138001', '1', '29c67a30398638269fe600f73a054934', '111111', '0', '0', 'admin', NOW());

-- 获取运营用户ID
SET @operation_user_id = LAST_INSERT_ID();

-- 为运营用户分配角色
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (@operation_user_id, @operation_role_id);

-- 插入仓库测试用户
INSERT INTO `sys_user` (`dept_id`, `login_name`, `user_name`, `email`, `phonenumber`, `sex`, `password`, `salt`, `status`, `del_flag`, `create_by`, `create_time`) VALUES
(103, 'warehouse001', '仓库用户', 'warehouse001@example.com', '13800138002', '1', '29c67a30398638269fe600f73a054934', '111111', '0', '0', 'admin', NOW());

-- 获取仓库用户ID
SET @warehouse_user_id = LAST_INSERT_ID();

-- 为仓库用户分配角色
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (@warehouse_user_id, @warehouse_role_id);

-- 插入商品分类测试数据
INSERT INTO `goods_category` (`category_name`, `parent_id`, `order_num`, `status`, `create_by`, `create_time`) VALUES
('电子产品', 0, '10', '1', 'admin', NOW()),
('手机', 1, '10', '1', 'admin', NOW()),
('电脑', 1, '20', '1', 'admin', NOW()),
('服装鞋帽', 0, '20', '1', 'admin', NOW()),
('男装', 4, '10', '1', 'admin', NOW()),
('女装', 4, '20', '1', 'admin', NOW());

-- 插入商品测试数据
INSERT INTO `goods_goods` (`goods_name`, `goods_code`, `category_id`, `price`, `stock`, `status`, `remark`, `create_by`, `create_time`) VALUES
('iPhone 15 Pro', 'IP15P001', 2, 7999.00, 50, '1', '最新款苹果手机', 'admin', NOW()),
('MacBook Pro 14', 'MBP14001', 3, 12999.00, 30, '1', '高性能笔记本电脑', 'admin', NOW()),
('小米14', 'XM14001', 2, 4999.00, 100, '1', '性价比之王', 'admin', NOW()),
('华为Mate 60 Pro', 'HWM60P001', 2, 6999.00, 80, '1', '国产旗舰手机', 'admin', NOW());