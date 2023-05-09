package shop.mtcoding.restend.model.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.mtcoding.restend.model.event.Event;
import shop.mtcoding.restend.model.event.EventType;
import shop.mtcoding.restend.model.order.Order;
import shop.mtcoding.restend.model.order.OrderState;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {


    @Query("select u from User u where u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);

    @Query("select u from User u where u.username = :username")
    Optional<User> findByUsername(@Param("username") String username);

    /**
     * 이름으로 유저검색
     * @param username
     * @return
     */
    List<User> findByUsernameContainingAndStatusTrue(String username);

    /***
     * 이메일로 유저검색
     * @param email
     * @return
     */
    List<User> findByEmailContaining(String email);

    /***
     * 둘다 검색
     * @param keyword1
     * @param keyword2
     * @return
     */
    List<User> findByUsernameContainingOrEmailContaining(String keyword1, String keyword2);


    /***
     * 회원가입 요청 리스트 조회(status : false)
     * @return
     */
    @Query("SELECT u FROM User u WHERE u.status = :status")
    List<User> findUsersByStatus(@Param("status")Boolean status);


    @Query("SELECT u.id FROM User u WHERE u.username LIKE %:search% OR u.email LIKE %:search%")
    List<Long> findUserIdsByUsernameOrEmail(@Param("search") String search);




}







