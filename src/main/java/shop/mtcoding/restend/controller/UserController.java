package shop.mtcoding.restend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
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
import shop.mtcoding.restend.service.AnnualService;
import shop.mtcoding.restend.service.EventService;
import shop.mtcoding.restend.service.UserService;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final EventService eventService;
    private final AnnualService annualService;
    //private final DutyService dutyService;

    @MyErrorLog
    @MyLog
    @PostMapping("/signup")
    public ResponseEntity<?> join(@RequestBody @Valid UserRequest.JoinInDTO joinInDTO, Errors errors) {
        UserResponse.JoinOutDTO joinOutDTO = userService.회원가입(joinInDTO);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(joinOutDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserRequest.LoginInDTO loginInDTO){
        String jwt = userService.로그인(loginInDTO);
        ResponseDTO<?> responseDTO = new ResponseDTO<>();
        return ResponseEntity.ok().header(MyJwtProvider.HEADER, jwt).body(responseDTO);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id, @AuthenticationPrincipal MyUserDetails myUserDetails) throws JsonProcessingException {
        if(id.longValue() != myUserDetails.getUser().getId()){
            throw new Exception403("권한이 없습니다");
        }
        UserResponse.DetailOutDTO detailOutDTO = userService.회원상세보기(id);
        //System.out.println(new ObjectMapper().writeValueAsString(detailOutDTO));
        ResponseDTO<?> responseDTO = new ResponseDTO<>(detailOutDTO);
        return ResponseEntity.ok(responseDTO);
    }

    // 모든 연차 당직 리스트
    @GetMapping("/user/list")
    public ResponseEntity<?> list() throws JsonProcessingException {
        EventResponse.EventListDto eventListDto = (EventResponse.EventListDto) eventService.findAll();
        return ResponseEntity.ok(eventListDto);
    }

    // 연차 신청
    @PostMapping("/user/annual/add")
    public ResponseEntity<?> add(@RequestBody @Valid AnnualRequest.AnnualAddDto annualAddDto, @AuthenticationPrincipal MyUserDetails myUserDetails, Errors errors) throws JsonProcessingException {
        annualService.add(annualAddDto, myUserDetails.getUser());
        return ResponseEntity.ok(annualAddDto);
    }

    // 마이페이지
    @GetMapping("/user/mypage")
    public ResponseEntity<?> mypage(@AuthenticationPrincipal MyUserDetails myUserDetails) throws JsonProcessingException {
        UserResponse.DetailOutDTO detailOutDTO = userService.회원상세보기(myUserDetails.getUser().getId());
        ResponseDTO<?> responseDTO = new ResponseDTO<>(detailOutDTO);
        return ResponseEntity.ok(responseDTO);
    }
}
