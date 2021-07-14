package com.abuyork.tiktokvideodownloader.models;

public class Model_Video {
    String str_path,str_thumb;
    boolean boolean_selected;
    int duration,Id;

    public String getStr_path() {
        return str_path;
    }

    public void setStr_path(String str_path) {
        this.str_path = str_path;
    }

    public String getStr_thumb() {
        return str_thumb;
    }

    public void setStr_thumb(String str_thumb) {
        this.str_thumb = str_thumb;
    }

    public boolean isBoolean_selected() {
        return boolean_selected;
    }

    public void setBoolean_selected(boolean boolean_selected) {
        this.boolean_selected = boolean_selected;
    }

    public int getDuration(){
        return duration;
    }
    public void setDuration(int duration){
        this.duration=duration;
    }
    public void setId(int Id){
        this.Id=Id;
    }
    public int getId(){
        return Id;
    }
}