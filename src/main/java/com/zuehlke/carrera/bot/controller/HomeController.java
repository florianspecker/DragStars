/*
 * (c) Copyright 2014 Swisscom AG
 * All Rights Reserved.
 */
package com.zuehlke.carrera.bot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 */
@Controller
public class HomeController {

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = "text/plain")
    public String home() {
        return "Welcome to MyBot!";
    }
}
