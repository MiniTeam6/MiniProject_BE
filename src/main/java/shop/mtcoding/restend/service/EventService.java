package shop.mtcoding.restend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.mtcoding.restend.core.exception.Exception404;
import shop.mtcoding.restend.core.exception.Exception500;
import shop.mtcoding.restend.dto.event.EventRequest;
import shop.mtcoding.restend.dto.order.OrderRequest;
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
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final AnnualRepository annualRepository;
    private final DutyRepository dutyRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public void insertEvent(Long userid, EventRequest.EventAddDto eventAddDto){
        Optional<User> user = userRepository.findById(userid);
        if(user.isEmpty()){
            throw new Exception404( "해당 User를 찾을 수 없습니다. ");
        }
        if (EventType.ANNUAL == EventType.valueOf(eventAddDto.getEventType())) {
            Annual annualToAdd = eventAddDto.annualToEntity();
            List<Event> annualEvents = eventRepository.findAllByEventTypeAndUser(EventType.ANNUAL, user.get());
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
            else {
                    Annual annual = annualRepository.save(annualToAdd);
                    Event event = eventRepository.save(eventAddDto.annualToEventEntity(user.get(), annual));
                    user.get().setAnnualCount(); //연차개수 마이너스
                    userRepository.save(user.get());
                    Order order = Order.builder()
                            .event(event)
                            .orderState(OrderState.WAITING)
                            .approver(null)
                            .build();
                    orderRepository.save(order);
                }
            }
        else{
            Duty dutyToAdd = eventAddDto.dutyToEntity();
            List<Event> dutyEvents = eventRepository.findAllByEventTypeAndUser(EventType.DUTY, user.get());
            boolean isOverlap = dutyEvents.stream()
                    .anyMatch(annualEvent -> {
                        LocalDate existingDate = annualEvent.getDuty().getDate();
                        LocalDate newDate = dutyToAdd.getDate();
                        return existingDate.equals(newDate);
                    });
            if (isOverlap) {
                throw new Exception404("당직을 요청한 날짜에 이미 당직신청 내역이 존재합니다. ");
            }
            else{
                Duty duty= dutyRepository.save(eventAddDto.dutyToEntity());
                Event event = eventRepository.save(eventAddDto.dutyToEventEntity(user.get(),duty));
                Order order = Order.builder()
                        .event(event)
                        .orderState(OrderState.WAITING)
                        .approver(null)
                        .build();
                orderRepository.save(order);
            }


        }

    }
    public List<Event> findAll() {
        return eventRepository.findAll();
    }
}
