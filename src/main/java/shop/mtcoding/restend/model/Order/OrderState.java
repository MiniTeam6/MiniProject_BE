package shop.mtcoding.restend.model.Order;

public enum OrderState {
	WAITING("대기"),
	APPROVED("승인"),
	REJECTED("반려");

	private final String state;

	OrderState(String state) {
		this.state = state;
	}

	public String getState() {
		return state;
	}
}

