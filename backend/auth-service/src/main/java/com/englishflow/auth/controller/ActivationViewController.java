package com.englishflow.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ActivationViewController {

    @GetMapping("/activation-pending")
    public String activationPending() {
        return "activation-pending";
    }

    @GetMapping("/activation-success")
    public String activationSuccess() {
        return "activation-success";
    }

    @GetMapping("/activation-error")
    public String activationError() {
        return "activation-error";
    }
}
