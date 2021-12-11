/**
 * Copyright 2021 bejson.com
 */
package me.hsy.mycanvas.ui.curriculum.beans;
import java.util.List;

/**
 * Auto-generated: 2021-12-11 23:11:17
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Curriculum {

    private String name;
    private int intValue;
    private String theme;
    private String id;
    private List<LectureTime> lectureTime;
    private String term;
    private String info;
    private int credit;
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }
    public int getIntValue() {
        return intValue;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }
    public String getTheme() {
        return theme;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }

    public void setLectureTime(List<LectureTime> lectureTime) {
        this.lectureTime = lectureTime;
    }
    public List<LectureTime> getLectureTime() {
        return lectureTime;
    }

    public void setTerm(String term) {
        this.term = term;
    }
    public String getTerm() {
        return term;
    }

    public void setInfo(String info) {
        this.info = info;
    }
    public String getInfo() {
        return info;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }
    public int getCredit() {
        return credit;
    }

}