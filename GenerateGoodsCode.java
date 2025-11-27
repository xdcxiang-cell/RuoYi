import com.ruoyi.generator.domain.GenTable;
import com.ruoyi.generator.domain.GenTableColumn;
import com.ruoyi.generator.service.IGenTableService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;

public class GenerateGoodsCode {

    public static void main(String[] args) {
        // 加载Spring配置文件
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        IGenTableService genTableService = context.getBean(IGenTableService.class);

        // 创建商品管理表信息
        GenTable genTable = new GenTable();
        genTable.setTableName("goods_goods");
        genTable.setTableComment("商品管理表");
        genTable.setClassName("Goods");
        genTable.setModuleName("goods");
        genTable.setBusinessName("goods");
        genTable.setPackageName("com.ruoyi.goods");
        genTable.setFunctionName("商品管理");
        genTable.setFunctionAuthor("admin");
        genTable.setTreeCode("category_id");
        genTable.setTreeParentCode("parent_id");
        genTable.setTreeName("category_name");

        // 创建表字段信息
        List<GenTableColumn> columns = new ArrayList<>();

        // 商品ID
        GenTableColumn column1 = new GenTableColumn();
        column1.setColumnName("goods_id");
        column1.setColumnComment("商品ID");
        column1.setColumnType("bigint(20)");
        column1.setJavaType("Long");
        column1.setJavaField("goodsId");
        column1.setIsPk("Y");
        column1.setIsIncrement("Y");
        column1.setIsRequired("Y");
        column1.setIsInsert("N");
        column1.setIsEdit("N");
        column1.setIsList("N");
        column1.setIsQuery("N");
        columns.add(column1);

        // 商品名称
        GenTableColumn column2 = new GenTableColumn();
        column2.setColumnName("goods_name");
        column2.setColumnComment("商品名称");
        column2.setColumnType("varchar(100)");
        column2.setJavaType("String");
        column2.setJavaField("goodsName");
        column2.setIsPk("N");
        column2.setIsIncrement("N");
        column2.setIsRequired("Y");
        column2.setIsInsert("Y");
        column2.setIsEdit("Y");
        column2.setIsList("Y");
        column2.setIsQuery("Y");
        column2.setQueryType("LIKE");
        column2.setHtmlType("input");
        columns.add(column2);

        // 商品编号
        GenTableColumn column3 = new GenTableColumn();
        column3.setColumnName("goods_code");
        column3.setColumnComment("商品编号");
        column3.setColumnType("varchar(50)");
        column3.setJavaType("String");
        column3.setJavaField("goodsCode");
        column3.setIsPk("N");
        column3.setIsIncrement("N");
        column3.setIsRequired("Y");
        column3.setIsInsert("Y");
        column3.setIsEdit("Y");
        column3.setIsList("Y");
        column3.setIsQuery("Y");
        column3.setQueryType("EQ");
        column3.setHtmlType("input");
        columns.add(column3);

        // 分类ID
        GenTableColumn column4 = new GenTableColumn();
        column4.setColumnName("category_id");
        column4.setColumnComment("分类ID");
        column4.setColumnType("bigint(20)");
        column4.setJavaType("Long");
        column4.setJavaField("categoryId");
        column4.setIsPk("N");
        column4.setIsIncrement("N");
        column4.setIsRequired("Y");
        column4.setIsInsert("Y");
        column4.setIsEdit("Y");
        column4.setIsList("Y");
        column4.setIsQuery("Y");
        column4.setQueryType("EQ");
        column4.setHtmlType("treeSelect");
        column4.setDictType("goods_category");
        columns.add(column4);

        // 价格
        GenTableColumn column5 = new GenTableColumn();
        column5.setColumnName("price");
        column5.setColumnComment("价格");
        column5.setColumnType("decimal(10,2)");
        column5.setJavaType("BigDecimal");
        column5.setJavaField("price");
        column5.setIsPk("N");
        column5.setIsIncrement("N");
        column5.setIsRequired("Y");
        column5.setIsInsert("Y");
        column5.setIsEdit("Y");
        column5.setIsList("Y");
        column5.setIsQuery("N");
        column5.setHtmlType("input");
        columns.add(column5);

        // 库存
        GenTableColumn column6 = new GenTableColumn();
        column6.setColumnName("stock");
        column6.setColumnComment("库存");
        column6.setColumnType("int(11)");
        column6.setJavaType("Integer");
        column6.setJavaField("stock");
        column6.setIsPk("N");
        column6.setIsIncrement("N");
        column6.setIsRequired("Y");
        column6.setIsInsert("Y");
        column6.setIsEdit("Y");
        column6.setIsList("Y");
        column6.setIsQuery("N");
        column6.setHtmlType("input");
        columns.add(column6);

        // 状态
        GenTableColumn column7 = new GenTableColumn();
        column7.setColumnName("status");
        column7.setColumnComment("状态（0停用/1正常）");
        column7.setColumnType("char(1)");
        column7.setJavaType("String");
        column7.setJavaField("status");
        column7.setIsPk("N");
        column7.setIsIncrement("N");
        column7.setIsRequired("Y");
        column7.setIsInsert("Y");
        column7.setIsEdit("Y");
        column7.setIsList("Y");
        column7.setIsQuery("Y");
        column7.setQueryType("EQ");
        column7.setHtmlType("radio");
        column7.setDictType("sys_normal_disable");
        columns.add(column7);

        // 备注
        GenTableColumn column8 = new GenTableColumn();
        column8.setColumnName("remark");
        column8.setColumnComment("备注");
        column8.setColumnType("varchar(500)");
        column8.setJavaType("String");
        column8.setJavaField("remark");
        column8.setIsPk("N");
        column8.setIsIncrement("N");
        column8.setIsRequired("N");
        column8.setIsInsert("Y");
        column8.setIsEdit("Y");
        column8.setIsList("N");
        column8.setIsQuery("N");
        column8.setHtmlType("textarea");
        columns.add(column8);

        // 创建时间
        GenTableColumn column9 = new GenTableColumn();
        column9.setColumnName("create_time");
        column9.setColumnComment("创建时间");
        column9.setColumnType("datetime");
        column9.setJavaType("Date");
        column9.setJavaField("createTime");
        column9.setIsPk("N");
        column9.setIsIncrement("N");
        column9.setIsRequired("N");
        column9.setIsInsert("N");
        column9.setIsEdit("N");
        column9.setIsList("Y");
        column9.setIsQuery("Y");
        column9.setQueryType("BETWEEN");
        column9.setHtmlType("datetime");
        columns.add(column9);

        // 更新时间
        GenTableColumn column10 = new GenTableColumn();
        column10.setColumnName("update_time");
        column10.setColumnComment("更新时间");
        column10.setColumnType("datetime");
        column10.setJavaType("Date");
        column10.setJavaField("updateTime");
        column10.setIsPk("N");
        column10.setIsIncrement("N");
        column10.setIsRequired("N");
        column10.setIsInsert("N");
        column10.setIsEdit("N");
        column10.setIsList("N");
        column10.setIsQuery("N");
        column10.setHtmlType("datetime");
        columns.add(column10);

        // 创建者
        GenTableColumn column11 = new GenTableColumn();
        column11.setColumnName("create_by");
        column11.setColumnComment("创建者");
        column11.setColumnType("varchar(64)");
        column11.setJavaType("String");
        column11.setJavaField("createBy");
        column11.setIsPk("N");
        column11.setIsIncrement("N");
        column11.setIsRequired("N");
        column11.setIsInsert("N");
        column11.setIsEdit("N");
        column11.setIsList("N");
        column11.setIsQuery("N");
        column11.setHtmlType("input");
        columns.add(column11);

        // 更新者
        GenTableColumn column12 = new GenTableColumn();
        column12.setColumnName("update_by");
        column12.setColumnComment("更新者");
        column12.setColumnType("varchar(64)");
        column12.setJavaType("String");
        column12.setJavaField("updateBy");
        column12.setIsPk("N");
        column12.setIsIncrement("N");
        column12.setIsRequired("N");
        column12.setIsInsert("N");
        column12.setIsEdit("N");
        column12.setIsList("N");
        column12.setIsQuery("N");
        column12.setHtmlType("input");
        columns.add(column12);

        // 设置表字段
        genTable.setColumns(columns);

        // 生成代码
        try {
            genTableService.generatorCode(new GenTable[]{genTable});
            System.out.println("商品管理模块代码生成成功！");
        } catch (Exception e) {
            System.out.println("商品管理模块代码生成失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
}