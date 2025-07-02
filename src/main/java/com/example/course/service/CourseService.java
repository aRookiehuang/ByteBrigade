package com.example.course.service;

import com.example.course.pojo.Course;
import com.example.course.pojo.TimeSlot;
import com.example.course.pojo.TimeSlotId;
import com.example.course.pojo.User;
import com.example.course.pojo.dto.CourseInsertDTO;
import com.example.course.pojo.dto.CourseWeekDTO;
import com.example.course.pojo.dto.TimeSlotDTO;
import com.example.course.repository.CourseRepository;
import com.example.course.repository.TimeSlotRepository;
import com.example.course.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

@Service
public class CourseService {
    @Autowired
    CourseRepository courseRepository;

    @Autowired
    TimeSlotRepository timeSlotRepository;
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Transactional
    public Course add_course(CourseInsertDTO courseInsertDTO){
        Course course = new Course();
        course.setCoursename(courseInsertDTO.getCourse_name());
        course.setElective(courseInsertDTO.getElective());
        course.setLocation(courseInsertDTO.getLocation());
        course.setTeacher(courseInsertDTO.getTeacher());
        Course savedCourse = courseRepository.save(course);

        savedCourse.setColor(savedCourse.getCourseId() % 10);
        savedCourse = courseRepository.save(savedCourse);

        // 遍历每个时间段
        for (CourseInsertDTO.TimeSlotRequestDTO timeSlotRequest : courseInsertDTO.getTimeSlots()) {
            String[] weekList = timeSlotRequest.getWeekList().split(",");

            // 为每个时间段的每个周次创建 TimeSlot
            for (String w : weekList) {
                TimeSlot timeSlot = new TimeSlot();
                TimeSlotId timeSlotId = new TimeSlotId();
                timeSlotId.setCourseId(savedCourse.getCourseId());
                timeSlotId.setPeriod(timeSlotRequest.getPeriod());
                timeSlotId.setWeek(Integer.parseInt(w.trim())); // 添加 trim() 处理空格
                timeSlotId.setDayOfweek(timeSlotRequest.getDayOfweek());

                timeSlot.setCourse(savedCourse);
                timeSlot.setId(timeSlotId);
                timeSlotRepository.save(timeSlot);
            }
        }

        userService.addCourseToUser(courseInsertDTO.getUserId(), savedCourse.getCourseId());
        return savedCourse;
    }


    private CourseWeekDTO convertToCourseWeekDTO(Course course, int week) {
        CourseWeekDTO dto = new CourseWeekDTO();
        dto.setCoursename(course.getCoursename());
        dto.setCourseId(course.getCourseId());
        dto.setTeacher(course.getTeacher());
        dto.setLocation(course.getLocation());
        dto.setColor(course.getColor());
        dto.setIsElective(course.getElective());

        // 只包含指定周的时间段
        List<TimeSlotDTO> timeSlotDTOs = course.getTimeSlots().stream()
                .filter(timeSlot -> timeSlot.getId().getWeek() == week)
                .map(this::convertToTimeSlotDTO)
                .toList();
        dto.setTimeSlots(timeSlotDTOs);

        return dto;
    }

    public List<CourseWeekDTO> getCoursesByWeek(String userId, int week) {
        List<Course> courses = courseRepository.findCoursesByUserIdAndWeek(userId, week);
        return courses.stream()
                .map(course -> convertToCourseWeekDTO(course, week))
                .toList();
    }


    private TimeSlotDTO convertToTimeSlotDTO(TimeSlot timeSlot) {
        TimeSlotDTO dto = new TimeSlotDTO();
        dto.setWeek(timeSlot.getId().getWeek());
        dto.setDayOfweek(timeSlot.getId().getDayOfweek());
        dto.setPeriod(timeSlot.getId().getPeriod());
        return dto;
    }

    @Transactional
    public void deleteUserCourse(String userId, Integer courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("课程不存在"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 检查该用户是否选择了这门课程
        boolean hasThisCourse = course.getUsers().contains(user);

        if (!hasThisCourse) {
            throw new RuntimeException("用户未选择该课程");
        }

        // 先移除用户与课程的关联关系
        course.getUsers().remove(user);
        user.getCourses().remove(course);

        // 保存更改以更新中间表
        userRepository.save(user);
        courseRepository.save(course);

        // 如果删除后没有用户选择该课程，则删除整个课程
        if (course.getUsers().isEmpty()) {
            courseRepository.delete(course);
        }
    }
}
