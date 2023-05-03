package shop.mtcoding.restend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.mtcoding.restend.dto.event.EventRequest;
import shop.mtcoding.restend.dto.event.EventResponse;
import shop.mtcoding.restend.model.Annual.Annual;
import shop.mtcoding.restend.model.Annual.AnnualRepository;
import shop.mtcoding.restend.model.Duty.DutyRepository;
import shop.mtcoding.restend.model.Event.Event;
import shop.mtcoding.restend.model.Event.EventRepository;
import shop.mtcoding.restend.model.Event.EventType;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final AnnualRepository annualRepository;
    private final DutyRepository dutyRepository;


    public List<Event> findAll() {
        return eventRepository.findAll();
    }
}
