package shop.mtcoding.restend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.multipart.MultipartFile;

import shop.mtcoding.restend.core.auth.jwt.MyJwtProvider;
import shop.mtcoding.restend.core.auth.session.MyUserDetails;
import shop.mtcoding.restend.core.exception.Exception400;
import shop.mtcoding.restend.core.exception.Exception401;
import shop.mtcoding.restend.core.exception.Exception404;
import shop.mtcoding.restend.core.exception.Exception500;
import shop.mtcoding.restend.dto.event.EventResponse;
import shop.mtcoding.restend.dto.user.UserRequest;
import shop.mtcoding.restend.dto.user.UserResponse;
import shop.mtcoding.restend.model.annual.Annual;
import shop.mtcoding.restend.model.annual.AnnualRepository;
import shop.mtcoding.restend.model.duty.Duty;
import shop.mtcoding.restend.model.duty.DutyRepository;
import shop.mtcoding.restend.model.event.Event;
import shop.mtcoding.restend.model.event.EventRepository;
import shop.mtcoding.restend.model.event.EventType;
import shop.mtcoding.restend.model.order.Order;
import shop.mtcoding.restend.model.order.OrderRepository;
import shop.mtcoding.restend.model.user.User;
import shop.mtcoding.restend.model.user.UserRepository;
import shop.mtcoding.restend.model.user.UserRole;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final AnnualRepository annualRepository;
    private final DutyRepository dutyRepository;
    private final EventRepository eventRepository;
    private final OrderRepository orderRepository;
    private final BCryptPasswordEncoder passwordEncoder;


    @Transactional
    public UserResponse.SignupOutDTO 회원가입(UserRequest.SignupInDTO signupInDTO, MultipartFile image){

        // 이미지 S3 에 저장
        // 썸네일 생성
        String imageUri = "imageUri";
        String thumbnailUri = "thumbnailUri";

        Optional<User> userOP =userRepository.findByEmail(signupInDTO.getEmail());
        if(userOP.isPresent()){
            // 이 부분이 try catch 안에 있으면 Exception500에게 제어권을 뺏긴다.
            throw new Exception400("email", "이메일이 존재합니다");
        }
        String encPassword = passwordEncoder.encode(signupInDTO.getPassword()); // 60Byte
        signupInDTO.setPassword(encPassword);
        System.out.println("encPassword : "+encPassword);

        // 디비 save 되는 쪽만 try catch로 처리하자.
        try {
            User userPS = userRepository.save(signupInDTO.toEntity(imageUri, thumbnailUri));
            return new UserResponse.SignupOutDTO(userPS);
        }catch (Exception e){
            throw new Exception500("회원가입 실패 : "+e.getMessage());
        }
    }


    public Object[] 로그인(UserRequest.LoginInDTO loginInDTO) {

        try {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                    = new UsernamePasswordAuthenticationToken(loginInDTO.getEmail(), loginInDTO.getPassword());
            Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
            MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();

            List<Event> events = eventRepository.findAllByUser(myUserDetails.getUser());
            List<Annual> annuals = annualRepository.findByIdIn(events.stream()
                    .filter(event -> event.getEventType().equals(EventType.ANNUAL))
                    .map(event -> event.getAnnual().getId())
                    .collect(Collectors.toList()));
            List<Duty> duties = dutyRepository.findByIdIn(events.stream()
                    .filter(event -> event.getEventType().equals(EventType.DUTY))
                    .map(event -> event.getDuty().getId())
                    .collect(Collectors.toList()));

            Object[] result = new Object[2];
            result[0] = new UserResponse.LoginOutDTO(myUserDetails.getUser());
            result[1] = MyJwtProvider.create(myUserDetails.getUser());
            return result;

        }catch (Exception e){
            throw new Exception401("인증되지 않았습니다");
        }
    }


    public UserResponse.UserDetailOutDTO 회원상세보기(Long id) {

        User userPS = userRepository.findById(id).orElseThrow(
                ()-> new Exception400("id", "해당 유저를 찾을 수 없습니다")
        );
        return new UserResponse.UserDetailOutDTO(userPS);
    }


    @Transactional
    public UserResponse.UserDetailOutDTO 회원정보수정(Long id, UserRequest.SignupInDTO signupInDTO, MultipartFile image) {
        User userPS = userRepository.findById(id).orElseThrow(
                ()-> new Exception400("id", "해당 유저를 찾을 수 없습니다")
        );

        // 이미지 변동 사항 파악
        // 변동 있으면 이미지 S3 에 저장
        // 썸네일 재생성

        String imageUri = "imageUri";
        String thumbnailUri = "thumbnailUri";

        userPS.update(signupInDTO.toEntity(imageUri, thumbnailUri));
        return new UserResponse.UserDetailOutDTO(userPS);
    }


    public List<UserResponse.UserListOutDTO> 회원리스트검색(UserRequest.SearchInDTO searchInDTO){
        if(searchInDTO.getSearchType().equals("name")){
            List<User> users = userRepository.findByUsernameContaining(searchInDTO.getKeyword());
            List<UserResponse.UserListOutDTO> userDTOs = users.stream()
                    .map(user->new UserResponse.UserListOutDTO(user))
                    .collect(Collectors.toList());
            return userDTOs;
        }else{
            List<User> users = userRepository.findByEmailContaining(searchInDTO.getKeyword());
            List<UserResponse.UserListOutDTO> userDTOs = users.stream()
                    .map(UserResponse.UserListOutDTO::new)
                    .collect(Collectors.toList());
            return userDTOs;
        }
    }

    public List<UserResponse.UserListOutDTO> 회원전체리스트(){
        List<User> users = userRepository.findUsersByStatus(true);
        List<UserResponse.UserListOutDTO> userDTOs = users.stream()
                .map(user -> new UserResponse.UserListOutDTO(user))
                .collect(Collectors.toList());
        return userDTOs;
    }
    @Transactional
    public UserResponse.UserDetailOutDTO 권한업데이트(UserRequest.RoleUpdateInDTO roleUpdateInDTO){
        Optional<User> user = userRepository.findByEmail(roleUpdateInDTO.getEmail());
        if(user.isEmpty()){
            throw new Exception404(roleUpdateInDTO.getEmail()+"  User를 찾을 수 없습니다. ");
        }

        user.get().setRole(UserRole.valueOf(roleUpdateInDTO.getRole()));
        try{
            User userPS=userRepository.save(user.get());
            return new UserResponse.UserDetailOutDTO(userPS);
        }catch (Exception e){
            throw new Exception500(e+roleUpdateInDTO.getEmail()+"유저권한 업데이트 실패");
        }
    }

    @Transactional
    public UserResponse.StatusUpdateOutDTO 회원가입승인(UserRequest.StatusUpdateInDTO statusUpdateInDTO){
        Optional<User> user = userRepository.findByEmail(statusUpdateInDTO.getEmail());
        if(user.isEmpty()){
            throw new Exception404(statusUpdateInDTO.getEmail()+"  User를 찾을 수 없습니다. ");
        }
        user.get().setStatus(true);
        try{
            User userPS=userRepository.save(user.get());
            return new UserResponse.StatusUpdateOutDTO(userPS);
        }catch (Exception e){
            throw new Exception500(e+statusUpdateInDTO.getEmail()+"유저권한 업데이트 실패");
        }
    }

    public List<UserResponse.UserListOutDTO> 회원가입요청목록(){
        List<User>users=userRepository.findUsersByStatus(false);
        List<UserResponse.UserListOutDTO> userDTOs = users.stream()
                .map(user->new UserResponse.UserListOutDTO(user))
                .collect(Collectors.toList());
        return userDTOs;
    }


    public Slice<EventResponse.EventListOutDTO> 내연차리스트(MyUserDetails myUserDetails, Pageable pageable) {
        User user = userRepository.findById(myUserDetails.getUser().getId()).orElseThrow(
                ()-> new Exception400("id", "해당 유저를 찾을 수 없습니다")
        );

        //Sort sort = pageable.getSort().and(Sort.by("startDate").descending());
        Pageable page = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());

        Slice<Event> events = eventRepository.findByUserAndEventTypeOrderByAnnual_StartDateDesc(user, EventType.ANNUAL, page);

        Slice<EventResponse.EventListOutDTO> myAnnuals = events.map(
                event -> {
                    User u = event.getUser();
                    Order order = orderRepository.findByEvent(event);
                    return EventResponse.EventListOutDTO.builder()
                            .eventId(event.getId())
                            .userId(u.getId())
                            .userUsername(u.getUsername())
                            .userEmail(u.getEmail())
                            .userImageUri(u.getImageUri())
                            .userThumbnailUri(u.getThumbnailUri())
                            .eventType(event.getEventType())
                            .id(event.getAnnual().getId())
                            .startDate(event.getAnnual().getStartDate())
                            .endDate(event.getAnnual().getEndDate())
                            .createdAt(event.getCreatedAt())
                            .updatedAt(event.getUpdatedAt())
                            .orderOrderState(order.getOrderState())
                            .build();
                });

        return myAnnuals;
    }


    public Slice<EventResponse.EventListOutDTO> 내당직리스트(MyUserDetails myUserDetails, Pageable pageable) {
        User user = userRepository.findById(myUserDetails.getUser().getId()).orElseThrow(
                ()-> new Exception400("id", "해당 유저를 찾을 수 없습니다")
        );

        //Sort sort = pageable.getSort().and(Sort.by("startDate").descending());
        Pageable page = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());

        Slice<Event> events = eventRepository.findByUserAndEventTypeOrderByDuty_DateDesc(user, EventType.DUTY, page);

        // User 객체 만들어서 넣어주기
        Slice<EventResponse.EventListOutDTO> myDutys = events.map(
                event -> {
                    User u = event.getUser();
                    Order order = orderRepository.findByEvent(event);
                    return EventResponse.EventListOutDTO.builder()
                            .eventId(event.getId())
                            .userId(u.getId())
                            .userUsername(u.getUsername())
                            .userEmail(u.getEmail())
                            .userImageUri(u.getImageUri())
                            .userThumbnailUri(u.getThumbnailUri())
                            .eventType(event.getEventType())
                            .id(event.getDuty().getId())
                            .startDate(event.getDuty().getDate())
                            .endDate(event.getDuty().getDate())
                            .createdAt(event.getCreatedAt())
                            .updatedAt(event.getUpdatedAt())
                            .orderOrderState(order.getOrderState())
                            .build();
                });


        return myDutys;
    }
}
