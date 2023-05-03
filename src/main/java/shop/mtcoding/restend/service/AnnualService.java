package shop.mtcoding.restend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.mtcoding.restend.dto.annual.AnnualRequest;
import shop.mtcoding.restend.model.Annual.Annual;
import shop.mtcoding.restend.model.Annual.AnnualRepository;
import shop.mtcoding.restend.model.Event.Event;
import shop.mtcoding.restend.model.Event.EventRepository;
import shop.mtcoding.restend.model.Event.EventType;
import shop.mtcoding.restend.model.user.User;

@Service
@RequiredArgsConstructor
public class AnnualService {
    private final EventRepository eventRepository;
    private final AnnualRepository annualRepository;

    public Object add(AnnualRequest.AnnualAddDto eventAddDto, User user) {

        eventRepository.save(Event.builder()
                .user(user)
                .eventType(EventType.ANNUAL)
                .build());
        annualRepository.save(Annual.builder()
                .startDate(eventAddDto.getStartDate())
                .endDate(eventAddDto.getEndDate())
                .build());
        return null;
    }

}
