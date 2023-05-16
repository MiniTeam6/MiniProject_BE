package com.miniproject.pantry.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EventType {
	DUTY("당직"),
	ANNUAL("연차");

	private final String type;

}
