package shop.mtcoding.restend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import shop.mtcoding.restend.core.MyRestDoc;
import shop.mtcoding.restend.core.auth.jwt.MyJwtProvider;
import shop.mtcoding.restend.core.dummy.DummyEntity;
import shop.mtcoding.restend.dto.event.EventRequest;
import shop.mtcoding.restend.dto.event.EventRequest.EventAddInDto;
import shop.mtcoding.restend.dto.event.EventRequest.EventCancelInDto;
import shop.mtcoding.restend.dto.event.EventRequest.EventModifyInDto;
import shop.mtcoding.restend.dto.user.UserRequest;
import shop.mtcoding.restend.model.annual.Annual;
import shop.mtcoding.restend.model.annual.AnnualRepository;
import shop.mtcoding.restend.model.duty.Duty;
import shop.mtcoding.restend.model.duty.DutyRepository;
import shop.mtcoding.restend.model.event.EventRepository;
import shop.mtcoding.restend.model.event.EventType;
import shop.mtcoding.restend.model.user.User;
import shop.mtcoding.restend.model.user.UserRepository;

import javax.persistence.EntityManager;

import java.time.LocalDate;

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
    private EntityManager em;

    @BeforeEach
    public void setUp() {
        User ssar = userRepository.save(dummy.newUser("사르", "ADMIN"));
        User cos = userRepository.save(dummy.newUser("코스", "USER"));
        Annual annual1 = annualRepository.save(dummy.newAnnual(LocalDate.of(2023, 06, 1), LocalDate.of(2023, 06, 30)));
        Duty duty1 = dutyRepository.save(dummy.newDuty(LocalDate.of(2023, 06, 1)));
        eventRepository.save(dummy.newEvent(cos, "ANNUAL",annual1, null));
        eventRepository.save(dummy.newEvent(cos, "DUTY", null, duty1));
        em.clear();
    }
    @DisplayName("연차 신청 성공")
    @WithUserDetails(value = "cos@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void event_add_annual_test() throws Exception {
        // given
        EventRequest.EventAddInDto eventAddInDto=new EventAddInDto();
        eventAddInDto.setEventType("연차");
        eventAddInDto.setStartDate(LocalDate.of(2023,05,9));
        eventAddInDto.setEndDate(LocalDate.of(2023, 06, 1));

        // when
        String requestBody = om.writeValueAsString(eventAddInDto);
        System.out.println("테스트 : "+requestBody);

        // when
        ResultActions resultActions = mvc
                .perform(post("/api/user/event/add").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("성공"));
        resultActions.andExpect(jsonPath("$.data.eventId").value(3L));
        resultActions.andExpect(jsonPath("$.data.userId").value(2L));
        resultActions.andExpect(jsonPath("$.data.eventType").value("ANNUAL"));
        resultActions.andExpect(jsonPath("$.data.startDate").value("2023-05-09"));
        resultActions.andExpect(jsonPath("$.data.endDate").value("2023-06-01"));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("연차 신청 실패")
    @Test
    public void event_add_annual_fail_test() throws Exception {
        // given
        EventRequest.EventAddInDto eventAddInDto=new EventAddInDto();
        eventAddInDto.setEventType("연차");
        eventAddInDto.setStartDate(LocalDate.of(2023,05,9));
        eventAddInDto.setEndDate(LocalDate.of(2023, 06, 1));

        // when
        String requestBody = om.writeValueAsString(eventAddInDto);
        System.out.println("테스트 : "+requestBody);

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
        EventRequest.EventAddInDto eventAddInDto=new EventAddInDto();
        eventAddInDto.setEventType("당직");
        eventAddInDto.setStartDate(LocalDate.of(2023,05,10));

        // when
        String requestBody = om.writeValueAsString(eventAddInDto);
        System.out.println("테스트 : "+requestBody);

        // when
        ResultActions resultActions = mvc
                .perform(post("/api/user/event/add").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("성공"));
        resultActions.andExpect(jsonPath("$.data.eventId").value(3L));
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
        EventRequest.EventAddInDto eventAddInDto=new EventAddInDto();
        eventAddInDto.setEventType("당직");
        eventAddInDto.setStartDate(LocalDate.of(2023,05,10));

        // when
        String requestBody = om.writeValueAsString(eventAddInDto);
        System.out.println("테스트 : "+requestBody);

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

    @DisplayName("모든 이벤트 리스트")
    @WithUserDetails(value = "cos@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void all_search_test() throws Exception{
        //given

        // when
        ResultActions resultActions = mvc
                .perform(get("/api/user/event"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        //eventId":1,"userId":2,"eventType":"ANNUAL","id":1,"startDate":"2023-06-01","endDate
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("성공"));
        resultActions.andExpect(jsonPath("$.data[0].eventId").value(1L));
        resultActions.andExpect(jsonPath("$.data[0].userId").value(2L));
        resultActions.andExpect(jsonPath("$.data[0].eventType").value("ANNUAL"));
        resultActions.andExpect(jsonPath("$.data[0].id").value(1L));
        resultActions.andExpect(jsonPath("$.data[0].startDate").value("2023-06-01"));
        resultActions.andExpect(jsonPath("$.data[0].endDate").value("2023-06-30"));
        resultActions.andExpect(jsonPath("$.data[1].eventId").value(2L));
        resultActions.andExpect(jsonPath("$.data[1].userId").value(2L));
        resultActions.andExpect(jsonPath("$.data[1].eventType").value("DUTY"));
        resultActions.andExpect(jsonPath("$.data[1].id").value(1L));
        resultActions.andExpect(jsonPath("$.data[1].startDate").value("2023-06-01"));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }


    @DisplayName("연차 신청 취소")
    @WithUserDetails(value = "cos@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void event_cancel_annual_test() throws Exception {
        // given
        EventRequest.EventCancelInDto eventCancelInDto=new EventCancelInDto();
        eventCancelInDto.setEventType("ANNUAL");
        eventCancelInDto.setId(1L);

        // when
        String requestBody = om.writeValueAsString(eventCancelInDto);
        System.out.println("테스트 : "+requestBody);

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
        EventRequest.EventCancelInDto eventCancelInDto=new EventCancelInDto();
        eventCancelInDto.setEventType("DUTY");
        eventCancelInDto.setId(2L);

        // when
        String requestBody = om.writeValueAsString(eventCancelInDto);
        System.out.println("테스트 : "+requestBody);

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

//    @DisplayName("연차/당직 신청 수정")
//    @WithUserDetails(value = "cos@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
//    @Test
//    public void event_modify_duty_test() throws Exception {
//        // given
//        EventRequest.EventModifyInDto eventModifyInDto=new EventModifyInDto();
//        eventModifyInDto.setId(1L);
//        eventModifyInDto.setEventType("연차");
//        eventModifyInDto.setStartDate(LocalDate.of(2023,07,1));
//        eventModifyInDto.setEndDate(LocalDate.of(2023,07,10));
//
//        // when
//        String requestBody = om.writeValueAsString(eventModifyInDto);
//        System.out.println("테스트 : "+requestBody);
//
//        // when
//        ResultActions resultActions = mvc
//                .perform(post("/api/user/event/modify").content(requestBody).contentType(MediaType.APPLICATION_JSON));
//        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
//        System.out.println("테스트 : " + responseBody);
//
//        // then
//        resultActions.andExpect(jsonPath("$.status").value(200));
//        resultActions.andExpect(jsonPath("$.msg").value("성공"));
//        resultActions.andExpect(jsonPath("$.data[0].eventId").value(2L));
//        resultActions.andExpect(jsonPath("$.data[0].userId").value(2L));
//        resultActions.andExpect(jsonPath("$.data[0].eventType").value("DUTY"));
//        resultActions.andExpect(jsonPath("$.data[0].id").value(1L));
//        resultActions.andExpect(jsonPath("$.data[0].startDate").value("2023-06-01"));
//        resultActions.andExpect(jsonPath("$.data[0].endDate").value("2023-06-30"));
//        resultActions.andExpect(status().isOk());
//        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
//    }

    @Test
    @DisplayName("모든 연차 리스트")
    @WithUserDetails(value = "cos@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void all_annual_test() throws Exception{
        //given
        // when
        ResultActions resultActions = mvc
                .perform(get("/api/user/event/annual"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        //eventId":1,"userId":2,"eventType":"ANNUAL","id":1,"startDate":"2023-06-01","endDate
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("성공"));
        resultActions.andExpect(jsonPath("$.data[0].eventId").value(1L));
        resultActions.andExpect(jsonPath("$.data[0].userId").value(2L));
        resultActions.andExpect(jsonPath("$.data[0].eventType").value("ANNUAL"));
        resultActions.andExpect(jsonPath("$.data[0].id").value(1L));
        resultActions.andExpect(jsonPath("$.data[0].startDate").value("2023-06-01"));
        resultActions.andExpect(jsonPath("$.data[0].endDate").value("2023-06-30"));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);

    }
    @Test
    @DisplayName("모든 당직 리스트")
    @WithUserDetails(value = "cos@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void all_duty_test() throws Exception{
        //given
        // when
        ResultActions resultActions = mvc
                .perform(get("/api/user/event/duty"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        //eventId":1,"userId":2,"eventType":"ANNUAL","id":1,"startDate":"2023-06-01","endDate
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("성공"));
        resultActions.andExpect(jsonPath("$.data[0].eventId").value(2L));
        resultActions.andExpect(jsonPath("$.data[0].userId").value(2L));
        resultActions.andExpect(jsonPath("$.data[0].eventType").value("DUTY"));
        resultActions.andExpect(jsonPath("$.data[0].id").value(1L));
        resultActions.andExpect(jsonPath("$.data[0].startDate").value("2023-06-01"));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }
}
