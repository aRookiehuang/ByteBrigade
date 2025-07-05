package com.example.course.service;

import com.example.course.pojo.Course;
import com.example.course.pojo.User;
import com.example.course.repository.CourseRepository;
import com.example.course.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class UserService
{
    private static final String AVATAR_DIR = "E:/avator/";
    @Autowired
    UserRepository userRepository;

    @Autowired
    CourseRepository courseRepository;

    public User add(User user){
        return userRepository.save(user);
    }
    public User get(String userid){
        return userRepository.findById(userid).orElseThrow(()->new IllegalArgumentException("找不到用户"));
    }
    public User getByUserIdAndPassword(String userId, String password) {
        return userRepository.findByUserIdAndPassword(userId, password);
    }

    @Transactional
    public void addCourseToUser(String userId, Integer courseId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("课程不存在"));
        user.getCourses().add(course);
        userRepository.save(user);
    }
    public String saveAvatar(String userId, MultipartFile file) throws IOException {
        // 确保目录存在
        File dir = new File(AVATAR_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 生成文件名
        String fileName = userId+".png" ;
        File avatarFile = new File(AVATAR_DIR + fileName);

        // 保存文件
        try {
            file.transferTo(avatarFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 更新用户头像路径
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setAvatarPath(avatarFile.getAbsolutePath());
        userRepository.save(user);

        return avatarFile.getAbsolutePath();
    }

    public File getAvatarFile(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        File avatarFile = new File(user.getAvatarPath());
        if (!avatarFile.exists()) {
            throw new RuntimeException("头像文件不存在");
        }

        return avatarFile;
    }
}
