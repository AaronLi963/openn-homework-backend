package com.example.demo;

import com.example.demo.config.TestRedisConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestRedisConfig.class)
class DemoApplicationTests {

	@Test
	void contextLoads() {
	}

}
