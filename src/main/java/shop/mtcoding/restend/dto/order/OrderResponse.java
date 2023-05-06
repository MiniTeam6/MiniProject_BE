package shop.mtcoding.restend.dto.order;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import shop.mtcoding.restend.model.order.Order;

import java.time.LocalDate;

public class OrderResponse {
	/**
	 * 연차승인
	 */
	@JsonSerialize
	@Getter
	@Setter
	public static class AnnualApprovalOutDTO{
		private Long eventId;
		private Long userId;
		private String eventType;
		private Long id;
		private LocalDate startDate;
		private LocalDate endDate;
		private String orderState;
		private String approvalUser;

		public AnnualApprovalOutDTO(Order order){
			this.eventId=order.getEvent().getId();
			this.userId=order.getEvent().getUser().getId();
			this.eventType=order.getEvent().getEventType().toString();
			this.id= order.getId();
			this.startDate=order.getEvent().getAnnual().getStartDate();
			this.endDate=order.getEvent().getAnnual().getEndDate();
			this.orderState=order.getOrderState().toString();
			this.approvalUser=order.getApprover().getUsername();
		}
	}

	/**
	 * 당직승인
	 */
	@JsonSerialize
	@Getter
	@Setter
	public static class DutyApprovalOutDTO{
		private Long eventId;
		private Long userId;
		private String eventType;
		private Long id;
		private LocalDate date;
		private String orderState;
		private String approvalUser;

		public DutyApprovalOutDTO(Order order){
			this.eventId=order.getEvent().getId();
			this.userId=order.getEvent().getUser().getId();
			this.eventType=order.getEvent().getEventType().toString();
			this.id= order.getId();
			this.date=order.getEvent().getDuty().getDate();
			this.orderState=order.getOrderState().toString();
			this.approvalUser=order.getApprover().getUsername();
		}
	}

	/**
	 * 연차요청
	 */
	@JsonSerialize
	@Getter
	@Setter
	public static class AnnualRequestOutDTO{
		private Long eventId;
		private Long userId;
		private String eventType;
		private Long id;
		private LocalDate startDate;
		private LocalDate endDate;
		private String orderState;


		public AnnualRequestOutDTO(Order order){
			this.eventId=order.getEvent().getId();
			this.userId=order.getEvent().getUser().getId();
			this.eventType=order.getEvent().getEventType().toString();
			this.id= order.getId();
			this.startDate=order.getEvent().getAnnual().getStartDate();
			this.endDate=order.getEvent().getAnnual().getEndDate();
			this.orderState=order.getOrderState().toString();
		}
	}

	/**
	 * 당직요청
	 */
	@JsonSerialize
	@Getter
	@Setter
	public static class DutyRequestOutDTO{
		private Long eventId;
		private Long userId;
		private String eventType;
		private Long id;
		private LocalDate date;
		private String orderState;


		public DutyRequestOutDTO(Order order){
			this.eventId=order.getEvent().getId();
			this.userId=order.getEvent().getUser().getId();
			this.eventType=order.getEvent().getEventType().toString();
			this.id= order.getId();
			this.date=order.getEvent().getDuty().getDate();
			this.orderState=order.getOrderState().toString();
		}
	}




}