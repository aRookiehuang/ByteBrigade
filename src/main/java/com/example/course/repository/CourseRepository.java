package com.example.course.repository;

import com.example.course.pojo.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course,Integer> {
    @Query("SELECT DISTINCT c FROM Course c " +
            "JOIN c.timeSlots ts " +
            "JOIN c.users u " +
            "WHERE u.userId = :userId AND ts.id.week = :week")
    List<Course> findCoursesByUserIdAndWeek(@Param("userId") String userId, @Param("week") int week);
}
