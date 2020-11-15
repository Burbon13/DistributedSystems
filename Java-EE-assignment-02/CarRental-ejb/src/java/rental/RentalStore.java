package rental;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public class RentalStore {

    /*
    private static Map<String, CarRentalCompany> rentals;

    public static CarRentalCompany getRental(String company) {
        CarRentalCompany out = RentalStore.getRentals().get(company);
        if (out == null) {
            throw new IllegalArgumentException("Company doesn't exist!: " + company);
        }
        return out;
    }
    
    public static synchronized Map<String, CarRentalCompany> getRentals(){
        if(rentals == null){
            rentals = new HashMap<String, CarRentalCompany>();
            loadRental("hertz.csv");
            loadRental("dockx.csv");
        }
        return rentals;
    }
    */

    public static CarRentalCompany loadRental(String datafile) throws IOException {
        CrcData data = loadData(datafile);
        CarRentalCompany company = new CarRentalCompany(data.name, data.regions, data.cars);
        return company;
    }

    private static CrcData loadData(String datafile)
            throws NumberFormatException, IOException {

        CrcData out = new CrcData();
        StringTokenizer csvReader;
        //int nextuid = 0;
       
        //open file from jar
        BufferedReader in = new BufferedReader(new InputStreamReader(RentalStore.class.getClassLoader().getResourceAsStream(datafile)));
        
        try {
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
                    //create N new cars with given type, where N is the 5th field
                    for (int i = Integer.parseInt(csvReader.nextToken()); i > 0; i--) {
                        out.cars.add(new Car(type));
                    }        
                }
            } 
        } finally {
            in.close();
        }

        return out;
    }
    
    static class CrcData {
            public List<Car> cars = new LinkedList<Car>();
            public String name;
            public List<String> regions =  new LinkedList<String>();
    }
}