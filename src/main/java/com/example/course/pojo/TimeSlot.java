package com.example.course.pojo;


import jakarta.persistence.*;

@Entity
@Table(name = "TimeSlot", uniqueConstraints = {
        // 对应 SQL 中的 UNIQUE (week, dayOfweek, period)
        @UniqueConstraint(columnNames = {"course_id","week", "dayOfweek", "period"})
})
public class TimeSlot {

    @EmbeddedId // 标记使用嵌入式的复合主键
    private TimeSlotId id;

    // --- 关系映射 ---

    // 多对一关系：多个 TimeSlot 对应一门课程
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("courseId") // 将本实体中的 course 属性映射到复合主键 id 中的 courseId 字段
    @JoinColumn(name = "course_id") // 指定数据库中的外键列名
    private Course course;

    // --- 构造函数, Getters 和 Setters ---

    public TimeSlot() {
    }

    // 省略 Getters 和 Setters...
    public TimeSlotId getId() {
        return id;
    }

    public void setId(TimeSlotId id) {
        this.id = id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}
