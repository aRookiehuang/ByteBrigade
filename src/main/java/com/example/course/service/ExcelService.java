package com.example.course.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.example.course.excel.CourseExcelDTO;
import com.example.course.pojo.dto.CourseInsertDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class ExcelService {

    @Autowired
    private CourseService courseService;

    public List<String> importCourses(MultipartFile file, String userId) throws IOException {
        List<CourseExcelDTO> allExcelData = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();

        // 读取Excel文件
        EasyExcel.read(file.getInputStream(), CourseExcelDTO.class,
                        new PageReadListener<CourseExcelDTO>(allExcelData::addAll))
                .sheet()
                .doRead();

        // 按课程名称+教师分组，同一门课程的多个时间段会被分在一组
        Map<String, List<CourseExcelDTO>> courseGroups = new HashMap<>();
        for (CourseExcelDTO excelDTO : allExcelData) {
            String courseKey = excelDTO.getCourseName() + "|" + excelDTO.getTeacher()+ "|" +excelDTO.getLocation()+ "|" +excelDTO.getElective();
            courseGroups.computeIfAbsent(courseKey, k -> new ArrayList<>()).add(excelDTO);
        }

        // 为每个课程组创建一门课程（包含多个时间段）
        for (Map.Entry<String, List<CourseExcelDTO>> entry : courseGroups.entrySet()) {
            try {
                CourseInsertDTO courseInsertDTO = convertToInsertDTO(entry.getValue(), userId);
                courseService.add_course(courseInsertDTO);
            } catch (Exception e) {
                String courseName = entry.getKey().split("\\|")[0];
                errorMessages.add("导入课程失败: " + courseName + " - " + e.getMessage());
            }
        }

        return errorMessages;
    }

    private CourseInsertDTO convertToInsertDTO(List<CourseExcelDTO> excelDTOs, String userId) {
        if (excelDTOs.isEmpty()) {
            throw new RuntimeException("课程数据为空");
        }

        CourseExcelDTO firstRecord = excelDTOs.get(0);
        CourseInsertDTO insertDTO = new CourseInsertDTO();

        // 设置课程基本信息（从第一行记录获取）
        insertDTO.setUserId(userId);
        insertDTO.setCourse_name(firstRecord.getCourseName());
        insertDTO.setTeacher(firstRecord.getTeacher());
        insertDTO.setLocation(firstRecord.getLocation());
        insertDTO.setElective(parseElective(firstRecord.getElective()));

        // 处理所有时间段（每行Excel记录对应一个或多个时间段）
        List<CourseInsertDTO.TimeSlotRequestDTO> allTimeSlots = new ArrayList<>();
        for (CourseExcelDTO excelDTO : excelDTOs) {
            List<CourseInsertDTO.TimeSlotRequestDTO> timeSlots = parseTimeSlots(excelDTO);
            allTimeSlots.addAll(timeSlots);
        }
        insertDTO.setTimeSlots(allTimeSlots);

        return insertDTO;
    }

    private List<CourseInsertDTO.TimeSlotRequestDTO> parseTimeSlots(CourseExcelDTO excelDTO) {
        List<CourseInsertDTO.TimeSlotRequestDTO> timeSlots = new ArrayList<>();

        // 解析星期
        int dayOfWeek = parseDayOfWeek(excelDTO.getDayOfWeek());

        // 解析节次（支持单节次或连续节次）
        List<Integer> periods = parsePeriods(excelDTO.getPeriod());

        // 解析周次
        String weekList = parseWeekList(excelDTO.getWeekList());

        // 为每个节次创建一个时间段
        for (Integer period : periods) {
            CourseInsertDTO.TimeSlotRequestDTO timeSlot = new CourseInsertDTO.TimeSlotRequestDTO();
            timeSlot.setWeekList(weekList);
            timeSlot.setDayOfweek(dayOfWeek);
            timeSlot.setPeriod(period);
            timeSlots.add(timeSlot);
        }

        return timeSlots;
    }

    private Boolean parseElective(String elective) {
        if (elective == null) return false;
        String normalized = elective.trim().toLowerCase();
        return normalized.equals("是") || normalized.equals("选修") ||
                normalized.equals("true") || normalized.equals("1");
    }

    private int parseDayOfWeek(String dayOfWeek) {
        Map<String, Integer> dayMap = Map.ofEntries(
                Map.entry("周一", 1), Map.entry("周二", 2), Map.entry("周三", 3), Map.entry("周四", 4),
                Map.entry("周五", 5), Map.entry("周六", 6), Map.entry("周日", 7),
                Map.entry("星期一", 1), Map.entry("星期二", 2), Map.entry("星期三", 3), Map.entry("星期四", 4),
                Map.entry("星期五", 5), Map.entry("星期六", 6), Map.entry("星期日", 7)
        );

        Integer day = dayMap.get(dayOfWeek.trim());
        if (day == null) {
            throw new RuntimeException("无效的星期格式: " + dayOfWeek);
        }
        return day;
    }

    private List<Integer> parsePeriods(String period) {
        List<Integer> periods = new ArrayList<>();
        String cleanPeriod = period.replaceAll("[节课]", "").trim();

        if (cleanPeriod.contains("-")) {
            // 范围格式：1-2
            String[] parts = cleanPeriod.split("-");
            int start = Integer.parseInt(parts[0].trim());
            int end = Integer.parseInt(parts[1].trim());
            for (int i = start; i <= end; i++) {
                periods.add(i);
            }
        } else {
            // 单节次
            periods.add(Integer.parseInt(cleanPeriod));
        }

        return periods;
    }

    private String parseWeekList(String weekList) {
        String cleanWeekList = weekList.replaceAll("[周]", "").trim();

        if (cleanWeekList.contains("-")) {
            // 范围格式：1-16
            String[] parts = cleanWeekList.split("-");
            int start = Integer.parseInt(parts[0].trim());
            int end = Integer.parseInt(parts[1].trim());
            List<String> weeks = new ArrayList<>();
            for (int i = start; i <= end; i++) {
                weeks.add(String.valueOf(i));
            }
            return String.join(",", weeks);
        }

        return cleanWeekList;
    }
}