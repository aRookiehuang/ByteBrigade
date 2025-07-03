package com.example.course.excel;

import com.alibaba.excel.annotation.ExcelProperty;

public class CourseExcelDTO {
    @ExcelProperty("课程名称")
    private String courseName;

    @ExcelProperty("任课教师")
    private String teacher;

    @ExcelProperty("上课地点")
    private String location;

    @ExcelProperty("是否选修")
    private String elective;

    @ExcelProperty("周次")
    private String weekList;

    @ExcelProperty("星期")
    private String dayOfWeek;

    @ExcelProperty("节次")
    private String period;

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
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

    public String getElective() {
        return elective;
    }

    public void setElective(String elective) {
        this.elective = elective;
    }

    public String getWeekList() {
        return weekList;
    }

    public void setWeekList(String weekList) {
        this.weekList = weekList;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }
}
