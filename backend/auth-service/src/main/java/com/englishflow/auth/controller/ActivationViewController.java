package com.englishflow.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ActivationViewController {

    @GetMapping("/activation-pending")
    public String activationPending() {
        return "activation-pending";
    }
}
