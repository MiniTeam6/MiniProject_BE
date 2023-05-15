package shop.mtcoding.restend.core.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import shop.mtcoding.restend.dto.ResponseDTO;


// 권한 없음
@Getter
public class Exception403 extends RuntimeException {
    private String key;
    private String value;
    private Integer code;
    public Exception403(String message) {
        super(message);
        this.code=2;
    }

    public ResponseDTO<?> body(){
        return new ResponseDTO<>(HttpStatus.FORBIDDEN, "forbidden", getMessage(),code);
    }

    public HttpStatus status(){
        return HttpStatus.FORBIDDEN;
    }
}