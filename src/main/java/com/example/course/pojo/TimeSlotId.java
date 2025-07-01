package com.example.course.pojo;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable // 表示这个类可以被嵌入到其他实体中
public class TimeSlotId implements Serializable {

    private int week;
    private int dayOfweek;
    private int period;

    // 注意：这里是 course_id，而不是 Course 对象
    private Integer courseId;

    // --- 构造函数, Getters, Setters, hashCode, equals ---

    public TimeSlotId() {
    }

    // **非常重要**: 复合主键类必须重写 hashCode() 和 equals() 方法！
    // JPA 使用它们来判断两个主键是否相同。

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeSlotId that = (TimeSlotId) o;
        return week == that.week && dayOfweek == that.dayOfweek && period == that.period && Objects.equals(courseId, that.courseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(week, dayOfweek, period, courseId);
    }

    // 省略 Getters 和 Setters...
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

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }
}