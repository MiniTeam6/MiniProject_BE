package shop.mtcoding.restend.core.dummy;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import shop.mtcoding.restend.model.annual.Annual;
import shop.mtcoding.restend.model.annual.AnnualRepository;
import shop.mtcoding.restend.model.duty.Duty;
import shop.mtcoding.restend.model.duty.DutyRepository;
import shop.mtcoding.restend.model.event.Event;
import shop.mtcoding.restend.model.event.EventRepository;
import shop.mtcoding.restend.model.event.EventType;
import shop.mtcoding.restend.model.user.User;
import shop.mtcoding.restend.model.user.UserRepository;

import java.time.LocalDate;

@Component
public class DataInit extends DummyEntity{
    private final AnnualRepository annualRepository;

    public DataInit(AnnualRepository annualRepository) {
        this.annualRepository = annualRepository;
    }
//    private final EventRepository eventRepository;
//
//    public DataInit(EventRepository eventRepository) {
//        this.eventRepository = eventRepository;
//    }

    @Profile("dev")
    @Bean
    CommandLineRunner init(UserRepository userRepository, EventRepository eventRepository, AnnualRepository annualRepository, DutyRepository dutyRepository){
        return args -> {
            User ssar = userRepository.save(newUser("사르", "ADMIN",true));
            User cos = userRepository.save(newUser("코스", "USER",true));
            User love = userRepository.save(newUser("러브", "USER",false));
//            userRepository.save(newMockUser(2L,"코스", "USER"));
            Annual annual1 = annualRepository.save(newAnnual(LocalDate.of(2023, 06, 1), LocalDate.of(2023, 06, 30)));
            Duty duty1 = dutyRepository.save(newDuty(LocalDate.of(2023, 06, 1)));
            eventRepository.save(newEvent(cos, "ANNUAL",annual1, null));
            eventRepository.save(newEvent(cos, "DUTY", null, duty1));

        };
    }
}
