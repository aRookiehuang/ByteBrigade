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
    UserService userService;

    @Transactional
    public Course add_course(CourseInsertDTO courseInsertDTO){
        Course course=new Course();
        course.setElective(courseInsertDTO.getElective());
        course.setLocation(courseInsertDTO.getLocation());
        course.setTeacher(courseInsertDTO.getTeacher());
        Course savedCourse = courseRepository.save(course);
        savedCourse.setColor(savedCourse.getCourseId()%10);
        savedCourse = courseRepository.save(savedCourse);

        String [] WeekList = courseInsertDTO.getWeekList().split(",");

        for(String w:WeekList){
            TimeSlot timeSlot =new TimeSlot();
            TimeSlotId timeSlotId = new TimeSlotId();
            timeSlotId.setCourseId(savedCourse.getCourseId());
            timeSlotId.setPeriod(courseInsertDTO.getPeriod());
            timeSlotId.setWeek(Integer.parseInt(w));
            timeSlotId.setDayOfweek(courseInsertDTO.getDay_ofweek()); // 添加这一行
            timeSlot.setCourse(savedCourse);
            timeSlot.setId(timeSlotId);

            timeSlotRepository.save(timeSlot);
        }
        userService.addCourseToUser(courseInsertDTO.getUserId(), savedCourse.getCourseId());

        return savedCourse;
    }


    public List<CourseWeekDTO> getCoursesByWeek(String userId, int week) {
        List<Course> courses = courseRepository.findCoursesByUserIdAndWeek(userId, week);
        return courses.stream().map(this::convertToCourseWeekDTO).toList();
    }

    private CourseWeekDTO convertToCourseWeekDTO(Course course) {
        CourseWeekDTO dto = new CourseWeekDTO();
        dto.setCourseId(course.getCourseId());
        dto.setTeacher(course.getTeacher());
        dto.setLocation(course.getLocation());
        dto.setColor(course.getColor());
        dto.setIsElective(course.getElective());

        List<TimeSlotDTO> timeSlotDTOs = course.getTimeSlots().stream()
                .map(this::convertToTimeSlotDTO)
                .toList();
        dto.setTimeSlots(timeSlotDTOs);

        return dto;
    }

    private TimeSlotDTO convertToTimeSlotDTO(TimeSlot timeSlot) {
        TimeSlotDTO dto = new TimeSlotDTO();
        dto.setWeek(timeSlot.getId().getWeek());
        dto.setDayOfweek(timeSlot.getId().getDayOfweek());
        dto.setPeriod(timeSlot.getId().getPeriod());
        return dto;
    }
}
