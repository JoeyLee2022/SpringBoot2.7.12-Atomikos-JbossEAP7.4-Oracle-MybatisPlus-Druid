package com.joeylee.config.datasource;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.joeylee.domain.properties.CommonDatasourceProperties;
import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * 通用 数据源配置
 *
 * @author JoeyLee
 */
public class BaseDataSourceConfig {

  protected final Logger log = LoggerFactory.getLogger(this.getClass());

  /**
   * 配置 Atomikos 的数据源
   *
   * @param properties 数据源连接信息
   * @return
   */
  public DataSource dataSource(
      CommonDatasourceProperties properties) {

    AtomikosDataSourceBean atomikosDataSourceBean = new AtomikosDataSourceBean();
    atomikosDataSourceBean.setXaDataSource(properties);
    atomikosDataSourceBean.setUniqueResourceName(properties.getUniqueResourceName());
    return atomikosDataSourceBean;
  }

  /**
   * 创建Mybatis的会话连接工厂
   *
   * @param dataSource 数据源
   * @param properties 数据源连接信息
   * @return
   * @throws Exception
   */
  public SqlSessionFactory sqlSessionFactory(
      DataSource dataSource,
      CommonDatasourceProperties properties) throws Exception {

    final MybatisSqlSessionFactoryBean sqlSessionFactoryBean = new MybatisSqlSessionFactoryBean();
    sqlSessionFactoryBean.setDataSource(dataSource);

    sqlSessionFactoryBean.setMapperLocations(
        new PathMatchingResourcePatternResolver().getResources(
            properties.getXmlPath()));
    return sqlSessionFactoryBean.getObject();
  }

  /**
   * SqlSessionTemplate是线程安全的，生命周期由spring管理的，同spring事务一起协作来保证真正执行的SqlSession是在spring的事务中的一个SqlSession的实现类。
   *
   * @param sqlSessionFactory Mybatis的会话连接工厂
   * @return
   */
  public SqlSessionTemplate sqlSessionTemplate(
      SqlSessionFactory sqlSessionFactory) {
    return new SqlSessionTemplate(sqlSessionFactory);
  }


}