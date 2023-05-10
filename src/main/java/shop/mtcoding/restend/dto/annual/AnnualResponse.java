package shop.mtcoding.restend.dto.annual;

import lombok.Getter;

public class AnnualResponse {
    @Getter
    public static class AnnualListOutDTO {
        private Long id;
        private String startDate;
        private String endDate;
    }
}
