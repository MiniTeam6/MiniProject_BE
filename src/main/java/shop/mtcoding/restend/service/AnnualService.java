package shop.mtcoding.restend.service;

import io.sentry.spring.tracing.SentrySpan;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.mtcoding.restend.dto.annual.AnnualRequest;

import shop.mtcoding.restend.model.annual.Annual;
import shop.mtcoding.restend.model.annual.AnnualRepository;
import shop.mtcoding.restend.model.event.Event;
import shop.mtcoding.restend.model.event.EventRepository;
import shop.mtcoding.restend.model.event.EventType;
import shop.mtcoding.restend.model.user.User;
@Service
@RequiredArgsConstructor
public class AnnualService {
    private final EventRepository eventRepository;
    private final AnnualRepository annualRepository;

    @SentrySpan

    public Object 연차추가(AnnualRequest.AnnualAddDto eventAddDto, User user) {

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
