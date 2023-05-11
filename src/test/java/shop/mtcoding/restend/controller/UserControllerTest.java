package shop.mtcoding.restend.controller;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import shop.mtcoding.restend.core.MyRestDoc;
import shop.mtcoding.restend.core.auth.jwt.MyJwtProvider;
import shop.mtcoding.restend.core.auth.session.MyUserDetails;
import shop.mtcoding.restend.core.dummy.DummyEntity;
import shop.mtcoding.restend.dto.user.UserRequest;
import shop.mtcoding.restend.dto.user.UserRequest.SignupInDTO;
import shop.mtcoding.restend.model.user.UserRepository;
import shop.mtcoding.restend.service.S3Service;

import javax.persistence.EntityManager;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.restdocs.snippet.Attributes.key;
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

        S3Service s3ServiceMock = mock(S3Service.class);

        // when
        String requestBody = om.writeValueAsString(signupInDTO);
        System.out.println("테스트 : "+requestBody);
        // when
        MockMultipartFile signUpInDTO = new MockMultipartFile("signupInDTO",
                null,
                "application/json",
                om.writeValueAsBytes(signupInDTO)
        );

        MockMultipartFile image = new MockMultipartFile("image",
                "image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                new FileInputStream(new File("src/main/resources/image.jpg")));

        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.multipart(HttpMethod.POST,"/api/signup")
                        .file(signUpInDTO)
                        .file(image)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
        );


        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.data.id").value(3L));
        resultActions.andExpect(jsonPath("$.data.username").value("러브"));
        resultActions.andExpect(jsonPath("$.data.email").value("love@nate.com"));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);

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
                null,
                "application/json",
                om.writeValueAsBytes(signupInDTO)
        );

//        File emptyFile = new File("src/main/resources/image.jpg");
//        FileInputStream inputStream= new FileInputStream(getClass().getResource("/img/raspberry.png").getFile());

        MockMultipartFile image = new MockMultipartFile("image",
                "image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                new FileInputStream(new File("src/main/resources/image.jpg")));

        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.multipart(HttpMethod.POST,"/api/signup")
                        .file(signUpInDTO)
                        .file(image)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
        );

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);


        // then
        resultActions.andExpect(jsonPath("$.status").value(400));
        resultActions.andExpect(jsonPath("$.msg").value("badRequest"));
        resultActions.andExpect(jsonPath("$.data.key").value("email"));
        resultActions.andExpect(jsonPath("$.data.value").value("이메일이 존재합니다"));
        resultActions.andExpect(status().isBadRequest());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
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
                null,
                "application/json",
                om.writeValueAsBytes(signupInDTO)
        );
        MockMultipartFile image = new MockMultipartFile("image",
                "image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                new FileInputStream(new File("src/main/resources/image.jpg")));

        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.multipart(HttpMethod.POST,"/api/signup")
                        .file(signUpInDTO)
                        .file(image)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
        );

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(400));
        resultActions.andExpect(jsonPath("$.msg").value("badRequest"));
        resultActions.andExpect(jsonPath("$.data.key").value("username"));
        resultActions.andExpect(jsonPath("$.data.value").value("올바른 이름 형식으로 작성해주세요."));
        resultActions.andExpect(status().isBadRequest());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
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