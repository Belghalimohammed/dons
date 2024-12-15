package com.isima.dons.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(Model model) {
        // You can capture the error code and message dynamically.
        model.addAttribute("errorCode", "404"); // Or retrieve dynamically
        model.addAttribute("errorMessage", "Oops! The page you're looking for could not be found.");
        return "error"; // renders error.html
    }

}