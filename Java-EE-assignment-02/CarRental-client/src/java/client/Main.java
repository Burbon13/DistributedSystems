package client;

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
    
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public Main(String scriptFile) {
        super(scriptFile);
    }

    public static void main(String[] args) throws Exception {
        // TODO: use updated manager interface to load cars into companies
        
        final String scriptFile = "trips1";
        
        logger.log(Level.INFO, "Running file {0}", new Object[]{scriptFile});
        
        new Main("trips1").run();
    }

    @Override
    protected Set<String> getBestClients(ManagerSessionRemote ms) throws Exception {
        logger.log(Level.INFO, "[MANAGER] Retrieving best client");
        return ms.getBestClients();
    }

    @Override
    protected String getCheapestCarType(ReservationSessionRemote session, Date start, Date end, String region) throws Exception {
        logger.log(Level.INFO, "[CLIENT] Retrieving cheapest car type");
        return session.getCheaestCarType(start, end, region);
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
}