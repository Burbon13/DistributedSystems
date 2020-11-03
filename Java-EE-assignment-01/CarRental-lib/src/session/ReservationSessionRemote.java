package session;

import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.ejb.Remote;
import rental.CarType;
import rental.Quote;
import rental.Reservation;

@Remote
public interface ReservationSessionRemote {

    Set<String> getAllRentalCompanies();
    
    Set<CarType> getAvailableCarTypes(Date start, Date end);
    
    void createQuote(String name, Date start, Date end, String carType, String region) throws Exception;
    
    List<Quote> getCurrentQuotes();
    
    List<Reservation> confirmQuotes(String name) throws Exception;
}
