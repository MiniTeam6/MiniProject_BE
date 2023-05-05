package shop.mtcoding.restend.model.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import shop.mtcoding.restend.model.event.EventType;

import java.util.List;
@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
	/**
	 * 대기중인 연차 조회 = 연차요청목록
	 * @param orderState
	 * @param eventType
	 * @return
	 */
	@Query("SELECT o FROM Order o WHERE o.orderState = :orderState AND o.event.eventType = :eventType")
	List<Order> findByOrderStateAndEventType(@Param("orderState") OrderState orderState, @Param("eventType") EventType eventType);

	/***
	 * 결재완료된 연차조회 = 요청중인(대기)연차 제외하고 불러옴
	 * @param orderState
	 * @param eventType
	 * @return
	 */
	@Query("SELECT o FROM Order o WHERE o.orderState <> :orderState AND o.event.eventType = :eventType")
	List<Order> findByOrderStateNotAndEventType(@Param("orderState") OrderState orderState, @Param("eventType") EventType eventType);
	/***
	 * 연차요청내역/승인내역 월별로 조회
	 * @param orderstate
	 * @param month
	 * @param year
	 * @return
	 */
	@Query("SELECT o FROM Order o JOIN o.event e JOIN e.annual a WHERE o.orderState = :orderState AND ((MONTH(a.startDate) <= :month AND MONTH(a.endDate) >= :month) OR (YEAR(a.startDate) < :year AND MONTH(a.endDate) = :month))")
	List<Order> findAnnualOrdersByMonthAndOrderstate(@Param("orderState") OrderState orderstate, @Param("month") int month, @Param("year") int year);

	/***
	 * 당직요청내역/승인내역 월별로 조회
	 * @param orderstate
	 * @param month
	 * @param year
	 * @return
	 */
	@Query("SELECT o FROM Order o JOIN o.event e JOIN e.duty d WHERE o.orderState = :orderState AND MONTH(d.date) = :month AND YEAR (d.date)= :year")
	List<Order> findDutyOrdersByMonthAndOrderstate(@Param("orderState") OrderState orderstate, @Param("month") int month, @Param("year") int year);


}
