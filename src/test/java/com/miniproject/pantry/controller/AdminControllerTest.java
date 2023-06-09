package com.miniproject.pantry.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miniproject.pantry.core.dummy.DummyEntity;
import com.miniproject.pantry.dto.user.UserRequest.RoleUpdateInDTO;
import com.miniproject.pantry.model.user.User;
import com.miniproject.pantry.model.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.persistence.EntityManager;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@DisplayName("ADMIN API(User관련)")
@AutoConfigureRestDocs(uriScheme = "http", uriHost = "localhost", uriPort = 8080)
@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class AdminControllerTest {
	private DummyEntity dummy = new DummyEntity();

	@Autowired
	private MockMvc mvc;
	@Autowired
	private ObjectMapper om;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private EntityManager em;

	@BeforeEach
	public void setUp() {
		User ssar = userRepository.save(dummy.newUser("사르", "ADMIN", true));
		User cos = userRepository.save(dummy.newUser("코스", "USER", true));
		User love = userRepository.save(dummy.newUser("러브", "USER", false));
		em.clear();
	}
	@Test
	@DisplayName("회원권한 업데이트")
	@WithUserDetails(value = "ssar@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void role_update_test()throws Exception{
		RoleUpdateInDTO roleUpdateInDTO=new RoleUpdateInDTO();
		roleUpdateInDTO.setEmail("cos@nate.com");
		roleUpdateInDTO.setRole("ADMIN");

		String requestBody= om.writeValueAsString(roleUpdateInDTO);
		System.out.println("테스트:" +requestBody);

		ResultActions resultActions = mvc
				.perform(post("/api/admin/role/update").content(requestBody).contentType(MediaType.APPLICATION_JSON));
		String responseBody = resultActions.andReturn().getResponse().getContentAsString();
		System.out.println("테스트 : " + responseBody);

		resultActions.andExpect(jsonPath("$.status").value(200));
		resultActions.andExpect(jsonPath("$.msg").value("성공"));
		resultActions.andExpect(jsonPath("$.errCode").value("0"));
		resultActions.andExpect(jsonPath("$.data.username").value("코스"));
		resultActions.andExpect(jsonPath("$.data.email").value("cos@nate.com"));
		resultActions.andExpect(jsonPath("$.data.role").value("ADMIN"));

	}
	@Test
	@DisplayName("회원 리스트(status=true User)")
	@WithUserDetails(value = "ssar@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void user_list_test() throws Exception {
		ResultActions resultActions = mvc
				.perform(get("/api/admin/role/list"));
		String responseBody = resultActions.andReturn().getResponse().getContentAsString();
		System.out.println("테스트 : " + responseBody);

		resultActions.andExpect(jsonPath("$.status").value(200));
		resultActions.andExpect(jsonPath("$.msg").value("성공"));
		resultActions.andExpect(jsonPath("$.errCode").value("0"));
		resultActions.andExpect(jsonPath("$.data.content[0].id").value(1L));
		resultActions.andExpect(jsonPath("$.data.content[0].username").value("사르"));
		//resultActions.andExpect(jsonPath("$.data.content[0].createAt").value(LocalDateTime.now()));
		resultActions.andExpect(jsonPath("$.data.content[0].imageUri").value("https://test"));
		resultActions.andExpect(jsonPath("$.data.content[0].email").value("ssar@nate.com"));
		resultActions.andExpect(jsonPath("$.data.content[0].role").value("ADMIN"));

		resultActions.andExpect(jsonPath("$.data.content[1].id").value(2L));
		resultActions.andExpect(jsonPath("$.data.content[1].username").value("코스"));
		//resultActions.andExpect(jsonPath("$.data.content[0].createAt").value(LocalDateTime.now()));
		resultActions.andExpect(jsonPath("$.data.content[1].imageUri").value("https://test"));
		resultActions.andExpect(jsonPath("$.data.content[1].email").value("cos@nate.com"));
		resultActions.andExpect(jsonPath("$.data.content[1].role").value("USER"));

	}


	@Test
	@DisplayName("회원가입 요청리스트(status=false User)")
	@WithUserDetails(value = "ssar@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void user_request_list_test() throws Exception {
		ResultActions resultActions = mvc
				.perform(get("/api/admin/signup/list"));
		String responseBody = resultActions.andReturn().getResponse().getContentAsString();
		System.out.println("테스트 : " + responseBody);

		resultActions.andExpect(jsonPath("$.status").value(200));
		resultActions.andExpect(jsonPath("$.msg").value("성공"));
		resultActions.andExpect(jsonPath("$.errCode").value("0"));
		resultActions.andExpect(jsonPath("$.data.content[0].id").value(3L));
		resultActions.andExpect(jsonPath("$.data.content[0].username").value("러브"));
		//resultActions.andExpect(jsonPath("$.data.content[0].createAt").value(LocalDateTime.now()));
		resultActions.andExpect(jsonPath("$.data.content[0].imageUri").value("https://test"));
		resultActions.andExpect(jsonPath("$.data.content[0].email").value("love@nate.com"));
		resultActions.andExpect(jsonPath("$.data.content[0].role").value("USER"));

	}

	@Test
	@DisplayName("회원 이메일로 검색")
	@WithUserDetails(value = "ssar@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void email_search_test() throws Exception {
		ResultActions resultActions = mvc
				.perform(get("/api/admin/search/?type=email&keyword=ssar"));
		String responseBody = resultActions.andReturn().getResponse().getContentAsString();
		System.out.println("테스트 : " + responseBody);

		resultActions.andExpect(jsonPath("$.status").value(200));
		resultActions.andExpect(jsonPath("$.msg").value("성공"));
		resultActions.andExpect(jsonPath("$.errCode").value("0"));
		resultActions.andExpect(jsonPath("$.data.content[0].id").value(1L));
		resultActions.andExpect(jsonPath("$.data.content[0].username").value("사르"));
		//resultActions.andExpect(jsonPath("$.data.content[0].createAt").value(LocalDateTime.now()));
		resultActions.andExpect(jsonPath("$.data.content[0].imageUri").value("https://test"));
		resultActions.andExpect(jsonPath("$.data.content[0].email").value("ssar@nate.com"));
		resultActions.andExpect(jsonPath("$.data.content[0].role").value("ADMIN"));

	}
	@Test
	@DisplayName("회원 이름으로 검색")
	@WithUserDetails(value = "ssar@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void username_search_test() throws Exception {
		ResultActions resultActions = mvc
				.perform(get("/api/admin/search/?type=username&keyword=사르"));
		String responseBody = resultActions.andReturn().getResponse().getContentAsString();
		System.out.println("테스트 : " + responseBody);

		resultActions.andExpect(jsonPath("$.status").value(200));
		resultActions.andExpect(jsonPath("$.msg").value("성공"));
		resultActions.andExpect(jsonPath("$.errCode").value("0"));
		resultActions.andExpect(jsonPath("$.data.content[0].id").value(1L));
		resultActions.andExpect(jsonPath("$.data.content[0].username").value("사르"));
		//resultActions.andExpect(jsonPath("$.data.content[0].createAt").value(LocalDateTime.now()));
		resultActions.andExpect(jsonPath("$.data.content[0].imageUri").value("https://test"));
		resultActions.andExpect(jsonPath("$.data.content[0].email").value("ssar@nate.com"));
		resultActions.andExpect(jsonPath("$.data.content[0].role").value("ADMIN"));

	}






}