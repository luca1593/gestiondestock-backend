package com.devtech.gestiondestock.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static com.devtech.gestiondestock.utils.Constants.APP_ROOT;

@RestController
public class HomeController {

    @RequestMapping(value= APP_ROOT + "/home", method = RequestMethod.GET)
    public String home(){
        System.out.println("-------------------------------------------------------------------------------------------------");
        System.out.println("Home Controller ok");
        System.out.println("-------------------------------------------------------------------------------------------------");
        return "home controleur";
    }

}
