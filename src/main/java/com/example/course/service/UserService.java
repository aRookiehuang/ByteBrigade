package com.example.course.service;

import com.example.course.pojo.Course;
import com.example.course.pojo.User;
import com.example.course.repository.CourseRepository;
import com.example.course.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class UserService
{
    //private static final String AVATAR_DIR = "E:/avator/";
    @Value("${app.avatar.dir:./uploads/avatars/}")
    private String avatarDir;

    @Value("${app.avatar.url-prefix:/avatars/}")
    private String avatarUrlPrefix;
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
        System.out.println("开始保存头像，userId: " + userId);
        System.out.println("文件名: " + file.getOriginalFilename());
        System.out.println("文件大小: " + file.getSize());
        System.out.println("配置的头像目录: " + avatarDir);

        // 获取项目根目录的绝对路径
        String projectRoot = System.getProperty("user.dir");
        Path dirPath = Paths.get(projectRoot, "uploads", "avatars");
        System.out.println("项目根目录: " + projectRoot);
        System.out.println("头像目录绝对路径: " + dirPath.toAbsolutePath());

        // 确保目录存在
        if (!Files.exists(dirPath)) {
            System.out.println("目录不存在，正在创建...");
            try {
                Files.createDirectories(dirPath);
                System.out.println("目录创建成功");
            } catch (IOException e) {
                System.err.println("目录创建失败: " + e.getMessage());
                throw new RuntimeException("无法创建头像目录: " + e.getMessage(), e);
            }
        }

        // 生成文件名
        String fileName = userId + ".png";
        Path avatarPath = dirPath.resolve(fileName);
        System.out.println("完整文件路径: " + avatarPath.toAbsolutePath());

        // 保存文件 - 使用 Files.copy 替代 transferTo
        try {
            Files.copy(file.getInputStream(), avatarPath,
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            System.out.println("文件保存成功");
        } catch (IOException e) {
            System.err.println("文件保存失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("文件保存失败: " + e.getMessage(), e);
        }

        // 更新用户头像路径
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setAvatarPath(avatarUrlPrefix + fileName);
        userRepository.save(user);

        return avatarPath.toString();
    }

    public File getAvatarFile(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 使用绝对路径
        String projectRoot = System.getProperty("user.dir");
        String fileName = userId + ".png";
        Path avatarPath = Paths.get(projectRoot, "uploads", "avatars", fileName);

        if (!Files.exists(avatarPath)) {
            throw new RuntimeException("头像文件不存在");
        }

        return avatarPath.toFile();
    }

    @Transactional
    public User updateUserName(String userId, String newUserName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 验证用户名长度（可根据需要调整）
        if (newUserName.length() > 50) {
            throw new RuntimeException("用户名长度不能超过50个字符");
        }

        user.setUserName(newUserName);
        return userRepository.save(user);
    }
    @Transactional
    public User getUserName(String userId){
        User user=userRepository.findById(userId)
                .orElseThrow(()->new RuntimeException("用户不存在"));
        return user;
    }
}
