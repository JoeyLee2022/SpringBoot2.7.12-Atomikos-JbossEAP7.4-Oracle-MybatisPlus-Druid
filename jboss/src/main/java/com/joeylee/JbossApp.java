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
