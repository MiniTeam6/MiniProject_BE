package shop.mtcoding.restend.model.Order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.mtcoding.restend.model.Event.EventType;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Long> {
	@Query("SELECT o FROM Order o WHERE o.orderState = :orderState AND o.event.eventType = :eventType")
	List<Order> findByOrderStateAndEventType(@Param("orderState") OrderState orderState, @Param("eventType") EventType eventType);

	/***
	 * 연차요청내역/승인내역 월별로 조회
	 * @param orderstate
	 * @param month
	 * @param year
	 * @return
	 */
	@Query("SELECT o FROM Order o JOIN o.event e JOIN e.annual a WHERE o.orderState = :orderstate AND ((MONTH(a.startDate) <= :month AND MONTH(a.endDate) >= :month) OR (YEAR(a.startDate) < :year AND MONTH(a.endDate) = :month))")
	List<Order> findAnnualOrdersByMonthAndOrderstate(@Param("orderState") OrderState orderstate, @Param("month") int month, @Param("year") int year);

	/***
	 * 당직요청내역/승인내역 월별로 조회
	 * @param orderstate
	 * @param month
	 * @param year
	 * @return
	 */
	@Query("SELECT o FROM Order o JOIN o.event e JOIN e.duty d WHERE o.orderState = :orderstate AND MONTH(d.date) = :month AND YEAR (d.date)= :year")
	List<Order> findDutyOrdersByMonthAndOrderstate(@Param("orderstate") OrderState orderstate, @Param("month") int month, @Param("year") int year);


}
