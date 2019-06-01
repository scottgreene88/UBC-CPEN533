package data;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

import com.google.gson.*;

public class MachineList {

    public String writeJson(ArrayList<Object> array) throws IOException {
        Gson gson = new Gson();
        String jsonArray = gson.toJson(array);
        FileWriter writer = new FileWriter("machinelist.json");
        writer.write(jsonArray);
        writer.close();
        return(jsonArray);
    }

    public String parseJson(String filepath) throws FileNotFoundException {
        Gson gson = new Gson();
        String path = "machinelist.json";
        BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
        Machine[] machineArray = gson.fromJson(bufferedReader, Machine[].class);
        String stringMachineArray = Arrays.toString(machineArray);
        return(stringMachineArray);

    }
}
