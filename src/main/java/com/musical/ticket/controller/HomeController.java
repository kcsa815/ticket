package com.musical.ticket.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class HomeController {

    @GetMapping("/")
    public String home(){
        return "index"; // templates/index.html을 보여줌
    }
    
    // public String home(){
    //     return "index"; //templates/index.html을 보여줌
    // }
    
}
