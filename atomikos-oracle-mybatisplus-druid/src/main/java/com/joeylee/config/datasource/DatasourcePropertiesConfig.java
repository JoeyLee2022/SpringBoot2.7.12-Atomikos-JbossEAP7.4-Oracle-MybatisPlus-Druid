package com.joeylee.config.datasource;

import com.joeylee.domain.constant.DataSourceConstant.PrimaryDataSource;
import com.joeylee.domain.constant.DataSourceConstant.SecondDataSource;
import com.joeylee.domain.properties.CommonDatasourceProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 多数据源属性 配置
 *
 * @author JoeyLee
 */
@Configuration
@ConditionalOnProperty(value = "multi-datasource.enabled", havingValue = "XA")
public class DatasourcePropertiesConfig {

  @ConfigurationProperties(prefix = PrimaryDataSource.configurationPropertiesPrefix)
  @Bean(PrimaryDataSource.datasourceProperties)
  public CommonDatasourceProperties primaryDatasourceProperties() {
    return new CommonDatasourceProperties();
  }

  @ConfigurationProperties(prefix = SecondDataSource.configurationPropertiesPrefix)
  @Bean(SecondDataSource.datasourceProperties)
  public CommonDatasourceProperties secondDatasourceProperties() {
    return new CommonDatasourceProperties();
  }

}
