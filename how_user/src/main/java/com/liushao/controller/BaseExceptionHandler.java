package com.liushao.controller;

import com.liushao.entity.Result;
import com.liushao.entity.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
/**
 * 统一异常处理类
 */
@ControllerAdvice
@Slf4j
public class BaseExceptionHandler {

    @ExceptionHandler(org.springframework.web.server.ResponseStatusException.class)
    public org.springframework.http.ResponseEntity<Result> status(org.springframework.web.server.ResponseStatusException e) {
        return org.springframework.http.ResponseEntity.status(e.getStatusCode())
                .body(new Result(false, StatusCode.ERROR, e.getReason()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public org.springframework.http.ResponseEntity<Result> invalid(IllegalArgumentException e) {
        return org.springframework.http.ResponseEntity.badRequest().body(new Result(false, StatusCode.ERROR, e.getMessage()));
    }
	
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result error(Exception e){
        e.printStackTrace();
        log.error(e.getMessage());
        return new Result(false, StatusCode.ERROR, e.getMessage());
    }
}
