package com.miniproject.pantry.service;

import com.miniproject.pantry.model.event.EventRepository;
import io.sentry.spring.tracing.SentrySpan;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.miniproject.pantry.dto.annual.AnnualRequest;

import com.miniproject.pantry.model.annual.Annual;
import com.miniproject.pantry.model.annual.AnnualRepository;
import com.miniproject.pantry.model.event.Event;
import com.miniproject.pantry.model.event.EventType;
import com.miniproject.pantry.model.user.User;
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
