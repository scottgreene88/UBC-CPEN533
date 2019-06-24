package data;


import java.util.Vector;

public class MasterFileList {

    private int nodeCounter;

    private Vector<FileInfo> masterList;
    private Vector<String> masterIndex;

    public MasterFileList()
    {
        nodeCounter = 1;
        masterList = new Vector<>();
        masterIndex = new Vector<>();
    }


    public int getNodeCounter()
    {
        return nodeCounter++;
    }

    public void addNewFile(String fs533FileName)
    {
        masterList.add(new FileInfo(fs533FileName));
    }

    public void addIpToFileInfo(String fs533FileName, String ip)
    {


        FileInfo temp = masterList.get(this.masterIndex.indexOf(fs533FileName));

        if(!temp.locationIP.contains(ip))
        {
            temp.locationIP.add(ip);

            masterList.set(this.masterIndex.indexOf(fs533FileName), temp);
        }
    }
}
