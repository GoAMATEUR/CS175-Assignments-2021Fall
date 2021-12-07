/**
  * Copyright 2021 bejson.com 
  */
package me.hsy.mycanvas.ui.curriculum.beans;
import java.util.List;


public class CurriculumJson {

    private List<Curriculum> CurrentCurriculum;
    private List<Curriculum> PastCurriculum;
    public void setCurrentCurriculum(List<Curriculum> CurrentCurriculum) {
         this.CurrentCurriculum = CurrentCurriculum;
     }
     public List<Curriculum> getCurrentCurriculum() {
         return CurrentCurriculum;
     }

    public void setPastCurriculum(List<Curriculum> PastCurriculum) {
         this.PastCurriculum = PastCurriculum;
     }
     public List<Curriculum> getPastCurriculum() {
         return PastCurriculum;
     }

}