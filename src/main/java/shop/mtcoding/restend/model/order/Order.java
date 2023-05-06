package shop.mtcoding.restend.model.order;

import lombok.*;
import shop.mtcoding.restend.model.event.Event;
import shop.mtcoding.restend.model.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "order_tb")
@Entity
public class Order {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY)
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





	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
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
