package com.miniproject.pantry.controller;

import java.io.File;
import java.io.FileInputStream;

import com.miniproject.pantry.core.MyRestDoc;
import com.miniproject.pantry.model.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
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
import com.miniproject.pantry.core.auth.jwt.MyJwtProvider;
import com.miniproject.pantry.core.dummy.DummyEntity;
import com.miniproject.pantry.dto.user.UserRequest;
import com.miniproject.pantry.dto.user.UserRequest.SignupInDTO;

import javax.persistence.EntityManager;

import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("회원 API")
@AutoConfigureRestDocs(uriScheme = "http", uriHost = "localhost", uriPort = 8080)
@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class UserControllerTest extends MyRestDoc {
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
    private EntityManager em;

    @BeforeEach
    public void setUp() {
        userRepository.save(dummy.newUser("사르", "ADMIN", true));
        userRepository.save(dummy.newUser("코스", "USER",true));

        em.clear();
    }

    @DisplayName("회원가입 성공")
    @Test
    public void join_test() throws Exception {
        // given
        SignupInDTO signupInDTO = new SignupInDTO();
        signupInDTO.setUsername("러브");
        signupInDTO.setPassword("aaaa1234@@");
        signupInDTO.setEmail("love@nate.com");
        signupInDTO.setPhone("010-0000-0000");

        String requestBody = om.writeValueAsString(signupInDTO);
        // when
        System.out.println("테스트 : "+requestBody);
        // when
        MockMultipartFile signUpInDTO = new MockMultipartFile("signupInDTO",
                "signupInDTO.json",
                "application/json",
                om.writeValueAsBytes(signupInDTO)
        );

        MockMultipartFile image = new MockMultipartFile("image",
                "image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                new FileInputStream(new File("src/main/resources/image.jpg")));

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/signup")
                .file(signUpInDTO)
                .file(image)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .content(requestBody)
        );
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.data.id").value(3L));
        resultActions.andExpect(jsonPath("$.data.username").value("러브"));
        resultActions.andExpect(jsonPath("$.data.email").value("love@nate.com"));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(documentImage);
    }

    /**
     * https://jjay2222.tistory.com/114
     * @throws Exception
     */
    @DisplayName("회원가입 실패")
    @Test
    public void join_fail_bad_request_test() throws Exception {
        // given
        SignupInDTO signupInDTO = new SignupInDTO();
        signupInDTO.setUsername("사르");
        signupInDTO.setPassword("aaaa1234@@");
        signupInDTO.setPhone("010-0000-0000");
        signupInDTO.setEmail("ssar@nate.com");
        String requestBody = om.writeValueAsString(signupInDTO);
        System.out.println("테스트 : "+requestBody);
        // when
        MockMultipartFile signUpInDTO = new MockMultipartFile("signupInDTO",
                "signupInDTO.json",
                "application/json",
                om.writeValueAsBytes(signupInDTO)
        );

        MockMultipartFile image = new MockMultipartFile("image",
                "image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                new FileInputStream(new File("src/main/resources/image.jpg")));

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/signup")
                .file(signUpInDTO)
                .file(image)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .content(requestBody)
        );
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);


        // then
        resultActions.andExpect(jsonPath("$.status").value(400));
        resultActions.andExpect(jsonPath("$.msg").value("badRequest"));
        resultActions.andExpect(jsonPath("$.data.key").value("email"));
        resultActions.andExpect(jsonPath("$.data.value").value("이메일이 존재합니다"));
        resultActions.andExpect(status().isBadRequest());
        resultActions.andDo(documentImage);
    }

    @DisplayName("회원가입 유효성 검사 실패")
    @Test
    public void join_fail_valid_test() throws Exception {
        // given
        SignupInDTO signupInDTO = new SignupInDTO();
        signupInDTO.setUsername("사");
        signupInDTO.setPassword("aaaa1234@@");
        signupInDTO.setEmail("ssar@nate.com");
        signupInDTO.setPhone("010-0000-0000");
        String requestBody = om.writeValueAsString(signupInDTO);
        System.out.println("테스트 : "+requestBody);
        // when
        MockMultipartFile signUpInDTO = new MockMultipartFile("signupInDTO",
                "signupInDTO.json",
                "application/json",
                om.writeValueAsBytes(signupInDTO)
        );
        MockMultipartFile image = new MockMultipartFile("image",
                "image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                new FileInputStream(new File("src/main/resources/image.jpg")));


        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.multipart("/api/signup")
                        .file(image)
                        .file(signUpInDTO)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .content(requestBody)
        );

//        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
//        System.out.println("테스트 : " + responseBody);

//        // then
        resultActions.andExpect(jsonPath("$.status").value(400));
        resultActions.andExpect(jsonPath("$.msg").value("badRequest"));
        resultActions.andExpect(jsonPath("$.data.key").value("username"));
        resultActions.andExpect(jsonPath("$.data.value").value("올바른 이름 형식으로 작성해주세요."));
        resultActions.andExpect(status().isBadRequest());
        resultActions.andDo(documentImage);
    }

    @DisplayName("로그인 성공")
    @Test
    public void login_test() throws Exception {
        // given
        UserRequest.LoginInDTO loginInDTO = new UserRequest.LoginInDTO();
        loginInDTO.setEmail("ssar@nate.com");
        loginInDTO.setPassword("aaaa1234@@");
        String requestBody = om.writeValueAsString(loginInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/api/login").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        String jwtToken = resultActions.andReturn().getResponse().getHeader(MyJwtProvider.HEADER);
        Assertions.assertThat(jwtToken.startsWith(MyJwtProvider.TOKEN_PREFIX)).isTrue();
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("로그인 인증 실패")
    @Test
    public void login_fail_un_authorized_test() throws Exception {
        // given
        UserRequest.LoginInDTO loginInDTO = new UserRequest.LoginInDTO();
        loginInDTO.setEmail("ssar@nate.com");
        loginInDTO.setPassword("12345");
        String requestBody = om.writeValueAsString(loginInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/api/login").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(401));
        resultActions.andExpect(jsonPath("$.msg").value("unAuthorized"));
        resultActions.andExpect(jsonPath("$.data").value("인증되지 않았습니다"));
        resultActions.andExpect(status().isUnauthorized());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    // jwt token -> 인증필터 -> 시큐리티 세션생성
    // setupBefore=TEST_METHOD (setUp 메서드 실행전에 수행)
    // setupBefore=TEST_EXECUTION (saveAccount_test 메서드 실행전에 수행)
    // @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    // authenticationManager.authenticate() 실행해서 MyUserDetailsService를 호출하고
    // usrename=ssar을 찾아서 세션에 담아주는 어노테이션
    @DisplayName("회원상세보기 성공")
    @WithUserDetails(value = "ssar@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void detail_test() throws Exception {
        // given
        Long id = 1L;

        // when
        ResultActions resultActions = mvc
                .perform(get("/api/user/users/"+id));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.data.id").value(1L));
        resultActions.andExpect(jsonPath("$.data.username").value("사르"));
        resultActions.andExpect(jsonPath("$.data.email").value("ssar@nate.com"));
        resultActions.andExpect(jsonPath("$.data.role").value("ADMIN"));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("회원상세보기 인증 실패")
    @Test
    public void detail_fail_un_authorized__test() throws Exception {
        // given
        Long id = 1L;

        // when
        ResultActions resultActions = mvc
                .perform(get("/api/user/"+id));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(401));
        resultActions.andExpect(jsonPath("$.msg").value("unAuthorized"));
        resultActions.andExpect(jsonPath("$.data").value("인증되지 않았습니다"));
        resultActions.andExpect(status().isUnauthorized());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

//    @DisplayName("회원상세보기 권한 실패")
//    @WithUserDetails(value = "cos@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
//    @Test
//    public void detail_fail_forbidden_test() throws Exception {
//        // given
//        Long id = 1L;
//
//        // when
//        ResultActions resultActions = mvc
//                .perform(get("/api/user/users/"+id));
//        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
//        System.out.println("테스트 : " + responseBody);
//
//        // then
//        resultActions.andExpect(jsonPath("$.status").value(403));
//        resultActions.andExpect(jsonPath("$.msg").value("forbidden"));
//        resultActions.andExpect(jsonPath("$.data").value("권한이 없습니다"));
//        resultActions.andExpect(status().isForbidden());
//        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
//    }
}
