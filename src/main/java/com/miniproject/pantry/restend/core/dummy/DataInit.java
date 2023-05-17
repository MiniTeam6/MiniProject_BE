package com.miniproject.pantry.restend.core.dummy;

import com.miniproject.pantry.restend.model.annual.Annual;
import com.miniproject.pantry.restend.model.annual.AnnualRepository;
import com.miniproject.pantry.restend.model.duty.Duty;
import com.miniproject.pantry.restend.model.duty.DutyRepository;
import com.miniproject.pantry.restend.model.event.Event;
import com.miniproject.pantry.restend.model.event.EventRepository;
import com.miniproject.pantry.restend.model.order.OrderRepository;
import com.miniproject.pantry.restend.model.order.OrderState;
import com.miniproject.pantry.restend.model.user.User;
import com.miniproject.pantry.restend.model.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInit extends DummyEntity {
//    private final AnnualRepository annualRepository;
//
//    public DataInit(AnnualRepository annualRepository) {
//        this.annualRepository = annualRepository;
//    }

    @Profile("dev")
    @Bean
    CommandLineRunner initDev(UserRepository userRepository, EventRepository eventRepository, AnnualRepository annualRepository, DutyRepository dutyRepository, OrderRepository orderRepository) {
        return args -> {
            User ssar = registerUser(userRepository, "사르", "ADMIN", true);
            User cos = registerUser(userRepository, "코스", "USER", true);
            User love = registerUser(userRepository, "러브", "USER", false);

            Annual annual1 = annualRepository.save(newAnnual(LocalDate.of(2023, 06, 1), LocalDate.of(2023, 06, 5), 5L));
            Duty duty1 = dutyRepository.save(newDuty(LocalDate.of(2023, 06, 1)));
            Annual annual2 = annualRepository.save(newAnnual(LocalDate.of(2023, 06, 7), LocalDate.of(2023, 06, 9), 3L));
            Duty duty2 = dutyRepository.save(newDuty(LocalDate.of(2023, 06, 5)));
            Event event1 = eventRepository.save(newEvent(cos, "ANNUAL", annual1, null));
            Event event2 = eventRepository.save(newEvent(cos, "DUTY", null, duty1));
            Event event3 = eventRepository.save(newEvent(cos, "ANNUAL", annual2, null));
            Event event4 = eventRepository.save(newEvent(cos, "DUTY", null, duty2));
            orderRepository.save(newOrder(event1, OrderState.WAITING, null));
            orderRepository.save(newOrder(event2, OrderState.WAITING, null));
            orderRepository.save(newOrder(event3, OrderState.APPROVED, ssar));
            orderRepository.save(newOrder(event4, OrderState.APPROVED, ssar));
        };
    }

    private User registerUser(UserRepository userRepository, String username, String role, boolean enabled) {
        if (userRepository.findByUsername(username).isEmpty()) {
            return userRepository.save(newUser(username, role, enabled));
        }
        return userRepository.findByUsername(username).get();
    }



    @Profile("prod")
    @Bean
    CommandLineRunner initProd(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByUsername("사르").isEmpty()) {
                User ssar = userRepository.save(newUser("사르", "ADMIN", true));
            }
        };
    }

}
