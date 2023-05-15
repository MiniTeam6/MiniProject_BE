package shop.mtcoding.restend.service;

import io.sentry.spring.tracing.SentrySpan;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.mtcoding.restend.core.exception.Exception400;
import shop.mtcoding.restend.core.exception.Exception404;
import shop.mtcoding.restend.dto.order.OrderRequest;
import shop.mtcoding.restend.dto.order.OrderResponse;
import shop.mtcoding.restend.dto.user.UserRequest;
import shop.mtcoding.restend.dto.user.UserResponse;
import shop.mtcoding.restend.model.event.Event;
import shop.mtcoding.restend.model.event.EventRepository;
import shop.mtcoding.restend.model.event.EventType;
import shop.mtcoding.restend.model.order.Order;
import shop.mtcoding.restend.model.order.OrderRepository;
import shop.mtcoding.restend.model.order.OrderState;
import shop.mtcoding.restend.model.user.User;
import shop.mtcoding.restend.model.user.UserRepository;

import java.util.*;
import java.util.stream.Collectors;
@SentrySpan
@Service
@RequiredArgsConstructor
public class OrderService {
	private final OrderRepository orderRepository;
	private final EventRepository eventRepository;
	private final UserRepository userRepository;

	@SentrySpan

	@Transactional
	public OrderResponse.AnnualApprovalOutDTO 연차승인(Long approvalId, OrderRequest.ApprovalInDTO approvalInDTO) {
		Optional<User> adminUser = userRepository.findById(approvalId);//승인자
		if (adminUser.isEmpty()) {
			throw new Exception400("승인 User 찾을 수 없음", "해당 유저를 찾을 수 없습니다",4);
		}
		Optional<Event> event = eventRepository.findById(approvalInDTO.getEventId());
		if (event.isEmpty()) {
			throw new Exception400("요청 Event 찾을 수 없음", "연차 신청내역을 찾을 수 없습니다",10);
		}
		Order approval = orderRepository.findByEvent_Id(event.get().getId());
		approval.setOrderState(OrderState.valueOf(approvalInDTO.getOrderState()));
		approval.setApprover(adminUser.get());
		orderRepository.save(approval);

		//==============================

		if(OrderState.APPROVED==OrderState.valueOf(approvalInDTO.getOrderState())){
			Optional<User> user = userRepository.findById(event.get().getUser().getId());
			if (user.isEmpty()) {
				throw new Exception400("요청 User 찾을 수 없음", "해당 유저를 찾을 수 없습니다",4);
			}
			user.get().verificationAnnualCount();
			user.get().setAnnualCount(event.get().getAnnual().getCount());
			userRepository.save(user.get());
		}

		return new OrderResponse.AnnualApprovalOutDTO(approval);
	}
	@SentrySpan
	@Transactional
	public OrderResponse.DutyApprovalOutDTO 당직승인(Long approvalId, OrderRequest.ApprovalInDTO approvalInDTO) {
		Optional<User> user = userRepository.findById(approvalId);
		if (user.isEmpty()) {
			throw new Exception400("승인 User 찾을 수 없음", "해당 유저를 찾을 수 없습니다",4);
		}
		Optional<Event> event = eventRepository.findById(approvalInDTO.getEventId());
		if (event.isEmpty()) {
			throw new Exception400("요청 Event 찾을 수 없음", "당직 신청내역을 찾을 수 없습니다",10);
		}

		Order approval = orderRepository.findByEvent_Id(event.get().getId());
		approval.setOrderState(OrderState.valueOf(approvalInDTO.getOrderState()));
		approval.setApprover(user.get());
		orderRepository.save(approval);
		return new OrderResponse.DutyApprovalOutDTO(approval);

	}
	@SentrySpan
	@Transactional
	public Page<OrderResponse.AnnualRequestOutDTO> 연차요청내역(int page, int size) {
		Pageable pageable = PageRequest.of(page, size,Sort.by(Sort.Direction.DESC, "event.annual.startDate"));
		Page<Order> annualRequests = orderRepository.findByOrderStateAndEventType(OrderState.WAITING, EventType.ANNUAL, pageable);
		return annualRequests.map(request -> new OrderResponse.AnnualRequestOutDTO(request));
	}

	@SentrySpan
	@Transactional
	public Page<OrderResponse.AnnualApprovalOutDTO> 연차승인내역(String type,String keyword, int page, int size) {
		Pageable pageable = PageRequest.of(page, size,Sort.by(Sort.Direction.DESC, "event.annual.startDate"));
		if(type.equals("username")){
			Page<Order> annualApprovals = orderRepository.findApprovalSearchUserName(OrderState.WAITING, EventType.ANNUAL, keyword, pageable);
			return annualApprovals.map(request -> new OrderResponse.AnnualApprovalOutDTO(request));
		}else {
			Page<Order> annualApprovals = orderRepository.findApprovalSearchEmail(OrderState.WAITING, EventType.ANNUAL, keyword, pageable);
			return annualApprovals.map(request -> new OrderResponse.AnnualApprovalOutDTO(request));
		}
	}

	@SentrySpan
	@Transactional
	public Page<OrderResponse.DutyRequestOutDTO> 당직요청내역(int page, int size) {
		Pageable pageable = PageRequest.of(page, size,Sort.by(Sort.Direction.DESC, "event.duty.date"));
		Page<Order> dutyRequest = orderRepository.findByOrderStateAndEventType(OrderState.WAITING, EventType.DUTY,pageable);
		return dutyRequest.map(request -> new OrderResponse.DutyRequestOutDTO(request));
	}

	@SentrySpan
	@Transactional
	public Page<OrderResponse.DutyApprovalOutDTO> 당직승인내역(String type, String keyword, int page, int size) {
		Pageable pageable = PageRequest.of(page, size,Sort.by(Sort.Direction.DESC, "event.duty.date"));
		if(type.equals("username")){
			Page<Order> dutyApproval = orderRepository.findApprovalSearchUserName(OrderState.WAITING, EventType.DUTY,keyword,pageable);
			return dutyApproval.map(request-> new OrderResponse.DutyApprovalOutDTO(request));
		}else {
			Page<Order> dutyApproval = orderRepository.findApprovalSearchEmail(OrderState.WAITING, EventType.DUTY,keyword,pageable);
			return dutyApproval.map(request-> new OrderResponse.DutyApprovalOutDTO(request));
		}

	}





}




