package com.example.course.service;

import com.example.course.pojo.User;
import com.example.course.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService
{
    @Autowired
    UserRepository userRepository;
    public User add(User user){
        return userRepository.save(user);
    }
    public User get(String userid){
        return userRepository.findById(userid).orElseThrow(()->new IllegalArgumentException("找不到用户"));
    }
    public User getByUserIdAndPassword(String userId, String password) {
        return userRepository.findByUserIdAndPassword(userId, password);
    }

}
