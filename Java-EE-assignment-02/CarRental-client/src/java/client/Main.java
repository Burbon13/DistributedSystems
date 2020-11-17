package client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import rental.CarType;
import rental.Reservation;
import rental.ReservationConstraints;
import session.ManagerSessionRemote;
import session.ReservationSessionRemote;

public class Main extends AbstractTestManagement<ReservationSessionRemote, ManagerSessionRemote> {
    
    private static final Logger LOG = Logger.getLogger(Main.class.getName());

    public Main(String scriptFile) {
        super(scriptFile);
    }

    public static void main(String[] args) throws Exception {        
        final String scriptFile = "trips";
        LOG.log(Level.INFO, "Running script {0}", scriptFile);
        new Main("trips").loadCarRentalCompanies().run();
    }

    @Override
    protected Set<String> getBestClients(ManagerSessionRemote ms) throws Exception {
        LOG.log(Level.INFO, "[MANAGER] Retrieving best client");
        return ms.getBestClients();
    }

    @Override
    protected String getCheapestCarType(ReservationSessionRemote session, Date start, Date end, String region) throws Exception {
        LOG.log(Level.INFO, "[CLIENT] Retrieving cheapest car type");
        return session.getCheapestCarType(start, end, region);
    }

    @Override
    protected CarType getMostPopularCarTypeIn(ManagerSessionRemote ms, String carRentalCompanyName, int year) throws Exception {
        LOG.log(Level.INFO, "[MANAGER] Retrieving most popular car type in company <{0}> and year <{1}>", new Object[]{carRentalCompanyName, year});
        return ms.getMostPopularCarTypeIn(carRentalCompanyName, year);
    }

    @Override
    protected ReservationSessionRemote getNewReservationSession(String name) throws Exception {
        LOG.log(Level.INFO, "[CLIENT] Retrieving SESSION");
        InitialContext context = new InitialContext();
        return (ReservationSessionRemote) context.lookup(ReservationSessionRemote.class.getName());
    }

    @Override
    protected ManagerSessionRemote getNewManagerSession(String name) throws Exception {
        LOG.log(Level.INFO, "[MANAGER] Retrieving SESSION");
        InitialContext context = new InitialContext();
        return (ManagerSessionRemote) context.lookup(ManagerSessionRemote.class.getName());
    }

    @Override
    protected void getAvailableCarTypes(ReservationSessionRemote session, Date start, Date end) throws Exception {
        LOG.log(Level.INFO, "[CLIENT] Retrieving available cars between <{0}> and <{1}>", new Object[]{start, end});
        List<CarType> availableCarTypes = session.getAvailableCarTypes(start, end);
        for(CarType ct: availableCarTypes) {
            System.out.println(ct.toString());
        }
    }

    @Override
    protected void createQuote(ReservationSessionRemote session, String company, Date start, Date end, String carType, String region) throws Exception {
        LOG.log(Level.INFO, "[CLIENT] Creating quote");
        session.createQuote(company, new ReservationConstraints(start, end, carType, region));
    }

    @Override
    protected List<Reservation> confirmQuotes(ReservationSessionRemote session, String name) throws Exception {
        LOG.log(Level.INFO, "[CLIENT] Confirming quotes");
        return session.confirmQuotes();
    }

    @Override
    protected int getNumberOfReservationsBy(ManagerSessionRemote ms, String clientName) throws Exception {
        LOG.log(Level.INFO, "[MANAGER] Retrieving nr. of reservations by {0}", clientName);
        return ms.getNumberOfReservationsBy(clientName);
    }

    @Override
    protected int getNumberOfReservationsByCarType(ManagerSessionRemote ms, String carRentalName, String carType) throws Exception {
        LOG.log(Level.INFO, "[MANAGER] Retrieving nr. of reservations by car type {0} at company {1}", new Object[]{carType, carRentalName});
        return ms.getNumberOfReservations(carRentalName, carType);
    }
    
    // =============== LOADING THE COMPANY ===============
    
    private Main loadCarRentalCompanies() throws Exception {
        LOG.info("Loading companies");
     
        List<String> inFiles = new ArrayList<>();
        inFiles.add("hertz.csv");
        inFiles.add("dockx.csv");
        List<RentalLoader.CrcData> companies = RentalLoader.loadCompanies(inFiles);
        
        ManagerSessionRemote managerSession = getNewManagerSession("LoadingManager");
        
        for(RentalLoader.CrcData company: companies) {
            LOG.log(Level.INFO, "Sending company {0}", company.name);
            managerSession.initializeNewCarRentalCompany(company.name, company.regions);
            company.nrOfCars.forEach((carType, nrOfCars) -> {
                LOG.log(Level.INFO, "Loading <{0}> of type <{1}>", new Object[]{nrOfCars, carType});
                try {
                    managerSession.insertNewCar(company.name, carType, nrOfCars);
                } catch (Exception ex) {
                    LOG.log(Level.SEVERE, "Could not insert new cars: {0}", ex.getMessage());
                }
            });
        }
        
        LOG.info("Loaded car rental companies!");
        return this;
    }
}