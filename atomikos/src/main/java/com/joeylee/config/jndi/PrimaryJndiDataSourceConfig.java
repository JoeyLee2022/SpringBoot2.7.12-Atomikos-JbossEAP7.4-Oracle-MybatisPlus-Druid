package com.joeylee.config.jndi;

import com.joeylee.domain.constant.DataSourceConstant.PrimaryDataSource;
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
 * JNDI 主数据源配置
 *
 * @author joeylee
 */
@Configuration
@MapperScan(basePackages = "${spring.datasource.druid.basePackages:com.lee.mbk.call.test.mapper}", sqlSessionTemplateRef = PrimaryDataSource.sessionTemplate, sqlSessionFactoryRef = PrimaryDataSource.sessionFactory)
@ConditionalOnProperty(value = "multi-datasource.enabled", havingValue = "JNDI")
public class PrimaryJndiDataSourceConfig extends JndiDataSourceConfig {

  @ConfigurationProperties(prefix = PrimaryDataSource.configurationPropertiesPrefix)
  @Bean(PrimaryDataSource.datasourceProperties)
  public JndiDatasourceProperties datasourceProperties() {
    return new JndiDatasourceProperties();
  }

  @PostConstruct
  public void postConstruct() {
    log.debug("datasourceProperties: {}", datasourceProperties());
  }

  @Bean(name = PrimaryDataSource.dataSource)
  public DataSource dataSource() {
    return super.dataSource(datasourceProperties());
  }


  @Bean(PrimaryDataSource.sessionFactory)
  public SqlSessionFactory sqlSessionFactory() throws Exception {
    return super.sqlSessionFactory(dataSource(), datasourceProperties());
  }

  @Bean(PrimaryDataSource.sessionTemplate)
  public SqlSessionTemplate sqlSessionTemplate() throws Exception {
    return super.sqlSessionTemplate(sqlSessionFactory());
  }


}