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
 * @author JoeyLee
 */
@Configuration
@EnableTransactionManagement
@ConditionalOnProperty(value = "multi-datasource.enabled", havingValue = "XA")
public class XATransactionManagerConfig {

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
}