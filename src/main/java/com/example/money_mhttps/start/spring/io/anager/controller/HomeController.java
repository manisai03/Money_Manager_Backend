package com.example.money_mhttps.start.spring.io.anager.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping({"/status","/health"})
public class HomeController {

    @GetMapping
    public String healthCheck(){
        return "Application is running";
    }
}
