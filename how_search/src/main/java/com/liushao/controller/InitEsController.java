package com.liushao.controller;

import com.liushao.entity.Result;
import com.liushao.entity.StatusCode;
import com.liushao.service.InitEsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 前缀 /search
 * @author huangshen
 */
@Tag(name = "初始化elasticsearch接口")
@RestController
@CrossOrigin
@RequestMapping("/search")
public class InitEsController {

    @Autowired
    private InitEsService initEsService;

    /**
     * 初始化elasticsearch索引
     */
    @Operation(summary = "初始化elasticsearch索引")
    @RequestMapping(value = "/init", method= RequestMethod.POST)
    public Result init(){
        initEsService.init();
        return new Result(true, StatusCode.OK, "操作成功");
    }
}
