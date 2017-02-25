package com.devopsbuddy.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by madgergely on 2017.02.25..
 */
@Controller
public class CopyController {

    @RequestMapping("about")
    public String about() {
        return "copy/about";
    }
}
