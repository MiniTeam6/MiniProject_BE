package com.miniproject.pantry.restend.model.duty;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "duty_tb")
@Entity
public class Duty {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false)
	private LocalDate date;

	@Builder
	public Duty(Long id, LocalDate date) {
		this.id = id;
		this.date = date;
	}

	public void update(LocalDate date) {
		this.date = date;
	}
}
