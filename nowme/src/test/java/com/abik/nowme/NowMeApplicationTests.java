package com.abik.nowme;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "app.seed.enabled=false",
        "security.jwt.secret=test-secret-for-context-loads-0123456789"
})
class NowMeApplicationTests {

	@Test
	void contextLoads() {
	}

}
