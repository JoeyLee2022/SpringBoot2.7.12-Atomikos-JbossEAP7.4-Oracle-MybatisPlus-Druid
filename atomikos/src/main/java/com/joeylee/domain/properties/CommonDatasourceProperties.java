package com.joeylee.domain.properties;

import com.alibaba.druid.pool.xa.DruidXADataSource;

/**
 * 数据源 配置信息
 *
 * @author joeylee
 */
public class CommonDatasourceProperties extends DruidXADataSource {

  //  Mybatis的mapper的xml文件路径
  private String xmlPath = "classpath*:mapper/*.xml";

  //  atomikos 资源名称
  private String uniqueResourceName = "oracle 1";


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