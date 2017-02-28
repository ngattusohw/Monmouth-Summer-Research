
package monmouthsum;

import com.thingmagic.MultiReadPlan;
import com.thingmagic.ReadPlan;
import com.thingmagic.Reader;
import com.thingmagic.SimpleReadPlan;
import com.thingmagic.TagReadData;
import java.io.IOException;
import com.thingmagic.TagData;
import com.thingmagic.TagProtocol;
import java.util.*;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

public class MonmouthSum {

    public static void main(String[] args) throws Exception{
        
        int x=0,y=0,holder=0;
        Reader r,r2;
        ArrayList<TagReads> reads = new ArrayList<>();
        ArrayList<Reader> readers = new ArrayList<Reader>();
        File file =new File("tester.txt");
        
        TagReadData[] d;
        long unixTime = System.currentTimeMillis() / 1000L;
        r = Reader.create("llrp://10.1.17.16:5084/"); //Connects to the reader, returns a Reader object
        r2 = Reader.create("llrp://10.1.17.26:5084/");
        readers.add(r);
        readers.add(r2);  
        
        DBConnector jdbc = new DBConnector("10.11.34.174", "3306", "shelter_mac", "root", "PLATO");
        System.out.println(readers);
        
        jdbc.updateOrAddReader(readers);
        jdbc.updateOrAddChannels(readers);
 
        try{		
    		//if file doesnt exists, then create it
    		if(!file.exists()){
    			file.createNewFile();
    		}
            }catch(IOException e){
    		e.printStackTrace();
            }
        
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        while(true){
            r.connect();
            System.out.println("Connected..");
            d=r.read((long)10000);
            r.destroy();
            for(TagReadData data: d){
                reads.add(new TagReads(data.epcString(),data.getRssi(),data.getAntenna(),unixTime,Math.log((data.getRssi()+80.14)/35.04)/-.38,data.getReader()));
                FileWriter fileWritter = new FileWriter(file.getName(),true);
                BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
    	        bufferWritter.write("\n" + data.epcString() + " RSSI :: " + data.getRssi() + " ANT :: "+ data.getAntenna()+ " TIME :: " + unixTime + " DISTANCE ::" + (Math.log((data.getRssi()+80.14)/35.04)/-.38) + " READER ::" +  data.getReader() + " \n ");
    	        bufferWritter.newLine();
                bufferWritter.close();
                
            }

            r2.connect();
            d=r2.read((long)10000);
            r2.destroy();

        
            for(TagReadData data: d){
                reads.add(new TagReads(data.epcString(),data.getRssi(),data.getAntenna(),unixTime,Math.log((data.getRssi()+80.14)/35.04)/-.38,data.getReader()));
                FileWriter fileWritterr = new FileWriter(file.getName(),true);
                BufferedWriter bufferWritterr = new BufferedWriter(fileWritterr);
    	        bufferWritterr.write("\n" + data.epcString() + " RSSI :: " + data.getRssi() + " ANT :: "+ data.getAntenna()+ " TIME :: " + unixTime + " DISTANCE ::" + (Math.log((data.getRssi()+80.14)/35.04)/-.38) + " READER ::" +  data.getReader() + " \n ");
    	        bufferWritterr.newLine();
                bufferWritterr.close();
            }
            
            FileWriter fileWritter = new FileWriter(file.getName(),true);
            BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
            bufferWritter.write("///////////////////////////////////////////////////////////////////////////////////////////////////////");
            bufferWritter.newLine();
            bufferWritter.close();
                   
            jdbc.addTagAndReads(reads);
            
        }    
    }
}