package com.miniproject.pantry.controller;

import com.miniproject.pantry.core.auth.session.MyUserDetails;
import com.miniproject.pantry.dto.ResponseDTO;
import com.miniproject.pantry.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import com.miniproject.pantry.dto.event.EventRequest;
import com.miniproject.pantry.dto.event.EventResponse;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class EventController {
    private final EventService eventService;

    // 연차/당직 신청
    @PostMapping("/user/event/add")
    public ResponseEntity<?> add(@RequestBody @Valid EventRequest.EventAddInDto eventAddInDTO, Errors errors, @AuthenticationPrincipal MyUserDetails myUserDetails, HttpSession session) {
//        if (eventAddInDTO.getEventType().equals("ANNUAL")) {
//            session.setAttribute("count", eventAddInDTO.getCount());
//        }
        EventResponse.EventAddOutDTO eventAddOutDTO = eventService.연차당직신청(eventAddInDTO, myUserDetails.getUser());
        ResponseDTO<?> responseDTO = new ResponseDTO<>(eventAddOutDTO);
        return ResponseEntity.ok(responseDTO);
    }

    // 연차/당직 신청 취소
    @PostMapping("/user/event/cancel")
    public ResponseEntity<?> cancel(@RequestBody @Valid EventRequest.EventCancelInDto eventCancelInDTO, Errors errors, @AuthenticationPrincipal MyUserDetails myUserDetails) {
        ResponseDTO<?> responseDTO = new ResponseDTO<>(eventService.연차당직신청취소(eventCancelInDTO, myUserDetails.getUser()));
        return ResponseEntity.ok(responseDTO);
    }

    // 연차/당직 신청 수정
    @PostMapping("/user/event/modify")
    public ResponseEntity<?> modify(@RequestBody @Valid EventRequest.EventModifyInDto eventModifyInDTO, Errors errors, @AuthenticationPrincipal MyUserDetails myUserDetails) {
        ResponseDTO<?> responseDTO = new ResponseDTO<>(eventService.연차당직신청수정(eventModifyInDTO, myUserDetails.getUser()));
        return ResponseEntity.ok(responseDTO);
    }

    // 모든 이벤트 리스트 (테스트용)
    @GetMapping("/user/event")
    public ResponseEntity<?> list(@AuthenticationPrincipal MyUserDetails myUserDetails) {
        ResponseDTO<?> responseDTO = new ResponseDTO<>(eventService.이벤트리스트());
        return ResponseEntity.ok(responseDTO);
    }


    // 모든 연차 리스트 (테스트용)
    @GetMapping("/user/event/annual")
    public ResponseEntity<?> annual(@AuthenticationPrincipal MyUserDetails myUserDetails) {
        ResponseDTO<?> responseDTO = new ResponseDTO<>(eventService.연차리스트());
        return ResponseEntity.ok(responseDTO);
    }

    // 모든 당직 리스트 (테스트용)
    @GetMapping("/user/event/duty")
    public ResponseEntity<?> duty(@AuthenticationPrincipal MyUserDetails myUserDetails) {
        ResponseDTO<?> responseDTO = new ResponseDTO<>(eventService.당직리스트());
        return ResponseEntity.ok(responseDTO);
    }


    // 연차 당직 리스트
    // 파리미터 없으면 전체
    // 파라미터로 탭 구분: 연차/당직 없으면 전체
    @GetMapping("/user/event/list")
    public ResponseEntity<?> list(@RequestParam(required = false) @Pattern(regexp = "ANNUAL|DUTY") String eventType,
                                  @RequestParam(required = false) @Pattern(regexp = "^\\d{4}-\\d{2}$") String yearMonth,
                                  @AuthenticationPrincipal MyUserDetails myUserDetails) {
        ResponseDTO<?> responseDTO = new ResponseDTO<>(eventService.연차당직리스트(eventType, yearMonth, myUserDetails.getUser()));
        return ResponseEntity.ok(responseDTO);
    }
}
