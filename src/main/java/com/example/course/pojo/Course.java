package com.example.course.pojo;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "course")
// 注意: JPA标准注解不支持CHECK约束的直接生成。
// 这通常在数据库层面强制执行，或在业务逻辑层（Service）中进行验证。
public class Course {

    @Id
    @Column(name = "course_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer courseId;

    @Column(name="course_name")
    private String coursename;

    @Column(name = "teacher")
    private String teacher;

    @Column(name = "location")
    private String location;

    @Column(name = "color", nullable = false)
    private int color;

    @Column(name = "isElective")
    private Boolean isElective; // 使用Boolean类型映射smallint，更符合Java习惯

    // --- 关系映射 ---1,2,3,4

    // 一对多关系：一门课程有多个时间段 (TimeSlot)
    @OneToMany(
            mappedBy = "course", // "course" 是 TimeSlot 类中 Course 类型属性的名称
            cascade = CascadeType.ALL, // 级联所有操作：保存、更新、删除课程时，其关联的TimeSlot也一并操作
            orphanRemoval = true // 当从课程的 timeSlots 集合中移除一个 TimeSlot 时，该 TimeSlot 记录将从数据库中删除
    )
    private List<TimeSlot> timeSlots = new ArrayList<>();

    // 多对多关系：一门课程可以被多个用户选择 (这是User类中多对多关系的另一端)
    @ManyToMany(mappedBy = "courses") // "courses" 是 User 类中 Set<Course> 属性的名称
    private Set<User> users = new HashSet<>();

    // --- 构造函数, Getters 和 Setters ---
    public Course() {
    }

    // 省略 Getters 和 Setters...
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

    public Boolean getElective() {
        return isElective;
    }

    public void setElective(Boolean elective) {
        isElective = elective;
    }

    public List<TimeSlot> getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(List<TimeSlot> timeSlots) {
        this.timeSlots = timeSlots;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public String getCoursename() {
        return coursename;
    }

    public void setCoursename(String coursename) {
        this.coursename = coursename;
    }
}
