package com.iam.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class IamServerApplication {
  public static void main(final String[] args) {
    SpringApplication.run(IamServerApplication.class, args);
  }
}

