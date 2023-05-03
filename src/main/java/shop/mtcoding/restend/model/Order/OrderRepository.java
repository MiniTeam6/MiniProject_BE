package shop.mtcoding.restend.model.Order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.mtcoding.restend.model.Event.EventType;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Long> {
	@Query("SELECT o FROM Order o WHERE o.orderState = :orderState AND o.event.eventType = :eventType")
	List<Order> findByOrderStateAndEventType(@Param("orderState") OrderState orderState, @Param("eventType") EventType eventType);
}
