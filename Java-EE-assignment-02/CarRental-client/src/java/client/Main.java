package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import rental.A;
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
    protected void createQuote(ReservationSessionRemote session, String name, Date start, Date end, String carType, String region) throws Exception {
        LOG.log(Level.INFO, "[CLIENT] Creating quote");
        session.createQuote(region, new ReservationConstraints(start, end, carType, region));
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
    
    private Main loadCarRentalCompanies() throws Exception {
        LOG.log(Level.INFO, "Sending companies data from files to the server");
        ManagerSessionRemote managerSession = getNewManagerSession("LoadingManager");
        managerSession.loadCarRentalCompanies();
        LOG.info("Loaded car rental companies!");
        return this;
    }
    
    // =============== TESTING PURPOSES FOR RMI ERRORS ===============
    
    private void testConnection() throws Exception {
        LOG.info("TESTING CONNECTION FOR SERIALIZATION ISSUES");
        ReservationSessionRemote session = getNewReservationSession("razvan");
        LOG.info("Retrieved ReservationSession");
        session.reveiveA(new A("A-FROM-CLIENT"));
        A a = session.sendA("A-FROM-SERVER");
        LOG.log(Level.INFO, "Received A {0}", a.getName());
        try {
            Set<A> setA = new HashSet<>();
            setA.add(new A("a1"));
            setA.add(new A("a2"));
            setA.add(new A("a3"));
            session.receiveSetOfA(setA);
            LOG.info("SUCCESSFULY sent set<A>");
        } catch(Exception e) {
            LOG.info("COULD NOT SEND set<A>" + e.getMessage());
            //e.printStackTrace();
        }
        try {
            Set<String> setString = new HashSet<>();
            setString.add("s1");
            setString.add("s2");
            setString.add("s3");
            session.receiveSetOfString(setString);
            LOG.info("SUCCESSFULY sent set<String>");
        } catch(Exception e) {
            LOG.info("COULD NOT SEND set<String>");
        }
        try {
            Set<A> setA = session.sendSetA();
            LOG.info("RECEIVED set<A>");
            setA.forEach((x) -> LOG.info(x.getName()));
        } catch(Exception e) {
           LOG.info("COULD NOT RECEIVE set<A>");
        }
    }
}