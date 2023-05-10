package shop.mtcoding.restend.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.*;
@DisplayName("ADMIN API(연차/당직 관련)")
@AutoConfigureRestDocs(uriScheme = "http", uriHost = "localhost", uriPort = 8080)
@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class AdminControllerTest2 {

	@Test
	void annualOrder() {
	}

	@Test
	void dutyOrder() {
	}

	@Test
	void annualRequest() {
	}

	@Test
	void dutyRequest() {
	}

	@Test
	void annualApproval() {
	}

	@Test
	void dutyApproval() {
	}
}