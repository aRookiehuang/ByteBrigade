package com.example.course.pojo.dto;

import java.util.List;

public class CourseInsertDTO {
    private String userId;
    private String course_name;
    private String teacher;
    private String location;
    private Boolean elective;
    private List<TimeSlotRequestDTO> timeSlots;

    // 内部类定义时间段
    public static class TimeSlotRequestDTO {
        private String weekList;
        private int dayOfweek;
        private int period;

        public String getWeekList() {
            return weekList;
        }

        public void setWeekList(String weekList) {
            this.weekList = weekList;
        }

        public int getDayOfweek() {
            return dayOfweek;
        }

        public void setDayOfweek(int dayOfweek) {
            this.dayOfweek = dayOfweek;
        }

        public int getPeriod() {
            return period;
        }

        public void setPeriod(int period) {
            this.period = period;
        }
    }

    // Getters and Setters


    public String getCourse_name() {
        return course_name;
    }

    public void setCourse_name(String course_name) {
        this.course_name = course_name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Boolean getElective() {
        return elective;
    }

    public void setElective(Boolean elective) {
        this.elective = elective;
    }

    public List<TimeSlotRequestDTO> getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(List<TimeSlotRequestDTO> timeSlots) {
        this.timeSlots = timeSlots;
    }
}