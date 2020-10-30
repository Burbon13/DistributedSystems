package client;

import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.naming.InitialContext;
import rental.CarType;
import rental.Reservation;
import session.ReservationSessionRemote;
import session.ManagerSessionRemote;


public class Main extends AbstractTestAgency<ReservationSessionRemote, ManagerSessionRemote>{
    
    //@EJB
    //static ReservationSessionRemote reservationSession;
    
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
        InitialContext context = new InitialContext();
        return (ReservationSessionRemote) context.lookup(ReservationSessionRemote.class.getName());
    }

    @Override
    protected ManagerSessionRemote getNewManagerSession(String name) throws Exception {
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
        session.confirmQuotes(name);
    }

    @Override
    protected int getNumberOfReservationsBy(ManagerSessionRemote ms, String clientName) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected int getNumberOfReservationsForCarType(ManagerSessionRemote ms, String carRentalName, String carType) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
   
}
