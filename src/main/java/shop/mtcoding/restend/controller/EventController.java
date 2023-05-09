package shop.mtcoding.restend.controller;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import shop.mtcoding.restend.core.auth.session.MyUserDetails;
import shop.mtcoding.restend.dto.ResponseDTO;
import shop.mtcoding.restend.dto.event.EventRequest;
import shop.mtcoding.restend.dto.event.EventResponse;
import shop.mtcoding.restend.service.EventService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
        if (eventAddInDTO.getEventType().equals("ANNUAL") && eventAddInDTO.getCount() == null) {
            throw new NullPointerException("연차 신청시 연차일수를 입력해주세요.");
        }
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
        if (eventModifyInDTO.getEventType().equals("ANNUAL") && eventModifyInDTO.getCount() == null) {
            throw new NullPointerException("연차 신청시 연차일수를 입력해주세요.");
        }
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
    // 파리미터 없으면 현재 월
    // 파라미터로 탭 구분: 연차/당직
    @GetMapping("/user/event/list")
    public ResponseEntity<?> list(@RequestParam @Pattern(regexp = "ANNUAL|DUTY") String eventType,
                                  @RequestParam(required = false) @Pattern(regexp = "^\\d{4}-\\d{2}$") String yearMonth,
                                  @AuthenticationPrincipal MyUserDetails myUserDetails,
                                  @PageableDefault(size = 10) Pageable pageable) {
        if (yearMonth == null) {
            yearMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        }
        ResponseDTO<?> responseDTO = new ResponseDTO<>(eventService.연차당직리스트(eventType, yearMonth, myUserDetails.getUser(), pageable));
        return ResponseEntity.ok(responseDTO);
    }
}
