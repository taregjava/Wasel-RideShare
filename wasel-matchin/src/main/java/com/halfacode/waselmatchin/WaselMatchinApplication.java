package com.halfacode.waselmatchin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class WaselMatchinApplication {

	public static void main(String[] args) {
		SpringApplication.run(WaselMatchinApplication.class, args);
	}

}
