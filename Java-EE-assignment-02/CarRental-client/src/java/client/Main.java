package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import rental.Car;
import rental.CarRentalCompany;
import rental.CarType;
import rental.Reservation;
import rental.ReservationConstraints;
import session.ManagerSessionRemote;
import session.ReservationSessionRemote;

public class Main extends AbstractTestManagement<ReservationSessionRemote, ManagerSessionRemote> {
    
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public Main(String scriptFile) {
        super(scriptFile);
    }

    public static void main(String[] args) throws Exception {        
        final String scriptFile = "trips1";
        logger.log(Level.INFO, "Running file {0}", new Object[]{scriptFile});
        new Main("trips1").loadCarRentalCompanies();
    }

    @Override
    protected Set<String> getBestClients(ManagerSessionRemote ms) throws Exception {
        logger.log(Level.INFO, "[MANAGER] Retrieving best client");
        return ms.getBestClients();
    }

    @Override
    protected String getCheapestCarType(ReservationSessionRemote session, Date start, Date end, String region) throws Exception {
        logger.log(Level.INFO, "[CLIENT] Retrieving cheapest car type");
        return session.getCheapestCarType(start, end, region);
    }

    @Override
    protected CarType getMostPopularCarTypeIn(ManagerSessionRemote ms, String carRentalCompanyName, int year) throws Exception {
        logger.log(Level.INFO, "[MANAGER] Retrieving most popular car type in company <{0}> and year <{1}>", new Object[]{carRentalCompanyName, year});
        return ms.getMostPopularCarTypeIn(carRentalCompanyName, year);
    }

    @Override
    protected ReservationSessionRemote getNewReservationSession(String name) throws Exception {
        logger.log(Level.INFO, "[CLIENT] Retrieving SESSION");
        InitialContext context = new InitialContext();
        return (ReservationSessionRemote) context.lookup(ReservationSessionRemote.class.getName());
    }

    @Override
    protected ManagerSessionRemote getNewManagerSession(String name) throws Exception {
        logger.log(Level.INFO, "[MANAGER] Retrieving SESSION");
        InitialContext context = new InitialContext();
        return (ManagerSessionRemote) context.lookup(ManagerSessionRemote.class.getName());
    }

    @Override
    protected void getAvailableCarTypes(ReservationSessionRemote session, Date start, Date end) throws Exception {
        logger.log(Level.INFO, "[CLIENT] Retrieving available cars between <{0}> and <{1}>", new Object[]{start, end});
        List<CarType> availableCarTypes = session.getAvailableCarTypes(start, end);
        for(CarType ct: availableCarTypes) {
            System.out.println(ct.toString());
        }
    }

    @Override
    protected void createQuote(ReservationSessionRemote session, String name, Date start, Date end, String carType, String region) throws Exception {
        logger.log(Level.INFO, "[CLIENT] Creating quote");
        session.createQuote(region, new ReservationConstraints(start, end, carType, region));
    }

    @Override
    protected List<Reservation> confirmQuotes(ReservationSessionRemote session, String name) throws Exception {
        logger.log(Level.INFO, "[CLIENT] Confirming quotes");
        return session.confirmQuotes();
    }

    @Override
    protected int getNumberOfReservationsBy(ManagerSessionRemote ms, String clientName) throws Exception {
        logger.log(Level.INFO, "[MANAGER] Retrieving nr. of reservations by {0}", clientName);
        return ms.getNumberOfReservationsBy(clientName);
    }

    @Override
    protected int getNumberOfReservationsByCarType(ManagerSessionRemote ms, String carRentalName, String carType) throws Exception {
        logger.log(Level.INFO, "[MANAGER] Retrieving nr. of reservations by car type {0} at company {1}", new Object[]{carType, carRentalName});
        return ms.getNumberOfReservations(carRentalName, carType);
    }
    
    private Main loadCarRentalCompanies() throws Exception {
        logger.log(Level.INFO, "Sending companies data from files to the server");
        ManagerSessionRemote managerSession = getNewManagerSession("LoadingManager");
        List<CarRentalCompany> companies = new ArrayList<>();
        companies.add(loadRental("hertz.csv"));
        companies.add(loadRental("dockx.csv"));
        managerSession.addCarRentalCompany(companies);
        return this;
    }


    protected static CarRentalCompany loadRental(String datafile) throws IOException{
        CrcData data = loadData(datafile);
        CarRentalCompany company = new CarRentalCompany(data.name, data.regions, data.cars);
        Logger.getLogger(Main.class.getName()).log(Level.INFO, "Loaded {0} from file {1}", new Object[]{data.name, datafile});
        return company;

    }

    protected static CrcData loadData(String datafile)
            throws NumberFormatException, IOException {

        CrcData out = new CrcData();
        StringTokenizer csvReader;
        int nextuid = 0;
       
        //open file from jar
        BufferedReader in = new BufferedReader(new InputStreamReader(Main.class.getClassLoader().getResourceAsStream(datafile)));
        
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
                        out.cars.add(new Car(nextuid++, type));
                    }        
                }
            } 
        } finally {
            in.close();
        }

        return out;
    }
    
    static class CrcData {
            public List<Car> cars = new LinkedList<>();
            public String name;
            public List<String> regions =  new LinkedList<>();
    }
}