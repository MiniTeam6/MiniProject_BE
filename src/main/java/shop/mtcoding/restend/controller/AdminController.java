package shop.mtcoding.restend.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import shop.mtcoding.restend.core.auth.session.MyUserDetails;
import shop.mtcoding.restend.dto.ResponseDTO;
import shop.mtcoding.restend.dto.order.OrderRequest;
import shop.mtcoding.restend.dto.order.OrderResponse;
import shop.mtcoding.restend.dto.user.UserRequest;
import shop.mtcoding.restend.dto.user.UserResponse;
import shop.mtcoding.restend.model.event.EventRepository;
import shop.mtcoding.restend.model.user.User;
import shop.mtcoding.restend.service.EventService;
import shop.mtcoding.restend.service.OrderService;
import shop.mtcoding.restend.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

	private final UserService userService;
	private final OrderService orderService;

	/**
	 * 회원리스트 검색 -> 이름, 이메일
	 * @param searchInDTO
	 * @return
	 */
	@PostMapping("/search")
	public ResponseEntity<?> userSearch(@RequestBody @Valid UserRequest.SearchInDTO searchInDTO){
		List<UserResponse.UserListOutDTO> userListOutDTO = userService.회원리스트검색(searchInDTO);
		ResponseDTO<?> responseDTO = new ResponseDTO<>(userListOutDTO);
		return ResponseEntity.ok(responseDTO);
	}

	/**
	 * 회원권한 수정
	 * @param roleUpdateInDTO
	 * @return
	 */
	@PostMapping("/role/update")
	public ResponseEntity<?> roleUpdate(@RequestBody @Valid UserRequest.RoleUpdateInDTO roleUpdateInDTO){
		UserResponse.DetailOutDTO detailOutDTO= userService.권한업데이트(roleUpdateInDTO);
		ResponseDTO<?>responseDTO = new ResponseDTO<>(detailOutDTO);
		return ResponseEntity.ok(responseDTO);
	}

	/**
	 * 회원가입요청 승인
	 * @param statusUpdateInDTO
	 * @return
	 */
	@PostMapping("/status")
	public ResponseEntity<?> statusUpdate(@RequestBody @Valid UserRequest.StatusUpdateInDTO statusUpdateInDTO){
		UserResponse.StatusUpdateOutDTO statusUpdateOutDTO = userService.회원가입승인(statusUpdateInDTO);
		ResponseDTO<?>responseDTO = new ResponseDTO<>(statusUpdateOutDTO);
		return ResponseEntity.ok(responseDTO);
	}

	/***
	 * 회원가입 요청 목록(status->false)
	 * @return
	 */
	@GetMapping("/signup/list")
	public ResponseEntity<?>signupList (){
		List<UserResponse.UserListOutDTO>userListOutDTOS=userService.회원가입요청목록();
		ResponseDTO<?>responseDTO=new ResponseDTO<>(userListOutDTOS);
		return ResponseEntity.ok(responseDTO);
	}

	/***
	 * 가입된 회원 목록(status->true)
	 * @return
	 */

	@GetMapping("/role/list")
	public ResponseEntity<?>roleList(){
		List<UserResponse.UserListOutDTO>userListOutDTOS=userService.회원전체리스트();
		ResponseDTO<?>responseDTO=new ResponseDTO<>(userListOutDTOS);
		return ResponseEntity.ok(responseDTO);
	}

	/**
	 * 연차 결재 (승인/반려)처리
	 * @param approvalInDTO
	 * @param myUserDetails
	 * @return
	 */
	@PostMapping("/annual/order")
	public ResponseEntity<?>annualOrder(@RequestBody OrderRequest.ApprovalInDTO approvalInDTO, @AuthenticationPrincipal MyUserDetails myUserDetails){
		OrderResponse.AnnualApprovalOutDTO annualApprovalOutDTO=orderService.연차승인(myUserDetails.getUser().getId(), approvalInDTO);
		ResponseDTO<?> responseDTO = new ResponseDTO<>(annualApprovalOutDTO);
		return ResponseEntity.ok(responseDTO);
	}

	/**
	 * 당직 결재(승인/반려)처리
	 * @param approvalInDTO
	 * @param myUserDetails
	 * @return
	 */
	@PostMapping("/duty/order")
	public ResponseEntity<?> dutyOrder(@RequestBody OrderRequest.ApprovalInDTO approvalInDTO, @AuthenticationPrincipal MyUserDetails myUserDetails){
		OrderResponse.DutyApprovalOutDTO dutyApprovalOutDTO=orderService.당직승인(myUserDetails.getUser().getId(), approvalInDTO);
		ResponseDTO<?> responseDTO = new ResponseDTO<>(dutyApprovalOutDTO);
		return ResponseEntity.ok(responseDTO);
	}

	/**
	 * 연차 요청리스트
	 * @return
	 */
	@GetMapping("/annual/request")
	public ResponseEntity<?> annualRequest(){
		List<OrderResponse.AnnualRequestOutDTO> annualRequestOutDTOS = orderService.연차요청내역();
		ResponseDTO<?> responseDTO = new ResponseDTO<>(annualRequestOutDTOS);
		return ResponseEntity.ok(responseDTO);
	}


	/**
	 * 당직 요청리스트
	 * @return
	 */
	@GetMapping("/duty/request")
	public ResponseEntity<?> dutyRequest(){
		List<OrderResponse.DutyRequestOutDTO> dutyRequestOutDTOS = orderService.당직요청내역();
		ResponseDTO<?> responseDTO = new ResponseDTO<>(dutyRequestOutDTOS);
		return ResponseEntity.ok(responseDTO);
	}

	/***
	 * 연차 승인리스트
	 * @return
	 */
	@GetMapping("/annual/approval")
	public ResponseEntity<?> annualApproval(){
		List<OrderResponse.AnnualApprovalOutDTO> annualApprovalOutDTOS = orderService.연차승인내역();
		ResponseDTO<?> responseDTO = new ResponseDTO<>(annualApprovalOutDTOS);
		return ResponseEntity.ok(responseDTO);
	}

	/***
	 * 당직 승인리스트
	 * @return
	 */
	@GetMapping("/duty/approval")
	public ResponseEntity<?> dutyApproval(){
		List<OrderResponse.DutyApprovalOutDTO> dutyApprovalOutDTOS = orderService.당직승인내역();
		ResponseDTO<?>responseDTO = new ResponseDTO<>(dutyApprovalOutDTOS);
		return ResponseEntity.ok(responseDTO);
	}








}
