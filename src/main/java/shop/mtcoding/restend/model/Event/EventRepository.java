package shop.mtcoding.restend.model.Event;

import org.springframework.data.jpa.repository.JpaRepository;

<<<<<<< HEAD
import java.util.List;


public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAll();
=======
public interface EventRepository extends JpaRepository<Event,Long> {


>>>>>>> a7036f2 (one to one 관계 추가, service 회원전체리스트, 검색 기능 추가)
}
