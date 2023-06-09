package com.miniproject.pantry.model.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderState {
	WAITING("대기"),
	APPROVED("승인"),
	REJECTED("반려");

	private final String state;

}

