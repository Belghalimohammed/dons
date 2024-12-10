package com.isima.dons.entities;
import java.util.ArrayList;
import java.util.List;

public class FilterCriteria {
    

    private ArrayList<String> keywordslist; 
    private List<String> EtatObjetlist;     
    private String zone;                    


    public FilterCriteria() {
        this.keywordslist = new ArrayList<>();
        this.EtatObjetlist = new ArrayList<>();
        this.zone = "";
    }


    public ArrayList<String> getKeywordslist() {
        return keywordslist;
    }

    public void setKeywordslist(ArrayList<String> keywordslist) {
        this.keywordslist = keywordslist;
    }

    public List<String> getEtatObjetlist() {
        return EtatObjetlist;
    }

    public void setEtatObjetlist(List<String> EtatObjetlist) {
        this.EtatObjetlist = EtatObjetlist;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    // toString for debugging
    @Override
    public String toString() {
        return "FilterCriteria{" +
                "keywordslist=" + keywordslist +
                ", EtatObjetlist=" + EtatObjetlist +
                ", zone='" + zone + '\'' +
                '}';
    }
}