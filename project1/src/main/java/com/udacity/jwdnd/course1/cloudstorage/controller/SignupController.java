package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.User;
import com.udacity.jwdnd.course1.cloudstorage.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/signup")
public class SignupController {

    private final UserService userService;

    public SignupController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public String signupView() {
        return "signup";
    }

    @PostMapping()
    public String signupUser(@ModelAttribute User user) {
        boolean hasError = false;

        if (!userService.isUserNameAvailable(user.getUserName())) {
            return "redirect:/signup?duplicatedError";
        }

        int rowsAdded = userService.createUser(user);
        if (rowsAdded < 0) {
            hasError = true;
        }

        if (hasError) {
            return "redirect:/signup?error";
        } else {
            return "redirect:/login?signup";
        }
    }
}
