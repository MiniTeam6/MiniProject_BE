//package shop.mtcoding.restend.service;
//
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.Spy;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.web.multipart.MultipartFile;
//import shop.mtcoding.restend.core.auth.session.MyUserDetails;
//import shop.mtcoding.restend.core.dummy.DummyEntity;
//import shop.mtcoding.restend.dto.user.UserRequest;
//import shop.mtcoding.restend.dto.user.UserResponse;
//import shop.mtcoding.restend.model.user.User;
//import shop.mtcoding.restend.model.user.UserRepository;
//
//import java.util.Optional;
//
//import static org.mockito.ArgumentMatchers.any;
//
//@ActiveProfiles("test")
//@ExtendWith(MockitoExtension.class)
//public class UserServiceTest extends DummyEntity {
//
//    // 진짜 userService 객체를 만들고 Mock로 Load된 모든 객체를 userService에 주입
//    @InjectMocks
//    private UserService userService;
//
//    // 진짜 객체를 만들어서 Mockito 환경에 Load
//    @Mock
//    private UserRepository userRepository;
//
//    // 가짜 객체를 만들어서 Mockito 환경에 Load
//    @Mock
//    private AuthenticationManager authenticationManager;
//
//    // 진짜 객체를 만들어서 Mockito 환경에 Load
//    @Spy
//    private BCryptPasswordEncoder bCryptPasswordEncoder;
//
//    @Test
//    public void hello_test(){
//        String pw = "1234";
//        String enc = bCryptPasswordEncoder.encode(pw);
//        System.out.println(enc);
//    }
//
//    @Test
//    public void 회원가입_test() throws Exception{
//
//        // given
//        UserRequest.SignupInDTO signupInDTO = new UserRequest.SignupInDTO();
//        signupInDTO.setUsername("코스");
//        signupInDTO.setPassword("1234");
//        signupInDTO.setEmail("cos@nate.com");
//
//        // stub 1
////        Mockito.when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
//
//        // stub 2
//        User cos = newMockUser(1L, "코스","USER");
//        MultipartFile image=null;
//        Mockito.when(userRepository.save(any())).thenReturn(cos);
//
//        // when
//        UserResponse.SignupOutDTO signupOutDTO = userService.회원가입(signupInDTO, image);
//
//        // then
//        Assertions.assertThat(signupOutDTO.getId()).isEqualTo(1L);
//        Assertions.assertThat(signupOutDTO.getUsername()).isEqualTo("코스");
//    }
//
//    @Test
//    public void 로그인_test() throws Exception{
//        // given
//        UserRequest.LoginInDTO loginInDTO = new UserRequest.LoginInDTO();
//        loginInDTO.setEmail("cos@nate.com");
//        loginInDTO.setPassword("1234");
//
//        // stub
//        User cos = newMockUser(1L,  "코스", "USER");
//        MyUserDetails myUserDetails = new MyUserDetails(cos);
//        Authentication authentication = new UsernamePasswordAuthenticationToken(
//                myUserDetails, myUserDetails.getPassword(), myUserDetails.getAuthorities()
//        );
//        Mockito.when(authenticationManager.authenticate(any())).thenReturn(authentication);
//
//        // when
//        Object[] tmp = userService.로그인(loginInDTO);
//        String jwt=(String) tmp[1];
//        System.out.println("디버그 : "+jwt);
//
//        // then
//        Assertions.assertThat(jwt.startsWith("Bearer ")).isTrue();
//    }
//
//    @Test
//    public void 유저상세보기_test() throws Exception{
//        // given
//        Long id = 1L;
//
//        // stub
//        User cos = newMockUser(1L, "코스","USER");
//        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(cos));
//
//        // when
//        UserResponse.UserDetailOutDTO detailOutDTO  = userService.회원상세보기(id);
//
//        // then
//        Assertions.assertThat(detailOutDTO.getId()).isEqualTo(1L);
//        Assertions.assertThat(detailOutDTO.getUsername()).isEqualTo("코스");
//        Assertions.assertThat(detailOutDTO.getEmail()).isEqualTo("cos@nate.com");
//        Assertions.assertThat(detailOutDTO.getRole()).isEqualTo("USER");
//    }
//}
