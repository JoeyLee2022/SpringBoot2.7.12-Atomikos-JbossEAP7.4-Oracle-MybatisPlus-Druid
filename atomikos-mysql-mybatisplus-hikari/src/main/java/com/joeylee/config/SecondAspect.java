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
 * 次数据源切面
 *
 * @author JoeyLee
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