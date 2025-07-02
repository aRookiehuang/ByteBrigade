package com.example.course.pojo.dto;

public class TimeSlotDTO {
    private int week;
    private int dayOfweek;
    private int period;

    // 构造函数、Getters 和 Setters
    public TimeSlotDTO() {}

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
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