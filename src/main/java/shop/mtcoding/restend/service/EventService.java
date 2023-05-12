package shop.mtcoding.restend.service;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import shop.mtcoding.restend.core.exception.Exception404;
import shop.mtcoding.restend.core.exception.Exception500;
import shop.mtcoding.restend.dto.event.EventRequest;
import shop.mtcoding.restend.dto.order.OrderRequest;

import shop.mtcoding.restend.dto.event.EventRequest;
import shop.mtcoding.restend.dto.event.EventResponse;

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

import java.time.LocalDate;
import java.util.*;


import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final AnnualRepository annualRepository;
    private final DutyRepository dutyRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public void insertEvent(Long userid, EventRequest.EventAddDto eventAddDto) {
        Optional<User> user = userRepository.findById(userid);
        if (user.isEmpty()) {
            throw new Exception404("해당 User를 찾을 수 없습니다. ");
        }
        if (EventType.ANNUAL == EventType.valueOf(eventAddDto.getEventType())) {
            Annual annualToAdd = eventAddDto.annualToEntity();
            checkAnnualOverlap(user.get(), annualToAdd); // 연차 중복 체크
            Annual annual = annualRepository.save(annualToAdd);
            Event event = eventRepository.save(eventAddDto.annualToEventEntity(user.get(), annual));
            userRepository.save(user.get());
            Order order = Order.builder()
                    .event(event)
                    .orderState(OrderState.WAITING)
                    .approver(null)
                    .build();
            orderRepository.save(order);
        } else {
            Duty dutyToAdd = eventAddDto.dutyToEntity();
            checkDutyOverlap(user.get(), dutyToAdd); // 당직 중복 체크
            Duty duty = dutyRepository.save(eventAddDto.dutyToEntity());
            Event event = eventRepository.save(eventAddDto.dutyToEventEntity(user.get(), duty));
            Order order = Order.builder()
                    .event(event)
                    .orderState(OrderState.WAITING)
                    .approver(null)
                    .build();
            orderRepository.save(order);
        }
    }

    private void checkAnnualOverlap(User user, Annual annualToAdd) {
        List<Event> annualEvents = eventRepository.findAllByEventTypeAndUser(EventType.ANNUAL, user);
        boolean isOverlap = annualEvents.stream()
                .anyMatch(annualEvent -> {
                    LocalDate existingStartDate = annualEvent.getAnnual().getStartDate();
                    LocalDate existingEndDate = annualEvent.getAnnual().getEndDate();
                    LocalDate newStartDate = annualToAdd.getStartDate();
                    LocalDate newEndDate = annualToAdd.getEndDate();
                    return (newStartDate.isBefore(existingEndDate)) && (newEndDate.isAfter(existingStartDate)) ||
                            (newStartDate.isEqual(existingStartDate)) || (newEndDate.isEqual(existingEndDate)) ||
                            (newStartDate.isBefore(existingStartDate) && newEndDate.isAfter(existingEndDate));
                });
        if (isOverlap) {
            throw new Exception404("연차를 요청한 기간에 이미 연차신청 내역이 존재합니다. ");
        }
    }

    private void checkDutyOverlap(User user, Duty dutyToAdd) {
        List<Event> dutyEvents = eventRepository.findAllByEventTypeAndUser(EventType.DUTY, user);
        boolean isOverlap = dutyEvents.stream()
                .anyMatch(dutyEvent -> dutyEvent.getDuty().getDate().equals(dutyToAdd.getDate()));
        if (isOverlap) {
            throw new Exception404("당직을 요청한 날짜에 이미 당직신청 내역이 존재합니다. ");
        }
    }

    public EventResponse.EventAddOutDTO 연차당직신청(EventRequest.EventAddInDto eventAddInDto, User user) {
        Event event = null;
        switch (eventAddInDto.getEventType()) {
            case "ANNUAL":
                // 연차 신청
                if (eventAddInDto.getCount() == null) {
                    throw new Exception404("연차 신청시 연차 사용일수를 입력해주세요. ");
                }
                if (eventAddInDto.getCount() > user.getAnnualCount()) {
                    throw new Exception404("연차 신청시 연차 사용일수가 보유 연차일수보다 많습니다. ");
                }
                Annual annual = annualRepository.save(Annual.builder()
                        .startDate(eventAddInDto.getStartDate())
                        .endDate(eventAddInDto.getEndDate())
                        .count(eventAddInDto.getCount())
                        .build());
                event = eventRepository.save(Event.builder()
                        .user(user)
                        .eventType(EventType.ANNUAL)
                        .annual(annual)
                        .build());
                orderRepository.save(Order.builder()
                        .event(event)
                        .orderState(OrderState.WAITING)
                        .build());
                break;
            case "DUTY":
                // 당직 신청
                Duty duty = dutyRepository.save(Duty.builder()
                        .date(eventAddInDto.getStartDate())
                        .build());
                event = eventRepository.save(Event.builder()
                        .user(user)
                        .eventType(EventType.DUTY)
                        .duty(duty)
                        .build());
                orderRepository.save(Order.builder()
                        .event(event)
                        .orderState(OrderState.WAITING)
                        .build());
                break;
        }
        return EventResponse.EventAddOutDTO.builder()
                .eventId(event.getId())
                .userId(event.getUser().getId())
                .eventType(event.getEventType())
                .id(event.getEventType() == EventType.ANNUAL ? event.getAnnual().getId() : event.getDuty().getId())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .startDate(event.getEventType() == EventType.ANNUAL ? event.getAnnual().getStartDate() : event.getDuty().getDate())
                .endDate(event.getEventType() == EventType.ANNUAL ? event.getAnnual().getEndDate() : event.getDuty().getDate())
                .build();
    }

    public boolean 연차당직신청취소(EventRequest.EventCancelInDto eventCancelInDTO, User user) {
        Event event = null;
        Order order = null;
        switch (eventCancelInDTO.getEventType()) {
            case "ANNUAL":
                event = eventRepository.findById(eventCancelInDTO.getEventId()).orElseThrow(() -> new IllegalArgumentException("해당 이벤트가 없습니다."));
                if (!(Objects.equals(event.getUser().getId(), user.getId()))) {
                    throw new IllegalArgumentException("해당 이벤트에 대한 권한이 없습니다.");
                }
                order = orderRepository.findByEvent_Id(event.getId());
                if (order.getOrderState() == OrderState.WAITING) {
                    orderRepository.deleteById(order.getId());
                    Long annualId = event.getAnnual().getId();
                    eventRepository.deleteById(event.getId());
                    annualRepository.deleteById(annualId);
                } else {
                    throw new IllegalArgumentException("이미 처리된 이벤트입니다.");
                }
                break;
            case "DUTY":
                event = eventRepository.findById(eventCancelInDTO.getEventId()).orElseThrow(() -> new IllegalArgumentException("해당 이벤트가 없습니다."));
                if (!(Objects.equals(event.getUser().getId(), user.getId()))) {
                    throw new IllegalArgumentException("해당 이벤트에 대한 권한이 없습니다.");
                }
                order = orderRepository.findByEvent_Id(event.getId());
                if (order.getOrderState() == OrderState.WAITING) {
                    orderRepository.deleteById(order.getId());
                    Long dutyId = event.getDuty().getId();
                    eventRepository.deleteById(event.getId());
                    dutyRepository.deleteById(dutyId);
                } else {
                    throw new IllegalArgumentException("이미 처리된 이벤트입니다.");
                }
                break;
        }
        return true;
    }

    @Transactional
    public EventResponse.EventModifyOutDTO 연차당직신청수정(EventRequest.EventModifyInDto eventModifyInDTO, User user) {
        Event event = null;
        Order order = null;
        switch (eventModifyInDTO.getEventType()) {
            case "ANNUAL":
                if (eventModifyInDTO.getCount() == null) {
                    throw new Exception404("연차 신청시 연차 사용일수를 입력해주세요. ");
                }
                if (eventModifyInDTO.getCount() > user.getAnnualCount()) {
                    throw new Exception404("연차 신청시 연차 사용일수가 보유 연차일수보다 많습니다. ");
                }
                event = eventRepository.findById(eventModifyInDTO.getEventId()).orElseThrow(() -> new IllegalArgumentException("해당 이벤트가 없습니다."));
                if (!(Objects.equals(event.getUser().getId(), user.getId()))) {
                    throw new IllegalArgumentException("해당 이벤트에 대한 권한이 없습니다.");
                }
                order = orderRepository.findByEvent_Id(event.getId());
                if (order.getOrderState() == OrderState.WAITING) {
                    event.getAnnual().update(eventModifyInDTO.getStartDate(), eventModifyInDTO.getEndDate(), eventModifyInDTO.getCount());
                } else {
                    throw new IllegalArgumentException("이미 처리된 이벤트입니다.");
                }
                break;

            case "DUTY":
                event = eventRepository.findById(eventModifyInDTO.getEventId()).orElseThrow(() -> new IllegalArgumentException("해당 이벤트가 없습니다."));
                if (!(Objects.equals(event.getUser().getId(), user.getId()))) {
                    throw new IllegalArgumentException("해당 이벤트에 대한 권한이 없습니다.");
                }
                order = orderRepository.findByEvent_Id(event.getId());
                if (order.getOrderState() == OrderState.WAITING) {
                    event.getDuty().update(eventModifyInDTO.getStartDate());
                } else {
                    throw new IllegalArgumentException("이미 처리된 이벤트입니다.");
                }
                break;
        }
        return EventResponse.EventModifyOutDTO.builder()
                .eventType(event.getEventType())
                .eventId(event.getId())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .startDate(event.getEventType() == EventType.ANNUAL ? event.getAnnual().getStartDate() : event.getDuty().getDate())
                .endDate(event.getEventType() == EventType.ANNUAL ? event.getAnnual().getEndDate() : event.getDuty().getDate())
                .count(event.getEventType() == EventType.ANNUAL ? event.getAnnual().getCount() : null)
                .build();
    }

    // 이벤트 리스트
    public List<EventResponse.EventListOutDTO> 이벤트리스트() {
        List<Event> eventList = eventRepository.findAll();

        List<EventResponse.EventListOutDTO> results = new ArrayList<>();
        for (Event event : eventList) {
            EventResponse.EventListOutDTO eventListOutDTO = EventResponse.EventListOutDTO.builder()
                    .eventId(event.getId())
                    .userId(event.getUser().getId())
                    .userName(event.getUser().getUsername())
                    .userEmail(event.getUser().getEmail())
                    .userImageUri(event.getUser().getImageUri())
                    .userThumbnailUri(event.getUser().getThumbnailUri())
                    .eventType(event.getEventType())
                    .id(event.getEventType() == EventType.ANNUAL ? event.getAnnual().getId() : event.getDuty().getId())
                    .createdAt(event.getCreatedAt())
                    .updatedAt(event.getUpdatedAt())
                    .startDate(event.getEventType() == EventType.ANNUAL ? event.getAnnual().getStartDate() : event.getDuty().getDate())
                    .endDate(event.getEventType() == EventType.ANNUAL ? event.getAnnual().getEndDate() : event.getDuty().getDate())
                    .build();
            results.add(eventListOutDTO);
        }
        return results;
    }


    // 연차 리스트
    public List<EventResponse.EventListOutDTO> 연차리스트() {
        List<Event> eventList = eventRepository.findAll();
        List<Event> annualList = eventList.stream().filter(event -> event.getEventType() == EventType.ANNUAL).collect(Collectors.toList());

        List<EventResponse.EventListOutDTO> results = new ArrayList<>();
        for (Event event : annualList) {
            EventResponse.EventListOutDTO eventListOutDTO = EventResponse.EventListOutDTO.builder()
                    .eventId(event.getId())
                    .userId(event.getUser().getId())
                    .userName(event.getUser().getUsername())
                    .userEmail(event.getUser().getEmail())
                    .userImageUri(event.getUser().getImageUri())
                    .userThumbnailUri(event.getUser().getThumbnailUri())
                    .eventType(event.getEventType())
                    .id(event.getAnnual().getId())
                    .startDate(event.getAnnual().getStartDate())
                    .endDate(event.getAnnual().getEndDate())
                    .createdAt(event.getCreatedAt())
                    .updatedAt(event.getUpdatedAt())
                    .build();
            results.add(eventListOutDTO);
        }
        return results;
    }

    // 당직 리스트
    public List<EventResponse.EventListOutDTO> 당직리스트() {
        List<Event> eventList = eventRepository.findAll();
        List<Event> dutyList = eventList.stream().filter(event -> event.getEventType() == EventType.DUTY).collect(Collectors.toList());

        List<EventResponse.EventListOutDTO> results = new ArrayList<>();
        for (Event event : dutyList) {
            EventResponse.EventListOutDTO eventListOutDTO = EventResponse.EventListOutDTO.builder()
                    .eventId(event.getId())
                    .userId(event.getUser().getId())
                    .userName(event.getUser().getUsername())
                    .userEmail(event.getUser().getEmail())
                    .userImageUri(event.getUser().getImageUri())
                    .userThumbnailUri(event.getUser().getThumbnailUri())
                    .eventType(event.getEventType())
                    .id(event.getDuty().getId())
                    .startDate(event.getDuty().getDate())
                    .endDate(event.getDuty().getDate())
                    .createdAt(event.getCreatedAt())
                    .updatedAt(event.getUpdatedAt())
                    .build();
            results.add(eventListOutDTO);
        }
        return results;
    }

    // 연차당직 리스트 (메인)
    @Transactional
    public Slice<EventResponse.EventListOutDTO> 연차당직리스트(String eventType, String yearMonth, User user, Pageable pageable) {
        Slice<Order> orders = null;
        Slice<Event> events = null;

        if (eventType != null) {
            switch (eventType) {
                case "ANNUAL":
                    if (yearMonth != null) {
                        LocalDate start = LocalDate.parse(yearMonth + "-01");
                        LocalDate end = start.plusMonths(1).minusDays(1);
                        events = orderRepository.findByOrderStateAndEvent_EventTypeAndEvent_Annual_StartDateBetweenOrderByEvent_Annual_StartDateDesc(OrderState.APPROVED, EventType.ANNUAL, start, end, pageable).map(Order::getEvent);
                    } else {
                        events = orderRepository.findByOrderStateAndEvent_EventTypeOrderByEvent_Annual_StartDateDesc(OrderState.APPROVED, EventType.ANNUAL, pageable).map(Order::getEvent);
                    }
                    break;
                case "DUTY":
                    if (yearMonth != null) {
                        LocalDate start = LocalDate.parse(yearMonth + "-01");
                        LocalDate end = start.plusMonths(1).minusDays(1);
                        events = orderRepository.findByOrderStateAndEvent_EventTypeAndEvent_Duty_DateBetweenOrderByEvent_Duty_DateDesc(OrderState.APPROVED, EventType.DUTY, start, end, pageable).map(Order::getEvent);
                    } else {
                        events = orderRepository.findByOrderStateAndEvent_EventTypeOrderByEvent_Duty_DateDesc(OrderState.APPROVED, EventType.DUTY, pageable).map(Order::getEvent);
                    }
                    break;
            }
        } else {
            events = orderRepository.findByOrderState(OrderState.APPROVED, pageable).map(Order::getEvent);
            Map<Event, LocalDate> eventMap = new HashMap<>();

            if (yearMonth != null) {
                LocalDate start = LocalDate.parse(yearMonth + "-01");
                LocalDate end = start.plusMonths(1).minusDays(1);
                // 추가 로직 필요
                // map 으로 event_anuual 의 startdate 나 event_duty 의 date 를 가져와서
                // 기간을 비교하고 정렬
                for (Event event : events) {
                    if (event.getEventType() == EventType.ANNUAL) {
                        if (event.getAnnual().getStartDate().isAfter(start) && event.getAnnual().getStartDate().isBefore(end)) {
                            eventMap.put(event, event.getAnnual().getStartDate());
                        }
                    } else {
                        if (event.getDuty().getDate().isAfter(start) && event.getDuty().getDate().isBefore(end)) {
                            eventMap.put(event, event.getDuty().getDate());
                        }
                    }
                }

            } else {
                for (Event event : events) {
                    if (event.getEventType() == EventType.ANNUAL) {
                        eventMap.put(event, event.getAnnual().getStartDate());
                    } else {
                        eventMap.put(event, event.getDuty().getDate());
                    }
                }
            }

            List<Event> sortedEvents = eventMap.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            int startIdx = (int) pageable.getOffset();
            int endIdx = (startIdx + pageable.getPageSize()) > sortedEvents.size() ? sortedEvents.size() : (startIdx + pageable.getPageSize());
            events = new SliceImpl<>(sortedEvents.subList(startIdx, endIdx), pageable, sortedEvents.size() > endIdx);
        }

        return events.map(event -> EventResponse.EventListOutDTO.builder()
                .eventId(event.getId())
                .userId(event.getUser().getId())
                .userName(event.getUser().getUsername())
                .userEmail(event.getUser().getEmail())
                .userImageUri(event.getUser().getImageUri())
                .userThumbnailUri(event.getUser().getThumbnailUri())
                .eventType(event.getEventType())
                .startDate(event.getAnnual() != null ? event.getAnnual().getStartDate() : event.getDuty().getDate())
                .endDate(event.getAnnual() != null ? event.getAnnual().getEndDate() : event.getDuty().getDate())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build());
    }
}

