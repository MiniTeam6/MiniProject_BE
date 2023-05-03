package shop.mtcoding.restend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.mtcoding.restend.model.Event.EventRepository;
import shop.mtcoding.restend.model.Order.OrderRepository;

@Service
@RequiredArgsConstructor
public class OrderService {
	private OrderRepository orderRepository;
	private EventRepository eventRepository;
	
}
