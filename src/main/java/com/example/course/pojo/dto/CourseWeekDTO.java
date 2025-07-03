package com.example.course.pojo.dto;

import java.util.List;

public class CourseWeekDTO {
    private Integer courseId;
    private String coursename;
    private String teacher;
    private String location;
    private int color;
    private Boolean isElective;
    private List<TimeSlotDTO> timeSlots;

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getCoursename() {
        return coursename;
    }

    public void setCoursename(String coursename) {
        this.coursename = coursename;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public List<TimeSlotDTO> getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(List<TimeSlotDTO> timeSlots) {
        this.timeSlots = timeSlots;
    }

    public Boolean getElective() {
        return isElective;
    }

    public void setElective(Boolean elective) {
        isElective = elective;
    }
}
