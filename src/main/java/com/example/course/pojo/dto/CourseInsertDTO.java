package com.example.course.pojo.dto;

import jakarta.persistence.Column;

public class CourseInsertDTO {

    private String teacher;

    private String location;

    private Boolean isElective;

    private String weekList;

    private int period;

    private int day_ofweek;

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public int getDay_ofweek() {
        return day_ofweek;
    }

    public void setDay_ofweek(int day_ofweek) {
        this.day_ofweek = day_ofweek;
    }

    public Boolean getElective() {
        return isElective;
    }

    public void setElective(Boolean elective) {
        isElective = elective;
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

    public String getWeekList() {
        return weekList;
    }

    public void setWeekList(String weekList) {
        this.weekList = weekList;
    }

    @Override
    public String toString() {
        return "CourseInsertDTO{" +
                "teacher='" + teacher + '\'' +
                ", location='" + location + '\'' +
                ", isElective=" + isElective +
                ", weekList='" + weekList + '\'' +
                ", period=" + period +
                ", day_ofweek=" + day_ofweek +
                '}';
    }
}
