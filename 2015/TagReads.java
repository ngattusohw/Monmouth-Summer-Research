/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monmouthsum;

import com.thingmagic.Reader;



/**
 *
 * @author mmckeon sux
 */
public class TagReads {
    private int rs;
    private double distance;
    private String tagname;
    private Reader reader;
    private int ant;
    private long time;
    
    
    public TagReads(String t, int r, int a,long tt, double d, Reader rea){
        tagname=t;
        rs=r;
        distance=d;
        reader=rea;
        ant=a;
        time=tt;
        
    }
      
    public int getRssiData(){
        return rs;
    }
    
    public String getTagName(){
        return tagname;
    }
    
    public double getDistance(){
        return distance;
    }
    
    public Reader getTheReader(){
        return reader;
    }
    
    public int getAnt(){
        return ant;
    }
    
    public long getUnixTime(){
        return time;
    }
   
    @Override
    public String toString(){
        return "Tag :: " + getTagName() + " Rssi :: " + getRssiData() + " Distance:: " + getDistance() + " Reader :: " + getTheReader() + " Ant " + ant;
    }
}
