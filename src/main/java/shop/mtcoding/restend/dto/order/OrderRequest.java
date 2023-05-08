package shop.mtcoding.restend.dto.order;

import lombok.Getter;
import lombok.Setter;
import shop.mtcoding.restend.model.event.Event;
import shop.mtcoding.restend.model.order.Order;
import shop.mtcoding.restend.model.order.OrderState;
import shop.mtcoding.restend.model.user.User;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

public class OrderRequest {
	@Getter @Setter
	public static class ApprovalInDTO{
		@NotEmpty
		private Long eventId;
		@Pattern(regexp = "APPROVED|REJECTED")
		@NotEmpty
		private String orderState;

		public Order toEntity(User approval, Event event){
			return Order.builder()
						.event(event)
						.approver(approval)
						.orderState(OrderState.valueOf(orderState))
						.build();
		}
	}

	@Getter @Setter
	public static class ApprovalWaitInDTO{



	}
}
