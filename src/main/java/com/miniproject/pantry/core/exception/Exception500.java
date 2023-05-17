package com.miniproject.pantry.core.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import com.miniproject.pantry.dto.ResponseDTO;

// 서버 에러
@Getter
public class Exception500 extends RuntimeException {
    private String key;
    private String value;
    private Integer code;
    public Exception500(String message,Integer code) {
        super(message);
        this.code=code;
    }

    public ResponseDTO<?> body(){
        return new ResponseDTO<>(HttpStatus.INTERNAL_SERVER_ERROR, "serverError", getMessage(), code);
    }

    public HttpStatus status(){
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
