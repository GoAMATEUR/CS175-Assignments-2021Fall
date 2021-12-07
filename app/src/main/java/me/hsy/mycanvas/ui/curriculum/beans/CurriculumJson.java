/**
  * Copyright 2021 bejson.com 
  */
package me.hsy.mycanvas.ui.curriculum.beans;
import java.util.List;


public class CurriculumJson {

    private List<CurrentCurriculum> CurrentCurriculum;
    private List<PastCurriculum> PastCurriculum;
    public void setCurrentCurriculum(List<CurrentCurriculum> CurrentCurriculum) {
         this.CurrentCurriculum = CurrentCurriculum;
     }
     public List<CurrentCurriculum> getCurrentCurriculum() {
         return CurrentCurriculum;
     }

    public void setPastCurriculum(List<PastCurriculum> PastCurriculum) {
         this.PastCurriculum = PastCurriculum;
     }
     public List<PastCurriculum> getPastCurriculum() {
         return PastCurriculum;
     }

}