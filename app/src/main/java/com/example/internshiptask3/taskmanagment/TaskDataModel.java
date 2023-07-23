package com.example.internshiptask3.taskmanagment;

public class TaskDataModel {

    String TaskTitle, TaskDesc, TaskPriority, TaskSubject, DueDate;
    //Long dayOfMonth, month, year, minute, hour; - datatype should be long

    public TaskDataModel(){

    }

    public TaskDataModel(String taskTitle, String taskDesc, String taskPriority, String taskSubject, String dueDate) {
        TaskTitle = taskTitle;
        TaskDesc = taskDesc;
        TaskPriority = taskPriority;
        TaskSubject = taskSubject;
        DueDate = dueDate;
    }

    public String getTaskTitle() {
        return TaskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        TaskTitle = taskTitle;
    }

    public String getTaskDesc() {
        return TaskDesc;
    }

    public void setTaskDesc(String taskDesc) {
        TaskDesc = taskDesc;
    }

    public String getTaskPriority() {
        return TaskPriority;
    }

    public void setTaskPriority(String taskPriority) {
        TaskPriority = taskPriority;
    }

    public String getTaskSubject() {
        return TaskSubject;
    }

    public void setTaskSubject(String taskSubject) {
        TaskSubject = taskSubject;
    }

    public String getDueDate() {
        return DueDate;
    }

    public void setDueDate(String dueDate) {
        DueDate = dueDate;
    }

    //    public String getDayOfMonth() {
//        return dayOfMonth;
//    }
//
//    public void setDayOfMonth(String dayOfMonth) {
//        this.dayOfMonth = dayOfMonth;
//    }

//    public String getMonth() {
//        return month;
//    }
//
//    public void setMonth(String month) {
//        this.month = month;
//    }

//    public String getYear() {
//        return year;
//    }
//
//    public void setYear(String year) {
//        this.year = year;
//    }

//    public String getMinute() {
//        return minute;
//    }
//
//    public void setMinute(String minute) {
//        this.minute = minute;
//    }

//    public String getHour() {
//        return hour;
//    }
//
//    public void setHour(String hour) {
//        this.hour = hour;
//    }
}
