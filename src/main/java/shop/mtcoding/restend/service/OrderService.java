package shop.mtcoding.restend.service;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.mtcoding.restend.core.exception.Exception404;
import shop.mtcoding.restend.dto.order.OrderRequest;
import shop.mtcoding.restend.dto.order.OrderResponse;
import shop.mtcoding.restend.dto.user.UserResponse;
import shop.mtcoding.restend.model.event.Event;
import shop.mtcoding.restend.model.event.EventRepository;
import shop.mtcoding.restend.model.event.EventType;
import shop.mtcoding.restend.model.order.Order;
import shop.mtcoding.restend.model.order.OrderRepository;
import shop.mtcoding.restend.model.order.OrderState;
import shop.mtcoding.restend.model.user.User;
import shop.mtcoding.restend.model.user.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
	private final OrderRepository orderRepository;
	private final EventRepository eventRepository;
	private final UserRepository userRepository;
	@Transactional
	public OrderResponse.AnnualApprovalOutDTO 연차승인(Long approvalId, OrderRequest.ApprovalInDTO approvalInDTO){
		Optional<User> user = userRepository.findById(approvalId);
		if(user.isEmpty()){
			throw new Exception404( "해당 User를 찾을 수 없습니다. ");
		}
		Optional<Event> event = eventRepository.findById(approvalInDTO.getEventId());
		if(event.isEmpty()){
			throw new Exception404( "해당 Event를 찾을 수 없습니다. ");
		}

		Order approval = approvalInDTO.toEntity(user.get(),event.get());
		orderRepository.save(approval);
		return new OrderResponse.AnnualApprovalOutDTO(approval);

	}

	@Transactional
	public OrderResponse.DutyApprovalOutDTO 당직승인(Long approvalId, OrderRequest.ApprovalInDTO approvalInDTO){
		Optional<User> user = userRepository.findById(approvalId);
		if(user.isEmpty()){
			throw new Exception404( "해당 User를 찾을 수 없습니다. ");
		}
		Optional<Event> event = eventRepository.findById(approvalInDTO.getEventId());
		if(event.isEmpty()){
			throw new Exception404( "해당 Event를 찾을 수 없습니다. ");
		}

		Order approval = approvalInDTO.toEntity(user.get(),event.get());
		orderRepository.save(approval);
		return new OrderResponse.DutyApprovalOutDTO(approval);

	}

	public List<OrderResponse.AnnualRequestOutDTO> 연차요청내역(){
		List<Order> annualRequest = orderRepository.findByOrderStateAndEventType(OrderState.WAITING,EventType.ANNUAL);
		List<OrderResponse.AnnualRequestOutDTO> requestOutDTOS =
				annualRequest.stream().map(request->new OrderResponse.AnnualRequestOutDTO(request))
				.collect(Collectors.toList());
		return requestOutDTOS;
	}
	public List<OrderResponse.AnnualApprovalOutDTO> 연차승인내역(){
		List<Order> annualRequest = orderRepository.findByOrderStateNotAndEventType(OrderState.WAITING,EventType.ANNUAL);
		List<OrderResponse.AnnualApprovalOutDTO> approvalOutDTOS =
				annualRequest.stream().map(request->new OrderResponse.AnnualApprovalOutDTO(request))
						.collect(Collectors.toList());
		return approvalOutDTOS;
	}
	public List<OrderResponse.DutyRequestOutDTO> 당직요청내역(){
		List<Order> annualRequest = orderRepository.findByOrderStateAndEventType(OrderState.WAITING,EventType.DUTY);
		List<OrderResponse.DutyRequestOutDTO> approvalOutDTOS =
				annualRequest.stream().map(request->new OrderResponse.DutyRequestOutDTO(request))
						.collect(Collectors.toList());
		return approvalOutDTOS;
	}
	public List<OrderResponse.DutyApprovalOutDTO> 당직승인내역(){
		List<Order> annualRequest = orderRepository.findByOrderStateNotAndEventType(OrderState.WAITING,EventType.DUTY);
		List<OrderResponse.DutyApprovalOutDTO> requestOutDTOS =
				annualRequest.stream().map(request->new OrderResponse.DutyApprovalOutDTO(request))
						.collect(Collectors.toList());
		return requestOutDTOS ;
	}
}
