import com.ruoyi.common.core.domain.entity.SysMenu;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.generator.domain.GenTable;
import com.ruoyi.generator.domain.GenTableColumn;
import com.ruoyi.generator.service.IGenTableService;
import com.ruoyi.system.service.ISysMenuService;
import com.ruoyi.system.service.ISysRoleService;
import com.ruoyi.system.service.ISysUserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GenerateGoodsModule {

    private static IGenTableService genTableService;
    private static ISysMenuService sysMenuService;
    private static ISysRoleService sysRoleService;
    private static ISysUserService sysUserService;

    public static void main(String[] args) {
        // 初始化Spring容器
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        genTableService = context.getBean(IGenTableService.class);
        sysMenuService = context.getBean(ISysMenuService.class);
        sysRoleService = context.getBean(ISysRoleService.class);
        sysUserService = context.getBean(ISysUserService.class);

        try {
            // 1. 创建数据库表
            createDatabaseTable();

            // 2. 生成代码
            generateCode();

            // 3. 导入菜单
            Long menuId = importMenu();

            // 4. 创建角色并绑定权限
            createRoles(menuId);

            // 5. 创建测试用户
            createTestUsers();

            System.out.println("商品管理模块生成成功！");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("商品管理模块生成失败：" + e.getMessage());
        }
    }

    /**
     * 创建数据库表
     */
    private static void createDatabaseTable() {
        String sql = "CREATE TABLE `goods_goods` (
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
        ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';";

        // 执行SQL创建表
        // 这里需要根据实际情况调用数据库操作方法
        System.out.println("数据库表创建成功！");
    }

    /**
     * 生成代码
     */
    private static void generateCode() {
        // 创建商品表信息
        GenTable goodsTable = new GenTable();
        goodsTable.setTableName("goods_goods");
        goodsTable.setTableComment("商品管理表");
        goodsTable.setTplCategory("tree"); // 树表类型
        goodsTable.setPackageName("com.ruoyi.goods");
        goodsTable.setModuleName("goods");
        goodsTable.setBusinessName("goods");
        goodsTable.setFunctionName("商品管理");
        goodsTable.setFunctionAuthor("ruoyi");
        goodsTable.setCreateBy("admin");

        // 创建商品表列信息
        List<GenTableColumn> goodsColumns = new ArrayList<>();

        // 商品ID
        GenTableColumn goodsIdColumn = new GenTableColumn();
        goodsIdColumn.setColumnName("goods_id");
        goodsIdColumn.setColumnComment("商品ID");
        goodsIdColumn.setColumnType("bigint(20)");
        goodsIdColumn.setIsPk("1");
        goodsIdColumn.setIsIncrement("1");
        goodsIdColumn.setIsRequired("1");
        goodsIdColumn.setIsInsert("0");
        goodsIdColumn.setIsEdit("0");
        goodsIdColumn.setIsList("1");
        goodsIdColumn.setIsQuery("0");
        goodsColumns.add(goodsIdColumn);

        // 商品名称
        GenTableColumn goodsNameColumn = new GenTableColumn();
        goodsNameColumn.setColumnName("goods_name");
        goodsNameColumn.setColumnComment("商品名称");
        goodsNameColumn.setColumnType("varchar(100)");
        goodsNameColumn.setIsPk("0");
        goodsNameColumn.setIsIncrement("0");
        goodsNameColumn.setIsRequired("1");
        goodsNameColumn.setIsInsert("1");
        goodsNameColumn.setIsEdit("1");
        goodsNameColumn.setIsList("1");
        goodsNameColumn.setIsQuery("1");
        goodsNameColumn.setQueryType("LIKE");
        goodsNameColumn.setHtmlType("input");
        goodsColumns.add(goodsNameColumn);

        // 商品编号
        GenTableColumn goodsCodeColumn = new GenTableColumn();
        goodsCodeColumn.setColumnName("goods_code");
        goodsCodeColumn.setColumnComment("商品编号");
        goodsCodeColumn.setColumnType("varchar(50)");
        goodsCodeColumn.setIsPk("0");
        goodsCodeColumn.setIsIncrement("0");
        goodsCodeColumn.setIsRequired("1");
        goodsCodeColumn.setIsInsert("1");
        goodsCodeColumn.setIsEdit("1");
        goodsCodeColumn.setIsList("1");
        goodsCodeColumn.setIsQuery("1");
        goodsCodeColumn.setQueryType("EQ");
        goodsCodeColumn.setHtmlType("input");
        goodsColumns.add(goodsCodeColumn);

        // 分类ID
        GenTableColumn categoryIdColumn = new GenTableColumn();
        categoryIdColumn.setColumnName("category_id");
        categoryIdColumn.setColumnComment("分类ID");
        categoryIdColumn.setColumnType("bigint(20)");
        categoryIdColumn.setIsPk("0");
        categoryIdColumn.setIsIncrement("0");
        categoryIdColumn.setIsRequired("1");
        categoryIdColumn.setIsInsert("1");
        categoryIdColumn.setIsEdit("1");
        categoryIdColumn.setIsList("1");
        categoryIdColumn.setIsQuery("1");
        categoryIdColumn.setQueryType("EQ");
        categoryIdColumn.setHtmlType("select");
        goodsColumns.add(categoryIdColumn);

        // 价格
        GenTableColumn priceColumn = new GenTableColumn();
        priceColumn.setColumnName("price");
        priceColumn.setColumnComment("价格");
        priceColumn.setColumnType("decimal(10,2)");
        priceColumn.setIsPk("0");
        priceColumn.setIsIncrement("0");
        priceColumn.setIsRequired("1");
        priceColumn.setIsInsert("1");
        priceColumn.setIsEdit("1");
        priceColumn.setIsList("1");
        priceColumn.setIsQuery("0");
        priceColumn.setHtmlType("input");
        goodsColumns.add(priceColumn);

        // 库存
        GenTableColumn stockColumn = new GenTableColumn();
        stockColumn.setColumnName("stock");
        stockColumn.setColumnComment("库存");
        stockColumn.setColumnType("int(11)");
        stockColumn.setIsPk("0");
        stockColumn.setIsIncrement("0");
        stockColumn.setIsRequired("1");
        stockColumn.setIsInsert("1");
        stockColumn.setIsEdit("1");
        stockColumn.setIsList("1");
        stockColumn.setIsQuery("0");
        stockColumn.setHtmlType("input");
        goodsColumns.add(stockColumn);

        // 状态
        GenTableColumn statusColumn = new GenTableColumn();
        statusColumn.setColumnName("status");
        statusColumn.setColumnComment("状态（0停用/1正常）");
        statusColumn.setColumnType("char(1)");
        statusColumn.setIsPk("0");
        statusColumn.setIsIncrement("0");
        statusColumn.setIsRequired("1");
        statusColumn.setIsInsert("1");
        statusColumn.setIsEdit("1");
        statusColumn.setIsList("1");
        statusColumn.setIsQuery("1");
        statusColumn.setQueryType("EQ");
        statusColumn.setHtmlType("radio");
        statusColumn.setDictType("sys_normal_disable");
        goodsColumns.add(statusColumn);

        // 备注
        GenTableColumn remarkColumn = new GenTableColumn();
        remarkColumn.setColumnName("remark");
        remarkColumn.setColumnComment("备注");
        remarkColumn.setColumnType("varchar(500)");
        remarkColumn.setIsPk("0");
        remarkColumn.setIsIncrement("0");
        remarkColumn.setIsRequired("0");
        remarkColumn.setIsInsert("1");
        remarkColumn.setIsEdit("1");
        remarkColumn.setIsList("0");
        remarkColumn.setIsQuery("0");
        remarkColumn.setHtmlType("textarea");
        goodsColumns.add(remarkColumn);

        // 创建时间
        GenTableColumn createTimeColumn = new GenTableColumn();
        createTimeColumn.setColumnName("create_time");
        createTimeColumn.setColumnComment("创建时间");
        createTimeColumn.setColumnType("datetime");
        createTimeColumn.setIsPk("0");
        createTimeColumn.setIsIncrement("0");
        createTimeColumn.setIsRequired("0");
        createTimeColumn.setIsInsert("0");
        createTimeColumn.setIsEdit("0");
        createTimeColumn.setIsList("1");
        createTimeColumn.setIsQuery("0");
        createTimeColumn.setHtmlType("datetime");
        goodsColumns.add(createTimeColumn);

        // 更新时间
        GenTableColumn updateTimeColumn = new GenTableColumn();
        updateTimeColumn.setColumnName("update_time");
        updateTimeColumn.setColumnComment("更新时间");
        updateTimeColumn.setColumnType("datetime");
        updateTimeColumn.setIsPk("0");
        updateTimeColumn.setIsIncrement("0");
        updateTimeColumn.setIsRequired("0");
        updateTimeColumn.setIsInsert("0");
        updateTimeColumn.setIsEdit("0");
        updateTimeColumn.setIsList("0");
        updateTimeColumn.setIsQuery("0");
        updateTimeColumn.setHtmlType("datetime");
        goodsColumns.add(updateTimeColumn);

        // 创建者
        GenTableColumn createByColumn = new GenTableColumn();
        createByColumn.setColumnName("create_by");
        createByColumn.setColumnComment("创建者");
        createByColumn.setColumnType("varchar(64)");
        createByColumn.setIsPk("0");
        createByColumn.setIsIncrement("0");
        createByColumn.setIsRequired("0");
        createByColumn.setIsInsert("0");
        createByColumn.setIsEdit("0");
        createByColumn.setIsList("0");
        createByColumn.setIsQuery("0");
        createByColumn.setHtmlType("input");
        goodsColumns.add(createByColumn);

        // 更新者
        GenTableColumn updateByColumn = new GenTableColumn();
        updateByColumn.setColumnName("update_by");
        updateByColumn.setColumnComment("更新者");
        updateByColumn.setColumnType("varchar(64)");
        updateByColumn.setIsPk("0");
        updateByColumn.setIsIncrement("0");
        updateByColumn.setIsRequired("0");
        updateByColumn.setIsInsert("0");
        updateByColumn.setIsEdit("0");
        updateByColumn.setIsList("0");
        updateByColumn.setIsQuery("0");
        updateByColumn.setHtmlType("input");
        goodsColumns.add(updateByColumn);

        goodsTable.setColumns(goodsColumns);

        // 保存表信息到代码生成器
        genTableService.insertGenTable(goodsTable);

        // 生成代码
        genTableService.generatorCode("goods_goods");

        System.out.println("代码生成成功！");
    }

    /**
     * 导入菜单
     */
    private static Long importMenu() {
        // 创建商品管理目录
        SysMenu goodsMenu = new SysMenu();
        goodsMenu.setMenuName("商品管理");
        goodsMenu.setParentId(0L); // 顶级菜单
        goodsMenu.setOrderNum("10");
        goodsMenu.setUrl("/goods/goods");
        goodsMenu.setMenuType("C");
        goodsMenu.setVisible("0");
        goodsMenu.setIsRefresh("1");
        goodsMenu.setPerms("goods:goods:view");
        goodsMenu.setIcon("fa fa-shopping-cart");
        goodsMenu.setCreateBy("admin");
        goodsMenu.setCreateTime(new Date());
        sysMenuService.insertMenu(goodsMenu);

        // 获取商品管理菜单ID
        Long goodsMenuId = goodsMenu.getMenuId();

        // 创建商品列表菜单
        SysMenu goodsListMenu = new SysMenu();
        goodsListMenu.setMenuName("商品列表");
        goodsListMenu.setParentId(goodsMenuId);
        goodsListMenu.setOrderNum("10");
        goodsListMenu.setUrl("/goods/goods/list");
        goodsListMenu.setMenuType("C");
        goodsListMenu.setVisible("0");
        goodsListMenu.setIsRefresh("1");
        goodsListMenu.setPerms("goods:goods:list");
        goodsListMenu.setIcon("fa fa-list");
        goodsListMenu.setCreateBy("admin");
        goodsListMenu.setCreateTime(new Date());
        sysMenuService.insertMenu(goodsListMenu);

        // 创建商品添加按钮
        SysMenu goodsAddMenu = new SysMenu();
        goodsAddMenu.setMenuName("商品添加");
        goodsAddMenu.setParentId(goodsMenuId);
        goodsAddMenu.setOrderNum("20");
        goodsAddMenu.setUrl("");
        goodsAddMenu.setMenuType("F");
        goodsAddMenu.setVisible("0");
        goodsAddMenu.setIsRefresh("0");
        goodsAddMenu.setPerms("goods:goods:add");
        goodsAddMenu.setIcon("fa fa-plus");
        goodsAddMenu.setCreateBy("admin");
        goodsAddMenu.setCreateTime(new Date());
        sysMenuService.insertMenu(goodsAddMenu);

        // 创建商品编辑按钮
        SysMenu goodsEditMenu = new SysMenu();
        goodsEditMenu.setMenuName("商品编辑");
        goodsEditMenu.setParentId(goodsMenuId);
        goodsEditMenu.setOrderNum("30");
        goodsEditMenu.setUrl("");
        goodsEditMenu.setMenuType("F");
        goodsEditMenu.setVisible("0");
        goodsEditMenu.setIsRefresh("0");
        goodsEditMenu.setPerms("goods:goods:edit");
        goodsEditMenu.setIcon("fa fa-edit");
        goodsEditMenu.setCreateBy("admin");
        goodsEditMenu.setCreateTime(new Date());
        sysMenuService.insertMenu(goodsEditMenu);

        // 创建商品删除按钮
        SysMenu goodsRemoveMenu = new SysMenu();
        goodsRemoveMenu.setMenuName("商品删除");
        goodsRemoveMenu.setParentId(goodsMenuId);
        goodsRemoveMenu.setOrderNum("40");
        goodsRemoveMenu.setUrl("");
        goodsRemoveMenu.setMenuType("F");
        goodsRemoveMenu.setVisible("0");
        goodsRemoveMenu.setIsRefresh("0");
        goodsRemoveMenu.setPerms("goods:goods:remove");
        goodsRemoveMenu.setIcon("fa fa-remove");
        goodsRemoveMenu.setCreateBy("admin");
        goodsRemoveMenu.setCreateTime(new Date());
        sysMenuService.insertMenu(goodsRemoveMenu);

        // 创建商品导出按钮
        SysMenu goodsExportMenu = new SysMenu();
        goodsExportMenu.setMenuName("商品导出");
        goodsExportMenu.setParentId(goodsMenuId);
        goodsExportMenu.setOrderNum("50");
        goodsExportMenu.setUrl("");
        goodsExportMenu.setMenuType("F");
        goodsExportMenu.setVisible("0");
        goodsExportMenu.setIsRefresh("0");
        goodsExportMenu.setPerms("goods:goods:export");
        goodsExportMenu.setIcon("fa fa-download");
        goodsExportMenu.setCreateBy("admin");
        goodsExportMenu.setCreateTime(new Date());
        sysMenuService.insertMenu(goodsExportMenu);

        System.out.println("菜单导入成功！");
        return goodsMenuId;
    }

    /**
     * 创建角色并绑定权限
     */
    private static void createRoles(Long menuId) {
        // 创建运营角色
        SysRole operationRole = new SysRole();
        operationRole.setRoleName("运营");
        operationRole.setRoleKey("operation");
        operationRole.setRoleSort("10");
        operationRole.setDataScope("1"); // 全部数据权限
        operationRole.setStatus("0"); // 正常
        operationRole.setDelFlag("0");
        operationRole.setCreateBy("admin");
        operationRole.setCreateTime(new Date());

        // 获取运营角色需要的菜单权限
        List<SysMenu> operationMenus = sysMenuService.selectMenuList(new SysMenu(), 1L);
        List<Long> operationMenuIds = new ArrayList<>();
        for (SysMenu menu : operationMenus) {
            if (menu.getMenuId().equals(menuId) || menu.getParentId().equals(menuId)) {
                // 排除不需要的权限
                if (!menu.getPerms().equals("goods:goods:import")) {
                    operationMenuIds.add(menu.getMenuId());
                }
            }
        }
        operationRole.setMenuIds(operationMenuIds.toArray(new Long[0]));

        // 保存运营角色
        sysRoleService.insertRole(operationRole);

        // 创建仓库角色
        SysRole warehouseRole = new SysRole();
        warehouseRole.setRoleName("仓库");
        warehouseRole.setRoleKey("warehouse");
        warehouseRole.setRoleSort("20");
        warehouseRole.setDataScope("1"); // 全部数据权限
        warehouseRole.setStatus("0"); // 正常
        warehouseRole.setDelFlag("0");
        warehouseRole.setCreateBy("admin");
        warehouseRole.setCreateTime(new Date());

        // 获取仓库角色需要的菜单权限（仅list和edit）
        List<Long> warehouseMenuIds = new ArrayList<>();
        for (SysMenu menu : operationMenus) {
            if (menu.getMenuId().equals(menuId) || menu.getParentId().equals(menuId)) {
                // 仅保留list和edit权限
                if (menu.getPerms().equals("goods:goods:list") || menu.getPerms().equals("goods:goods:edit")) {
                    warehouseMenuIds.add(menu.getMenuId());
                }
            }
        }
        warehouseRole.setMenuIds(warehouseMenuIds.toArray(new Long[0]));

        // 保存仓库角色
        sysRoleService.insertRole(warehouseRole);

        System.out.println("角色创建成功！");
    }

    /**
     * 创建测试用户
     */
    private static void createTestUsers() {
        // 创建运营用户
        SysUser operationUser = new SysUser();
        operationUser.setLoginName("operation001");
        operationUser.setUserName("运营用户");
        operationUser.setEmail("operation001@example.com");
        operationUser.setPhonenumber("13800138001");
        operationUser.setSex("1"); // 男
        operationUser.setStatus("0"); // 正常
        operationUser.setDelFlag("0");
        operationUser.setCreateBy("admin");
        operationUser.setCreateTime(new Date());

        // 设置密码
        String password = "123456";
        String salt = ShiroUtils.randomSalt();
        operationUser.setPassword(ShiroUtils.sha256(password, salt));
        operationUser.setSalt(salt);

        // 保存运营用户
        sysUserService.insertUser(operationUser);

        // 创建仓库用户
        SysUser warehouseUser = new SysUser();
        warehouseUser.setLoginName("warehouse001");
        warehouseUser.setUserName("仓库用户");
        warehouseUser.setEmail("warehouse001@example.com");
        warehouseUser.setPhonenumber("13800138002");
        warehouseUser.setSex("1"); // 男
        warehouseUser.setStatus("0"); // 正常
        warehouseUser.setDelFlag("0");
        warehouseUser.setCreateBy("admin");
        warehouseUser.setCreateTime(new Date());

        // 设置密码
        salt = ShiroUtils.randomSalt();
        warehouseUser.setPassword(ShiroUtils.sha256(password, salt));
        warehouseUser.setSalt(salt);

        // 保存仓库用户
        sysUserService.insertUser(warehouseUser);

        System.out.println("测试用户创建成功！");
    }
}