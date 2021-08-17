package com.linhx.sso.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * CommonController
 *
 * @author linhx
 * @since 28/02/2021
 */
@Controller
public class CommonController {
    @GetMapping("/home")
    @ResponseBody
    public String pageHome() {
        return "home page"; // TODO for testing
    }
}
