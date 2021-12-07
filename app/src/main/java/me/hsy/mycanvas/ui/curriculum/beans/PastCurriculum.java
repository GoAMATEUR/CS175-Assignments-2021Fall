/**
  * Copyright 2021 bejson.com 
  */
package me.hsy.mycanvas.ui.curriculum.beans;


public class PastCurriculum {

    private String name;
    private int intValue;
    private String id;
    private String term;
    private String info;
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

    public void setId(String id) {
         this.id = id;
     }
     public String getId() {
         return id;
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

}