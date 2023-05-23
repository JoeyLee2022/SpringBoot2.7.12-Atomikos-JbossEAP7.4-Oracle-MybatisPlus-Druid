package com.joeylee.domain.constant;

/**
 * 数据源配置 常量
 *
 * @author JoeyLee
 */
public interface DataSourceConstant {

  /**
   * 主数据源
   */
  interface PrimaryDataSource {

    String dataSource = "PrimaryDataSource";
    String datasourceProperties = "PrimaryDatasourceProperties";
    String configurationPropertiesPrefix = "spring.datasource";
    String sessionTemplate = "PrimarySessionTemplate";
    String transactionManager = "PrimaryTransactionManager";
    String sessionFactory = "PrimarySessionFactory";

    String atomikosDataSourceBean = "PrimaryAtomikosDataSourceBean";
  }

  /**
   * 次数据源
   */
  interface SecondDataSource {

    String dataSource = "SecondDataSource";
    String datasourceProperties = "SecondDatasourceProperties";
    String configurationPropertiesPrefix = "multi-datasource.datasource";
    String sessionTemplate = "SecondSessionTemplate";
    String transactionManager = "SecondTransactionManager";
    String sessionFactory = "SecondSessionFactory";

    String atomikosDataSourceBean = "SecondAtomikosDataSourceBean";
  }

  /**
   * XA相关配置
   */
  interface XA {

    String userTransaction = "userTransaction";
    String transactionManager = "atomikosTransactionManager";
    String PlatformTransactionManager = "txManager";
  }


}
