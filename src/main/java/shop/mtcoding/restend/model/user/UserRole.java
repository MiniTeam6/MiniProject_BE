package shop.mtcoding.restend.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRole {
	ADMIN("ADMIN"),USER("USER");

	private final String description;

}
