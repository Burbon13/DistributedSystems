package client;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.naming.InitialContext;
import rental.CarType;
import rental.Quote;
import rental.Reservation;
import session.ReservationSessionRemote;
import session.ManagerSessionRemote;


public class Main extends AbstractTestAgency<ReservationSessionRemote, ManagerSessionRemote>{
    
    //@EJB
    //static ReservationSessionRemote reservationSession;
    private static Logger LOGGER = Logger.getLogger(Main.class.getName());
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //System.out.println("found rental companies: "+session.getAllRentalCompanies());
        Main main = new Main();
        try {
            main.run();
        } catch(Exception e) {
            
        }
    }
    
    Main() {
        super("simpleTrips");
    }

    @Override
    protected ReservationSessionRemote getNewReservationSession(String name) throws Exception {
        LOGGER.info("Retrieving new reservation session");
        InitialContext context = new InitialContext();
        return (ReservationSessionRemote) context.lookup(ReservationSessionRemote.class.getName());
    }

    @Override
    protected ManagerSessionRemote getNewManagerSession(String name) throws Exception {
        LOGGER.info("Retrieving new manager session");
        InitialContext context = new InitialContext();
        return (ManagerSessionRemote) context.lookup(ManagerSessionRemote.class.getName());
    }

    @Override
    protected void getAvailableCarTypes(ReservationSessionRemote session, Date start, Date end) throws Exception {
        for(CarType ct: session.getAvailableCarTypes(start, end)) {
            System.out.println(ct);
        }
    }

    @Override
    protected void createQuote(ReservationSessionRemote session, String name, Date start, Date end, String carType, String region) throws Exception {
        session.createQuote(name, start, end, carType, region);
    }

    @Override
    protected List<Reservation> confirmQuotes(ReservationSessionRemote session, String name) throws Exception {
        return session.confirmQuotes(name);
    }
    
    public void getCurrentQuotes(ReservationSessionRemote session) {
        for(Quote quote: session.getCurrentQuotes()) {
            System.out.println(quote);
        }
    }

    @Override
    protected int getNumberOfReservationsBy(ManagerSessionRemote ms, String clientName) throws Exception {
        return ms.getNumberOfReservationsBy(clientName);
    }

    @Override
    protected int getNumberOfReservationsForCarType(ManagerSessionRemote ms, String carRentalName, String carType) throws Exception {
        return ms.getNumberOfReservationsForCarType(carRentalName, carType);
    } 
}
