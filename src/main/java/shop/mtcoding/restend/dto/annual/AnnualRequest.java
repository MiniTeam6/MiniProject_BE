package shop.mtcoding.restend.dto.annual;

import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;
import shop.mtcoding.restend.model.annual.Annual;

import java.time.LocalDate;

public class AnnualRequest {

    @Getter
    public static class AnnualAddDto {

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate startDate;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate endDate;

        public Annual toEntity(){
            return Annual.builder()
                        .startDate(startDate)
                        .endDate(endDate)
                        .build();
        }
    }
}
