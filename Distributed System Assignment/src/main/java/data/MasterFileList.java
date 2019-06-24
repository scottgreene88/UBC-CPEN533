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

    public void addNewFile(FileInfo file)
    {
        int index = getIndexOfFile(file.fs533FileName);

        if(index == -1) {
            System.out.println("Adding to master list " + file.fs533FileName);
            masterIndex.add(file.fs533FileName);
            masterList.add(file);
        }else
        {
            incrementVersionNum(index);
        }
    }

    public Vector<String> getMasterIndex()
    {
        return masterIndex;
    }

    public int getIndexOfFile(String fs533FileName)
    {
        return masterIndex.indexOf(fs533FileName);
    }

    public FileInfo getFileFromIndex(int index)
    {
        return masterList.get(index);
    }

    public void removeFileFromList(int index)
    {
        masterList.remove(index);
        masterIndex.remove(index);
    }

    public void incrementVersionNum(int index)
    {
        FileInfo temp = masterList.get(index);
        temp.versionNum++;
        System.out.println("Incrementing " + temp.fs533FileName + " to version " + temp.versionNum);
        masterList.set(index, temp);
    }
}
