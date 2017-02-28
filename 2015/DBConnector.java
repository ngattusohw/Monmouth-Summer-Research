/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monmouthsum;

import java.sql.*;
import com.thingmagic.Reader;
import com.thingmagic.ReaderException;
import com.thingmagic.TMConstants;
import java.util.ArrayList;


/**
 *
 * @author mmckeon
 */
public class DBConnector {
    private Connection jdbc;
    public DBConnector(String hostname, String port, String database, String user, String pass) throws SQLException, ClassNotFoundException{
        Class.forName("org.mariadb.jdbc.Driver");
        jdbc = DriverManager.getConnection("jdbc:mysql://"+hostname+":"+port+"/"+database,user,pass);
    }
    public void close() throws SQLException{
        jdbc.close();
    }
    public void updateOrAddReader(ArrayList<Reader> readers) throws SQLException, ReaderException { //"CONNECTED"
        PreparedStatement getReader = jdbc.prepareStatement("SELECT * FROM readers WHERE readerid=? AND readervendor=\"thingmagic\"");
        PreparedStatement getReaders = jdbc.prepareStatement("SELECT * FROM readers WHERE readervendor=\"thingmagic\"");
        PreparedStatement insertReader = jdbc.prepareStatement("INSERT INTO readers VALUES (?,\"thingmagic\",?,?,?)");
        PreparedStatement updateReader = jdbc.prepareStatement("UPDATE readers SET hostname=?, readertype=?, connectstatus=? WHERE readerid=? AND readervendor=\"thingmagic\"");
        System.out.println("SQL DONE");
        /*for(Reader r : readers){
            getReader.setString(1, (String) r.paramGet(TMConstants.TMR_PARAM_VERSION_SERIAL));
            if(!getReader.executeQuery().next()){
                insertReader.setString(1, (String) r.paramGet(TMConstants.TMR_PARAM_VERSION_SERIAL));
                insertReader.setString(2, (String) r.paramGet(TMConstants.TMR_PARAM_VERSION_MODEL));
                insertReader.setString(3, (String) r.paramGet(TMConstants.TMR_PARAM_READER_URI));
                insertReader.setString(4, "CONNECTED");
            } else {
                updateReader.setString(1, (String) r.paramGet(TMConstants.TMR_PARAM_READER_URI));
                updateReader.setString(2, (String) r.paramGet(TMConstants.TMR_PARAM_VERSION_MODEL));
                updateReader.setString(3, "CONNECTED");
                updateReader.setString(4, (String) r.paramGet(TMConstants.TMR_PARAM_VERSION_SERIAL));
            }
        } */
        ResultSet tmreaders = getReaders.executeQuery();
        System.out.println("Execte");
        if(tmreaders.next()){
            System.out.println("Next");
            do{
                boolean isInArray=false;
                for(Reader r : readers){
                    r.connect();
                    System.out.println("Test");
                    
                    if(tmreaders.getString("readerid").equals((String)r.paramGet(TMConstants.TMR_PARAM_VERSION_SERIAL))){
                        isInArray=true;
                    }
                    getReader.setString(1, (String) r.paramGet(TMConstants.TMR_PARAM_VERSION_SERIAL));
                    if(!getReader.executeQuery().next()){
                        insertReader.setString(1, (String) r.paramGet(TMConstants.TMR_PARAM_VERSION_SERIAL));
                        insertReader.setString(2, (String) r.paramGet(TMConstants.TMR_PARAM_VERSION_MODEL));
                        insertReader.setString(3, (String) r.paramGet(TMConstants.TMR_PARAM_READER_URI));
                        insertReader.setString(4, "ACTIVE");
                        insertReader.executeUpdate();
                    } else {
                        updateReader.setString(1, (String) r.paramGet(TMConstants.TMR_PARAM_READER_URI));
                        updateReader.setString(2, (String) r.paramGet(TMConstants.TMR_PARAM_VERSION_MODEL));
                        updateReader.setString(3, "ACTIVE");
                        updateReader.setString(4, (String) r.paramGet(TMConstants.TMR_PARAM_VERSION_SERIAL));
                        updateReader.executeUpdate();
                    }
                    System.out.println("First reader done");
                    r.destroy();
                }
                if(!isInArray){
                    updateReader.setString(1, tmreaders.getString("hostname"));
                    updateReader.setString(2, tmreaders.getString("readertype"));
                    updateReader.setString(3, "CONNECTFAILURE");
                    updateReader.setString(4, tmreaders.getString("readerid"));
                    updateReader.executeUpdate();
                }
            } while(tmreaders.next());
        } else {
            for(Reader r : readers){
                r.connect();
                getReader.setString(1, (String) r.paramGet(TMConstants.TMR_PARAM_VERSION_SERIAL));
                if(!getReader.executeQuery().next()){
                    insertReader.setString(1, (String) r.paramGet(TMConstants.TMR_PARAM_VERSION_SERIAL));
                    insertReader.setString(2, (String) r.paramGet(TMConstants.TMR_PARAM_VERSION_MODEL));
                    insertReader.setString(3, (String) r.paramGet(TMConstants.TMR_PARAM_READER_URI));
                    insertReader.setString(4, "ACTIVE");
                    insertReader.executeUpdate();
                } else {
                    updateReader.setString(1, (String) r.paramGet(TMConstants.TMR_PARAM_READER_URI));
                    updateReader.setString(2, (String) r.paramGet(TMConstants.TMR_PARAM_VERSION_MODEL));
                    updateReader.setString(3, "ACTIVE");
                    updateReader.setString(4, (String) r.paramGet(TMConstants.TMR_PARAM_VERSION_SERIAL));
                    updateReader.executeUpdate();
                }
                r.destroy();
            }
        }
    }
    public void updateOrAddChannels(ArrayList<Reader> readers) throws SQLException, ReaderException{
        PreparedStatement getChannel = jdbc.prepareStatement("SELECT * FROM channels WHERE channelid=? AND channelvendor=\"thingmagic\"");
        PreparedStatement insertChannel = jdbc.prepareStatement("INSERT INTO channels VALUES (?,\"thingmagic\",?,\"thingmagic\", NULL)");
        PreparedStatement updateChannel = jdbc.prepareStatement("UPDATE channels SET readerid=?, readervendor=\"thingmagic\" WHERE channelid=? AND channelvendor=\"rfcode\"");
        for(Reader r : readers){
            r.connect();
            for(int a : (int[]) r.paramGet("/reader/antenna/connectedPortList")){
                getChannel.setString(1, (String) r.paramGet(TMConstants.TMR_PARAM_VERSION_SERIAL)+"_channel_"+String.valueOf(a));
                if(!getChannel.executeQuery().next()){
                    insertChannel.setString(1, (String) r.paramGet(TMConstants.TMR_PARAM_VERSION_SERIAL)+"_channel_"+String.valueOf(a));
                    insertChannel.setString(2, (String) r.paramGet(TMConstants.TMR_PARAM_VERSION_SERIAL));
                    insertChannel.executeUpdate();
                } else {
                    updateChannel.setString(1, (String) r.paramGet(TMConstants.TMR_PARAM_VERSION_SERIAL));
                    updateChannel.setString(2, (String) r.paramGet(TMConstants.TMR_PARAM_VERSION_SERIAL)+"_channel_"+String.valueOf(a));
                    updateChannel.executeUpdate();
                }
            }
            r.destroy();
        }
    }
    public void addTagAndReads(ArrayList<TagReads> reads) throws SQLException, ReaderException{
        PreparedStatement getTag = jdbc.prepareStatement("SELECT * FROM tags WHERE tagid=? AND tagvendor=\"thingmagic\"");
        PreparedStatement insertTag = jdbc.prepareStatement("INSERT INTO tags VALUES (?,\"thingmagic\")");
        PreparedStatement getTagRead = jdbc.prepareStatement("SELECT * FROM tagreads WHERE tagid=? AND tagvendor=\"thingmagic\" AND channelid=? AND channelvendor=\"thingmagic\" AND readtime=FROM_UNIXTIME(?)");
        PreparedStatement insertTagRead = jdbc.prepareStatement("INSERT INTO tagreads VALUES (?,\"thingmagic\",?,\"thingmagic\",FROM_UNIXTIME(?),?)");
        for(TagReads t : reads){
            getTag.setString(1, t.getTagName());
            if(!getTag.executeQuery().next()){
                insertTag.setString(1, t.getTagName());
                insertTag.executeUpdate();
            }
            //Timestamp time = new Timestamp(t.getUnixTime());
            t.getTheReader().connect();
            getTagRead.setString(1, t.getTagName());
            getTagRead.setString(2, (String) t.getTheReader().paramGet(TMConstants.TMR_PARAM_VERSION_SERIAL)+"_channel_"+String.valueOf(t.getAnt()));
            getTagRead.setLong(3, t.getUnixTime());
            if(!getTagRead.executeQuery().next()){
                insertTagRead.setString(1, t.getTagName());
                insertTagRead.setString(2, (String) t.getTheReader().paramGet(TMConstants.TMR_PARAM_VERSION_SERIAL)+"_channel_"+String.valueOf(t.getAnt()));
                insertTagRead.setLong(3, t.getUnixTime());
                insertTagRead.setInt(4, t.getRssiData());
                insertTagRead.executeUpdate();
                System.out.println("Executed..");
            }
            t.getTheReader().destroy();
        }
    }
}
