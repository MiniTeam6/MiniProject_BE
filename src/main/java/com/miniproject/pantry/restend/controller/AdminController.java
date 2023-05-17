package com.miniproject.pantry.restend.controller;

import com.miniproject.pantry.restend.service.OrderService;
import com.miniproject.pantry.restend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import com.miniproject.pantry.restend.core.auth.session.MyUserDetails;
import com.miniproject.pantry.restend.dto.ResponseDTO;
import com.miniproject.pantry.restend.dto.order.OrderRequest;
import com.miniproject.pantry.restend.dto.order.OrderResponse;
import com.miniproject.pantry.restend.dto.user.UserRequest;
import com.miniproject.pantry.restend.dto.user.UserResponse;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;

@Log4j2
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

	private final UserService userService;
	private final OrderService orderService;

	/***
	 * 회원리스트 검색(role)
	 * @param type
	 * @param keyword
	 * @param page
	 * @param size
	 * @return
	 */
	@GetMapping("/search")
	public ResponseEntity<?> userSearch(@RequestParam(name="type",required = false, defaultValue = "username")@Pattern(regexp = "username|email")
											String type,
										@RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
										@RequestParam(name = "page", defaultValue = "0") int page,
										@RequestParam(name = "size", defaultValue = "8") int size) {
		Page<UserResponse.UserListOutDTO> userListOutDTO = userService.회원리스트검색(type,keyword,page,size);
		ResponseDTO<?> responseDTO = new ResponseDTO<>(userListOutDTO);
		return ResponseEntity.ok(responseDTO);
	}

	/**
	 * 회원권한 수정
	 *
	 * @param roleUpdateInDTO
	 * @return
	 */
	@PostMapping("/role/update")
	public ResponseEntity<?> roleUpdate(@RequestBody @Valid UserRequest.RoleUpdateInDTO roleUpdateInDTO, Errors errors) {
		UserResponse.UserRoleUpdateOutDTO userDetailOutDTO = userService.권한업데이트(roleUpdateInDTO);
		ResponseDTO<?> responseDTO = new ResponseDTO<>(userDetailOutDTO);
		return ResponseEntity.ok(responseDTO);
	}

	/**
	 * 회원가입요청 승인
	 *
	 * @param statusUpdateInDTO
	 * @return
	 */
	@PostMapping("/status")
	public ResponseEntity<?> statusUpdate(@RequestBody @Valid UserRequest.StatusUpdateInDTO statusUpdateInDTO, Errors errors) {
		UserResponse.StatusUpdateOutDTO statusUpdateOutDTO = userService.회원가입승인(statusUpdateInDTO);
		ResponseDTO<?> responseDTO = new ResponseDTO<>(statusUpdateOutDTO);
		return ResponseEntity.ok(responseDTO);
	}

	/***
	 * 회원가입 요청 목록(status->false) list
	 * @return
	 */
	@GetMapping("/signup/list")
	public ResponseEntity<?> signupList(@RequestParam(name = "page", defaultValue = "0") int page,
										@RequestParam(name = "size", defaultValue = "8") int size) {

		Page<UserResponse.UserListOutDTO> userListOutDTOS = userService.회원가입요청목록(page,size);
		ResponseDTO<?> responseDTO = new ResponseDTO<>(userListOutDTOS);
		return ResponseEntity.ok(responseDTO);
	}

	/***
	 * 가입된 회원 목록(status->true) list
	 * @return
	 */
	@GetMapping("/role/list")
	public ResponseEntity<?> roleList(@RequestParam(name = "page", defaultValue = "0") int page,
									  @RequestParam(name = "size", defaultValue = "8") int size) {
		Page<UserResponse.UserApprovalListOutDTO> userListOutDTOS = userService.회원전체리스트(page,size);
		ResponseDTO<?> responseDTO = new ResponseDTO<>(userListOutDTOS);
		return ResponseEntity.ok(responseDTO);
	}
	@GetMapping("/role/list/sort")
	public ResponseEntity<?> roleListSort(@RequestParam(name = "page", defaultValue = "0") int page,
									  @RequestParam(name = "size", defaultValue = "8") int size) {
		Page<UserResponse.UserApprovalListOutDTO> userListOutDTOS = userService.회원전체리스트가입일정렬(page,size);
		ResponseDTO<?> responseDTO = new ResponseDTO<>(userListOutDTOS);
		return ResponseEntity.ok(responseDTO);
	}



	/**
	 * 연차 결재 (승인/반려)처리
	 *
	 * @param approvalInDTO
	 * @param myUserDetails
	 * @return
	 */
	@PostMapping("/annual/order")
	public ResponseEntity<?> annualOrder(@RequestBody @Valid OrderRequest.ApprovalInDTO approvalInDTO, @AuthenticationPrincipal MyUserDetails myUserDetails) {
		OrderResponse.AnnualApprovalOutDTO annualApprovalOutDTO = orderService.연차승인(myUserDetails.getUser().getId(), approvalInDTO);
		ResponseDTO<?> responseDTO = new ResponseDTO<>(annualApprovalOutDTO);
		return ResponseEntity.ok(responseDTO);
	}

	/**
	 * 당직 결재(승인/반려)처리
	 *
	 * @param approvalInDTO
	 * @param myUserDetails
	 * @return
	 */
	@PostMapping("/duty/order")
	public ResponseEntity<?> dutyOrder(@RequestBody @Valid OrderRequest.ApprovalInDTO approvalInDTO, @AuthenticationPrincipal MyUserDetails myUserDetails) {
		OrderResponse.DutyApprovalOutDTO dutyApprovalOutDTO = orderService.당직승인(myUserDetails.getUser().getId(), approvalInDTO);
		ResponseDTO<?> responseDTO = new ResponseDTO<>(dutyApprovalOutDTO);
		return ResponseEntity.ok(responseDTO);
	}

	/**
	 * 연차 요청리스트
	 * @return
	 */
	@GetMapping("/annual/request")
	public ResponseEntity<?> annualRequest(@RequestParam(name = "page", defaultValue = "0") int page,
										   @RequestParam(name = "size", defaultValue = "8") int size) {
		Page<OrderResponse.AnnualRequestOutDTO> annualRequestOutDTOS = orderService.연차요청내역(page,size);
		ResponseDTO<?> responseDTO = new ResponseDTO<>(annualRequestOutDTOS);
		return ResponseEntity.ok(responseDTO);
	}


	/**
	 * 당직 요청리스트
	 * @return
	 */
	@GetMapping("/duty/request")
	public ResponseEntity<?> dutyRequest(@RequestParam(name = "page", defaultValue = "0") int page,
										 @RequestParam(name = "size", defaultValue = "8") int size) {
		Page<OrderResponse.DutyRequestOutDTO> dutyRequestOutDTOS = orderService.당직요청내역(page,size);
		ResponseDTO<?> responseDTO = new ResponseDTO<>(dutyRequestOutDTOS);
		return ResponseEntity.ok(responseDTO);
	}

	/***
	 * 연차 승인리스트
	 * @return
	 */
	@GetMapping("/annual/approval")
	public ResponseEntity<?> annualApproval(@RequestParam(name="type",required = false, defaultValue = "username")@Pattern(regexp = "username|email")
											String type,
											@RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
											@RequestParam(name = "page", defaultValue = "0") int page,
											@RequestParam(name = "size", defaultValue = "8") int size,
											HttpSession session) {
		Integer count =(Integer) session.getAttribute("count");
		Page<OrderResponse.AnnualApprovalOutDTO> annualApprovalOutDTOS = orderService.연차승인내역(type,keyword, page, size);
		ResponseDTO<?> responseDTO = new ResponseDTO<>(annualApprovalOutDTOS);
		return ResponseEntity.ok(responseDTO);
	}

	/***
	 * 당직 승인리스트
	 * @return
	 */
	@GetMapping("/duty/approval")
	public ResponseEntity<?> dutyApproval(@RequestParam(name="type",required = false, defaultValue = "username")@Pattern(regexp = "username|email") String type,
										  @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
										  @RequestParam(name = "page", defaultValue = "0") int page,
										  @RequestParam(name = "size", defaultValue = "8") int size) {
		Page<OrderResponse.DutyApprovalOutDTO> dutyApprovalOutDTOS = orderService.당직승인내역(type,keyword, page, size);
		ResponseDTO<?> responseDTO = new ResponseDTO<>(dutyApprovalOutDTOS);
		return ResponseEntity.ok(responseDTO);
	}


}
