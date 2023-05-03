package shop.mtcoding.restend.model.Duty;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "duty_tb")
@Entity
public class Duty {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false)
	private LocalDate startDate;
	@Column(nullable = false)
	private LocalDate endDate;
}
