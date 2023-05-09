//package shop.mtcoding.restend.model.order;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.context.annotation.Import;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.test.context.ActiveProfiles;
//import shop.mtcoding.restend.model.event.Event;
//import shop.mtcoding.restend.model.event.EventRepository;
//import shop.mtcoding.restend.model.event.EventType;
//import shop.mtcoding.restend.model.user.User;
//
//import javax.persistence.EntityManager;
//
//import static org.junit.jupiter.api.Assertions.*;
//@ActiveProfiles("test")
//@DataJpaTest
//@Import(BCryptPasswordEncoder.class)
//class OrderRepositoryTest {
//	@Autowired
//	private OrderRepository orderRepository;
//	@Autowired
//	private EventRepository eventRepository;
//
//
//	@Autowired
//	private EntityManager em;
//
//	@Autowired
//	private BCryptPasswordEncoder passwordEncoder;
//
//	public void setUp(){
//		// User 객체 생성
//		User user = User.builder()
//				.username("사르")
//				.password(passwordEncoder.encode("1234"))
//				.email("ssar@nate.com")
//				.role("USER")
//				.status(false)
//				.build();
//
//		// Event 객체 생성
//				Event event = Event.builder()
//						.user(user)
//						.eventType(EventType.ANNUAL)
//						.build();
//
//		// Order 객체 생성
//				Order order = Order.builder()
//						.event(event)
//						.orderState(OrderState.WAITING)
//						.build();
//
//	}
//}