/**
  * Copyright 2021 bejson.com 
  */
package me.hsy.mycanvas.ui.main.beans;

public class Time {

    private int day; // Mon = 1, Tue = 2, ...
    private int order; // Order on that day

    public void setDay(int day) {
         this.day = day;
     }
     public int getDay() {
         return day;
     }

    public void setOrder(int order) {
         this.order = order;
     }
     public int getOrder() {
         return order;
     }

}