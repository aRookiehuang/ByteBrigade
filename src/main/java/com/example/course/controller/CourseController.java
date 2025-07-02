package com.example.course.controller;

import com.example.course.pojo.Course;
import com.example.course.pojo.ResponseMessage;
import com.example.course.pojo.dto.CourseInsertDTO;
import com.example.course.pojo.dto.CourseWeekDTO;
import com.example.course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @GetMapping("/week/{userId}/{week}")
    public ResponseMessage<List<CourseWeekDTO>> getCoursesByWeek(
            @PathVariable String userId,
            @PathVariable int week) {
        List<CourseWeekDTO> courses = courseService.getCoursesByWeek(userId, week);
        return ResponseMessage.success(courses);
    }
    @DeleteMapping("/delete/{userId}/{courseId}")
    public ResponseMessage<String> deleteUserCourse(
            @PathVariable String userId,
            @PathVariable Integer courseId) {
        courseService.deleteUserCourse(userId, courseId);
        return ResponseMessage.success("课程删除成功");
    }
}
