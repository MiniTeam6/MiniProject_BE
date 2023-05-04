package shop.mtcoding.restend.dto.annual;

import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class AnnualRequest {

    @Getter
    public static class AnnualAddDto {

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate startDate;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate endDate;
    }
}
