package session;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.Remote;
import rental.A;
import rental.CarType;
import rental.Quote;
import rental.Reservation;
import rental.ReservationConstraints;
import rental.ReservationException;

@Remote
public interface ReservationSessionRemote {
    
    public void setRenterName(String name) throws Exception;
    
    public String getRenterName() throws Exception;
    
    public Set<String> getAllRentalCompanies() throws Exception;
    
    public List<CarType> getAvailableCarTypes(Date start, Date end) throws Exception;
    
    public Quote createQuote(String company, ReservationConstraints constraints) throws ReservationException, Exception;
    
    public List<Quote> getCurrentQuotes() throws Exception;
    
    public List<Reservation> confirmQuotes() throws ReservationException, Exception;
    
    public String getCheapestCarType(Date start, Date end, String region) throws Exception;
    
    // =============== TESTING PURPOSES FOR RMI ERRORS ===============
    
    public void reveiveA(A a) throws Exception;
    
    public void receiveSetOfA(HashSet<A> setA) throws Exception;
    
    public A sendA(String name) throws Exception;
    
    public void receiveSetOfString(Set<String> setString) throws Exception;
    
    public Set<A> sendSetA() throws Exception;
    
}
