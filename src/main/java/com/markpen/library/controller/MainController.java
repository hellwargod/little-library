package com.markpen.library.controller;

import com.markpen.library.common.BaseResponse;
import com.markpen.library.common.ResultUtils;
import org.apache.catalina.util.RequestUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class MainController {
    @GetMapping("/health")
    public BaseResponse<String> health(){
        return ResultUtils.success("OK");
    }
}
