package shop.mtcoding.restend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.mtcoding.restend.dto.ResponseDTO;
import shop.mtcoding.restend.dto.user.UserRequest;
import shop.mtcoding.restend.dto.user.UserResponse;
import shop.mtcoding.restend.model.event.EventRepository;
import shop.mtcoding.restend.service.EventService;
import shop.mtcoding.restend.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

	private final UserService userService;
	private final EventService eventService;
	@PostMapping("/search")
	public ResponseEntity<?> userSearch(@RequestBody @Valid UserRequest.SearchInDTO searchInDTO){
		List<UserResponse.UserListOutDTO> userListOutDTO = userService.회원리스트검색(searchInDTO);
		ResponseDTO<?> responseDTO = new ResponseDTO<>(userListOutDTO);
		return ResponseEntity.ok(responseDTO);
	}

	@PostMapping("/role/update")
	public ResponseEntity<?> roleUpdate(@RequestBody @Valid UserRequest.RoleUpdateInDTO roleUpdateInDTO){
		UserResponse.DetailOutDTO detailOutDTO= userService.권한업데이트(roleUpdateInDTO);
		ResponseDTO<?>responseDTO = new ResponseDTO<>(detailOutDTO);
		return ResponseEntity.ok(responseDTO);
	}



}
