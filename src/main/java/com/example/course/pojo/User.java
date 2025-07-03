package com.example.course.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user") // 明确指定映射的数据库表名
public class User {

    @Id // 标记这是主键字段
    @Column(name = "user_id", length = 20) // 映射到 'user_id' 列，并指定长度

    private String userId;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "password")
    private String password;

    // --- 关系映射 ---

    // 多对多关系：一个用户可以选择多门课程
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "UserCourse", // 指定中间连接表的名称
            joinColumns = @JoinColumn(name = "user_id"), // 指定本类在中间表中的外键列
            inverseJoinColumns = @JoinColumn(name = "course_id") // 指定对方类在中间表中的外键列
    )
    @JsonIgnore
    private Set<Course> courses = new HashSet<>();

    // --- 构造函数, Getters 和 Setters ---

    public User() {
    }

    // 省略 Getters 和 Setters...
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Course> getCourses() {
        return courses;
    }

    public void setCourses(Set<Course> courses) {
        this.courses = courses;
    }
}
