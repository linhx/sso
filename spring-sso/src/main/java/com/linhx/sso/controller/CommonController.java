package com.linhx.sso.controller;

import com.linhx.sso.constants.Pages;
import com.linhx.sso.constants.Paths;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * CommonController
 *
 * @author linhx
 * @since 28/02/2021
 */
@Controller
public class CommonController {
    @GetMapping(value = {Paths.ROOT, Paths.HOME})
    public String pageHome() {
        return Pages.HOME;
    }
}
