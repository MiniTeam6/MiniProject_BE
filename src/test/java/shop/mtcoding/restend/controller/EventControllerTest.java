
package shop.mtcoding.restend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import shop.mtcoding.restend.core.MyRestDoc;
import shop.mtcoding.restend.core.auth.jwt.MyJwtProvider;
import shop.mtcoding.restend.core.auth.session.MyUserDetails;
import shop.mtcoding.restend.core.dummy.DummyEntity;
import shop.mtcoding.restend.dto.ResponseDTO;
import shop.mtcoding.restend.dto.event.EventRequest;
import shop.mtcoding.restend.dto.event.EventRequest.EventAddInDto;
import shop.mtcoding.restend.dto.event.EventRequest.EventCancelInDto;
import shop.mtcoding.restend.dto.event.EventRequest.EventModifyInDto;
import shop.mtcoding.restend.dto.event.EventResponse;
import shop.mtcoding.restend.dto.event.EventResponse.MyEventListOutDTO;
import shop.mtcoding.restend.dto.user.UserRequest;
import shop.mtcoding.restend.model.annual.Annual;
import shop.mtcoding.restend.model.annual.AnnualRepository;
import shop.mtcoding.restend.model.duty.Duty;
import shop.mtcoding.restend.model.duty.DutyRepository;
import shop.mtcoding.restend.model.event.Event;
import shop.mtcoding.restend.model.event.EventRepository;
import shop.mtcoding.restend.model.event.EventType;
import shop.mtcoding.restend.model.order.OrderRepository;
import shop.mtcoding.restend.model.order.OrderState;
import shop.mtcoding.restend.model.user.User;
import shop.mtcoding.restend.model.user.UserRepository;

import javax.persistence.EntityManager;
import javax.validation.constraints.Pattern;

import java.time.LocalDate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("연차/당직 API")
@AutoConfigureRestDocs(uriScheme = "http", uriHost = "localhost", uriPort = 8080)
@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class EventControllerTest extends MyRestDoc {
    private String buildMultipartParam(String name, String value) throws JsonProcessingException {
        MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add(name, value);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return om.writeValueAsString(multiValueMap);
    }

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
        // userRepository.save(newMockUser(2L,"코스", "USER"));
        Annual annual1 = annualRepository.save(dummy.newAnnual(LocalDate.of(2023, 06, 1), LocalDate.of(2023, 06, 5), 6L));
        Duty duty1 = dutyRepository.save(dummy.newDuty(LocalDate.of(2023, 06, 1)));
        Annual annual2 = annualRepository.save(dummy.newAnnual(LocalDate.of(2023, 06, 7), LocalDate.of(2023, 06, 9), 3L));
        Duty duty2 = dutyRepository.save(dummy.newDuty(LocalDate.of(2023, 06, 5)));
        Event event1 = eventRepository.save(dummy.newEvent(cos, "ANNUAL", annual1, null));
        Event event2 = eventRepository.save(dummy.newEvent(cos, "DUTY", null, duty1));
        Event event3 = eventRepository.save(dummy.newEvent(cos, "ANNUAL", annual2, null));
        Event event4 = eventRepository.save(dummy.newEvent(cos, "DUTY", null, duty2));
        orderRepository.save(dummy.newOrder(event1, OrderState.WAITING, cos));
        orderRepository.save(dummy.newOrder(event2, OrderState.WAITING, cos));
        orderRepository.save(dummy.newOrder(event3, OrderState.APPROVED, cos));
        orderRepository.save(dummy.newOrder(event4, OrderState.APPROVED, cos));
        em.clear();
    }

    @DisplayName("연차 신청 성공")
    @WithUserDetails(value = "cos@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void event_add_annual_test() throws Exception {
        // given
        EventAddInDto eventAddInDto = new EventAddInDto();
        eventAddInDto.setEventType("ANNUAL");
        eventAddInDto.setStartDate(LocalDate.of(2023, 05, 3));
        eventAddInDto.setEndDate(LocalDate.of(2023, 05, 5));
        eventAddInDto.setCount(2L);

        // when
        String requestBody = om.writeValueAsString(eventAddInDto);
        System.out.println("테스트 : " + requestBody);

        // when
        ResultActions resultActions = mvc
                .perform(post("/api/user/event/add").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("성공"));
        resultActions.andExpect(jsonPath("$.data.eventId").value(5L));
        resultActions.andExpect(jsonPath("$.data.userId").value(2L));
        resultActions.andExpect(jsonPath("$.data.eventType").value("ANNUAL"));
        resultActions.andExpect(jsonPath("$.data.startDate").value("2023-05-03"));
        resultActions.andExpect(jsonPath("$.data.endDate").value("2023-05-05"));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("연차 신청 실패")
    @Test
    public void event_add_annual_fail_test() throws Exception {
        // given
        EventAddInDto eventAddInDto = new EventAddInDto();
        eventAddInDto.setEventType("ANNUAL");
        eventAddInDto.setStartDate(LocalDate.of(2023, 05, 9));
        eventAddInDto.setEndDate(LocalDate.of(2023, 06, 1));

        // when
        String requestBody = om.writeValueAsString(eventAddInDto);
        System.out.println("테스트 : " + requestBody);

        // when
        ResultActions resultActions = mvc
                .perform(post("/api/user/event/add").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(401));
        resultActions.andExpect(jsonPath("$.msg").value("unAuthorized"));
        resultActions.andExpect(jsonPath("$.data").value("인증되지 않았습니다"));
        resultActions.andExpect(status().isUnauthorized());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("당직 신청 성공")
    @WithUserDetails(value = "cos@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void event_add_duty_test() throws Exception {
        // given
        EventAddInDto eventAddInDto = new EventAddInDto();
        eventAddInDto.setEventType("DUTY");
        eventAddInDto.setStartDate(LocalDate.of(2023, 05, 10));

        // when
        String requestBody = om.writeValueAsString(eventAddInDto);
        System.out.println("테스트 : " + requestBody);

        // when
        ResultActions resultActions = mvc
                .perform(post("/api/user/event/add").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("성공"));
        resultActions.andExpect(jsonPath("$.data.eventId").value(5L));
        resultActions.andExpect(jsonPath("$.data.userId").value(2L));
        resultActions.andExpect(jsonPath("$.data.eventType").value("DUTY"));
        resultActions.andExpect(jsonPath("$.data.startDate").value("2023-05-10"));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("당직 신청 실패")
    @Test
    public void event_add_duty_fail_test() throws Exception {
        // given
        EventAddInDto eventAddInDto = new EventAddInDto();
        eventAddInDto.setEventType("당직");
        eventAddInDto.setStartDate(LocalDate.of(2023, 05, 10));

        // when
        String requestBody = om.writeValueAsString(eventAddInDto);
        System.out.println("테스트 : " + requestBody);

        // when
        ResultActions resultActions = mvc
                .perform(post("/api/user/event/add").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(401));
        resultActions.andExpect(jsonPath("$.msg").value("unAuthorized"));
        resultActions.andExpect(jsonPath("$.data").value("인증되지 않았습니다"));
        resultActions.andExpect(status().isUnauthorized());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

//    @DisplayName("모든 이벤트 리스트-연차")
//    @WithUserDetails(value = "cos@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
//    @Test
//    public void all_search_annual_test() throws Exception {
//        //given
////        public ResponseEntity<?> list(@RequestParam @Pattern(regexp = "ANNUAL|DUTY") String eventType,
////                @RequestParam(required = false) @Pattern(regexp = "^\\d{4}-\\d{2}$") String yearMonth,
////                @AuthenticationPrincipal MyUserDetails myUserDetails,
////                @PageableDefault(size = 10) Pageable pageable) {


    @DisplayName("연차 신청 취소")
    @WithUserDetails(value = "cos@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void event_cancel_annual_test() throws Exception {
        // given
        EventCancelInDto eventCancelInDto = new EventCancelInDto();
        eventCancelInDto.setEventType("ANNUAL");
        eventCancelInDto.setEventId(1L);

        // when
        String requestBody = om.writeValueAsString(eventCancelInDto);
        System.out.println("테스트 : " + requestBody);

        // when
        ResultActions resultActions = mvc
                .perform(post("/api/user/event/cancel").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("성공"));
        resultActions.andExpect(jsonPath("$.data").value(true));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("당직 신청 취소")
    @WithUserDetails(value = "cos@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void event_cancel_duty_test() throws Exception {
        // given
        EventCancelInDto eventCancelInDto = new EventCancelInDto();
        eventCancelInDto.setEventType("DUTY");
        eventCancelInDto.setEventId(2L);

        // when
        String requestBody = om.writeValueAsString(eventCancelInDto);
        System.out.println("테스트 : " + requestBody);

        // when
        ResultActions resultActions = mvc
                .perform(post("/api/user/event/cancel").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("성공"));
        resultActions.andExpect(jsonPath("$.data").value(true));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    //트랜잭션의 원칙 위배 -> All or Nothing 안되면 안되고, 되면 모두 되어야하지만
    //현재, 성공이라고 뜨고 실제로는 해당 로직이 원하는 대로 동작하지 않는다. -> 치명적인 오류
    @DisplayName("연차 신청 수정")
    @WithUserDetails(value = "cos@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void event_modify_annual_test() throws Exception {
        // given
        EventRequest.EventModifyInDto eventModifyInDto = new EventModifyInDto();
        eventModifyInDto.setEventId(1L);
        eventModifyInDto.setEventType("ANNUAL");
        eventModifyInDto.setStartDate(LocalDate.of(2023, 07, 3));
        eventModifyInDto.setEndDate(LocalDate.of(2023, 07, 4));
        eventModifyInDto.setCount(2L);

        // when
        String requestBody = om.writeValueAsString(eventModifyInDto);
        System.out.println("테스트 : " + requestBody);

        // when
        ResultActions resultActions = mvc
                .perform(post("/api/user/event/modify").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        //{"status":200,"msg":"성공","data":{"content":[{"eventId":1,"eventType":"ANNUAL","startDate":"2023-06-01","endDate":"2023-06-30","createdAt":"2023-05-10T15:11:28.670271","updatedAt":null,"orderState":"WAITING"},{"eventId":3,"eventType":"ANNUAL","startDate":"2023-06-01","endDate":"2023-06-30","createdAt":"2023-05-10T15:11:28.672319","updatedAt":null,"orderState":"WAITING"}],"pageable":{"sort":{"empty":true,"unsorted":true,"sorted":false},"offset":0,"pageNumber":0,"pageSize":8,"paged":true,"unpaged":false},"first":true,"last":true,"sort":{"empty":true,"unsorted":true,"sorted":false},"number":0,"size":8,"numberOfElements":2,"empty":false}}
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("성공"));
        resultActions.andExpect(jsonPath("$.data.eventId").value(1L));
        resultActions.andExpect(jsonPath("$.data.eventType").value("ANNUAL"));
        resultActions.andExpect(jsonPath("$.data.startDate").value("2023-07-03"));
        resultActions.andExpect(jsonPath("$.data.endDate").value("2023-07-04"));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("당직 신청 수정")
    @WithUserDetails(value = "cos@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void event_modify_duty_test() throws Exception {
        // given
        EventRequest.EventModifyInDto eventModifyInDto = new EventModifyInDto();
        eventModifyInDto.setEventId(2L);
        eventModifyInDto.setEventType("DUTY");
        eventModifyInDto.setStartDate(LocalDate.of(2023, 07, 3));
        eventModifyInDto.setCount(2L);

        // when
        String requestBody = om.writeValueAsString(eventModifyInDto);
        System.out.println("테스트 : " + requestBody);

        // when
        ResultActions resultActions = mvc
                .perform(post("/api/user/event/modify").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        //{"status":200,"msg":"성공","data":{"content":[{"eventId":1,"eventType":"ANNUAL","startDate":"2023-06-01","endDate":"2023-06-30","createdAt":"2023-05-10T15:11:28.670271","updatedAt":null,"orderState":"WAITING"},{"eventId":3,"eventType":"ANNUAL","startDate":"2023-06-01","endDate":"2023-06-30","createdAt":"2023-05-10T15:11:28.672319","updatedAt":null,"orderState":"WAITING"}],"pageable":{"sort":{"empty":true,"unsorted":true,"sorted":false},"offset":0,"pageNumber":0,"pageSize":8,"paged":true,"unpaged":false},"first":true,"last":true,"sort":{"empty":true,"unsorted":true,"sorted":false},"number":0,"size":8,"numberOfElements":2,"empty":false}}
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("성공"));
        resultActions.andExpect(jsonPath("$.data.eventId").value(2L));
        resultActions.andExpect(jsonPath("$.data.eventType").value("DUTY"));
        resultActions.andExpect(jsonPath("$.data.startDate").value("2023-07-03"));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }


//    @Test
//    public void event_add_annual_fail_test() throws Exception {
//        // given
//        EventAddInDto eventAddInDto = new EventAddInDto();
//        eventAddInDto.setEventType("ANNUAL");
//        eventAddInDto.setStartDate(LocalDate.of(2023, 05, 9));
//        eventAddInDto.setEndDate(LocalDate.of(2023, 06, 1));
//
//        // when
//        String requestBody = om.writeValueAsString(eventAddInDto);
//        System.out.println("테스트 : " + requestBody);
//
//        // when
//        ResultActions resultActions = mvc
//                .perform(post("/api/user/event/add").content(requestBody).contentType(MediaType.APPLICATION_JSON));
//        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
//        System.out.println("테스트 : " + responseBody);
//
//        // then
//        resultActions.andExpect(jsonPath("$.status").value(401));
//        resultActions.andExpect(jsonPath("$.msg").value("unAuthorized"));
//        resultActions.andExpect(jsonPath("$.data").value("인증되지 않았습니다"));
//        resultActions.andExpect(status().isUnauthorized());
//        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
//    }
//
//    @DisplayName("당직 신청 성공")
//    @WithUserDetails(value = "cos@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
//    @Test
//    public void event_add_duty_test() throws Exception {
//        // given
//        EventAddInDto eventAddInDto = new EventAddInDto();
//        eventAddInDto.setEventType("DUTY");
//        eventAddInDto.setStartDate(LocalDate.of(2023, 05, 10));
//
//        // when
//        String requestBody = om.writeValueAsString(eventAddInDto);
//        System.out.println("테스트 : " + requestBody);
//
//        // when
//        ResultActions resultActions = mvc
//                .perform(post("/api/user/event/add").content(requestBody).contentType(MediaType.APPLICATION_JSON));
//        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
//        System.out.println("테스트 : " + responseBody);
//
//        // then
//        resultActions.andExpect(jsonPath("$.status").value(200));
//        resultActions.andExpect(jsonPath("$.msg").value("성공"));
//        resultActions.andExpect(jsonPath("$.data.eventId").value(5L));
//        resultActions.andExpect(jsonPath("$.data.userId").value(2L));
//        resultActions.andExpect(jsonPath("$.data.eventType").value("DUTY"));
//        resultActions.andExpect(jsonPath("$.data.startDate").value("2023-05-10"));
//        resultActions.andExpect(status().isOk());
//        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
//    }
//
//    @DisplayName("당직 신청 실패")
//    @Test
//    public void event_add_duty_fail_test() throws Exception {
//        // given
//        EventAddInDto eventAddInDto = new EventAddInDto();
//        eventAddInDto.setEventType("당직");
//        eventAddInDto.setStartDate(LocalDate.of(2023, 05, 10));
//
//        // when
//        String requestBody = om.writeValueAsString(eventAddInDto);
//        System.out.println("테스트 : " + requestBody);
//
//        // when
//        ResultActions resultActions = mvc
//                .perform(post("/api/user/event/add").content(requestBody).contentType(MediaType.APPLICATION_JSON));
//        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
//        System.out.println("테스트 : " + responseBody);
//
//        // then
//        resultActions.andExpect(jsonPath("$.status").value(401));
//        resultActions.andExpect(jsonPath("$.msg").value("unAuthorized"));
//        resultActions.andExpect(jsonPath("$.data").value("인증되지 않았습니다"));
//        resultActions.andExpect(status().isUnauthorized());
//        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
//    }
//
////    @DisplayName("모든 이벤트 리스트-연차")
////    @WithUserDetails(value = "cos@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
////    @Test
////    public void all_search_annual_test() throws Exception {
////        //given
//////        public ResponseEntity<?> list(@RequestParam @Pattern(regexp = "ANNUAL|DUTY") String eventType,
//////                @RequestParam(required = false) @Pattern(regexp = "^\\d{4}-\\d{2}$") String yearMonth,
//////                @AuthenticationPrincipal MyUserDetails myUserDetails,
//////                @PageableDefault(size = 10) Pageable pageable) {
////
////        // when
////        ResultActions resultActions = mvc
////                .perform(get("/api/user/event/list"));
////        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
////        System.out.println("테스트 : " + responseBody);
////
////        // then
////        //eventId":1,"userId":2,"eventType":"ANNUAL","id":1,"startDate":"2023-06-01","endDate
////        resultActions.andExpect(jsonPath("$.status").value(200));
////        resultActions.andExpect(jsonPath("$.msg").value("성공"));
////        resultActions.andExpect(jsonPath("$.data[0].eventId").value(1L));
////        resultActions.andExpect(jsonPath("$.data[0].userId").value(2L));
////        resultActions.andExpect(jsonPath("$.data[0].eventType").value("ANNUAL"));
////        resultActions.andExpect(jsonPath("$.data[0].startDate").value("2023-06-01"));
////        resultActions.andExpect(jsonPath("$.data[0].endDate").value("2023-06-30"));
////        resultActions.andExpect(jsonPath("$.data[1].eventId").value(2L));
////        resultActions.andExpect(jsonPath("$.data[1].userId").value(2L));
////        resultActions.andExpect(jsonPath("$.data[1].eventType").value("DUTY"));
////        resultActions.andExpect(jsonPath("$.data[1].id").value(1L));
////        resultActions.andExpect(jsonPath("$.data[1].startDate").value("2023-06-01"));
////        resultActions.andExpect(status().isOk());
////        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
////    }
//
//
//    @DisplayName("연차 신청 취소")
//    @WithUserDetails(value = "cos@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
//    @Test
////    public void event_cancel_annual_test() throws Exception {
////        // given
////        EventCancelInDto eventCancelInDto = new EventCancelInDto();
////        eventCancelInDto.setEventType("ANNUAL");
////        eventCancelInDto.setId(1L);
////
////        // when
////        String requestBody = om.writeValueAsString(eventCancelInDto);
////        System.out.println("테스트 : " + requestBody);
////
////        // when
////        ResultActions resultActions = mvc
////                .perform(post("/api/user/event/cancel").content(requestBody).contentType(MediaType.APPLICATION_JSON));
////        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
////        System.out.println("테스트 : " + responseBody);
////
////        // then
////        resultActions.andExpect(jsonPath("$.status").value(200));
////        resultActions.andExpect(jsonPath("$.msg").value("성공"));
////        resultActions.andExpect(jsonPath("$.data").value(true));
////        resultActions.andExpect(status().isOk());
////        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
////    }
////
////    @DisplayName("당직 신청 취소")
////    @WithUserDetails(value = "cos@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
////    @Test
////    public void event_cancel_duty_test() throws Exception {
////        // given
////        EventCancelInDto eventCancelInDto = new EventCancelInDto();
////        eventCancelInDto.setEventType("DUTY");
////        eventCancelInDto.setId(1L);
////
////        // when
////        String requestBody = om.writeValueAsString(eventCancelInDto);
////        System.out.println("테스트 : " + requestBody);
////
////        // when
////        ResultActions resultActions = mvc
////                .perform(post("/api/user/event/cancel").content(requestBody).contentType(MediaType.APPLICATION_JSON));
////        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
////        System.out.println("테스트 : " + responseBody);
////
////        // then
////        resultActions.andExpect(jsonPath("$.status").value(200));
////        resultActions.andExpect(jsonPath("$.msg").value("성공"));
////        resultActions.andExpect(jsonPath("$.data").value(true));
////        resultActions.andExpect(status().isOk());
////        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
////    }
//
//    //트랜잭션의 원칙 위배 -> All or Nothing 안되면 안되고, 되면 모두 되어야하지만
//    //현재, 성공이라고 뜨고 실제로는 해당 로직이 원하는 대로 동작하지 않는다. -> 치명적인 오류
////    @DisplayName("연차/당직 신청 수정 (연차)")
////    @WithUserDetails(value = "cos@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
////    @Test
////    public void event_modify_annual_test() throws Exception {
////        // given
////        EventRequest.EventModifyInDto eventModifyInDto = new EventModifyInDto();
////        eventModifyInDto.setId(1L);
////        eventModifyInDto.setEventType("ANNUAL");
////        eventModifyInDto.setStartDate(LocalDate.of(2023, 07, 3));
////        eventModifyInDto.setEndDate(LocalDate.of(2023, 07, 4));
////        eventModifyInDto.setCount(2L);
////
////        // when
////        String requestBody = om.writeValueAsString(eventModifyInDto);
////        System.out.println("테스트 : " + requestBody);
////
////        // when
////        ResultActions resultActions = mvc
////                .perform(post("/api/user/event/modify").content(requestBody).contentType(MediaType.APPLICATION_JSON));
////        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
////        System.out.println("테스트 : " + responseBody);
////
////        // then
////        //{"status":200,"msg":"성공","data":{"content":[{"eventId":1,"eventType":"ANNUAL","startDate":"2023-06-01","endDate":"2023-06-30","createdAt":"2023-05-10T15:11:28.670271","updatedAt":null,"orderState":"WAITING"},{"eventId":3,"eventType":"ANNUAL","startDate":"2023-06-01","endDate":"2023-06-30","createdAt":"2023-05-10T15:11:28.672319","updatedAt":null,"orderState":"WAITING"}],"pageable":{"sort":{"empty":true,"unsorted":true,"sorted":false},"offset":0,"pageNumber":0,"pageSize":8,"paged":true,"unpaged":false},"first":true,"last":true,"sort":{"empty":true,"unsorted":true,"sorted":false},"number":0,"size":8,"numberOfElements":2,"empty":false}}
////        resultActions.andExpect(jsonPath("$.status").value(200));
////        resultActions.andExpect(jsonPath("$.msg").value("성공"));
////        resultActions.andExpect(jsonPath("$.data.eventId").value(1L));
////        resultActions.andExpect(jsonPath("$.data.eventType").value("ANNUAL"));
////        resultActions.andExpect(jsonPath("$.data.startDate").value("2023-07-03"));
////        resultActions.andExpect(jsonPath("$.data.endDate").value("2023-07-04"));
////        resultActions.andExpect(status().isOk());
////        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
////    }
//
////    @Test
////    @DisplayName("모든 연차 리스트")
////    @WithUserDetails(value = "cos@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
////    public void all_annual_test() throws Exception{
////        //given
////        // when
////        ResultActions resultActions = mvc
////                .perform(get("/api/user/event/annual"));
////        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
////        System.out.println("테스트 : " + responseBody);
////
////        // then
////        //eventId":1,"userId":2,"eventType":"ANNUAL","id":1,"startDate":"2023-06-01","endDate
////        resultActions.andExpect(jsonPath("$.status").value(200));
////        resultActions.andExpect(jsonPath("$.msg").value("성공"));
////        resultActions.andExpect(jsonPath("$.data[0].eventId").value(1L));
////        resultActions.andExpect(jsonPath("$.data[0].userId").value(2L));
////        resultActions.andExpect(jsonPath("$.data[0].eventType").value("ANNUAL"));
////        resultActions.andExpect(jsonPath("$.data[0].id").value(1L));
////        resultActions.andExpect(jsonPath("$.data[0].startDate").value("2023-06-01"));
////        resultActions.andExpect(jsonPath("$.data[0].endDate").value("2023-06-30"));
////        resultActions.andExpect(status().isOk());
////        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
////
////    }
////    @Test
////    @DisplayName("모든 당직 리스트")
////    @WithUserDetails(value = "cos@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
////    public void all_duty_test() throws Exception{
////        //given
////        // when
////        ResultActions resultActions = mvc
////                .perform(get("/api/user/event/duty"));
////        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
////        System.out.println("테스트 : " + responseBody);
////
////        // then
////        //eventId":1,"userId":2,"eventType":"ANNUAL","id":1,"startDate":"2023-06-01","endDate
////        resultActions.andExpect(jsonPath("$.status").value(200));
////        resultActions.andExpect(jsonPath("$.msg").value("성공"));
////        resultActions.andExpect(jsonPath("$.data[0].eventId").value(2L));
////        resultActions.andExpect(jsonPath("$.data[0].userId").value(2L));
////        resultActions.andExpect(jsonPath("$.data[0].eventType").value("DUTY"));
////        resultActions.andExpect(jsonPath("$.data[0].id").value(1L));
////        resultActions.andExpect(jsonPath("$.data[0].startDate").value("2023-06-01"));
////        resultActions.andExpect(status().isOk());
////        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
////    }
//
//
////    // 내 연차 리스트
////    @GetMapping("/user/myannual")
////    public ResponseEntity<?> getMyAnnual(@AuthenticationPrincipal MyUserDetails myUserDetails,
////                                         @PageableDefault(size = 8) Pageable pageable) {
////        Slice<MyEventListOutDTO> eventListOutDTO = userService.내연차리스트(myUserDetails, pageable);
////        return ResponseEntity.ok(eventListOutDTO);
////    }
////
////    // 내 당직 리스트
////    @GetMapping("/user/myduty")
////    public ResponseEntity<?> getMyDuty(@AuthenticationPrincipal MyUserDetails myUserDetails,
////                                       @PageableDefault(size = 8) Pageable pageable) {
////        Slice<EventResponse.MyEventListOutDTO> eventListOutDTO = userService.내당직리스트(myUserDetails, pageable);
////        return ResponseEntity.ok(eventListOutDTO);
////    }
////
////
//
//
    //항상 일관된 DTO로 반환 해야하지만  ResponseDTO가 아닌, 원형 DTO 그대로 반환한 에러 수정
    @Test
    @DisplayName("사용자의 연차 리스트")
    @WithUserDetails(value = "cos@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void myAnnualList_test() throws Exception {
        //given
        // when
        ResultActions resultActions = mvc
                .perform(get("/api/user/myannual"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        //{"status":200,"msg":"성공","data":{"content":[{"eventId":1,"eventType":"ANNUAL","startDate":"2023-06-01","endDate":"2023-06-30","createdAt":"2023-05-10T14:56:54.443231","updatedAt":null,"orderState":"WAITING"}],"pageable":{"sort":{"empty":true,"unsorted":true,"sorted":false},"offset":0,"pageNumber":0,"pageSize":8,"paged":true,"unpaged":false},"number":0,"first":true,"last":true,"sort":{"empty":true,"unsorted":true,"sorted":false},"size":8,"numberOfElements":1,"empty":false}}
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("성공"));
        resultActions.andExpect(jsonPath("$.data.content[0].eventId").value(3L));
//        resultActions.andExpect(jsonPath("$.data.content[0].userId").value(2L));
        resultActions.andExpect(jsonPath("$.data.content[0].eventType").value("ANNUAL"));
        resultActions.andExpect(jsonPath("$.data.content[0].startDate").value("2023-06-07"));
        resultActions.andExpect(jsonPath("$.data.content[0].endDate").value("2023-06-09"));
        resultActions.andExpect(jsonPath("$.data.content[1].eventId").value(1L));
//        resultActions.andExpect(jsonPath("$.data.content[1].userId").value(2L));
        resultActions.andExpect(jsonPath("$.data.content[1].eventType").value("ANNUAL"));
        resultActions.andExpect(jsonPath("$.data.content[1].startDate").value("2023-06-01"));
        resultActions.andExpect(jsonPath("$.data.content[1].endDate").value("2023-06-05"));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }
    //항상 일관된 DTO로 반환 해야하지만  ResponseDTO가 아닌, 원형 DTO 그대로 반환한 에러 수정
    @Test
    @DisplayName("사용자의 당직 리스트")
    @WithUserDetails(value = "cos@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void myDutyList_test() throws Exception {
        //given
        // when
        ResultActions resultActions = mvc
                .perform(get("/api/user/myduty"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        //{"status":200,"msg":"성공","data":{"content":[{"eventId":1,"eventType":"ANNUAL","startDate":"2023-06-01","endDate":"2023-06-30","createdAt":"2023-05-10T14:56:54.443231","updatedAt":null,"orderState":"WAITING"}],"pageable":{"sort":{"empty":true,"unsorted":true,"sorted":false},"offset":0,"pageNumber":0,"pageSize":8,"paged":true,"unpaged":false},"number":0,"first":true,"last":true,"sort":{"empty":true,"unsorted":true,"sorted":false},"size":8,"numberOfElements":1,"empty":false}}
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("성공"));
        resultActions.andExpect(jsonPath("$.data.content[0].eventId").value(4L));
//        resultActions.andExpect(jsonPath("$.data.content[0].userId").value(2L));
        resultActions.andExpect(jsonPath("$.data.content[0].eventType").value("DUTY"));
        resultActions.andExpect(jsonPath("$.data.content[0].startDate").value("2023-06-05"));
        resultActions.andExpect(jsonPath("$.data.content[1].eventId").value(2L));
//        resultActions.andExpect(jsonPath("$.data.content[1].userId").value(2L));
        resultActions.andExpect(jsonPath("$.data.content[1].eventType").value("DUTY"));
        resultActions.andExpect(jsonPath("$.data.content[1].startDate").value("2023-06-01"));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    @DisplayName("가장 빠른 연차 당직")
    @WithUserDetails(value = "cos@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
    public void fastest_test() throws Exception {
        class TimeProvider {
            public LocalDate now() {
                return LocalDate.now();
            }
        }

        TimeProvider timeProvider = mock(TimeProvider.class);
        when(timeProvider.now()).thenReturn(LocalDate.of(2023, 5, 13));

        //given
        ResultActions resultActions = mvc
                .perform(get("/api/user/nextevent"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        //then
//        테스트 : {"status":200,"msg":"성공","data":{"nextAnnualDate":"2023-06-07","annualDDay":28,"nextDutyDate":"2023-06-05","dutyDDay":26}}
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("성공"));
        resultActions.andExpect(jsonPath("$.data.nextAnnualDate").value("2023-06-07"));
        resultActions.andExpect(jsonPath("$.data.dutyDDay").value("22"));
        resultActions.andExpect(jsonPath("$.data.nextDutyDate").value("2023-06-05"));
        resultActions.andExpect(jsonPath("$.data.dutyDDay").value("21"));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }
//    public ResponseEntity<?> list(@RequestParam(required = false) @Pattern(regexp = "ANNUAL|DUTY") String eventType,
//                                  @RequestParam(required = false) @Pattern(regexp = "^\\d{4}-\\d{2}$") String yearMonth,
//                                  @AuthenticationPrincipal MyUserDetails myUserDetails,
//                                  @PageableDefault(size = 10) Pageable pageable) {

    @DisplayName("모든 승인된 연차/당직 리스트")
    @WithUserDetails(value = "cos@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void all_event_test() throws Exception{
//        Slice<EventResponse.EventListOutDTO>
//        String eventType, String yearMonth, User user, Pageable pageable
        //given
        ResultActions resultActions = mvc
                .perform(get("/api/user/event/list"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        //when

        //then
//테스트 : {"status":200,"msg":"성공","data":
// {"content":[{"eventId":3,"userId":2,"userName":"코스","userEmail":"cos@nate.com","userImageUri":"https://test","userThumbnailUri":"https://test","eventType":"ANNUAL","id":null,"startDate":"2023-06-07","endDate":"2023-06-09","createdAt":"2023-05-11T17:20:20.277504","updatedAt":null,"orderState":null}
// ,{"eventId":4,"userId":2,"userName":"코스","userEmail":"cos@nate.com","userImageUri":"https://test","userThumbnailUri":"https://test","eventType":"DUTY","id":null,"startDate":"2023-06-05","endDate":"2023-06-05","createdAt":"2023-05-11T17:20:20.27847","updatedAt":null,"orderState":null}]
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("성공"));
        resultActions.andExpect(jsonPath("$.data[0].eventId").value(3L));
        resultActions.andExpect(jsonPath("$.data[0].eventType").value("ANNUAL"));
        resultActions.andExpect(jsonPath("$.data[0].startDate").value("2023-06-07"));
        resultActions.andExpect(jsonPath("$.data[0].endDate").value("2023-06-09"));
        resultActions.andExpect(jsonPath("$.data[1].eventId").value(4L));
        resultActions.andExpect(jsonPath("$.data[1].eventType").value("DUTY"));
        resultActions.andExpect(jsonPath("$.data[1].startDate").value("2023-06-05"));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);



    }
    @DisplayName("모든 승인된 연차 리스트")
    @WithUserDetails(value = "cos@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)

    @Test
    public void all_annual_test() throws Exception{
        //given
        ResultActions resultActions = mvc
                .perform(get("/api/user/event/list?eventType=ANNUAL"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        //when

        //then
//        테스트 : {"status":200,"msg":"성공","data":{"content":[{"eventId":3,"userId":2,"userName":"코스","userEmail":"cos@nate.com","userImageUri":"https://test","userThumbnailUri":"https://test","eventType":"ANNUAL","id":null,"startDate":"2023-06-07","endDate":"2023-06-09","createdAt":"2023-05-11T21:14:23.501564","updatedAt":null,"orderState":null}],"pageable":{"sort":{"empty":true,"sorted":false,"unsorted":true},"offset":0,"pageNumber":0,"pageSize":10,"paged":true,"unpaged":false},"sort":{"empty":true,"sorted":false,"unsorted":true},"size":10,"number":0,"first":true,"last":true,"numberOfElements":1,"empty":false}}
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("성공"));
        resultActions.andExpect(jsonPath("$.data[0].eventId").value(3L));
        resultActions.andExpect(jsonPath("$.data[0].eventType").value("ANNUAL"));
        resultActions.andExpect(jsonPath("$.data[0].startDate").value("2023-06-07"));
        resultActions.andExpect(jsonPath("$.data[0].endDate").value("2023-06-09"));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);

    }
    @DisplayName("모든 승인된 당직 리스트")
    @WithUserDetails(value = "cos@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)

    @Test
    public void all_duty_test() throws Exception{
        //given
        ResultActions resultActions = mvc
                .perform(get("/api/user/event/list?eventType=DUTY"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        //when

//        테스트 : {"status":200,"msg":"성공","data":{"content":[{"eventId":4,"userId":2,"userName":"코스","userEmail":"cos@nate.com","userImageUri":"https://test","userThumbnailUri":"https://test","eventType":"DUTY","id":null,"startDate":"2023-06-05","endDate":"2023-06-05","createdAt":"2023-05-11T21:14:24.153859","updatedAt":null,"orderState":null}],"pageable":{"sort":{"empty":true,"sorted":false,"unsorted":true},"offset":0,"pageNumber":0,"pageSize":10,"paged":true,"unpaged":false},"sort":{"empty":true,"sorted":false,"unsorted":true},"size":10,"number":0,"first":true,"last":true,"numberOfElements":1,"empty":false}}
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("성공"));
        resultActions.andExpect(jsonPath("$.data.[0].eventId").value(4L));
        resultActions.andExpect(jsonPath("$.data.[0].eventType").value("DUTY"));
        resultActions.andExpect(jsonPath("$.data.[0].startDate").value("2023-06-05"));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);

        //then

    }


}

