package com.huiaong.websocket.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/go")
public class HomeController {

    @RequestMapping("/socket")
    public ModelAndView goIndex(){
        return new ModelAndView("socket");
    }

}
