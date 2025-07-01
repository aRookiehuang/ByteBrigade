package com.example.course.service;

import com.example.course.pojo.Course;
import com.example.course.pojo.User;
import com.example.course.repository.CourseRepository;
import com.example.course.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService
{
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
}
