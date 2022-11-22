package com.joeylee.domain.properties;

/**
 * JNDI 数据源 配置信息
 *
 * @author joeylee
 */
public class JndiDatasourceProperties extends
    CommonDatasourceProperties {

  private String jndiDataSourceName;

  public String getJndiDataSourceName() {
    return jndiDataSourceName;
  }

  public void setJndiDataSourceName(String jndiDataSourceName) {
    this.jndiDataSourceName = jndiDataSourceName;
  }
}
