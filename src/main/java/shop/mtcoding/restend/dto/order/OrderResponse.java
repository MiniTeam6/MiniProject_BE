package shop.mtcoding.restend.dto.order;

import shop.mtcoding.restend.model.order.Order;

import java.time.LocalDate;

public class OrderResponse {
	public static class AnnualApprovalOutDTO{
		private Long eventId;
		private Long userId;
		private String eventType;
		private Long id;
		private LocalDate startDate;
		private LocalDate endDate;
		private String orderState;

		public AnnualApprovalOutDTO(Order order){
			this.eventId=order.getEvent().getId();
			this.userId=order.getEvent().getUser().getId();
			this.eventType=order.getEvent().getEventType().toString();
			this.id= order.getId();
			this.startDate=order.getEvent().getAnnual().getStartDate();
			this.endDate=order.getEvent().getAnnual().getEndDate();
			this.orderState=order.getOrderState().toString();
		}
	}

	public static class DutyApprovalOutDTO{
		private Long eventId;
		private Long userId;
		private String eventType;
		private Long id;
		private LocalDate date;
		private String orderState;

		public DutyApprovalOutDTO(Order order){
			this.eventId=order.getEvent().getId();
			this.userId=order.getEvent().getUser().getId();
			this.eventType=order.getEvent().getEventType().toString();
			this.id= order.getId();
			this.date=order.getEvent().getDuty().getDate();
			this.orderState=order.getOrderState().toString();
		}
	}
}
