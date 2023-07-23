package com.example.internshiptask3.taskmanagment.insights;

public class AssignedDataModel {
    String TaskTitle, TaskSubject, DueDate;

    public AssignedDataModel(){}

    public String getTaskTitle() {
        return TaskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        TaskTitle = taskTitle;
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
}
