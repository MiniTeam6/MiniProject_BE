package shop.mtcoding.restend.core.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import shop.mtcoding.restend.dto.ResponseDTO;


// 권한 없음
@Getter
public class Exception404 extends RuntimeException {
    private String key;
    private String value;
    private Integer code;
    public Exception404(String message, Integer code) {
        super(message);
        this.code=code;
    }

    public ResponseDTO<?> body(){
        return new ResponseDTO<>(HttpStatus.NOT_FOUND, "notFound", getMessage(),code);
    }

    public HttpStatus status(){
        return HttpStatus.NOT_FOUND;
    }
}