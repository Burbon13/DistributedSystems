package session;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateful;
import rental.CarType;
import rental.Quote;
import rental.RentalStore;
import rental.Reservation;
import rental.ReservationConstraints;

@Stateful
public class ReservationSession implements ReservationSessionRemote {
    
    private List<Quote> quotes = new ArrayList<>();

    @Override
    public Set<String> getAllRentalCompanies() {
        return new HashSet<String>(RentalStore.getRentals().keySet());
    }

    @Override
    public Set<CarType> getAvailableCarTypes(Date start, Date end) {
        Set<CarType> availableCarTypes = new HashSet<>();
        for(String crc: RentalStore.getRentals().keySet()) {
            availableCarTypes.addAll(RentalStore.getRental(crc).getAvailableCarTypes(start, end));
        }
        return availableCarTypes;
    }

    @Override
    public void createQuote(String name, Date start, Date end, String carType, String region) throws Exception {
        boolean succeess = false;
        for(String crc: RentalStore.getRentals().keySet()) {
            try {
                ReservationConstraints rc = new ReservationConstraints(start, end, carType, region);
                Quote createdQuote = RentalStore.getRental(crc).createQuote(rc , name);
                quotes.add(createdQuote);
                succeess = true;
                break;
            } catch(Exception ignored) {
            }
        }
        if (!succeess) {
            throw new Exception("Unable to create quote"); 
        }
    }

    @Override
    public List<Reservation> confirmQuotes(String name) {
        List<Reservation> tempReservations = new ArrayList<>();
        try {
            for(Quote quote: quotes) {
                Reservation newReservation = RentalStore.getRental(quote.getRentalCompany()).confirmQuote(quote);
                tempReservations.add(newReservation);
            }
        } catch(Exception ignored) {
            for(Reservation reservation: tempReservations) {
                RentalStore.getRental(reservation.getRentalCompany()).cancelReservation(reservation);
            }
        }
        return tempReservations;
    }
}
