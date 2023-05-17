package com.miniproject.pantry.restend.model.annual;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "annual_tb")
@Entity
public class Annual {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false)
	private LocalDate startDate;
	@Column(nullable = false)
	private LocalDate endDate;

	@Column(nullable = false)
	private Long count;

	@Builder
	public Annual(Long id, LocalDate startDate, LocalDate endDate, Long count) {
		this.id = id;
		this.startDate = startDate;
		this.endDate = endDate;
		this.count = count;
	}

	public void update(LocalDate startDate, LocalDate endDate, Long count) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.count = count;
	}

}
