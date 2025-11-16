package com.surest.memberapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MemberappApplication {

	public static void main(String[] args) {
		SpringApplication.run(MemberappApplication.class, args);
	}

}
