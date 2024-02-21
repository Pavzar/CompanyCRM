package com.pavzar.springboot.companycrm.controller;

import java.util.logging.Logger;

import com.pavzar.springboot.companycrm.entity.User;
import com.pavzar.springboot.companycrm.service.UserService;
import com.pavzar.springboot.companycrm.user.WebUser;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/register")
public class UserRegistrationController {

    private final Logger logger = Logger.getLogger(getClass().getName());

    private final UserService userService;

    @Autowired
    public UserRegistrationController(UserService userService) {
        this.userService = userService;
    }

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @GetMapping("/showRegistrationForm")
    public String showMyLoginPage(Model model) {

        model.addAttribute("webUser", new WebUser());

        return "register/registration-form";
    }

    @PostMapping("/processRegistrationForm")
    public String processRegistrationForm(
            @Valid @ModelAttribute("webUser") WebUser webUser,
            BindingResult bindingResult,
            HttpSession session, Model model) {

        String userName = webUser.getUserName();
        logger.info("Processing registration form for: " + userName);

        // form validation
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult);
            return "register/registration-form";
        }

        // check the database if user already exists
        User existingUsername = userService.findByUserName(userName);

        // check the database if email already exists
        String email = webUser.getEmail();
        User existingEmail = userService.findByEmail(email);

        if (existingUsername != null) {
            model.addAttribute("webUser", new WebUser());
            model.addAttribute("usernameError", "Username already exists.");

            if (existingEmail != null) {
                model.addAttribute("emailError", "Email already exists.");
            }

            return "register/registration-form";
        }

        // create user account and store in the database
        userService.save(webUser);

        logger.info("Successfully created user: " + userName);

        // place user in the web http session for later use
        session.setAttribute("user", webUser);

        return "register/registration-confirmation";
    }
}
