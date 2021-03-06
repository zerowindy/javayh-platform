/**
 *   Copyright [2020] [Yang Hai Ji of copyright owner]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.javayh.generator.file;

import com.javayh.generator.bean.FieldBean;
import com.javayh.generator.config.GenerateConfig;
import com.javayh.generator.util.FileUtils;
import com.javayh.generator.util.NameUtils;

import java.io.File;
import java.io.IOException;

/**
 * 生成XML文件
 */
public class GenMapperXmlFile {

	private GenMain gMain;

	private GenBeanFile beanFile;

	private GenMapperJavaFile mapperFile;

	private String packageName;

	private String className;

	private String fileName;

	private String classQuaName; // 全称

	public void generate() {
		// 包名
		packageName = GenerateConfig.xmlbasepathPrefix + "." + GenerateConfig.MAPPER_NAME
				+ "." + gMain.getModelName().replace(".", "/");
		className = NameUtils.formatClassName(gMain.getTableName())
				+ GenerateConfig.MAPPER_FILENAME;
		fileName = className + ".xml";
		classQuaName = packageName + "." + className;
		// String xmlapath = System.getProperty("user.dir") +
		// "/src/main/resources/mapper/" + gMain.getModelName().replace(".", "/");
		File file = new File(packageName.replace(".", "/"), fileName);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		System.out.println("生成mapper.xml文件的目录" + file.getAbsolutePath());
		if (!file.exists()) {
			try {
				file.createNewFile();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		// 修改文件编码
		FileUtils.changeFileEncoding(file, "UTF-8");

		// 向文件写入内容
		FileUtils.WriteStringToFile(file, genContent());
	}

	private String genContent() {
		String lineSeparator = System.getProperty("line.separator");
		StringBuffer content = new StringBuffer();

		// 写入文件头部
		content.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>")
				.append(lineSeparator);
		content.append(
				"<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\" >")
				.append(lineSeparator);
		content.append("<mapper namespace=\"").append(mapperFile.getClassQuaName())
				.append("\">").append(lineSeparator);
        content.append("<cache type=\"com.javayh.mybatis.cache.RedisCache\"/>").append(lineSeparator);
		// 写入共有列sql
		content.append(lineSeparator);
		content.append("\t")
				.append("<!-- 数据库中表名为:" + gMain.getTableName()
						+ "的列名,as前是数据库的列明,as后是列的别名用于映射成实体类中的属性 -->")
				.append(lineSeparator);
		content.append("\t").append("<sql id=\"")
				.append(NameUtils.formatName(gMain.getTableName())).append("Column\">")
				.append(lineSeparator);
		for (int i = 0; i < gMain.getStructureList().size(); i++) {
			FieldBean field = gMain.getStructureList().get(i);
			if (i == gMain.getStructureList().size() - 1) {
				content.append("\t\t").append(gMain.getTableName()).append(".")
						.append(field.getColumnName()).append(" as ")
						.append(field.getFieldName()).append(lineSeparator);
			}
			else {
				content.append("\t\t").append(gMain.getTableName()).append(".")
						.append(field.getColumnName()).append(" as ")
						.append(field.getFieldName()).append(",").append(lineSeparator);
			}
		}
		content.append("\t").append("</sql>").append(lineSeparator);

		// 写入新增sql
		content.append(lineSeparator);
		content.append("\t").append("<insert id=\"insert\" ").append("keyColumn=\"")
				.append(gMain.getStructureList().get(0).getColumnName())
				.append("\" keyProperty=\"")
				.append(gMain.getStructureList().get(0).getFieldName()).append("\">")
				.append(lineSeparator);
		content.append("\t\t").append("<selectKey ").append("keyColumn=\"")
				.append(gMain.getStructureList().get(0).getColumnName())
				.append("\" keyProperty=\"")
				.append(gMain.getStructureList().get(0).getFieldName())
				.append("\"  order=\"BEFORE\" resultType=\"Integer\">")
				.append(lineSeparator);
		content.append("\t\t\t").append("select LAST_INSERT_ID()").append(lineSeparator);
		content.append("\t\t").append("</selectKey>").append(lineSeparator);
		content.append("\t\t").append("insert into ").append(gMain.getTableName())
				.append(lineSeparator);
		content.append("\t\t").append("( ").append(lineSeparator);
		for (int i = 1; i < gMain.getStructureList().size(); i++) {
			FieldBean field = gMain.getStructureList().get(i);

			content.append("\t\t\t").append("<if test=\"").append(field.getFieldName())
					.append(" != null ");
			if (field.getVhClass().getName().contains("String")) {
				content.append(" and " + field.getFieldName()).append(" != '' \">")
						.append(lineSeparator);
			}
			else {
				content.append(" \">").append(lineSeparator);
			}
			if (i == 1) {
				content.append("\t\t\t\t").append(field.getColumnName())
						.append(lineSeparator);
			}
			else {
				content.append("\t\t\t\t,").append(field.getColumnName())
						.append(lineSeparator);
			}
			content.append("\t\t\t").append("</if>").append(lineSeparator);
		}
		content.append("\t\t").append(") ").append(lineSeparator);
		content.append("\t\t").append("values (").append(lineSeparator);
		for (int i = 1; i < gMain.getStructureList().size(); i++) {
			FieldBean field = gMain.getStructureList().get(i);
			content.append("\t\t\t").append("<if test=\"").append(field.getFieldName())
					.append(" != null ");
			if (field.getVhClass().getName().contains("String")) {
				content.append(" and " + field.getFieldName()).append(" != '' \">")
						.append(lineSeparator);
			}
			else {
				content.append(" \">").append(lineSeparator);
			}
			if (i == 1) {
				content.append("\t\t\t\t#{").append(field.getFieldName()).append("}")
						.append(lineSeparator);
			}
			else {
				content.append("\t\t\t\t,#{").append(field.getFieldName()).append("}")
						.append(lineSeparator);
			}
			content.append("\t\t\t").append("</if>").append(lineSeparator);
		}
		content.append("\t\t").append(") ").append(lineSeparator);
		content.append("\t").append("</insert>").append(lineSeparator);

		// 写入新增 批量操作sql
		content.append(lineSeparator);
		content.append("\t").append("<insert id=\"batchInsert\" >").append(lineSeparator);
		content.append("\t\t").append("insert into ").append(gMain.getTableName())
				.append(lineSeparator);
		content.append("\t\t").append("( ").append(lineSeparator);
		for (int i = 1; i < gMain.getStructureList().size(); i++) {
			FieldBean field = gMain.getStructureList().get(i);

			if (i == 1) {
				content.append("\t\t\t\t").append(field.getColumnName())
						.append(lineSeparator);
			}
			else {
				content.append("\t\t\t\t,").append(field.getColumnName())
						.append(lineSeparator);
			}
		}
		content.append("\t\t").append(") ").append(lineSeparator);
		content.append("\t\t").append("values ").append(lineSeparator);
		content.append("\t\t").append(" <foreach collection=\"list\" item=\"item\" separator=\",\">").append(lineSeparator);
		content.append("\t\t").append("(").append(lineSeparator);

		for (int i = 1; i < gMain.getStructureList().size(); i++) {
			FieldBean field = gMain.getStructureList().get(i);
			content.append("\t\t\t").append("<if test=\"").append("item."+field.getFieldName())
					.append(" != null ");
			if (field.getVhClass().getName().contains("String")) {
				content.append(" and item." + field.getFieldName()).append(" != '' \">")
						.append(lineSeparator);
			}
			else {
				content.append(" \">").append(lineSeparator);
			}
			if (i == 1) {
				content.append("\t\t\t\t#{").append("item."+field.getFieldName()).append("}")
						.append(lineSeparator);
			}
			else {
				content.append("\t\t\t\t,#{").append("item."+field.getFieldName()).append("}")
						.append(lineSeparator);
			}
			content.append("\t\t\t").append("</if>").append(lineSeparator);
		}
		content.append("\t\t").append(") ").append(lineSeparator);
		content.append("\t\t").append(" </foreach>").append(lineSeparator);
		content.append("\t").append("</insert>").append(lineSeparator);


		// 写入修改sql
		content.append(lineSeparator);
		content.append("\t").append("<update id=\"update\">").append(lineSeparator);
		content.append("\t\t").append("update ").append(gMain.getTableName())
				.append(lineSeparator);
		content.append("\t\t").append("<trim prefix=\"set\" suffixOverrides=\",\">")
				.append(lineSeparator);
		for (int i = 1; i < gMain.getStructureList().size(); i++) {
			FieldBean field = gMain.getStructureList().get(i);
			content.append("\t\t\t").append("<if test=\"").append(field.getFieldName())
					.append(" != null ");
			if (field.getVhClass().getName().contains("String")) {
				content.append(" and " + field.getFieldName()).append(" != '' \">")
						.append(lineSeparator);
			}
			else {
				content.append(" \">").append(lineSeparator);
			}
			content.append("\t\t\t\t").append(field.getColumnName()).append("=#{")
					.append(field.getFieldName()).append("}").append(",")
					.append(lineSeparator);
			content.append("\t\t\t").append("</if>").append(lineSeparator);
		}
		content.append("\t\t").append("</trim>").append(lineSeparator);
		content.append("\t\t").append("WHERE ")
				.append(gMain.getStructureList().get(0).getColumnName()).append("=#{")
				.append(gMain.getStructureList().get(0).getFieldName()).append("}")
				.append(lineSeparator);
		content.append("\t").append("</update>").append(lineSeparator);

		// 写入删除sql
		content.append(lineSeparator);
		content.append("\t").append("<delete id=\"deleteById\">").append(lineSeparator);
		content.append("\t\t").append("DELETE FROM  ").append(gMain.getTableName())
				.append(lineSeparator);
		content.append("\t\t").append("WHERE ")
				.append(gMain.getStructureList().get(0).getColumnName()).append("=#{")
				.append(gMain.getStructureList().get(0).getFieldName()).append("}")
				.append(lineSeparator);
		content.append("\t").append("</delete>").append(lineSeparator);

		// 写入查找sql
		content.append(lineSeparator);
		content.append("\t").append("<select id=\"findById\" resultType=\"")
				.append(beanFile.getClassQuaName()).append("\">").append(lineSeparator);
		content.append("\t\t").append("SELECT <include refid=\"")
				.append(NameUtils.formatName(gMain.getTableName())).append("Column")
				.append("\"/>").append(lineSeparator);
		content.append("\t\t").append("FROM ").append(gMain.getTableName())
				.append(lineSeparator);
		content.append("\t\t").append("WHERE ")
				.append(gMain.getStructureList().get(0).getColumnName()).append("=#{")
				.append(gMain.getStructureList().get(0).getFieldName()).append("}")
				.append(lineSeparator);
		content.append("\t").append("</select>").append(lineSeparator);

		// 写入查找sql
		content.append(lineSeparator);
		content.append("\t").append("<select id=\"findByPage\" resultType=\"")
				.append(beanFile.getClassQuaName()).append("\">").append(lineSeparator);
		content.append("\t\t").append("SELECT <include refid=\"")
				.append(NameUtils.formatName(gMain.getTableName())).append("Column")
				.append("\"/>").append(lineSeparator);
		content.append("\t\t").append("FROM ").append(gMain.getTableName())
				.append(lineSeparator);
		content.append("\t\t").append("WHERE  1=1 ").append(lineSeparator);
        FieldBean field =null;
		for (int i = 1; i < gMain.getStructureList().size(); i++) {
            field = gMain.getStructureList().get(i);
			content.append("\t\t\t").append("<if test=\"").append(field.getFieldName())
					.append(" != null ");
			if (field.getVhClass().getName().contains("String")) {
				content.append(" and " + field.getFieldName()).append(" != '' \">")
						.append(lineSeparator);
			}
			else {
				content.append(" \">").append(lineSeparator);
			}
			content.append("\t\t\t\t").append(" and ").append(field.getColumnName())
					.append("=#{").append(field.getFieldName()).append("}")
					.append(lineSeparator);
			content.append("\t\t\t").append("</if>").append(lineSeparator);
		}
        content.append("\t\t\t").append("<if test=\"").append("orderBy") .append(" != null ").
                append(" and orderBy != '' ").append(" \">").append(lineSeparator);
        content.append("\t\t\t\t").append(" order by ").append("#{orderBy}").append(lineSeparator);
        content.append("\t\t\t").append("</if>").append(lineSeparator);
		content.append("\t").append("</select>").append(lineSeparator);
		content.append("</mapper>").append(lineSeparator);
		return content.toString();
	}

	/**
	 * @param gMain
	 */
	public GenMapperXmlFile(GenMain gMain, GenBeanFile beanFile,
			GenMapperJavaFile mapperFile) {
		super();
		this.gMain = gMain;
		this.beanFile = beanFile;
		this.mapperFile = mapperFile;
	}

	/**
	 * @return the classQuaName
	 */
	public String getClassQuaName() {
		return classQuaName;
	}

	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}

}
