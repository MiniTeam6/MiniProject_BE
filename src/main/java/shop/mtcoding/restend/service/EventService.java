package shop.mtcoding.restend.service;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final AnnualRepository annualRepository;
    private final DutyRepository dutyRepository;
    private final OrderRepository orderRepository;


    public EventResponse.EventAddOutDTO 연차당직신청(EventRequest.EventAddInDto eventAddInDto, User user) {
        Event event = null;
        switch (eventAddInDto.getEventType()) {
            case "연차":
                // 연차 신청
                Annual annual = annualRepository.save(Annual.builder()
                        .startDate(eventAddInDto.getStartDate())
                        .endDate(eventAddInDto.getEndDate())
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
            case "당직":
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
            case "연차":
                Annual annual = annualRepository.findById(eventCancelInDTO.getId()).orElseThrow(() -> new IllegalArgumentException("해당 연차가 없습니다."));
                event = eventRepository.findByAnnual_Id(annual.getId());
                if (!(Objects.equals(event.getUser().getId(), user.getId()))) {
                    throw new IllegalArgumentException("해당 이벤트에 대한 권한이 없습니다.");
                }
                order = orderRepository.findByEvent_Id(event.getId());
                if (order.getOrderState() == OrderState.WAITING) {
                    orderRepository.deleteById(order.getId());
                    eventRepository.deleteById(event.getId());
                    annualRepository.deleteById(eventCancelInDTO.getId());
                } else {
                    throw new IllegalArgumentException("이미 처리된 이벤트입니다.");
                }
                break;
            case "당직":
                Duty duty = dutyRepository.findById(eventCancelInDTO.getId()).orElseThrow(() -> new IllegalArgumentException("해당 당직이 없습니다."));
                event = eventRepository.findByDuty_Id(duty.getId());
                if (!(Objects.equals(event.getUser().getId(), user.getId()))) {
                    throw new IllegalArgumentException("해당 이벤트에 대한 권한이 없습니다.");
                }
                order = orderRepository.findByEvent_Id(event.getId());
                if (order.getOrderState() == OrderState.WAITING) {
                    orderRepository.deleteById(order.getId());
                    eventRepository.deleteById(event.getId());
                    dutyRepository.deleteById(eventCancelInDTO.getId());
                } else {
                    throw new IllegalArgumentException("이미 처리된 이벤트입니다.");
                }
                break;
        }
        return true;
    }

    public EventResponse.EventModifyOutDTO 연차당직신청수정(EventRequest.EventModifyInDto eventModifyInDTO, User user) {
        Event event = null;
        Order order = null;
        switch (eventModifyInDTO.getEventType()) {
            case "연차":
                Annual annual = annualRepository.findById(eventModifyInDTO.getId()).orElseThrow(() -> new IllegalArgumentException("해당 연차가 없습니다."));
                event = eventRepository.findByAnnual_Id(annual.getId());
                if (!(Objects.equals(event.getUser().getId(), user.getId()))) {
                    throw new IllegalArgumentException("해당 이벤트에 대한 권한이 없습니다.");
                }
                order = orderRepository.findByEvent_Id(event.getId());
                if (order.getOrderState() == OrderState.WAITING) {
                    annual.update(eventModifyInDTO.getStartDate(), eventModifyInDTO.getEndDate());
                } else {
                    throw new IllegalArgumentException("이미 처리된 이벤트입니다.");
                }
                break;

            case "당직":
                Duty duty = dutyRepository.findById(eventModifyInDTO.getId()).orElseThrow(() -> new IllegalArgumentException("해당 당직이 없습니다."));
                event = eventRepository.findByDuty_Id(duty.getId());
                if (!(Objects.equals(event.getUser().getId(), user.getId()))) {
                    throw new IllegalArgumentException("해당 이벤트에 대한 권한이 없습니다.");
                }
                order = orderRepository.findByEvent_Id(event.getId());
                if (order.getOrderState() == OrderState.WAITING) {
                    duty.update(eventModifyInDTO.getStartDate());
                } else {
                    throw new IllegalArgumentException("이미 처리된 이벤트입니다.");
                }
                break;
        }
        return EventResponse.EventModifyOutDTO.builder()
                .eventType(event.getEventType())
                .id(event.getEventType() == EventType.ANNUAL ? event.getAnnual().getId() : event.getDuty().getId())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .startDate(event.getEventType() == EventType.ANNUAL ? event.getAnnual().getStartDate() : event.getDuty().getDate())
                .endDate(event.getEventType() == EventType.ANNUAL ? event.getAnnual().getEndDate() : event.getDuty().getDate())
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
                    .eventType(event.getEventType())
                    .id(event.getAnnual().getId())
                    .createdAt(event.getCreatedAt())
                    .updatedAt(event.getUpdatedAt())
                    .startDate(event.getAnnual().getStartDate())
                    .endDate(event.getAnnual().getEndDate())
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
                    .eventType(event.getEventType())
                    .id(event.getDuty().getId())
                    .createdAt(event.getCreatedAt())
                    .updatedAt(event.getUpdatedAt())
                    .startDate(event.getDuty().getDate())
                    .endDate(event.getDuty().getDate())
                    .build();
            results.add(eventListOutDTO);
        }
        return results;
    }

    public Slice<EventResponse.EventListOutDTO> 연차당직리스트(String eventType, Boolean myEvent, User user, Pageable pageable) {
        Slice<Event> events = null;
        Slice<EventResponse.EventListOutDTO> results = null;
        switch (eventType) {
            case "연차":
                if (myEvent) {
                    events = eventRepository.findByUserAndEventTypeOrderByAnnual_StartDateDesc(user, EventType.ANNUAL, pageable);
                } else {
                    events = eventRepository.findByEventTypeOrderByAnnual_StartDateDesc(EventType.ANNUAL, pageable);
                }
                results = events.map(event -> EventResponse.EventListOutDTO.builder()
                        .eventId(event.getId())
                        .userId(event.getUser().getId())
                        .eventType(event.getEventType())
                        .id(event.getAnnual().getId())
                        .startDate(event.getAnnual().getStartDate())
                        .endDate(event.getAnnual().getEndDate())
                        .createdAt(event.getCreatedAt())
                        .updatedAt(event.getUpdatedAt())
                        .build());
                break;

            case "당직":
                if (myEvent) {
                    events = eventRepository.findByUserAndEventTypeOrderByDuty_DateDesc(user, EventType.DUTY, pageable);
                } else {
                    events = eventRepository.findByEventTypeOrderByDuty_DateDesc(EventType.DUTY, pageable);
                }
                results = events.map(event -> EventResponse.EventListOutDTO.builder()
                        .eventId(event.getId())
                        .userId(event.getUser().getId())
                        .eventType(event.getEventType())
                        .id(event.getDuty().getId())
                        .startDate(event.getDuty().getDate())
                        .endDate(event.getDuty().getDate())
                        .createdAt(event.getCreatedAt())
                        .updatedAt(event.getUpdatedAt())
                        .build());
                break;
        }
        return results;
    }
}
