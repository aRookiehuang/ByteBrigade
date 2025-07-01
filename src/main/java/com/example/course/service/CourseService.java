package com.example.course.service;

import com.example.course.pojo.Course;
import com.example.course.pojo.TimeSlot;
import com.example.course.pojo.TimeSlotId;
import com.example.course.pojo.dto.CourseInsertDTO;
import com.example.course.repository.CourseRepository;
import com.example.course.repository.TimeSlotRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

@Service
public class CourseService {
    @Autowired
    CourseRepository courseRepository;

    @Autowired
    TimeSlotRepository timeSlotRepository;

    @Transactional
    public void add_course(CourseInsertDTO courseInsertDTO){
        Course course=new Course();
        course.setElective(courseInsertDTO.getElective());
        course.setLocation(courseInsertDTO.getLocation());
        course.setTeacher(courseInsertDTO.getTeacher());
        Course savedCourse = courseRepository.save(course);

        String [] WeekList = courseInsertDTO.getWeekList().split(",");

        for(String w:WeekList){
            TimeSlot timeSlot =new TimeSlot();
            TimeSlotId timeSlotId = new TimeSlotId();
            timeSlotId.setCourseId(savedCourse.getCourseId());
            timeSlotId.setPeriod(courseInsertDTO.getPeriod());
            timeSlotId.setWeek(Integer.parseInt(w));
            timeSlot.setCourse(savedCourse);
            timeSlot.setId(timeSlotId);

            timeSlotRepository.save(timeSlot);
        }
    }
}
