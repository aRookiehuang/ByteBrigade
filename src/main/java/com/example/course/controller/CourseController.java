package com.example.course.controller;

import com.example.course.pojo.Course;
import com.example.course.pojo.ResponseMessage;
import com.example.course.pojo.dto.CourseInsertDTO;
import com.example.course.pojo.dto.CourseUpdateDTO;
import com.example.course.pojo.dto.CourseWeekDTO;
import com.example.course.service.CourseService;
import com.example.course.service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/course")
public class CourseController {

    @Autowired
    private ExcelService excelService;

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
    @PutMapping("/{courseId}")
    public ResponseMessage<Course> updateCourse(
            @PathVariable Integer courseId,
            @RequestBody CourseUpdateDTO updateDTO) {
        try {
            Course updatedCourse = courseService.updateCourse(courseId, updateDTO);
            return ResponseMessage.success(updatedCourse);
        } catch (RuntimeException e) {
            return new ResponseMessage<>(400, e.getMessage(), null);
        }
    }
    @DeleteMapping("/delete/{userId}/{courseId}")
    public ResponseMessage<String> deleteUserCourse(
            @PathVariable String userId,
            @PathVariable Integer courseId) {
        courseService.deleteUserCourse(userId, courseId);
        return ResponseMessage.success("课程删除成功");
    }
    @DeleteMapping("/timeslot/delete")
    public ResponseMessage<String> deleteTimeSlot(
            @RequestParam Integer courseId,
            @RequestParam int week,
            @RequestParam int dayOfweek,
            @RequestParam int period) {
        courseService.deleteTimeSlot(courseId, week, dayOfweek, period);
        return ResponseMessage.success("时间段删除成功");
    }


    @PostMapping("/import")
    public ResponseMessage<Map<String, Object>> importCourses(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") String userId) {

        if (file.isEmpty()) {
            return new ResponseMessage<>(400, "文件不能为空", null);
        }

        try {
            List<String> errorMessages = excelService.importCourses(file, userId);

            Map<String, Object> result = new HashMap<>();
            result.put("success", errorMessages.isEmpty());
            result.put("errorMessages", errorMessages);
            result.put("totalErrors", errorMessages.size());

            if (errorMessages.isEmpty()) {
                return ResponseMessage.success(result);
            } else {
                return new ResponseMessage<>(206, "部分导入成功", result);
            }
        } catch (Exception e) {
            return new ResponseMessage<>(500, "导入失败: " + e.getMessage(), null);
        }
    }
    @GetMapping("/ai-suggestion/{userId}/{week}")
    public ResponseMessage<Map<String, String>> getAIStudySuggestion(
            @PathVariable String userId,
            @PathVariable int week) {
        try {
            int dayOfWeek=java.time.LocalDate.now().getDayOfWeek().getValue();
            Map<String, String> suggestion = courseService.getAIStudySuggestion(userId, week, dayOfWeek);
            return ResponseMessage.success(suggestion);
        } catch (Exception e) {
            return new ResponseMessage<>(500, "AI建议生成失败: " + e.getMessage(), null);
        }
    }
}
