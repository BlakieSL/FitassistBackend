package com.example.simplefullstackproject.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ViewController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/userInfo")
    public String displayUserInfo() {
        return "userInfo";
    }

    @GetMapping("/food/{foodId}")
    public String displayFood(@PathVariable("foodId") int foodId) {
        return "food";
    }

    @GetMapping("/cart")
    public String displayCart() {
        return "cart";
    }

    @GetMapping("/activity/{activityId}")
    public String displayActivity(@PathVariable("activityId") int activityId) {
        return "activity";
    }

    @GetMapping("/activities")
    public String displayActivities() {
        return "activities";
    }

    @GetMapping("/foods/add")
    public String displayAddFood() {
        return "foodAdd";
    }

    @GetMapping("/activities/add")
    public String displayAddActivity() {
        return "activityAdd";
    }
}