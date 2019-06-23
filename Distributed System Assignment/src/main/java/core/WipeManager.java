package core;

import java.io.File;


public class WipeManager {

    public void createFolder (String filePath) {

        new File(filePath).mkdir();
    }


    public void clearFolder (String filePath) {

        File dir = new File (filePath);

        for(File file: dir.listFiles())
            if (!file.isDirectory())
                file.delete();
            //System.out.println("Deleted files");
    }

}