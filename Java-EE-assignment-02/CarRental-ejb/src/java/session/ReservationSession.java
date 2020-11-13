package session;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import rental.CarRentalCompany;
import rental.CarType;
import rental.Quote;
import rental.RentalStore;
import rental.Reservation;
import rental.ReservationConstraints;
import rental.ReservationException;

@Stateful
public class ReservationSession implements ReservationSessionRemote {
    
    @PersistenceContext
    EntityManager em;

    private String renter;
    private List<Quote> quotes = new LinkedList<Quote>();

    @Override
    public Set<String> getAllRentalCompanies() {
        List<String> companies = em.createQuery("SELECT c.name FROM CarRentalCompany c")
                .getResultList();
        return new HashSet<>(companies);
    }
    
    @Override
    public List<CarType> getAvailableCarTypes(Date start, Date end) {
        List<CarType> availableCarTypes = new LinkedList<>();
        TypedQuery<CarRentalCompany> query = em.createQuery("SELECT crc FROM CarRentalCompany crc", CarRentalCompany.class);
        List<CarRentalCompany> rentals = query.getResultList();
        for(CarRentalCompany crc: rentals) {
            for(CarType ct : crc.getAvailableCarTypes(start, end)) {
                if(!availableCarTypes.contains(ct))
                    availableCarTypes.add(ct);
            }
        }
        return availableCarTypes;
    }

    @Override
    public Quote createQuote(String company, ReservationConstraints constraints) throws ReservationException {
        try {
            Quote out = RentalStore.getRental(company).createQuote(constraints, renter);
            quotes.add(out);
            return out;
        } catch(Exception e) {
            throw new ReservationException(e);
        }
    }

    @Override
    public List<Quote> getCurrentQuotes() {
        return quotes;
    }

    @Override
    public List<Reservation> confirmQuotes() throws ReservationException {
        List<Reservation> done = new LinkedList<>();
        try {
            for (Quote quote : quotes) {
                CarRentalCompany crc = em.find(CarRentalCompany.class, quote.getRentalCompany());
                done.add(crc.confirmQuote(quote));
            }
        } catch (Exception e) {
            // TODO: Rollback
            throw new ReservationException(e);
        }
        return done;
    }

    @Override
    public void setRenterName(String name) {
        if (renter != null) {
            throw new IllegalStateException("name already set");
        }
        renter = name;
    }

    @Override
    public String getRenterName() {
        return renter;
    }

    @Override
    public String getCheaestCarType(Date start, Date end, String region) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}