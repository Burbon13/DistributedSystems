package session;

import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.ejb.Remote;
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
    
}
