package com.example.course.controller;

import com.example.course.pojo.ResponseMessage;
import com.example.course.pojo.User;
import com.example.course.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
public class UserController {
    @Autowired
    UserService userService;
    @PostMapping("/verificate")
    public ResponseMessage<User> addUser(@RequestBody User user){
        User newuser=userService.add(user);
        return ResponseMessage.success(newuser);
    }
    @GetMapping("{userId}")
    public ResponseMessage<User> getUser(@PathVariable String userId){
        User existinguser=userService.get(userId);
        return ResponseMessage.success(existinguser);
    }
}
