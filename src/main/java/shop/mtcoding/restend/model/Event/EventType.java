package shop.mtcoding.restend.model.Event;

public enum EventType {
	DUTY("당직"),
	ANNUAL("연차");

	private final String type;

	EventType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
