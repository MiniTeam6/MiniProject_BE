package shop.mtcoding.restend.model.annual;

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

	@Builder
	public Annual(Long id, LocalDate startDate, LocalDate endDate) {
		this.id = id;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public void update(LocalDate startDate, LocalDate endDate) {
		this.startDate = startDate;
		this.endDate = endDate;
	}
}
