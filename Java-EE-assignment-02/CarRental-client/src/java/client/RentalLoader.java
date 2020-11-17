package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import rental.CarType;

public class RentalLoader {
    
    private static int carTypeId = 0;

    public static List<CrcData> loadCompanies(List<String> files)
            throws NumberFormatException, IOException {

        List<CrcData> out = new ArrayList<>();
        
        for(String in: files) {   
            out.add(loadOneCompany(in));
        }
        
        return out;
    }
    
    private static CrcData loadOneCompany(String datafile) 
            throws NumberFormatException, IOException  {
        CrcData out = new CrcData();
        StringTokenizer csvReader;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(RentalLoader.class.getClassLoader().getResourceAsStream(datafile)))) {
            while (in.ready()) {
                String line = in.readLine();
                
                if (line.startsWith("#")) {
                    // comment -> skip					
                } else if (line.startsWith("-")) {
                    csvReader = new StringTokenizer(line.substring(1), ",");
                    out.name = csvReader.nextToken();
                    out.regions = Arrays.asList(csvReader.nextToken().split(":"));
                } else {
                    csvReader = new StringTokenizer(line, ",");
                    //create new car type from first 5 fields
                    CarType type = new CarType(csvReader.nextToken(),
                            Integer.parseInt(csvReader.nextToken()),
                            Float.parseFloat(csvReader.nextToken()),
                            Double.parseDouble(csvReader.nextToken()),
                            Boolean.parseBoolean(csvReader.nextToken()));
                    type.setId(++carTypeId);
                    //create N new cars with given type, where N is the 5th field
                    out.nrOfCars.put(type, Integer.parseInt(csvReader.nextToken()));     
                }
            } 
        }

        return out;
    }
    
    static class CrcData {
            public Map<CarType, Integer> nrOfCars = new HashMap<>();
            public String name;
            public List<String> regions =  new LinkedList<>();
    }
}