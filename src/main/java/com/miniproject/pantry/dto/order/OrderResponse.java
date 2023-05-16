package com.miniproject.pantry.dto.order;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.miniproject.pantry.model.order.Order;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class OrderResponse {
	/**
	 * 연차승인
	 */
	@JsonSerialize
	@Getter
	@Setter
	@ToString
	public static class AnnualApprovalOutDTO{
		private Long eventId;
		private String userName;
		private String userEmail;
		private String userRole;
		private String eventType;
		private Long orderId;
		private LocalDate startDate;
		private LocalDate endDate;
		private String orderState;
		private String approvalUser;
		private String imgUrl;
		private String thumbnailUri; //썸네일 경로
		private LocalDateTime updatedAt;

		public AnnualApprovalOutDTO(Order order){
			this.eventId=order.getEvent().getId();
			this.userName=order.getEvent().getUser().getUsername();
			this.userEmail=order.getEvent().getUser().getEmail();
			this.userRole=order.getEvent().getUser().getRole().name();
			this.eventType=order.getEvent().getEventType().name();
			this.orderId= order.getId();
			this.startDate=order.getEvent().getAnnual().getStartDate();
			this.endDate=order.getEvent().getAnnual().getEndDate();
			this.orderState=order.getOrderState().toString();
			this.approvalUser=order.getApprover().getUsername();
			this.imgUrl=order.getEvent().getUser().getImageUri();
			this.thumbnailUri=order.getEvent().getUser().getThumbnailUri();
			this.updatedAt=order.getUpdatedAt();
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
		private String userName;
		private String userEmail;
		private String userRole;
		private String eventType;
		private Long orderId;
		private LocalDate date;
		private String orderState;
		private String approvalUser;
		private String imgUrl;
		private String thumbnailUri; //썸네일 경로
		private LocalDateTime updatedAt;

		public DutyApprovalOutDTO(Order order){
			this.eventId=order.getEvent().getId();
			this.userName=order.getEvent().getUser().getUsername();
			this.userEmail=order.getEvent().getUser().getEmail();
			this.userRole=order.getEvent().getUser().getRole().name();
			this.eventType=order.getEvent().getEventType().name();
			this.orderId= order.getId();
			this.date=order.getEvent().getDuty().getDate();
			this.orderState=order.getOrderState().toString();
			this.approvalUser=order.getApprover().getUsername();
			this.imgUrl=order.getEvent().getUser().getImageUri();
			this.thumbnailUri=order.getEvent().getUser().getThumbnailUri();
			this.updatedAt=order.getUpdatedAt();
		}
	}

	/**
	 * 연차요청
	 */
	@JsonSerialize
	@Getter
	@Setter
	public static class AnnualRequestOutDTO{
		private String userEmail;
		private Long eventId;
		private String userName;
		private String userRole;
		private String eventType;
		private Long orderId;
		private LocalDate startDate;
		private LocalDate endDate;
		private String orderState;
		private String imgUrl;
		private String thumbnailUri; //썸네일 경로
		private LocalDateTime createdAt;


		public AnnualRequestOutDTO(Order order){
			this.eventId=order.getEvent().getId();
			this.userName=order.getEvent().getUser().getUsername();
			this.userRole=order.getEvent().getUser().getRole().name();
			this.eventType=order.getEvent().getEventType().name();
			this.orderId= order.getId();
			this.startDate=order.getEvent().getAnnual().getStartDate();
			this.endDate=order.getEvent().getAnnual().getEndDate();
			this.orderState=order.getOrderState().toString();
			this.userEmail=order.getEvent().getUser().getEmail();
			this.imgUrl=order.getEvent().getUser().getImageUri();
			this.thumbnailUri=order.getEvent().getUser().getThumbnailUri();
			this.createdAt=order.getCreatedAt();
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
		private String userName;
		private String userEmail;
		private String userRole;
		private String eventType;
		private Long orderId;
		private LocalDate date;
		private String orderState;
		private String imgUrl;
		private String thumbnailUri; //썸네일 경로
		private LocalDateTime createdAt;


		public DutyRequestOutDTO(Order order){
			this.eventId=order.getEvent().getId();
			this.userName=order.getEvent().getUser().getUsername();
			this.userEmail=order.getEvent().getUser().getEmail();
			this.userRole=order.getEvent().getUser().getRole().name();
			this.eventType=order.getEvent().getEventType().name();
			this.orderId= order.getId();
			this.date=order.getEvent().getDuty().getDate();
			this.orderState=order.getOrderState().toString();
			this.imgUrl=order.getEvent().getUser().getImageUri();
			this.thumbnailUri=order.getEvent().getUser().getThumbnailUri();
			this.createdAt=order.getCreatedAt();
		}
	}




}
