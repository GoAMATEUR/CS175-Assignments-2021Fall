/**
  * Copyright 2021 bejson.com 
  */
package me.hsy.mycanvas.ui.main.beans;
import java.util.List;

public class InfoBean {

    private String name;
    private String lecturer;
    private String location;
    private int credit;
    private List<Grading> grading;
    private String description;
    private List<Time> time;
    public void setName(String name) {
         this.name = name;
     }
     public String getName() {
         return name;
     }

    public void setLecturer(String lecturer) {
         this.lecturer = lecturer;
     }
     public String getLecturer() {
         return lecturer;
     }

    public void setLocation(String location) {
         this.location = location;
     }
     public String getLocation() {
         return location;
     }

    public void setCredit(int credit) {
         this.credit = credit;
     }
     public int getCredit() {
         return credit;
     }

    public void setGrading(List<Grading> grading) {
         this.grading = grading;
     }
     public List<Grading> getGrading() {
         return grading;
     }

    public void setDescription(String description) {
         this.description = description;
     }
     public String getDescription() {
         return description;
     }

    public void setTime(List<Time> time) {
         this.time = time;
     }
     public List<Time> getTime() {
         return time;
     }

}