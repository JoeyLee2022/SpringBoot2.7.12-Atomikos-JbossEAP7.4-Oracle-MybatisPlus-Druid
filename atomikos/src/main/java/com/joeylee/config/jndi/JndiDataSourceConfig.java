package com.joeylee.config.jndi;

import com.joeylee.config.datasource.BaseDataSourceConfig;
import com.joeylee.domain.constant.JndiConstant;
import com.joeylee.domain.properties.JndiDatasourceProperties;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;

/**
 * JNDI 数据源配置
 *
 * @author joeylee
 */
@DependsOn(JndiConstant.DataSourceJndiConfig)
@ConditionalOnProperty(value = "multi-datasource.enabled", havingValue = "JNDI")
public class JndiDataSourceConfig extends BaseDataSourceConfig {

  /**
   * 获取JNDI容器上下文
   *
   * @return
   */
  @Bean(JndiConstant.InitialContext)
  public InitialContext initialContext() {
    try {
      InitialContext initialContext = new InitialContext();
      return initialContext;
    } catch (NamingException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * 从容器里通过JNDI名称获取数据源配置
   *
   * @param properties JNDI 数据源 配置信息
   * @return
   */
  public DataSource dataSource(
      JndiDatasourceProperties properties) {

    DataSource dataSource;
    try {
      dataSource = (DataSource) initialContext().lookup(
          properties.getJndiDataSourceName());
      log.debug("dataSource : {}", dataSource.getClass());
    } catch (NamingException e) {
      throw new RuntimeException(e);
    }
    return dataSource;
  }


}