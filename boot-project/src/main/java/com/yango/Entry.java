package com.yango;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileInputStream;

@SpringBootApplication
@MapperScan(basePackages = "com.yango.dao")
//@ComponentScan({"com.yango"})
public class Entry {
    public static void main(String[] args) {
        SpringApplication.run(Entry.class, args);
    }
}
