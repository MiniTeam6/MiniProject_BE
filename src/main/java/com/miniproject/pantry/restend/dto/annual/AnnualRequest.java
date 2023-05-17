package com.miniproject.pantry.restend.dto.annual;

import com.miniproject.pantry.restend.model.annual.Annual;
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

        public Annual toEntity(){
            return Annual.builder()
                        .startDate(startDate)
                        .endDate(endDate)
                        .build();
        }
    }
}
