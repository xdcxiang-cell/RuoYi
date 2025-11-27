package com.ruoyi;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.constant.GenConstants;
import com.ruoyi.common.core.domain.entity.SysMenu;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.text.Convert;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.common.utils.security.Md5Utils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.security.PermissionUtils;
import com.ruoyi.generator.domain.GenTable;
import com.ruoyi.generator.domain.GenTableColumn;
import com.ruoyi.generator.service.IGenTableService;
import com.ruoyi.system.service.ISysMenuService;
import com.ruoyi.system.service.ISysRoleService;
import com.ruoyi.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 商品管理模块一键生成器
 * 
 * @author ruoyi
 */
@SpringBootApplication(exclude = { org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class })
public class GoodsModuleGenerator implements CommandLineRunner {

    @Autowired
    private IGenTableService genTableService;

    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysUserService userService;

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(GoodsModuleGenerator.class, args);
        context.close();
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 1. 创建商品表结构
        createGoodsTable();

        // 2. 导入表结构到代码生成器
        importGenTable();

        // 3. 生成前后端代码
        generateCode();

        // 4. 导入菜单到sys_menu表
        importMenu();

        // 5. 创建运营角色和仓库角色并绑定权限
        createRoles();

        // 6. 插入测试用户
        createTestUsers();

        System.out.println("商品管理模块生成完成！");
    }

    /**
     * 创建商品表结构
     */
    private void createGoodsTable() throws Exception {
        String sql = "CREATE TABLE `goods_goods` (" +
                "  `goods_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商品ID'," +
                "  `goods_name` varchar(200) NOT NULL COMMENT '商品名称'," +
                "  `goods_code` varchar(100) NOT NULL COMMENT '商品编号'," +
                "  `category_id` bigint(20) NOT NULL COMMENT '分类ID'," +
                "  `price` decimal(10,2) NOT NULL COMMENT '价格'," +
                "  `stock` int(11) NOT NULL COMMENT '库存'," +
                "  `status` char(1) NOT NULL DEFAULT '1' COMMENT '状态（0停用 1正常）'," +
                "  `remark` varchar(500) DEFAULT NULL COMMENT '备注'," +
                "  `create_by` varchar(64) DEFAULT '' COMMENT '创建者'," +
                "  `create_time` datetime DEFAULT NULL COMMENT '创建时间'," +
                "  `update_by` varchar(64) DEFAULT '' COMMENT '更新者'," +
                "  `update_time` datetime DEFAULT NULL COMMENT '更新时间'," +
                "  PRIMARY KEY (`goods_id`)," +
                "  UNIQUE KEY `goods_code` (`goods_code`)" +
                ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='商品表';";

        List<SQLStatement> sqlStatements = SQLUtils.parseStatements(sql, com.alibaba.druid.DbType.mysql);
        for (SQLStatement sqlStatement : sqlStatements) {
            if (sqlStatement instanceof MySqlCreateTableStatement) {
                MySqlCreateTableStatement createTableStatement = (MySqlCreateTableStatement) sqlStatement;
                genTableService.createTable(createTableStatement.toString());
            }
        }
    }

    /**
     * 导入表结构到代码生成器
     */
    private void importGenTable() {
        String[] tableNames = {"goods_goods"};
        List<GenTable> tableList = genTableService.selectDbTableListByNames(tableNames);
        String operName = "admin";
        genTableService.importGenTable(tableList, operName);

        // 更新表生成配置
        GenTable genTable = genTableService.selectGenTableByName("goods_goods");
        genTable.setModuleName("goods");
        genTable.setBusinessName("goods");
        genTable.setPackageName("com.ruoyi.goods");
        genTable.setTplCategory(GenConstants.TPL_CRUD);
        genTable.setGenType("0"); // 0表示生成zip压缩包



        // 更新列配置
        List<GenTableColumn> columns = genTable.getColumns();
        for (GenTableColumn column : columns) {
            if ("goods_name".equals(column.getColumnName())) {
                column.setIsRequired("1");
                column.setIsList("1");
                column.setIsQuery("1");
                column.setQueryType("LIKE");
            } else if ("goods_code".equals(column.getColumnName())) {
                column.setIsRequired("1");
                column.setIsList("1");
                column.setIsQuery("1");
                column.setQueryType("EQ");
            } else if ("category_id".equals(column.getColumnName())) {
                column.setIsRequired("1");
                column.setIsList("1");
                column.setIsQuery("1");
                column.setQueryType("EQ");
                column.setHtmlType("treeSelect");
            } else if ("price".equals(column.getColumnName())) {
                column.setIsRequired("1");
                column.setIsList("1");
            } else if ("stock".equals(column.getColumnName())) {
                column.setIsRequired("1");
                column.setIsList("1");
            } else if ("status".equals(column.getColumnName())) {
                column.setIsRequired("1");
                column.setIsList("1");
                column.setIsQuery("1");
                column.setQueryType("EQ");
                column.setHtmlType("radio");
                column.setDictType("sys_normal_disable");
            } else if ("remark".equals(column.getColumnName())) {
                column.setIsList("0");
                column.setHtmlType("textarea");
            }
        }

        genTableService.updateGenTable(genTable);
    }

    /**
     * 生成前后端代码
     */
    private void generateCode() {
        genTableService.generatorCode("goods_goods");
    }

    /**
     * 导入菜单到sys_menu表
     */
    private void importMenu() {
        // 查询父菜单（假设导入到系统管理菜单下）
        SysMenu parentMenu = new SysMenu();
        parentMenu.setMenuName("系统管理");
        parentMenu.setMenuType("M");
        List<SysMenu> parentMenuList = menuService.selectMenuList(parentMenu, 1L);
        Long parentMenuId = parentMenuList.get(0).getMenuId();

        // 创建商品管理菜单
        SysMenu goodsMenu = new SysMenu();
        goodsMenu.setMenuName("商品管理");
        goodsMenu.setParentId(parentMenuId);
        goodsMenu.setOrderNum("10");
        goodsMenu.setUrl("/goods/goods");
        // goodsMenu.setComponent("goods/goods/index"); // 注释掉setComponent方法，因为SysMenu类中没有这个方法
        goodsMenu.setMenuType("C");
        goodsMenu.setVisible("0");
        goodsMenu.setPerms("goods:goods:list");
        goodsMenu.setIcon("el-icon-goods");
        goodsMenu.setCreateBy("admin");
        menuService.insertMenu(goodsMenu);

        // 创建商品管理按钮菜单
        Long goodsMenuId = goodsMenu.getMenuId();

        // 新增按钮
        SysMenu addButton = new SysMenu();
        addButton.setMenuName("新增");
        addButton.setParentId(goodsMenuId);
        addButton.setOrderNum("1");
        addButton.setUrl("/goods/goods/add");
        addButton.setMenuType("F");
        addButton.setVisible("0");
        addButton.setPerms("goods:goods:add");
        addButton.setIcon("");
        addButton.setCreateBy("admin");
        menuService.insertMenu(addButton);

        // 修改按钮
        SysMenu editButton = new SysMenu();
        editButton.setMenuName("修改");
        editButton.setParentId(goodsMenuId);
        editButton.setOrderNum("2");
        editButton.setUrl("/goods/goods/edit");
        editButton.setMenuType("F");
        editButton.setVisible("0");
        editButton.setPerms("goods:goods:edit");
        editButton.setIcon("");
        editButton.setCreateBy("admin");
        menuService.insertMenu(editButton);

        // 删除按钮
        SysMenu removeButton = new SysMenu();
        removeButton.setMenuName("删除");
        removeButton.setParentId(goodsMenuId);
        removeButton.setOrderNum("3");
        removeButton.setUrl("/goods/goods/remove");
        removeButton.setMenuType("F");
        removeButton.setVisible("0");
        removeButton.setPerms("goods:goods:remove");
        removeButton.setIcon("");
        removeButton.setCreateBy("admin");
        menuService.insertMenu(removeButton);

        // 创建导出按钮
        SysMenu exportButton = new SysMenu();
        exportButton.setMenuName("导出");
        exportButton.setParentId(goodsMenuId);
        exportButton.setOrderNum("4");
        exportButton.setUrl("/goods/goods/export");
        exportButton.setMenuType("F");
        exportButton.setVisible("0");
        exportButton.setPerms("goods:goods:export");
        exportButton.setIcon("");
        exportButton.setCreateBy("admin");
        menuService.insertMenu(exportButton);
    }

    /**
     * 创建运营角色和仓库角色并绑定权限
     */
    private void createRoles() {
        // 创建运营角色
        SysRole operationRole = new SysRole();
        operationRole.setRoleName("运营");
        operationRole.setRoleKey("operation");
        operationRole.setRoleSort("10");
        operationRole.setDataScope("1"); // 全部数据权限
        operationRole.setStatus("0");
        operationRole.setDelFlag("0");
        operationRole.setCreateBy("admin");

        // 绑定运营角色权限
        List<SysMenu> operationMenus = menuService.selectMenuList(new SysMenu(), 1L);
        List<Long> operationMenuIds = new ArrayList<>();
        for (SysMenu menu : operationMenus) {
            if (menu.getPerms() != null && (menu.getPerms().startsWith("goods:goods:list") ||
                    menu.getPerms().startsWith("goods:goods:add") ||
                    menu.getPerms().startsWith("goods:goods:edit") ||
                    menu.getPerms().startsWith("goods:goods:remove") ||
                    menu.getPerms().startsWith("goods:goods:export"))) {
                operationMenuIds.add(menu.getMenuId());
            }
        }
        operationRole.setMenuIds(operationMenuIds.toArray(new Long[0]));
        roleService.insertRole(operationRole);

        // 创建仓库角色
        SysRole warehouseRole = new SysRole();
        warehouseRole.setRoleName("仓库");
        warehouseRole.setRoleKey("warehouse");
        warehouseRole.setRoleSort("20");
        warehouseRole.setDataScope("1"); // 全部数据权限
        warehouseRole.setStatus("0");
        warehouseRole.setDelFlag("0");
        warehouseRole.setCreateBy("admin");

        // 绑定仓库角色权限
        List<Long> warehouseMenuIds = new ArrayList<>();
        for (SysMenu menu : operationMenus) {
            if (menu.getPerms() != null && (menu.getPerms().startsWith("goods:goods:list") ||
                    menu.getPerms().startsWith("goods:goods:edit"))) {
                warehouseMenuIds.add(menu.getMenuId());
            }
        }
        warehouseRole.setMenuIds(warehouseMenuIds.toArray(new Long[0]));
        roleService.insertRole(warehouseRole);
    }

    /**
     * 插入测试用户
     */
    private void createTestUsers() {
        // 创建运营用户
        SysUser operationUser = new SysUser();
        operationUser.setLoginName("operation001");
        operationUser.setUserName("运营用户");
        operationUser.setEmail("operation001@example.com");
        operationUser.setPhonenumber("13800138001");
        operationUser.setSex("1");
        operationUser.setStatus("0");
        operationUser.setCreateBy("admin");
        // 设置密码
        String password = "123456";
        operationUser.setPassword(Md5Utils.hash(operationUser.getLoginName() + password));
        // 设置岗位ID（默认使用第一个岗位）
        operationUser.setPostIds(new Long[]{1L});
        // 设置角色ID
        SysRole operationRole = new SysRole();
        operationRole.setRoleKey("operation");
        List<SysRole> operationRoles = roleService.selectRoleList(operationRole);
        if (operationRoles != null && !operationRoles.isEmpty()) {
            operationUser.setRoleIds(new Long[]{operationRoles.get(0).getRoleId()});
        }
        // 插入用户
        userService.insertUser(operationUser);

        // 创建仓库用户
        SysUser warehouseUser = new SysUser();
        warehouseUser.setLoginName("warehouse001");
        warehouseUser.setUserName("仓库用户");
        warehouseUser.setEmail("warehouse001@example.com");
        warehouseUser.setPhonenumber("13800138002");
        warehouseUser.setSex("1");
        warehouseUser.setStatus("0");
        warehouseUser.setCreateBy("admin");
        // 设置密码
        warehouseUser.setPassword(Md5Utils.hash(warehouseUser.getLoginName() + password));
        // 设置岗位ID（默认使用第一个岗位）
        warehouseUser.setPostIds(new Long[]{1L});
        // 设置角色ID
        SysRole warehouseRole = new SysRole();
        warehouseRole.setRoleKey("warehouse");
        List<SysRole> warehouseRoles = roleService.selectRoleList(warehouseRole);
        if (warehouseRoles != null && !warehouseRoles.isEmpty()) {
            warehouseUser.setRoleIds(new Long[]{warehouseRoles.get(0).getRoleId()});
        }
        // 插入用户
        userService.insertUser(warehouseUser);
    }
}