package com.example.course.controller;

import com.example.course.pojo.ResponseMessage;
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

    @PostMapping("/{userId}/avatar")
    public ResponseMessage<Map<String, String>> uploadAvatar(
            @PathVariable String userId,
            @RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return new ResponseMessage<>(400, "文件不能为空", null);
        }

        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return new ResponseMessage<>(400, "只能上传图片文件", null);
        }

        // 验证文件大小（5MB）
        if (file.getSize() > 5 * 1024 * 1024) {
            return new ResponseMessage<>(400, "文件大小不能超过5MB", null);
        }

        try {
            String avatarPath = userService.saveAvatar(userId, file);
            Map<String, String> result = new HashMap<>();
            result.put("avatarPath", avatarPath);
            result.put("message", "头像上传成功");
            return ResponseMessage.success(result);
        } catch (IOException e) {
            return new ResponseMessage<>(500, "头像上传失败: " + e.getMessage(), null);
        } catch (RuntimeException e) {
            return new ResponseMessage<>(400, e.getMessage(), null);
        }
    }

    @GetMapping("/{userId}/avatar")
    public ResponseEntity<FileSystemResource> getAvatar(@PathVariable String userId) {
        try {
            File avatarFile = userService.getAvatarFile(userId);
            FileSystemResource resource = new FileSystemResource(avatarFile);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + avatarFile.getName() + "\"")
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(resource);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}