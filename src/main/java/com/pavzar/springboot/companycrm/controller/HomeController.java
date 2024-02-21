package com.pavzar.springboot.companycrm.controller;

import com.pavzar.springboot.companycrm.entity.User;
import com.pavzar.springboot.companycrm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;
import java.util.List;

@Controller
public class HomeController {

    private final UserService userService;

    @Autowired
    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String showHome(Model model) {
        List<User> userList = userService.findAll();
        Collections.sort(userList);
        model.addAttribute("users", userList);

        return "employees/home";
    }
}
