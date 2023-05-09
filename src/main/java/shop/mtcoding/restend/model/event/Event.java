package shop.mtcoding.restend.model.event;

import lombok.*;
import shop.mtcoding.restend.model.annual.Annual;
import shop.mtcoding.restend.model.duty.Duty;
import shop.mtcoding.restend.model.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "event_tb")
@Entity
@ToString
public class Event {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
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

	@Builder
	public Event(Long id, User user, EventType eventType, Annual annual, Duty duty, LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.id = id;
		this.user = user;
		this.eventType = eventType;
		this.annual = annual;
		this.duty = duty;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
}
