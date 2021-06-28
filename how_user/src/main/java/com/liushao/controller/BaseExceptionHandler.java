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
	
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result error(Exception e){
        e.printStackTrace();
        log.error(e.getMessage());
        return new Result(false, StatusCode.ERROR, "执行出错");
    }
}
