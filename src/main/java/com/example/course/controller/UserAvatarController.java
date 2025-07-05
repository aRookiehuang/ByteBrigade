package com.example.course.controller;

import com.example.course.pojo.ResponseMessage;
import com.example.course.pojo.User;
import com.example.course.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserAvatarController {

    @Autowired
    private UserService userService;

    // 头像上传 - 移除 consumes 限制，添加错误处理
    @PostMapping("/{userId}/avatar")
    public ResponseMessage<String> uploadAvatar(
            @PathVariable String userId,
            @RequestParam(value = "file", required = false) MultipartFile file) {

        try {
            // 检查是否是有效的文件上传请求
            if (file == null || file.isEmpty()) {
                return new ResponseMessage<>(400, "请选择要上传的文件", null);
            }

            String avatarPath = userService.saveAvatar(userId, file);
            return ResponseMessage.success("头像上传成功");

        } catch (Exception e) {
            return new ResponseMessage<>(500, "头像上传失败: " + e.getMessage(), null);
        }
    }

    // 获取头像URL
    @GetMapping("/{userId}/avatar/url")
    public ResponseMessage<Map<String, String>> getAvatarUrl(@PathVariable String userId) {
        try {
            User user = userService.getUserName(userId);
            if (user.getAvatarPath() == null) {
                return new ResponseMessage<>(404, "用户暂无头像", null);
            }

            Map<String, String> result = new HashMap<>();
            String avatarUrl = "/avatars/" + userId + ".png";
            result.put("avatarUrl", avatarUrl);
            return ResponseMessage.success(result);

        } catch (RuntimeException e) {
            return new ResponseMessage<>(404, "用户不存在", null);
        }
    }

    // 昵称更新 - 使用JSON请求
    @PutMapping(value = "/{userId}/nickname")
    public ResponseMessage<User> updateNickname(
            @PathVariable String userId,
            @RequestBody Map<String, String> request) {
        try {
            String newUserName = request.get("nickName");
            if (newUserName == null || newUserName.trim().isEmpty()) {
                return new ResponseMessage<>(400, "用户名不能为空", null);
            }
            User updatedUser = userService.updateUserName(userId, newUserName.trim());
            return ResponseMessage.success(updatedUser);
        } catch (RuntimeException e) {
            return new ResponseMessage<>(400, e.getMessage(), null);
        }
    }
    @GetMapping("/{userId}/nickname")
    public ResponseMessage<User>getUserName(
            @PathVariable String userId
    ){

        return ResponseMessage.success(userService.getUserName(userId));
    }
}