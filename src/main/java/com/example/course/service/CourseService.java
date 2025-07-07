package com.example.course.service;

import com.example.course.pojo.Course;
import com.example.course.pojo.TimeSlot;
import com.example.course.pojo.TimeSlotId;
import com.example.course.pojo.User;
import com.example.course.pojo.dto.CourseInsertDTO;
import com.example.course.pojo.dto.CourseUpdateDTO;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Autowired
    AIService aiService;

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
        dto.setElective(course.getElective());

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
    @Transactional
    public void deleteTimeSlot(Integer courseId, int week, int dayOfweek, int period) {
        // 构造复合主键
        TimeSlotId timeSlotId = new TimeSlotId();
        timeSlotId.setCourseId(courseId);
        timeSlotId.setWeek(week);
        timeSlotId.setDayOfweek(dayOfweek);
        timeSlotId.setPeriod(period);

        // 检查时间段是否存在
        if (!timeSlotRepository.existsById(timeSlotId)) {
            throw new RuntimeException("指定的时间段不存在");
        }

        // 删除时间段
        timeSlotRepository.deleteById(timeSlotId);
    }
    @Transactional
    public Course updateCourse(Integer courseId, CourseUpdateDTO updateDTO) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("课程不存在"));

        // 只更新非null的字段
        if (updateDTO.getCourseName() != null) {
            course.setCoursename(updateDTO.getCourseName());
        }
        if (updateDTO.getTeacher() != null) {
            course.setTeacher(updateDTO.getTeacher());
        }
        if (updateDTO.getLocation() != null) {
            course.setLocation(updateDTO.getLocation());
        }
        if (updateDTO.getElective() != null) {
            course.setElective(updateDTO.getElective());
        }

        return courseRepository.save(course);
    }

    public Map<String, String> getAIStudySuggestion(String userId, int week, int dayOfWeek) {
        // 获取用户当天的课程安排
        List<CourseWeekDTO> weekCourses = getCoursesByWeek(userId, week);

        // 过滤出今天的课程
        List<CourseWeekDTO> todayCourses = weekCourses.stream()
                .filter(course -> course.getTimeSlots().stream()
                        .anyMatch(slot -> slot.getDayOfweek() == dayOfWeek))
                .toList();

        // 构建课表描述
        StringBuilder scheduleDescription = new StringBuilder();
        scheduleDescription.append("今天是第").append(week).append("周，星期").append(getDayName(dayOfWeek)).append("\n");
        scheduleDescription.append("课程安排：\n");

        if (todayCourses.isEmpty()) {
            scheduleDescription.append("今天没有课程安排");
        } else {
            for (CourseWeekDTO course : todayCourses) {
                scheduleDescription.append("- ").append(course.getCoursename())
                        .append("（").append(course.getTeacher()).append("）")
                        .append(" 地点：").append(course.getLocation());
                if (course.getElective()) {
                    scheduleDescription.append(" [选修课]");
                }
                scheduleDescription.append("\n");
            }
        }

        // 调用AI服务获取建议
        String aiSuggestion = aiService.getStudySuggestion(scheduleDescription.toString());

        Map<String, String> result = new HashMap<>();
        result.put("suggestion", aiSuggestion);
        result.put("courseCount", String.valueOf(todayCourses.size()));
        result.put("scheduleInfo", scheduleDescription.toString());

        return result;
    }

    private String getDayName(int dayOfWeek) {
        String[] days = {"", "一", "二", "三", "四", "五", "六", "日"};
        return days[dayOfWeek];
    }
}
