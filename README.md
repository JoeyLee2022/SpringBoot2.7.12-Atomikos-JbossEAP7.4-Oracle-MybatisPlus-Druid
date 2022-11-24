# SpringBoot2.7.5 + Atomikos + JbossEAP7.4 + Oracle + MybatisPlus + Druid 配置多数据源

基于springboot最新版本2.7.5版本，

oracle数据库需开启XA协议

使用 dba账号 通过以下命令给数据源的用户授予XA权限，开启用户的XA权限

```sql
grant select on sys.dba_pending_transactions to USER_NAME;
grant select on sys.pending_trans$ to USER_NAME;
grant select on sys.dba_2pc_pending to USER_NAME;
grant execute on sys.dbms_system to USER_NAME 
```

USER_NAME为需要配置XA数据源的用户



集成MybatisPlus，

用Druid作为数据库连接池

Atomikos 用来处理分布式事务

## 项目地址

https://github.com/JoeyLee2022/SpringBoot2.5-Atomikos-Oracle-MybatisPlus-Druid-.git
https://gitee.com/lee843416545/SpringBoot2.7.5-Atomikos-JbossEAP7.4-Oracle-MybatisPlus-Druid.git

## 配置数据源

### 通用 数据源配置

DataSource不能直接使用Druid提供的DruidDataSource, 需要使用atomikos来包装一下Druid提供的DruidXADataSource，来支持XA规范

```java
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
 * @author joeylee
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
    //DataSource不能直接使用Druid提供的DruidDataSource, 需要使用atomikos来包装一下Druid提供的DruidXADataSource，来支持XA规范
    atomikosDataSourceBean.setXaDataSource(properties);
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
```

### 主数据源

basePackages用来扫描类该数据源所在的mapper类

```java
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
 * @author joeylee
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

  @Bean(name = PrimaryDataSource.dataSource, initMethod = "init", destroyMethod = "close")
  @DependsOn({XA.PlatformTransactionManager})
  public DataSource dataSource() {
    return super.dataSource(datasourceProperties);
  }

  @Bean(PrimaryDataSource.sessionFactory)
  public SqlSessionFactory sqlSessionFactory() throws Exception {
    return super.sqlSessionFactory(dataSource(), datasourceProperties);
  }

  @Bean(PrimaryDataSource.sessionTemplate)
  public SqlSessionTemplate sqlSessionTemplate() throws Exception {
    return super.sqlSessionTemplate(sqlSessionFactory());
  }

}
```

### 次数据源

```java
package com.joeylee.config.datasource;

import com.joeylee.domain.constant.DataSourceConstant.SecondDataSource;
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
 * 次数据源配置
 *
 * @author joeylee
 */
@Configuration
@MapperScan(basePackages = "${multi-datasource.datasource.druid.basePackages:com.joeylee.mapper.second}", sqlSessionTemplateRef = SecondDataSource.sessionTemplate, sqlSessionFactoryRef = SecondDataSource.sessionFactory)
@ConditionalOnProperty(value = "multi-datasource.enabled", havingValue = "XA")
public class SecondDataSourceConfig extends BaseDataSourceConfig {

  @Autowired
  @Qualifier(SecondDataSource.datasourceProperties)
  private CommonDatasourceProperties datasourceProperties;

  @PostConstruct
  public void postConstruct() {
    log.debug("datasourceProperties: {}", datasourceProperties);
  }

  @Bean(name = SecondDataSource.dataSource, initMethod = "init", destroyMethod = "close")
  @DependsOn({XA.PlatformTransactionManager})
  public DataSource dataSource() {
    return super.dataSource(datasourceProperties);
  }


  @Bean(SecondDataSource.sessionFactory)
  public SqlSessionFactory sqlSessionFactory() throws Exception {
    return super.sqlSessionFactory(dataSource(), datasourceProperties);
  }

  @Bean(SecondDataSource.sessionTemplate)
  public SqlSessionTemplate sqlSessionTemplate() throws Exception {
    return super.sqlSessionTemplate(sqlSessionFactory());
  }


}
```

### 数据源属性配置类

用来配置所有数据源的通用属性，包括mapper扫描路径，xml路径，等

```java
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
 * @author joeylee
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
```

```java
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
```

### XA 分布式事务 配置

关键点

```java
package com.joeylee.config.datasource;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.joeylee.domain.constant.DataSourceConstant.XA;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;

/**
 * XA 分布式事务 配置
 *
 * @author joeylee
 */
@Configuration
@EnableTransactionManagement
@ConditionalOnProperty(value = "multi-datasource.enabled", havingValue = "XA")
public class XATransactionManagerConfig {

  /**
   * 是面向开发人员的接口,能够编程地控制事务处理。UserTransaction.begin方法开启一个全局事务，并且将该事务与调用线程关联起来
   *
   * @return
   * @throws SystemException
   */
  @Bean(XA.userTransaction)
  public UserTransaction userTransaction() throws SystemException {
    UserTransactionImp userTransactionImp = new UserTransactionImp();
    userTransactionImp.setTransactionTimeout(10000);
    return userTransactionImp;
  }

  /**
   * 允许应用程序服务器来控制代表正在管理的应用程序的事务。它本身并不承担实际的事务处理功能，它更多的是充当UserTransaction接口和Transaction接口之间的桥梁。
   *
   * @return
   */
  @Bean(name = XA.transactionManager, initMethod = "init", destroyMethod = "close")
  public TransactionManager atomikosTransactionManager() {
    UserTransactionManager userTransactionManager = new UserTransactionManager();
    userTransactionManager.setForceShutdown(false);
    return userTransactionManager;
  }

  /**
   * spring集成该事务管理器通常适用于处理分布式事务，即跨越多个资源的事务，以及控制应用程序服务器资源（例如JNDI中可用的JDBC数据源）上的事务
   *
   * @return
   * @throws SystemException
   */
  @Bean(XA.PlatformTransactionManager)
  @DependsOn({XA.userTransaction, XA.transactionManager})
  public PlatformTransactionManager transactionManager() throws SystemException {
    return new JtaTransactionManager(userTransaction(), atomikosTransactionManager());
  }
}
```

### 交易历史 数据源切面

可有可无，用来打印切换数据库的日志，可自定义

```Java
package com.joeylee.config;

import com.joeylee.domain.constant.DataSourceConstant.SecondDataSource;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 交易历史 数据源切面
 *
 * @author joeylee
 */
@Aspect
@Component
@ConditionalOnProperty(value = "multi-datasource.enabled")
public class SecondAspect {

  protected final Logger log = LoggerFactory.getLogger(this.getClass());

  @Pointcut("execution(* com.joeylee.mapper.second..*.*(..))")
  public void pointcut() {
  }

  @Before("pointcut()")
  public void doBefore(JoinPoint joinPoint) {
    log.info("use DataSource {}", SecondDataSource.dataSource);
  }
}
```

## JNDI方式

### JNDI 数据源配置

```java
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
```

```java
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
```

### 主数据源配置

```java
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
@MapperScan(basePackages = "${spring.datasource.druid.basePackages:com.fubon.mbk.call.test.mapper}", sqlSessionTemplateRef = PrimaryDataSource.sessionTemplate, sqlSessionFactoryRef = PrimaryDataSource.sessionFactory)
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
```

### 次数据源配置

```java
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
@MapperScan(basePackages = "${multi-datasource.datasource.druid.basePackages:com.fubon.mbk.common.mapper}", sqlSessionTemplateRef = SecondDataSource.sessionTemplate, sqlSessionFactoryRef = SecondDataSource.sessionFactory)
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
```

### pom文件配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns="http://maven.apache.org/POM/4.0.0"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <artifactId>boot-jboss</artifactId>

  <build>
    <finalName>${project.artifactId}</finalName>
  </build>

  <dependencies>

    <dependency>
      <artifactId>slf4j-api</artifactId>
      <groupId>org.slf4j</groupId>
    </dependency>

    <!--首先，我们设置“ war ”为包装单元
    然后，在spring-boot-starter-web中，我们排除了 logback-classic日志记录和tomcat依赖项。这是为了避免与 jboss 现有的 Web 服务器和日志记录模块发生冲突
    最后，我们需要Servlet依赖项才能编译SpringBootServletInitializer-->
    <dependency>
      <artifactId>spring-boot-starter-web</artifactId>
      <exclusions>
        <exclusion>
          <artifactId>logback-classic</artifactId>
          <groupId>ch.qos.logback</groupId>
        </exclusion>
        <exclusion>
          <artifactId>spring-boot-starter-tomcat</artifactId>
          <groupId>org.springframework.boot</groupId>
        </exclusion>
      </exclusions>
      <groupId>org.springframework.boot</groupId>
    </dependency>

    <dependency>
      <artifactId>javax.servlet-api</artifactId>
      <groupId>javax.servlet</groupId>
      <scope>provided</scope>
    </dependency>
    <dependency>
      <artifactId>spring-boot-starter-test</artifactId>
      <exclusions>
        <exclusion>
          <artifactId>junit-vintage-engine</artifactId>
          <groupId>org.junit.vintage</groupId>
        </exclusion>
      </exclusions>
      <groupId>org.springframework.boot</groupId>
      <scope>test</scope>
    </dependency>

    <dependency>
      <artifactId>boot-atomikos</artifactId>
      <exclusions>
        <exclusion>
          <artifactId>HikariCP</artifactId>
          <groupId>com.zaxxer</groupId>
        </exclusion>
      </exclusions>
      <groupId>com.joeylee</groupId>
      <version>${project.version}</version>
    </dependency>
  </dependencies>

  <modelVersion>4.0.0</modelVersion>
  <packaging>war</packaging>
  <parent>
    <artifactId>base</artifactId>
    <groupId>com.joeylee</groupId>
    <relativePath/>
    <version>1.0</version>
  </parent>


</project>
```

### 启动类

```java
package com.joeylee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class JbossApp extends SpringBootServletInitializer {

  public static void main(String[] args) {
    SpringApplication.run(JbossApp.class, args);
  }

  /**
   * war config
   *
   * @param builder
   * @return
   */
  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
    return super.configure(builder);
  }

}
```

## 测试分布式事务

### 服务类

```java
package com.joeylee.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.joeylee.domain.entity.UsrActivityDtl;
import com.joeylee.mapper.primary.PrimaryUsrActivityDtlMapper;
import com.joeylee.mapper.second.SecondUsrActivityDtlMapper;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 多数据源 服务类
 *
 * @author joeylee
 */
@Service
public class MultiDataSourceService {

  @Autowired(required = false)
  private PrimaryUsrActivityDtlMapper db1;

  @Autowired(required = false)
  private SecondUsrActivityDtlMapper db2;

  /**
   * atomikos效果：分布式事物。两个数据库都插入值
   *
   * @return
   */
  @Transactional
  public String insert(UsrActivityDtl dtl) {
    // 注意id自增问题，此处仅作为演示
    db1.insert(dtl);
    dtl.setId(null);
    db2.insert(dtl);
    return "OK";
  }

  /**
   * atomikos效果：分布式事物。 演示发生异常分布式事物回滚 这里无论error 1、2、3，任何一处发生异常，分布式事物都会回滚
   *
   * @return
   */
  @Transactional
  public String insertWithException(UsrActivityDtl dtl) {
    insert(dtl);
    int i = 1 / 0;
    return "OK";
  }

  /**
   * 多数据源 查询
   *
   * @return
   */
  public Map<String, Long> query() {
    Map<String, Long> map = new HashMap<>();
    Long selectCount = db1.selectCount(new QueryWrapper<>());
    map.put("db1", selectCount);
    selectCount = db2.selectCount(new QueryWrapper<>());
    map.put("db2", selectCount);
    return map;
  }


}
```

### 控制层

```java
package com.joeylee.controller;

import com.joeylee.domain.entity.UsrActivityDtl;
import com.joeylee.service.MultiDataSourceService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 多数据 分布式事务 测试接口
 *
 * @author joeylee
 */
@RestController
@RequestMapping("/multiDataSource")
public class MultiDataSourceController {

  @Autowired
  private MultiDataSourceService multiDataSourceService;


  @RequestMapping("/insert")
  public String insert(UsrActivityDtl dtl) {
    return multiDataSourceService.insert(dtl);
  }

  @RequestMapping("/insertWithException")
  public String insertWithException(UsrActivityDtl dtl) {
    return multiDataSourceService.insertWithException(dtl);
  }

  @RequestMapping("/query")
  public Map<String, Long> query() {
    return multiDataSourceService.query();
  }

}
```

调用接口http://localhost:8080/multiDataSource/insertWithException?name=12312

可看到分布式事务发生异常回滚的效果

## 配置文件

```yaml
spring:
  application:
    name: boot-atomikos
  datasource:
    druid:
      # 如果是 JNDI 模式，需要配置改项
      jndiDataSourceName: java:jboss/primary_OracleXADS

      # 如果是 XA 模式，根据实际情况，需配置如下
      basePackages: com.joeylee.mapper.primary
      xmlPath: classpath*:mapper/primary/*.xml
      uniqueResourceName: oracle 1

      url: jdbc:oracle:thin:@127.0.0.1:1521/ORACLE
      username: lee_account_dev
      password: lee123456
      #      driver: oracle.jdbc.driver.OracleDriver
      initial-size: 5
      max-active: 20
      min-idle: 10
      max-wait: 10


# 多数据源配置
multi-datasource:
  enabled: XA
  datasource:
    druid:
      # 如果是 JNDI 模式，需要配置改项
      jndiDataSourceName: java:jboss/second_OracleXADS

      # 如果是 XA 模式，根据实际情况，需配置如下
      basePackages: com.joeylee.mapper.second
      xmlPath: classpath*:mapper/second/*.xml
      uniqueResourceName: oracle 2

      url: jdbc:oracle:thin:@127.0.0.1:1521/ORACLE
      username: lee
      password: lee123456
      initialSize: 5
      maxActive: 20
      minIdle: 10
      maxWait: 10

#日志打印
logging:
  # 配置级别
  level:
    #分包配置级别，即不同的目录下可以使用不同的级别
    com: debug

joeylee:
  # 全局响应结果处理
  response-handler:
    enable: true
  # 全局异常处理
  exception-handler:
    enable: true
```
