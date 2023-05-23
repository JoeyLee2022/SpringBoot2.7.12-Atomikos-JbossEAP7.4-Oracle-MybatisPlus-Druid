package com.joeylee.config.datasource;

import com.joeylee.domain.constant.DataSourceConstant.PrimaryDataSource;
import com.joeylee.domain.constant.DataSourceConstant.XA;
import com.joeylee.domain.properties.CommonDatasourceProperties;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * 主数据源配置
 *
 * @author JoeyLee
 */
@Configuration
@MapperScan(basePackages = "${spring.datasource.druid.basePackages:com.joeylee.mapper.primary}", sqlSessionTemplateRef = PrimaryDataSource.sessionTemplate, sqlSessionFactoryRef = PrimaryDataSource.sessionFactory)
@ConditionalOnProperty(value = "multi-datasource.enabled", havingValue = "XA")
public class PrimaryDataSourceConfig extends BaseDataSourceConfig {

  @Autowired
  @Qualifier(PrimaryDataSource.datasourceProperties)
  private CommonDatasourceProperties datasourceProperties;

  @PostConstruct
  public void postConstruct() {
    log.debug("datasourceProperties: {}", datasourceProperties);
  }

  @Bean(PrimaryDataSource.sessionTemplate)
  public SqlSessionTemplate sqlSessionTemplate() throws Exception {
    return super.sqlSessionTemplate(sqlSessionFactory());
  }

  @Bean(PrimaryDataSource.sessionFactory)
  public SqlSessionFactory sqlSessionFactory() throws Exception {
    return super.sqlSessionFactory(dataSource(), datasourceProperties);
  }

  @Bean(name = PrimaryDataSource.dataSource, initMethod = "init", destroyMethod = "close")
  @DependsOn({XA.PlatformTransactionManager})
  public DataSource dataSource() {
    return super.dataSource(datasourceProperties);
  }

}