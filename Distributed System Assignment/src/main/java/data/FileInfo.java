package data;

import java.util.Date;
import java.util.Vector;

public class FileInfo {

    public String fs533FileName;
    public String mainIP;
    public String backupIP1;
    public String backupIP2;

    public int versionNum;
    public long submitTime;

    public FileInfo(String fs533FileName)
    {
        this.fs533FileName = fs533FileName;
        this.versionNum = 1;
        Date date = new Date();
        this.submitTime = date.getTime();
    }


}
