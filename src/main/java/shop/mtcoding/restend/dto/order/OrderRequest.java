package shop.mtcoding.restend.dto.order;

import lombok.Getter;
import lombok.Setter;
import shop.mtcoding.restend.model.event.Event;
import shop.mtcoding.restend.model.order.Order;
import shop.mtcoding.restend.model.order.OrderState;
import shop.mtcoding.restend.model.user.User;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class OrderRequest {
	@Getter @Setter
	public static class ApprovalInDTO{
		@NotNull
		private Long eventId;
		@Pattern(regexp = "APPROVED|REJECTED")
		@NotEmpty
		private String orderState;

	}

	@Getter @Setter
	public static class ApprovalWaitInDTO{



	}
}
