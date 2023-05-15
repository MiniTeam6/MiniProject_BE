package shop.mtcoding.restend.core.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import shop.mtcoding.restend.dto.ResponseDTO;
import shop.mtcoding.restend.dto.ValidDTO;

import javax.persistence.criteria.CriteriaBuilder;


// 유효성 검사 실패, 잘못된 파라메터 요청
@Getter
public class Exception400 extends RuntimeException {

    private String key;
    private String value;
    private Integer code;

    public Exception400(String key, String value, Integer code) {
        super(value);
        this.key = key;
        this.value = value;
        this.code=code;
    }

    public ResponseDTO<?> body(){
        ValidDTO validDTO = new ValidDTO(key, value);
        return new ResponseDTO<>(HttpStatus.BAD_REQUEST, "badRequest", validDTO, code);
    }

    public HttpStatus status(){
        return HttpStatus.BAD_REQUEST;
    }
}