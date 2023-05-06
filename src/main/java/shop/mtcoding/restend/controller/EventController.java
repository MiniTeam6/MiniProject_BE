package shop.mtcoding.restend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import shop.mtcoding.restend.core.auth.session.MyUserDetails;
import shop.mtcoding.restend.dto.ResponseDTO;
import shop.mtcoding.restend.dto.event.EventRequest;
import shop.mtcoding.restend.dto.event.EventResponse;
import shop.mtcoding.restend.service.EventService;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class EventController {
    private final EventService eventService;

    // 연차/당직 신청
    @PostMapping("/user/event/add")
    public ResponseEntity<?> add(@RequestBody @Valid EventRequest.EventAddInDto eventAddInDTO, @AuthenticationPrincipal MyUserDetails myUserDetails) {
        EventResponse.EventAddOutDTO eventAddOutDTO = eventService.연차당직신청(eventAddInDTO, myUserDetails.getUser());
        ResponseDTO<?> responseDTO = new ResponseDTO<>(eventAddOutDTO);
        return ResponseEntity.ok(responseDTO);
    }

    // 연차/당직 신청 취소
    @PostMapping("/user/event/cancel")
    public ResponseEntity<?> cancel(@RequestBody @Valid EventRequest.EventCancelInDto eventCancelInDTO, @AuthenticationPrincipal MyUserDetails myUserDetails) {
        ResponseDTO<?> responseDTO = new ResponseDTO<>(eventService.연차당직신청취소(eventCancelInDTO, myUserDetails.getUser()));
        return ResponseEntity.ok(responseDTO);
    }

    // 연차/당직 신청 수정
    @PostMapping("/user/event/modify")
    public ResponseEntity<?> modify(@RequestBody @Valid EventRequest.EventModifyInDto eventModifyInDTO, @AuthenticationPrincipal MyUserDetails myUserDetails) {
        ResponseDTO<?> responseDTO = new ResponseDTO<>(eventService.연차당직신청수정(eventModifyInDTO, myUserDetails.getUser()));
        return ResponseEntity.ok(responseDTO);
    }

    // 모든 이벤트 리스트
    @GetMapping("user/event")
    public ResponseEntity<?> list(@AuthenticationPrincipal MyUserDetails myUserDetails) {
        ResponseDTO<?> responseDTO = new ResponseDTO<>(eventService.이벤트리스트());
        return ResponseEntity.ok(responseDTO);
    }


    // 모든 연차 리스트
    @GetMapping("user/event/annual")
    public ResponseEntity<?> annual(@AuthenticationPrincipal MyUserDetails myUserDetails) {
        ResponseDTO<?> responseDTO = new ResponseDTO<>(eventService.연차리스트());
        return ResponseEntity.ok(responseDTO);
    }

    // 모든 당직 리스트
    @GetMapping("user/event/duty")
    public ResponseEntity<?> duty(@AuthenticationPrincipal MyUserDetails myUserDetails) {
        ResponseDTO<?> responseDTO = new ResponseDTO<>(eventService.당직리스트());
        return ResponseEntity.ok(responseDTO);
    }

}