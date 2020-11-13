package client;

import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.naming.InitialContext;
import rental.CarType;
import rental.Reservation;
import rental.ReservationConstraints;
import session.ManagerSessionRemote;
import session.ReservationSessionRemote;

public class Main extends AbstractTestManagement<ReservationSessionRemote, ManagerSessionRemote> {

    public Main(String scriptFile) {
        super(scriptFile);
    }

    public static void main(String[] args) throws Exception {
        // TODO: use updated manager interface to load cars into companies
        new Main("trips").run();
    }

    @Override
    protected Set<String> getBestClients(ManagerSessionRemote ms) throws Exception {
        return ms.getBestClients();
    }

    @Override
    protected String getCheapestCarType(ReservationSessionRemote session, Date start, Date end, String region) throws Exception {
        return session.getCheaestCarType(start, end, region);
    }

    @Override
    protected CarType getMostPopularCarTypeIn(ManagerSessionRemote ms, String carRentalCompanyName, int year) throws Exception {
        return ms.getMostPopularCarTypeIn(carRentalCompanyName, year);
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
        List<CarType> availableCarTypes = session.getAvailableCarTypes(start, end);
        for(CarType ct: availableCarTypes) {
            System.out.println(ct.toString());
        }
    }

    @Override
    protected void createQuote(ReservationSessionRemote session, String name, Date start, Date end, String carType, String region) throws Exception {
        session.createQuote(region, new ReservationConstraints(start, end, carType, region));
    }

    @Override
    protected List<Reservation> confirmQuotes(ReservationSessionRemote session, String name) throws Exception {
        return session.confirmQuotes();
    }

    @Override
    protected int getNumberOfReservationsBy(ManagerSessionRemote ms, String clientName) throws Exception {
        return ms.getNumberOfReservationsBy(clientName);
    }

    @Override
    protected int getNumberOfReservationsByCarType(ManagerSessionRemote ms, String carRentalName, String carType) throws Exception {
        return ms.getNumberOfReservations(carRentalName, carType);
    }
}