package com.example.course.controller;

import com.example.course.pojo.ResponseMessage;
import com.example.course.pojo.User;
import com.example.course.service.UserService;
import com.example.course.util.JwtUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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
    public ResponseMessage<String> getUser(@RequestBody User user){
        User existingUser = userService.getByUserIdAndPassword(user.getUserId(), user.getPassword());
        if (existingUser != null) {
            // 2. 登录成功，生成JWT Token
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", existingUser.getUserId());
            // 你也可以放入其他信息，比如用户角色
            // claims.put("role", existingUser.getRole());

            String token = JwtUtil.createToken(claims); // 使用我们之前定义的工具类

            // 3. 将 Token 作为成功结果返回
            return ResponseMessage.success(token);
        } else {
            // 登录失败的逻辑不变
            return new ResponseMessage<>(401, "用户名或密码错误", null);
        }

    }


}
