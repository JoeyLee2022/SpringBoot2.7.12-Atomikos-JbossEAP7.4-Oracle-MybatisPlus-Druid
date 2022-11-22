package com.joeylee.config.jndi;

import com.joeylee.domain.constant.DataSourceConstant.SecondDataSource;
import com.joeylee.domain.properties.JndiDatasourceProperties;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JNDI 次数据源配置
 *
 * @author joeylee
 */
@Configuration
@MapperScan(basePackages = "${multi-datasource.datasource.druid.basePackages:com.lee.mbk.common.mapper}", sqlSessionTemplateRef = SecondDataSource.sessionTemplate, sqlSessionFactoryRef = SecondDataSource.sessionFactory)
@ConditionalOnProperty(value = "multi-datasource.enabled", havingValue = "JNDI")
public class SecondJndiDataSourceConfig extends JndiDataSourceConfig {

  @ConfigurationProperties(prefix = SecondDataSource.configurationPropertiesPrefix)
  @Bean(SecondDataSource.datasourceProperties)
  public JndiDatasourceProperties datasourceProperties() {
    return new JndiDatasourceProperties();
  }

  @PostConstruct
  public void postConstruct() {
    log.debug("datasourceProperties: {}", datasourceProperties());
  }

  @Bean(name = SecondDataSource.dataSource)
  public DataSource dataSource() {
    return super.dataSource(datasourceProperties());
  }


  @Bean(SecondDataSource.sessionFactory)
  public SqlSessionFactory sqlSessionFactory() throws Exception {
    return super.sqlSessionFactory(dataSource(), datasourceProperties());
  }

  @Bean(SecondDataSource.sessionTemplate)
  public SqlSessionTemplate sqlSessionTemplate() throws Exception {
    return super.sqlSessionTemplate(sqlSessionFactory());
  }


}