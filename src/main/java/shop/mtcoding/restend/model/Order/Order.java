package shop.mtcoding.restend.model.Order;

import lombok.*;
import shop.mtcoding.restend.model.Event.Event;
import shop.mtcoding.restend.model.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
@Builder
@AllArgsConstructor
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

}