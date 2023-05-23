package com.joeylee.domain.properties;

import com.mysql.jdbc.jdbc2.optional.MysqlXADataSource;

/**
 * 数据源 配置信息
 *
 * @author JoeyLee
 */
public class CommonDatasourceProperties extends MysqlXADataSource {

  //  Mybatis的mapper接口文件路径
  private String[] basePackages;
  //  Mybatis的mapper的xml文件路径
  private String xmlPath = "classpath*:mapper/*.xml";

  //  atomikos 资源名称,每个数据源必须唯一
  private String uniqueResourceName = "oracle 1";

  public String[] getBasePackages() {
    return basePackages;
  }

  public void setBasePackages(String[] basePackages) {
    this.basePackages = basePackages;
  }

  public String getXmlPath() {
    return xmlPath;
  }

  public void setXmlPath(String xmlPath) {
    this.xmlPath = xmlPath;
  }

  public String getUniqueResourceName() {
    return uniqueResourceName;
  }

  public void setUniqueResourceName(String uniqueResourceName) {
    this.uniqueResourceName = uniqueResourceName;
  }
}