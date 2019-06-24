package data;

import java.util.Vector;

public class LocalFileList {

    private Vector<String> myFileList;
    private Vector<String> backUpFileList1;
    private Vector<String> backUpFileList2;

    public LocalFileList()
    {
        myFileList = new Vector<>();
        backUpFileList1 = new Vector<>();
        backUpFileList2 = new Vector<>();

    }

    public void addFileToLocalList(String fs533FileName, String listName)
    {

        if(listName.equals("main"))
        {
            if(!myFileList.contains(fs533FileName))
            {
                System.out.println("Added file to my list");
                myFileList.add(fs533FileName);
            }
        }else if(listName.equals("backUp1"))
        {
            if(!backUpFileList1.contains(fs533FileName))
            {
                System.out.println("Added file to backup 1");
                backUpFileList1.add(fs533FileName);
            }
        }
        else if(listName.equals("backUp2"))
        {
            if(!backUpFileList2.contains(fs533FileName))
            {
                System.out.println("Added file to backup 2");
                backUpFileList2.add(fs533FileName);
            }
        }


    }

    public Vector<String> getAList(String listName)
    {
        if(listName.equals("main"))
        {
            return myFileList;
        }else if(listName.equals("backUp1"))
        {
            return backUpFileList1;
        }
        else if(listName.equals("backUp2"))
        {
            return backUpFileList2;
        }
        else
        {
            return null;
        }
    }

    public void removeFileFromList(String fileName, String listName)
    {
        int index;

        if(listName.equals("main"))
        {
            index = myFileList.indexOf(fileName);
            myFileList.remove(index);

        }else if(listName.equals("backUp1"))
        {
            index = backUpFileList1.indexOf(fileName);
            backUpFileList1.remove(index);
        }
        else if(listName.equals("backUp2"))
        {
            index = backUpFileList2.indexOf(fileName);
            backUpFileList2.remove(index);
        }

    }
}
