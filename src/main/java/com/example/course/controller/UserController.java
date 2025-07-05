package com.example.course.controller;

import com.example.course.pojo.ResponseMessage;
import com.example.course.pojo.User;
import com.example.course.service.UserService;
import com.example.course.util.JwtUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
//@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/login")
public class UserController {
    @Autowired
    UserService userService;
    @PostMapping("/register")
    public ResponseMessage<User> addUser(@RequestBody User user){
        User newuser=userService.add(user);
        return ResponseMessage.success(newuser);
    }
    @PostMapping
    public ResponseMessage<User> getUser(@RequestBody User user){
        User existingUser = userService.getByUserIdAndPassword(user.getUserId(), user.getPassword());
        if (existingUser != null) {
            String token = JwtUtil.generateToken(existingUser.getUserId());
            return ResponseMessage.success(existingUser);
        } else {
            return new ResponseMessage<>(401, "用户名或密码错误", null);
        }
    }


}
