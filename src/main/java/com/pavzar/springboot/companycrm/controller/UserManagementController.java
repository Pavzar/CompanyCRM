package com.pavzar.springboot.companycrm.controller;


import com.pavzar.springboot.companycrm.entity.Role;
import com.pavzar.springboot.companycrm.entity.User;
import com.pavzar.springboot.companycrm.service.UserService;
import com.pavzar.springboot.companycrm.user.UserChangePasswordDTO;
import com.pavzar.springboot.companycrm.user.WebUser;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@Controller
@RequestMapping("/employees")
public class UserManagementController {
    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserManagementController(UserService userService, BCryptPasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @GetMapping("/changePassword")
    public String changePassword(Model model) {
        model.addAttribute("userChangePasswordDTO", new UserChangePasswordDTO());

        return "employees/employee-change-password";
    }

    @PostMapping("/changePasswordProcess")
    public String changePasswordProcess(@Valid @ModelAttribute("userChangePasswordDTO") UserChangePasswordDTO userChangePasswordDTO, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            System.out.println("errors");
            System.out.println(bindingResult);

            return "employees/employee-change-password";
        }

        String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userService.findByUserName(currentUserName);

        // if current user password is confirmed and equal
        if (passwordEncoder.matches(userChangePasswordDTO.getCurrentPassword(), currentUser.getPassword())) {
            // if new password is not equal to old password
            if (!Objects.equals(userChangePasswordDTO.getNewPassword(), userChangePasswordDTO.getCurrentPassword())) {
                // if new password is confirmed and equal
                if ((Objects.equals(userChangePasswordDTO.getNewPassword(), userChangePasswordDTO.getNewPasswordConfirm()))) {
                    currentUser.setPassword(passwordEncoder.encode(userChangePasswordDTO.getNewPassword()));
                    userService.save(currentUser);
                    model.addAttribute("success","Password has been successfully changed.");
                } else {
                    model.addAttribute("userChangePasswordDTO", userChangePasswordDTO);
                    model.addAttribute("errorNewPassConfirm", "New password is not the same.");
                }
            } else {
                model.addAttribute("userChangePasswordDTO", userChangePasswordDTO);
                model.addAttribute("errorOldPassConfirm", "Password is the same as old password.");

            }
        } else {
            model.addAttribute("userChangePasswordDTO", userChangePasswordDTO);
            model.addAttribute("errorCurrentPassConfirm", "Current password is invalid.");

        }
        return "employees/employee-change-password";
    }

    @GetMapping("/showUpdateUserForm")
    public String updateUser(@RequestParam("userId") int id, Model model) {
            User user;
            try{
                user = userService.findById(id);
                System.out.println(user.getUserRoles());
                Collection<Role> availableRoles = userService.findAllRoles();

                model.addAttribute("availableRoles", availableRoles);
                model.addAttribute("user", user);
                model.addAttribute("originalEmail", user.getEmail());

                return "employees/employee-update-form";

            } catch (RuntimeException e){
                return "error-404";
            }
    }

    @PostMapping("/saveUpdatedUser")
    public String saveUpdatedUser(@Valid @ModelAttribute("user") User user,
                                  BindingResult bindingResult, Model model) {

        User savedFormUser = userService.findById(user.getId());
        Collection<Role> userRoles = savedFormUser.getUserRoles();
        Collection<Role> availableRoles = userService.findAllRoles();
        String originalUserEmail = savedFormUser.getEmail();
        String updatedUserEmail = user.getEmail();
        User existingUserByEmail = userService.findByEmail(updatedUserEmail);
        String existingEmail;

        if (bindingResult.hasErrors()) {
            System.out.println("has errors");
            System.out.println(bindingResult);
            user.setUserRoles(userRoles);
            user.setEmail(originalUserEmail);
            model.addAttribute("availableRoles", availableRoles);
            model.addAttribute("user", user);

            return "employees/employee-update-form";
        }

        if (Objects.equals(originalUserEmail, updatedUserEmail)) {
            userService.save(user);

            return "redirect:/";
        }

        if (existingUserByEmail != null) {
            existingEmail = existingUserByEmail.getEmail();
            if (Objects.equals(existingEmail, updatedUserEmail)) {
                model.addAttribute("emailError", "Email already exists.");
                user.setUserRoles(userRoles);
                user.setEmail(originalUserEmail);
                model.addAttribute("availableRoles", availableRoles);
                model.addAttribute("user", user);

                return "employees/employee-update-form";
            }
        } else {
            userService.save(user);
            return "redirect:/";
        }

        return "redirect:/";
    }

    @PostMapping("/saveUser")
    public String saveEmployee(@ModelAttribute("user") WebUser user) {
        userService.save(user);

        return "redirect:/";
    }

    @GetMapping("/deleteUser")
    public String delete(@RequestParam("userId") int id) {
        userService.deleteUserById(id);

        return "redirect:/";
    }
}
