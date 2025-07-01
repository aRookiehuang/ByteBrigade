package com.example.course.controller;

import com.example.course.pojo.Course;
import com.example.course.pojo.ResponseMessage;
import com.example.course.pojo.dto.CourseInsertDTO;
import com.example.course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/course")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @PostMapping("/add")
    public ResponseMessage<Course> addCourse(@RequestBody CourseInsertDTO courseInsertDTO) {
        Course course = courseService.add_course(courseInsertDTO);
        return ResponseMessage.success(course);
    }
}
