package shop.mtcoding.restend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import shop.mtcoding.restend.core.annotation.MyErrorLog;
import shop.mtcoding.restend.core.annotation.MyLog;
import shop.mtcoding.restend.core.auth.jwt.MyJwtProvider;
import shop.mtcoding.restend.core.auth.session.MyUserDetails;
import shop.mtcoding.restend.core.exception.Exception403;
import shop.mtcoding.restend.dto.ResponseDTO;
import shop.mtcoding.restend.dto.annual.AnnualRequest;
import shop.mtcoding.restend.dto.event.EventResponse;
import shop.mtcoding.restend.dto.user.UserRequest;
import shop.mtcoding.restend.dto.user.UserResponse;
import shop.mtcoding.restend.model.event.EventType;
import shop.mtcoding.restend.service.AnnualService;
import shop.mtcoding.restend.service.EventService;
import shop.mtcoding.restend.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    // 회원가입
    @MyErrorLog
    @MyLog
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestPart @Valid UserRequest.SignupInDTO signupInDTO, @RequestPart MultipartFile image, Errors errors) {
        UserResponse.SignupOutDTO signupOutDTO = userService.회원가입(signupInDTO, image);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(signupOutDTO);
        return ResponseEntity.ok(responseDTO);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserRequest.LoginInDTO loginInDTO){
        Object[] result = userService.로그인(loginInDTO);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(result[0]);
        return ResponseEntity.ok().header(MyJwtProvider.HEADER, (String) result[1]).body(responseDTO);
    }

    // 모든 유저 리스트
    @GetMapping("/user/users")
    public ResponseEntity<?> findAllUser() {
        List<UserResponse.UserListOutDTO> userListOutDTO = userService.회원전체리스트();
        ResponseDTO<?> responseDTO = new ResponseDTO<>(userListOutDTO);
        return ResponseEntity.ok(responseDTO);
    }

    // 유저 상세 정보
    @GetMapping("/user/users/{id}")
    public ResponseEntity<?> userDetail(@PathVariable Long id) {
        UserResponse.UserDetailOutDTO userDetailOutDTO = userService.회원상세보기(id);
        //System.out.println(new ObjectMapper().writeValueAsString(detailOutDTO));
        ResponseDTO<?> responseDTO = new ResponseDTO<>(userDetailOutDTO);
        return ResponseEntity.ok(responseDTO);
    }

    // 내 정보 보기
    @GetMapping("/user/myinfo")
    public ResponseEntity<?> getMyInfo(@AuthenticationPrincipal MyUserDetails myUserDetails) {
        UserResponse.UserDetailOutDTO userDetailOutDTO = userService.회원상세보기(myUserDetails.getUser().getId());
        ResponseDTO<?> responseDTO = new ResponseDTO<>(userDetailOutDTO);
        return ResponseEntity.ok(responseDTO);
    }

    // 내 정보 수정
    @PostMapping("/user/myinfo")
    public ResponseEntity<?> updateMyInfo(@AuthenticationPrincipal MyUserDetails myUserDetails, @RequestPart @Valid UserRequest.SignupInDTO signupInDTO, @RequestPart MultipartFile image, Errors errors) {
        UserResponse.UserDetailOutDTO userDetailOutDTO = userService.회원정보수정(myUserDetails.getUser().getId(), signupInDTO, image);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(userDetailOutDTO);
        return ResponseEntity.ok(responseDTO);
    }

    // 내 연차 리스트
     // 무한스크롤 slice
//    @GetMapping("/user/myannual")
//    public ResponseEntity<?> getMyAnnual(@AuthenticationPrincipal MyUserDetails myUserDetails,
//                                         @PageableDefault(sort = "createAt", direction = Sort.Direction.DESC) Pageable pageable) {
//        List<EventResponse.EventListOutDTO> eventListOutDTO = userService.내연차리스트(myUserDetails, pageable);
//        return ResponseEntity.ok(responseDTO);
//    }

    // 내 당직 리스트
}
