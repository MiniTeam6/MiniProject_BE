package com.miniproject.pantry.restend.core.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;
import com.miniproject.pantry.restend.dto.ResponseDTO;


// 인증 안됨
@Getter
public class Exception401 extends RuntimeException {
    private String key;
    private String value;
    private Integer code;
    public Exception401(String message) {
        super(message);
        this.code=1;
    }

    public ResponseDTO<?> body(){
        return new ResponseDTO<>(HttpStatus.UNAUTHORIZED, "unAuthorized", getMessage(), code);
    }

    public HttpStatus status(){
        return HttpStatus.UNAUTHORIZED;
    }
}