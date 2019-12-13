package com.chain;

import com.chain.client.ChainClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class ChainApplication {

	public static ChainClient asset;

	@ResponseBody
	@RequestMapping("/hello")
	public String hello(){
		return "Hello World";
	}

	public static void main(String[] args) throws Exception{
		asset = new ChainClient();
		asset.initialize();

		SpringApplication.run(ChainApplication.class, args);
	}

}
