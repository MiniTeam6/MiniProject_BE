package shop.mtcoding.restend.service;

import io.sentry.spring.tracing.SentrySpan;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.*;
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
import shop.mtcoding.restend.dto.order.OrderResponse;
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
import shop.mtcoding.restend.model.order.OrderState;
import shop.mtcoding.restend.model.user.User;
import shop.mtcoding.restend.model.user.UserRepository;
import shop.mtcoding.restend.model.user.UserRole;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
    private final S3Service s3Service;


    @SentrySpan
    @Transactional
    public UserResponse.SignupOutDTO 회원가입(UserRequest.SignupInDTO signupInDTO, MultipartFile image) throws IOException {

        // 이미지 S3 에 저장
        // 썸네일 생성
        String imageUri = s3Service.uploadImage(image);
        String thumbnailUri = s3Service.uploadThumbnail(image, imageUri);

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

    @SentrySpan
    public Boolean 이메일중복확인(String email) {
        Optional<User> userOP = userRepository.findByEmail(email);
        return userOP.isPresent();
    }


    @SentrySpan
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


    @SentrySpan
    public UserResponse.UserDetailOutDTO 회원상세보기(Long id) {

        User userPS = userRepository.findById(id).orElseThrow(
                ()-> new Exception400("id", "해당 유저를 찾을 수 없습니다")
        );
        return new UserResponse.UserDetailOutDTO(userPS);
    }

    @SentrySpan
    @Transactional
    public UserResponse.UserDetailOutDTO 회원정보수정(Long id, UserRequest.ModifyInDTO modifyInDTO, MultipartFile image) throws IOException {
        User userPS = userRepository.findById(id).orElseThrow(
                ()-> new Exception400("id", "해당 유저를 찾을 수 없습니다")
        );

        // 이미지 변동 사항 파악
        // 변동 있으면 이미지 S3 에 저장
        // 썸네일 재생성

        String imageUri;
        String thumbnailUri;

        if (image == null) {
            imageUri = userPS.getImageUri();
            thumbnailUri = userPS.getThumbnailUri();
        } else {
            // S3 에서 작업 필요
            imageUri = s3Service.updateImage(userPS.getImageUri(), userPS.getThumbnailUri(), image);
            thumbnailUri = s3Service.uploadThumbnail(image, imageUri);
        }

        userPS.update(modifyInDTO.toEntity(userPS.getEmail(), imageUri, thumbnailUri));
        return new UserResponse.UserDetailOutDTO(userPS);
    }

    /***
     *
     * @param type
     * @param keyword
     * @param page
     * @param size
     * @return
     */

    @SentrySpan
    public Page<UserResponse.UserListOutDTO> 회원리스트검색(String type,String keyword,int page, int size){
        Pageable pageable = PageRequest.of(page, size,Sort.by(Sort.Direction.DESC, "username"));
        if(type.equals("username")){
            Page<User> users = userRepository.findByUsernameContainingAndStatusTrue(keyword, pageable);
            return users.map(request-> new UserResponse.UserListOutDTO(request));
        }else{
            Page<User> users = userRepository.findByEmailContainingAndStatusTrue(keyword,pageable);
            return users.map(request-> new UserResponse.UserListOutDTO(request));
        }
    }


    @SentrySpan
    @Transactional
    public Page<UserResponse.UserApprovalListOutDTO> 회원전체리스트(int page, int size){
        Pageable pageable = PageRequest.of(page, size,Sort.by(Sort.Direction.ASC, "username"));
        Page<User> users = userRepository.findUsersByStatus(true,pageable);
        return users.map(request-> new UserResponse.UserApprovalListOutDTO(request));
    }

    @SentrySpan
    @Transactional
    public Page<UserResponse.UserApprovalListOutDTO> 회원전체리스트가입일정렬(int page, int size){
        Pageable pageable = PageRequest.of(page, size,Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<User> users = userRepository.findUsersByStatus(true,pageable);
        return users.map(request-> new UserResponse.UserApprovalListOutDTO(request));
    }

    @SentrySpan
    @Transactional
    public List<UserResponse.UserListOutDTO> 회원전체리스트2(){
        List<User> users = userRepository.findUsersByStatus2(true);
        List<UserResponse.UserListOutDTO> userDTOs = users.stream()
                .map(user -> new UserResponse.UserListOutDTO(user))
                .collect(Collectors.toList());
        return userDTOs;
    }

    @SentrySpan
    @Transactional
    public UserResponse.UserRoleUpdateOutDTO 권한업데이트(UserRequest.RoleUpdateInDTO roleUpdateInDTO){
        Optional<User> user = userRepository.findByEmail(roleUpdateInDTO.getEmail());
        if(user.isEmpty()){
            throw new Exception404(roleUpdateInDTO.getEmail()+"  User를 찾을 수 없습니다. ");
        }

        user.get().setRole(UserRole.valueOf(roleUpdateInDTO.getRole()));
        try{
            User userPS=userRepository.save(user.get());
            return new UserResponse.UserRoleUpdateOutDTO(userPS);
        }catch (Exception e){
            throw new Exception500(e+roleUpdateInDTO.getEmail()+"유저권한 업데이트 실패");
        }
    }

    @SentrySpan
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


    @SentrySpan
    public Page<UserResponse.UserListOutDTO> 회원가입요청목록(int page, int size){
        Pageable pageable = PageRequest.of(page, size,Sort.by(Sort.Direction.ASC, "username"));
        Page<User>users=userRepository.findUsersByStatus(false,pageable);
        return users.map(request-> new UserResponse.UserListOutDTO(request));
    }

    @SentrySpan
    public Slice<EventResponse.MyEventListOutDTO> 내연차리스트(MyUserDetails myUserDetails, Pageable pageable) {
        User user = userRepository.findById(myUserDetails.getUser().getId()).orElseThrow(
                ()-> new Exception400("id", "해당 유저를 찾을 수 없습니다")
        );

        //Sort sort = pageable.getSort().and(Sort.by("startDate").descending());
        Pageable page = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());

        Slice<Event> events = eventRepository.findByUserAndEventTypeOrderByAnnual_StartDateDesc(user, EventType.ANNUAL, page);

        Slice<EventResponse.MyEventListOutDTO> myAnnuals = events.map(
                event -> {
                    User u = event.getUser();
                    Order order = orderRepository.findByEvent(event);
                    return EventResponse.MyEventListOutDTO.builder()
                            .eventId(event.getId())
                            .eventType(event.getEventType())
                            .startDate(event.getAnnual().getStartDate())
                            .endDate(event.getAnnual().getEndDate())
                            .createdAt(event.getCreatedAt())
                            .updatedAt(event.getUpdatedAt())
                            .orderState(order.getOrderState())
                            .build();
                });

        return myAnnuals;
    }

    @SentrySpan
    public Slice<EventResponse.MyEventListOutDTO> 내당직리스트(MyUserDetails myUserDetails, Pageable pageable) {
        User user = userRepository.findById(myUserDetails.getUser().getId()).orElseThrow(
                ()-> new Exception400("id", "해당 유저를 찾을 수 없습니다")
        );

        //Sort sort = pageable.getSort().and(Sort.by("startDate").descending());
        Pageable page = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());

        Slice<Event> events = eventRepository.findByUserAndEventTypeOrderByDuty_DateDesc(user, EventType.DUTY, page);

        // User 객체 만들어서 넣어주기
        Slice<EventResponse.MyEventListOutDTO> myDuties = events.map(
                event -> {
                    User u = event.getUser();
                    Order order = orderRepository.findByEvent(event);
                    return EventResponse.MyEventListOutDTO.builder()
                            .eventId(event.getId())
                            .eventType(event.getEventType())
                            .startDate(event.getDuty().getDate())
                            .endDate(event.getDuty().getDate())
                            .createdAt(event.getCreatedAt())
                            .updatedAt(event.getUpdatedAt())
                            .orderState(order.getOrderState())
                            .build();
                });
        return myDuties;
    }

    @SentrySpan
    public EventResponse.NextEventDTO 가장빠른연차당직(MyUserDetails myUserDetails) {
        User user = userRepository.findById(myUserDetails.getUser().getId()).orElseThrow(
                ()-> new Exception400("id", "해당 유저를 찾을 수 없습니다")
        );

        List<Order> orders = orderRepository.findByOrderState(OrderState.APPROVED);
        List<LocalDate> annualDate = orders.stream().filter(order ->
                order.getEvent().getEventType().equals(EventType.ANNUAL)).map(order ->
                order.getEvent().getAnnual().getStartDate()).filter(date -> date.isAfter(LocalDate.now()) | date.isEqual(LocalDate.now())).collect(Collectors.toList());
        LocalDate nextAnnualDate = annualDate.stream().min(LocalDate::compareTo).orElse(null);
        Long annualDDay = nextAnnualDate == null ? null : ChronoUnit.DAYS.between(LocalDate.now(), nextAnnualDate);

        List<LocalDate> dutyDate = orders.stream().filter(order ->
                order.getEvent().getEventType().equals(EventType.DUTY)).map(order ->
                order.getEvent().getDuty().getDate()).filter(date -> date.isAfter(LocalDate.now()) | date.isEqual(LocalDate.now())).collect(Collectors.toList());
        LocalDate nextDutyDate = dutyDate.stream().min(LocalDate::compareTo).orElse(null);
        Long dutyDDay = nextDutyDate == null ? null : ChronoUnit.DAYS.between(LocalDate.now(), nextDutyDate);

        return EventResponse.NextEventDTO.builder()
                .nextAnnualDate(nextAnnualDate)
                .annualDDay(annualDDay)
                .nextDutyDate(nextDutyDate)
                .dutyDDay(dutyDDay)
                .build();
    }
}
