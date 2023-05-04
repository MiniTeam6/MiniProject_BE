package shop.mtcoding.restend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.mtcoding.restend.model.event.EventRepository;
import shop.mtcoding.restend.model.order.OrderRepository;

@Service
@RequiredArgsConstructor
public class OrderService {
	private OrderRepository orderRepository;
	private EventRepository eventRepository;
	
}
