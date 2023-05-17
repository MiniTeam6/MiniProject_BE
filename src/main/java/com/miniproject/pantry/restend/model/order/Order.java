package com.miniproject.pantry.restend.model.order;

import com.miniproject.pantry.restend.model.event.Event;
import com.miniproject.pantry.restend.model.user.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
@NamedEntityGraph(name = "Order.detail",
		attributeNodes = {
				@NamedAttributeNode(value = "event"),
				@NamedAttributeNode(value = "approver")
		})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "order_tb")
@Entity
public class Order {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "event_id")
	private Event event;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private OrderState orderState;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User approver; //승인자


	@Column(nullable = false)
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;



	public void setOrderState(OrderState updateOrderState){
		this.orderState = updateOrderState;
	}

	public void setApprover(User approver){
		this.approver = approver;
	}


	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}

	@Builder
	public Order(Long id, Event event, OrderState orderState, User approver, LocalDateTime createdAt) {
		this.id = id;
		this.event = event;
		this.orderState = orderState;
		this.approver = approver;
		this.createdAt = createdAt;
	}


}
