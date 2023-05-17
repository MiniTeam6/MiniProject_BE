package com.miniproject.pantry.model.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.miniproject.pantry.model.event.Event;
import com.miniproject.pantry.model.event.EventType;
import com.miniproject.pantry.model.user.User;

import java.time.LocalDate;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Long> {
	/**
	 * 대기중인 연차 조회 = 연차요청목록
	 * @param orderState
	 * @param eventType
	 * @return
	 *
	 */
//	@Query("SELECT o FROM Order o JOIN FETCH o.event WHERE o.orderState = :orderState AND o.event.eventType = :eventType")
	@EntityGraph(attributePaths="event")
	@Query("SELECT o FROM Order o JOIN o.event WHERE o.orderState = :orderState AND o.event.eventType = :eventType")
	Page<Order> findByOrderStateAndEventType(@Param("orderState") OrderState orderState, @Param("eventType") EventType eventType, Pageable pageable);


//	@Query("SELECT o FROM Order o JOIN FETCH o.event WHERE o.orderState = :orderState AND o.event.eventType = :eventType")
//	Page<Order> findByOrderStateAndEventType(@Param("orderState") OrderState orderState, @Param("eventType") EventType eventType,Pageable pageable);


//	/***
//	 * 결재완료된 연차조회 = 요청중인(대기)연차 제외하고 불러옴
//	 * @param orderState
//	 * @param eventType
//	 * @return
//	 */
//	@EntityGraph(value = "Order.detail", type = EntityGraph.EntityGraphType.LOAD)
//	@Query("SELECT o FROM Order o WHERE o.orderState <> :orderState AND o.event.eventType = :eventType ")
//	List<Order> findByOrderStateNotAndEventType(@Param("orderState") OrderState orderState, @Param("eventType") EventType eventType);

	/***
	 * 결재완료된 연차조회 = 요청중인(대기)연차 제외하고 불러옴
	 * @param orderState
	 * @param eventType
	 * @param keyword
	 * @return
	 *
	 *
	 */
	@Query("SELECT o FROM Order o WHERE o.orderState <> :orderState AND o.event.eventType = :eventType AND o.event.user.username LIKE %:keyword%   " )
	Page<Order> findApprovalSearchUserName(@Param("orderState") OrderState orderState, @Param("eventType") EventType eventType, @Param("keyword") String keyword, Pageable pageable);

	@Query("SELECT o FROM Order o WHERE o.orderState <> :orderState AND o.event.eventType = :eventType AND o.event.user.email LIKE %:keyword%   " )
	Page<Order> findApprovalSearchEmail(@Param("orderState") OrderState orderState, @Param("eventType") EventType eventType, @Param("keyword") String keyword, Pageable pageable);



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


    Order findByEvent_Id(Long eventId);

	Order findByEvent(Event event);

	List<Order> findByOrderState(OrderState orderState);

	List<Order> findByOrderStateAndEvent_User(OrderState orderState, User user);

	Slice<Order> findByOrderState(OrderState orderState, Pageable pageable);


	@Query("SELECT o FROM Order o WHERE o.event.id IN :eventIds AND o.orderState <> :orderState")
	List<Order> findOrdersByEventIdsAndOrderStateNot(@Param("eventIds") List<Long> eventIds, @Param("orderState") OrderState orderState);
	
	List<Order> findByOrderStateAndEvent_EventTypeOrderByEvent_Annual_StartDateDesc(OrderState approved, EventType annual);

	List<Order> findByOrderStateAndEvent_EventTypeOrderByEvent_Duty_DateDesc(OrderState approved, EventType duty);

	List<Order> findByOrderStateAndEvent_EventTypeAndEvent_Annual_StartDateBetweenOrderByEvent_Annual_StartDateDesc(OrderState approved, EventType annual, LocalDate start, LocalDate end);

	List<Order> findByOrderStateAndEvent_EventTypeAndEvent_Duty_DateBetweenOrderByEvent_Duty_DateDesc(OrderState approved, EventType duty, LocalDate start, LocalDate end);
}

