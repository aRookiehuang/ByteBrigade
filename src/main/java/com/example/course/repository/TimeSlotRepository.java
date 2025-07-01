package com.example.course.repository;

import com.example.course.pojo.TimeSlot;
import com.example.course.pojo.TimeSlotId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, TimeSlotId> {
}
