package shop.mtcoding.restend.model.Event;

import lombok.*;
import shop.mtcoding.restend.model.Annual.Annual;
import shop.mtcoding.restend.model.Duty.Duty;
import shop.mtcoding.restend.model.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "event_tb")
@Entity
public class Event {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;
	@Column(nullable = false)
	private EventType eventType;
	@OneToOne
	@JoinColumn(name="annual_id")
	private Annual annual;
	@OneToOne
	@JoinColumn(name="duty_id")
	private Duty duty;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}
}
