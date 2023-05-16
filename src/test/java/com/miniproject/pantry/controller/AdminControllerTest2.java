package com.miniproject.pantry.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.miniproject.pantry.core.dummy.DummyEntity;
import com.miniproject.pantry.dto.order.OrderRequest;
import com.miniproject.pantry.model.annual.Annual;
import com.miniproject.pantry.model.annual.AnnualRepository;
import com.miniproject.pantry.model.duty.Duty;
import com.miniproject.pantry.model.duty.DutyRepository;
import com.miniproject.pantry.model.event.Event;
import com.miniproject.pantry.model.event.EventRepository;
import com.miniproject.pantry.model.order.OrderRepository;
import com.miniproject.pantry.model.order.OrderState;
import com.miniproject.pantry.model.user.User;
import com.miniproject.pantry.model.user.UserRepository;

import javax.persistence.EntityManager;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@DisplayName("ADMIN API(연차/당직 관련)")
@AutoConfigureRestDocs(uriScheme = "http", uriHost = "localhost", uriPort = 8080)
@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class AdminControllerTest2 {

	private DummyEntity dummy = new DummyEntity();

	@Autowired
	private MockMvc mvc;
	@Autowired
	private ObjectMapper om;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private AnnualRepository annualRepository;

	@Autowired
	private EventRepository eventRepository;
	@Autowired
	private DutyRepository dutyRepository;
	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private EntityManager em;

	@BeforeEach
	public void setUp() {
		User ssar = userRepository.save(dummy.newUser("사르", "ADMIN", true));
		User cos = userRepository.save(dummy.newUser("코스", "USER", true));
		User love = userRepository.save(dummy.newUser("러브", "USER", false));
		User coco = userRepository.save(dummy.newUser("코코", "ADMIN", true));
		// userRepository.save(newMockUser(2L,"코스", "USER"));
		Annual annual1 = annualRepository.save(dummy.newAnnual(LocalDate.of(2023, 06, 1), LocalDate.of(2023, 06, 5), 5L));
		Duty duty1 = dutyRepository.save(dummy.newDuty(LocalDate.of(2023, 06, 1)));
		Annual annual2 = annualRepository.save(dummy.newAnnual(LocalDate.of(2023, 06, 7), LocalDate.of(2023, 06, 9), 3L));
		Duty duty2 = dutyRepository.save(dummy.newDuty(LocalDate.of(2023, 06, 5)));
		Event event1 = eventRepository.save(dummy.newEvent(cos, "ANNUAL", annual1, null));
		Event event2 = eventRepository.save(dummy.newEvent(cos, "DUTY", null, duty1));
		Event event3 = eventRepository.save(dummy.newEvent(cos, "ANNUAL", annual2, null));
		Event event4 = eventRepository.save(dummy.newEvent(cos, "DUTY", null, duty2));
		orderRepository.save(dummy.newOrder(event1, OrderState.WAITING, null));
		orderRepository.save(dummy.newOrder(event2, OrderState.WAITING, null));
		orderRepository.save(dummy.newOrder(event3, OrderState.APPROVED, ssar));
		orderRepository.save(dummy.newOrder(event4, OrderState.APPROVED, ssar));
		em.clear();
	}

	@DisplayName("연차결재(승인/반려)처리")
	@Test
	@WithUserDetails(value = "ssar@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	void annualOrder() throws Exception{
		OrderRequest.ApprovalInDTO approvalInDTO=new OrderRequest.ApprovalInDTO();
		approvalInDTO.setEventId(1L);
		approvalInDTO.setOrderState("APPROVED");

		String requestBody= om.writeValueAsString(approvalInDTO);
		System.out.println("테스트:" +requestBody);

		ResultActions resultActions = mvc
				.perform(post("/api/admin/annual/order").content(requestBody).contentType(MediaType.APPLICATION_JSON));
		String responseBody = resultActions.andReturn().getResponse().getContentAsString();
		System.out.println("테스트 : " + responseBody);

		resultActions.andExpect(jsonPath("$.status").value(200));
		resultActions.andExpect(jsonPath("$.msg").value("성공"));
		resultActions.andExpect(jsonPath("$.data.userName").value("코스"));
		resultActions.andExpect(jsonPath("$.data.userEmail").value("cos@nate.com"));
		resultActions.andExpect(jsonPath("$.data.userRole").value("USER"));
		resultActions.andExpect(jsonPath("$.data.orderId").value(1L));
		resultActions.andExpect(jsonPath("$.data.orderState").value("APPROVED"));
		resultActions.andExpect(jsonPath("$.data.startDate").value("2023-06-01"));
		resultActions.andExpect(jsonPath("$.data.endDate").value("2023-06-05"));
		resultActions.andExpect(jsonPath("$.data.approvalUser").value("사르"));
	}

	@DisplayName("당직 결재(승인/반려)처리")
	@Test
	@WithUserDetails(value = "ssar@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	void dutyOrder() throws Exception{
		OrderRequest.ApprovalInDTO approvalInDTO=new OrderRequest.ApprovalInDTO();
		approvalInDTO.setEventId(2L);
		approvalInDTO.setOrderState("APPROVED");

		String requestBody= om.writeValueAsString(approvalInDTO);
		System.out.println("테스트:" +requestBody);

		ResultActions resultActions = mvc
				.perform(post("/api/admin/duty/order").content(requestBody).contentType(MediaType.APPLICATION_JSON));
		String responseBody = resultActions.andReturn().getResponse().getContentAsString();
		System.out.println("테스트 : " + responseBody);

		resultActions.andExpect(jsonPath("$.status").value(200));
		resultActions.andExpect(jsonPath("$.msg").value("성공"));
		resultActions.andExpect(jsonPath("$.data.userName").value("코스"));
		resultActions.andExpect(jsonPath("$.data.userEmail").value("cos@nate.com"));
		resultActions.andExpect(jsonPath("$.data.userRole").value("USER"));
		resultActions.andExpect(jsonPath("$.data.orderId").value(2L));
		resultActions.andExpect(jsonPath("$.data.orderState").value("APPROVED"));
		resultActions.andExpect(jsonPath("$.data.date").value("2023-06-01"));
		resultActions.andExpect(jsonPath("$.data.approvalUser").value("사르"));
	}

	@Test
	@WithUserDetails(value = "ssar@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	@DisplayName("연차신청 리스트")
	void annualRequestList() throws Exception{
		ResultActions resultActions = mvc
				.perform(get("/api/admin/annual/request"));
		String responseBody = resultActions.andReturn().getResponse().getContentAsString();
		System.out.println("테스트 : " + responseBody);


		resultActions.andExpect(jsonPath("$.status").value(200));
		resultActions.andExpect(jsonPath("$.msg").value("성공"));
		resultActions.andExpect(jsonPath("$.data.content[0].eventId").value(1L));
		resultActions.andExpect(jsonPath("$.data.content[0].userName").value("코스"));
		resultActions.andExpect(jsonPath("$.data.content[0].userEmail").value("cos@nate.com"));
		resultActions.andExpect(jsonPath("$.data.content[0].userRole").value("USER"));
		resultActions.andExpect(jsonPath("$.data.content[0].eventType").value("ANNUAL"));
		resultActions.andExpect(jsonPath("$.data.content[0].orderId").value(1L));
		resultActions.andExpect(jsonPath("$.data.content[0].startDate").value("2023-06-01"));
		resultActions.andExpect(jsonPath("$.data.content[0].endDate").value("2023-06-05"));
		resultActions.andExpect(jsonPath("$.data.content[0].orderState").value("WAITING"));


	}

	@Test
	@WithUserDetails(value = "ssar@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	@DisplayName("당직신청 리스트")
	void dutyRequest() throws Exception{
		ResultActions resultActions = mvc
				.perform(get("/api/admin/duty/request"));
		String responseBody = resultActions.andReturn().getResponse().getContentAsString();
		System.out.println("테스트 : " + responseBody);


		resultActions.andExpect(jsonPath("$.status").value(200));
		resultActions.andExpect(jsonPath("$.msg").value("성공"));
		resultActions.andExpect(jsonPath("$.data.content[0].eventId").value(2L));
		resultActions.andExpect(jsonPath("$.data.content[0].userName").value("코스"));
		resultActions.andExpect(jsonPath("$.data.content[0].userRole").value("USER"));
		resultActions.andExpect(jsonPath("$.data.content[0].eventType").value("DUTY"));
		resultActions.andExpect(jsonPath("$.data.content[0].orderId").value(2L));
		resultActions.andExpect(jsonPath("$.data.content[0].date").value("2023-06-01"));
		resultActions.andExpect(jsonPath("$.data.content[0].orderState").value("WAITING"));

	}

	@Test
	@WithUserDetails(value = "ssar@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	@DisplayName("연차승인 리스트")
	void annualApprovalList() throws Exception{

		ResultActions resultActions = mvc.perform(get("/api/admin/annual/approval"));
		String responseBody = resultActions.andReturn().getResponse().getContentAsString();
		System.out.println("테스트 : " + responseBody);

		resultActions.andExpect(jsonPath("$.status").value(200));
		resultActions.andExpect(jsonPath("$.msg").value("성공"));
		resultActions.andExpect(jsonPath("$.data.content[0].eventId").value(3L));
		resultActions.andExpect(jsonPath("$.data.content[0].userName").value("코스"));
		resultActions.andExpect(jsonPath("$.data.content[0].userEmail").value("cos@nate.com"));
		resultActions.andExpect(jsonPath("$.data.content[0].userRole").value("USER"));
		resultActions.andExpect(jsonPath("$.data.content[0].eventType").value("ANNUAL"));
		resultActions.andExpect(jsonPath("$.data.content[0].orderId").value(3L));
		resultActions.andExpect(jsonPath("$.data.content[0].startDate").value("2023-06-07"));
		resultActions.andExpect(jsonPath("$.data.content[0].endDate").value("2023-06-09"));
		resultActions.andExpect(jsonPath("$.data.content[0].orderState").value("APPROVED"));
		resultActions.andExpect(jsonPath("$.data.content[0].approvalUser").value("사르"));
	}

	@Test
	@WithUserDetails(value = "ssar@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	@DisplayName("당직승인 리스트")
	void dutyApprovalList() throws Exception{

		ResultActions resultActions = mvc
				.perform(get("/api/admin/duty/approval"));
		String responseBody = resultActions.andReturn().getResponse().getContentAsString();
		System.out.println("테스트 : " + responseBody);

		resultActions.andExpect(jsonPath("$.status").value(200));
		resultActions.andExpect(jsonPath("$.msg").value("성공"));
		resultActions.andExpect(jsonPath("$.data.content[0].eventId").value(4L));
		resultActions.andExpect(jsonPath("$.data.content[0].userName").value("코스"));
		resultActions.andExpect(jsonPath("$.data.content[0].userEmail").value("cos@nate.com"));
		resultActions.andExpect(jsonPath("$.data.content[0].userRole").value("USER"));
		resultActions.andExpect(jsonPath("$.data.content[0].eventType").value("DUTY"));
		resultActions.andExpect(jsonPath("$.data.content[0].orderId").value(4L));
		resultActions.andExpect(jsonPath("$.data.content[0].date").value("2023-06-05"));
		resultActions.andExpect(jsonPath("$.data.content[0].orderState").value("APPROVED"));
		resultActions.andExpect(jsonPath("$.data.content[0].approvalUser").value("사르"));

	}
}