/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
mY dEV 1
 */



package embeddedmediaplayer;

import java.io.Serializable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 *
 * @author Steve Bennett
 */


public class Clip implements Serializable {
    
private SimpleStringProperty title;
private SimpleIntegerProperty start;
private SimpleIntegerProperty end;
private SimpleIntegerProperty max;

public Clip () {
    this.title=new SimpleStringProperty();
    this.start=new SimpleIntegerProperty();
    this.end=new SimpleIntegerProperty();
    this.max = new SimpleIntegerProperty();
    title.set("");
    start.set(0);
    end.set(0);
    max.set(Integer.MAX_VALUE);
}

public Clip (String title, int start, int end) {
    this();
    if (title!=null) this.title = new SimpleStringProperty(title);
    this.start = new SimpleIntegerProperty(start);
    this.end = new SimpleIntegerProperty(end);
}


public String getTitle() {
return title.get();
}
public void setTitle(String s) {
if (s==null) return;
if (s=="") return;
title.set(s);
}



public int getStart() {
        return start.get();
    }

public void setStart(Integer start) {
    if(start==null)return;
    if (start<0) return;
    if (end.get()>0 && start>end.get()) return;
    if (start>max.get()) return;
    this.start.set(start);
}

public int getEnd() {
        return end.get();
    }

public void setEnd(Integer end) {
    if (end==null) return;
    if (end<start.get()) return;
    if (end>max.get()) return;
    this.end.set(end);
}

public int getMax() {
        return max.get();
    }

public void setMax(Integer max) {
    if (max==null) return;
    if (max<this.getEnd()) return;
    if (max<this.getStart()) return;
    this.max.set(max);
}

public boolean equals(Clip anotherClip)
{
    if (anotherClip==null) return false;
    if ((this.getStart()==anotherClip.getStart()) && (this.getEnd()==anotherClip.getEnd()) && (this.getTitle()==anotherClip.getTitle())) return true;
    return false;
}

@Override
public String toString() {
return (title.get() + " from " + start.get() + " to " + end.get());
}

public String toCSV()
{
    return start.get() + "," + end.get() + "," + title.get();
}


public void loadCSVLine(String s)
{
    String[] csvLine = s.split(",");
    String tmptitle = "";
    for (int i=2;i<csvLine.length;i++) tmptitle+=csvLine[i] + ",";
    tmptitle = tmptitle.substring(0, tmptitle.length()-1);
    tmptitle = tmptitle.replaceAll("[\\\\/:*?\"<>|]", "_");
    title.set(tmptitle);
    start.set(Integer.parseInt(csvLine[0]));
    end.set(Integer.parseInt(csvLine[1]));
    
}
}