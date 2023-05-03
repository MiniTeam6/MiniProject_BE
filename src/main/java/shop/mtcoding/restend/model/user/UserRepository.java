package shop.mtcoding.restend.model.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

<<<<<<< HEAD
    @Query("select u from User u where u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);
=======
    @Query("select u from User u where u.username = :username")
    Optional<User> findByUsername(@Param("username") String username);

    /**
     * 이름으로 유저검색
     * @param username
     * @return
     */
    List<User> findByUsernameContaining(String username);

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

>>>>>>> a7036f2 (one to one 관계 추가, service 회원전체리스트, 검색 기능 추가)
}