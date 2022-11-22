package com.joeylee;

import com.joeylee.common.annotation.EnableJoeyLeeConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableJoeyLeeConfiguration
public class AtomikosApp {


  public static void main(String[] args) {

    SpringApplication.run(AtomikosApp.class, args);
  }


}