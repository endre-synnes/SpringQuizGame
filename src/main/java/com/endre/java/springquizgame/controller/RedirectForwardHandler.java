package com.endre.java.springquizgame.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class RedirectForwardHandler {

    @GetMapping(value = "/")
    public String forward(){
        return "forward:index.xhtml";
    }
}
