package shop.mtcoding.restend.core.dummy;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import shop.mtcoding.restend.core.dummy.DummyEntity;
import shop.mtcoding.restend.model.annual.Annual;
import shop.mtcoding.restend.model.annual.AnnualRepository;
import shop.mtcoding.restend.model.duty.Duty;
import shop.mtcoding.restend.model.duty.DutyRepository;
import shop.mtcoding.restend.model.event.Event;
import shop.mtcoding.restend.model.event.EventRepository;
import shop.mtcoding.restend.model.order.OrderRepository;
import shop.mtcoding.restend.model.order.OrderState;
import shop.mtcoding.restend.model.user.User;
import shop.mtcoding.restend.model.user.UserRepository;

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
            User ssar = null;
            User cos = null;
            User love = null;
            if (userRepository.findByUsername("사르").isEmpty()) {
                ssar = userRepository.save(newUser("사르", "ADMIN", true));
            } else {
                ssar = userRepository.findByUsername("사르").get();
            }
            if (userRepository.findByUsername("코스").isEmpty()) {
                cos = userRepository.save(newUser("코스", "USER", true));
            } else {
                cos = userRepository.findByUsername("코스").get();
            }
            if (userRepository.findByUsername("러브").isEmpty()) {
                love = userRepository.save(newUser("러브", "USER", false));
            } else {
                love = userRepository.findByUsername("러브").get();
            }

            // 등록되어 있지 않은 경우에만 초기화
            if (userRepository.findByUsername("사르").isEmpty() || userRepository.findByUsername("코스").isEmpty() || userRepository.findByUsername("러브").isEmpty()) {
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
            }
        };
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
