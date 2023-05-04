package shop.mtcoding.restend.service;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.mtcoding.restend.core.exception.Exception404;
import shop.mtcoding.restend.dto.order.OrderRequest;
import shop.mtcoding.restend.dto.order.OrderResponse;
import shop.mtcoding.restend.model.event.Event;
import shop.mtcoding.restend.model.event.EventRepository;
import shop.mtcoding.restend.model.order.Order;
import shop.mtcoding.restend.model.order.OrderRepository;
import shop.mtcoding.restend.model.user.User;
import shop.mtcoding.restend.model.user.UserRepository;

import java.util.Optional;

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
}
