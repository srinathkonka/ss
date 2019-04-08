/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package embeddedmediaplayer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author comqsjb
 */
public class ClipBag {
    
    public String videoFileLocation;
    public  ObservableList<Clip> clipList;
    public final String cmd = "handbrakecli -i \"$fullPathToSource\" -t 1 --angle 1 --start-at duration:$startTimeSeconds --stop-at duration:$endTimeSeconds -o \"$fullPathToOutput\"  -f mkv  -w 1920 --loose-anamorphic  --modulus 2 -e x264 -q 20 --vfr -a 1 -E faac -6 dpl2 -R Auto -B 160 -D 0 --gain 0 --aencoder copy:av_mkv --audio-fallback ffac3 --x264-preset=veryfast  --x264-profile=main  --h264-level=\"4.0\"  --verbose=1";
    public String handBrakeExe="";
    public ClipBag()
    {
        clipList =   FXCollections.observableArrayList();
    }
    
    public void addClip(Clip clip)
    {
        clipList.add(clip);
    }
    
    public String toString()
    {
        String retval = "";
        for (int i=0;i<clipList.size();i++) retval+=clipList.get(i).toString() + "\n";
        return retval;
    
    }
    
    public void setVideoFileLocation(String fileUri)
    {
        System.out.println("here is the fileUrl:" + fileUri);
        
        videoFileLocation = fileUri;
    }
    
    public String createBatchFileTxt()
    {
        String retval = "";
        List<String> l = Arrays.asList();
        int j;
        int clipNumMax = clipList.size();
        
        for (int i=0;i<clipNumMax;i++)
        {
            j=1;
            Clip c = clipList.get(i);
            String fn = c.getTitle();
            fn = fn.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
            //while (l.contains(fn)) {fn = fn + "_" + j;j++;}
            //l.add(fn);
            retval+=makeCommandLine(c,fn) + "\n";  
            
        }
        retval = retval.replace("file:\\", "");
        retval = retval.replace("file:/", "");
        return retval;
        
    }
    
    public String makeCommandLine(Clip c, String outputFileName)
    {
        File f = new File(videoFileLocation);    
        
        String tmpCmd = cmd;
        int duration = c.getEnd()-c.getStart();
        tmpCmd = tmpCmd.replace("$fullPathToSource", f.getAbsolutePath() );
        tmpCmd = tmpCmd.replace("$startTimeSeconds", ""+ c.getStart());
        tmpCmd = tmpCmd.replace("$endTimeSeconds", ""+duration);
        tmpCmd = tmpCmd.replace("$fullPathToOutput",f.getParent() + "\\" + outputFileName + ".mkv");
        return tmpCmd;
    }
    
    public boolean save(String fn)
    {
        
        String csvContent = "";
        for (int i=0;i<clipList.size();i++) csvContent +=clipList.get(i).toCSV() + "\r\n";
        try (PrintWriter out = new PrintWriter(fn)) {
            out.print(csvContent);
            return true;
        }
        catch(FileNotFoundException fnfe)
        {
            System.out.println(fnfe.getMessage());
            return false;
        }
    }
    
    public void load(String fn)
    {
        try 
        {
        FileReader reader = new FileReader(fn);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line;
        while ((line = bufferedReader.readLine()) != null) {

          Clip clip = new Clip();
          clip.loadCSVLine(line);
          clipList.add(clip);
        }
        reader.close();
        System.out.println("After loading the cliplist is " + this.toString());

        } catch (IOException e) {
        e.printStackTrace();
        }
    }
    
    public boolean equals(ClipBag cp)
    {
        if (cp==null) return false;
        if (cp.clipList.size()!=clipList.size()) return false;
        
        for(int i=0;i<cp.clipList.size();i++)
            {
            if (!cp.clipList.get(i).equals(clipList.get(i))) return false;
            }
        return true;

    }
    
    
}
