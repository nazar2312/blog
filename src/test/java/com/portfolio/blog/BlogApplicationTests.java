package com.portfolio.blog;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class BlogApplicationTests {

	@MockitoBean
	private UserDetailsService userDetailsService;

	@Test
	void contextLoads() {
	}

}
