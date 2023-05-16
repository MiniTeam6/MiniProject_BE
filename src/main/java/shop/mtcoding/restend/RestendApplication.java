package shop.mtcoding.restend;

import io.sentry.spring.EnableSentry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableSentry
public class RestendApplication {
    public static void main(String[] args) {
        // Sentry.io 연결해서 !!
        SpringApplication.run(RestendApplication.class, args);
    }

}
