package data;

import java.util.Date;
import java.util.Vector;

public class FileInfo {

    public String fs533FileName;
    public Vector<String> locationIP;

    public int versionNum;
    public long submitTime;

    public FileInfo(String fs533FileName)
    {
        this.fs533FileName = fs533FileName;
        this.versionNum = 1;
        this.locationIP = new Vector<>();
        Date date = new Date();
        this.submitTime = date.getTime();
    }

    public void addLocationIP(String ip)
    {
        if(!this.locationIP.contains(ip))
        {
            this.locationIP.add(ip);
        }

    }
}
