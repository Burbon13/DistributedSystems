package session;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    
    private static final Logger logger = Logger.getLogger(ReservationSession.class.getName());
    
    @PersistenceContext
    EntityManager em;

    private String renter;
    private List<Quote> quotes = new LinkedList<>();

    @Override
    public Set<String> getAllRentalCompanies() {
        logger.log(Level.INFO, "Retrieving all car rental companies");
        List<String> companies = em.createQuery("SELECT c.name FROM CarRentalCompany c")
                .getResultList();
        return new HashSet<>(companies);
    }
    
    @Override
    public List<CarType> getAvailableCarTypes(Date start, Date end) throws Exception {
        logger.log(Level.INFO, "Retrieving available car types between {0} and {1}", new Object[]{start, end});
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
    public Quote createQuote(String company, ReservationConstraints constraints) throws ReservationException, Exception {
        logger.log(Level.INFO, "Creating quote for company {0}", company);
        try {
            CarRentalCompany crc = em.find(CarRentalCompany.class, company);
            Quote out = crc.createQuote(constraints, renter);
            quotes.add(out);
            logger.log(Level.INFO, "Created quote for company {0}", company);
            return out;
        } catch(Exception e) {
            logger.log(Level.WARNING, "Exception occurred on creating quote: {0}", e.getMessage());
            throw new ReservationException(e);
        }
    }

    @Override
    public List<Quote> getCurrentQuotes() {
        logger.log(Level.INFO, "Retrieving current quotes");
        return quotes;
    }

    @Override
    public List<Reservation> confirmQuotes() throws ReservationException , Exception{
        logger.log(Level.INFO, "Confirming quotes");
        List<Reservation> done = new LinkedList<>();
        try {
            for (Quote quote : quotes) {
                CarRentalCompany crc = em.find(CarRentalCompany.class, quote.getRentalCompany());
                done.add(crc.confirmQuote(quote));
            }
        } catch (Exception e) {
            // TODO Rollback
            logger.log(Level.WARNING, "Exception occurred on creating quote: {0}", e.getMessage());
            throw new ReservationException(e);
        }
        logger.log(Level.INFO, "Confirmed quotes");
        return done;
    }

    @Override
    public void setRenterName(String name) throws Exception {
        logger.log(Level.INFO, "Setting renter name to {0}", name);
        if (renter != null) {
            logger.log(Level.WARNING, "Renter name already set!");
            throw new IllegalStateException("name already set");
        }
        renter = name;
    }

    @Override
    public String getRenterName() {
        logger.log(Level.INFO, "Retrieving rental name");
        return renter;
    }

    @Override
    public String getCheapestCarType(Date start, Date end, String region) throws Exception {
        logger.log(Level.INFO, "Retreving cheapest car type {0} {1} {2}", new Object[]{start, end, region});
        TypedQuery<CarRentalCompany> query = em.createQuery("SELECT crc FROM CarRentalCompany crc", CarRentalCompany.class);
        List<CarRentalCompany> rentals = query.getResultList();
        double smallestPrice = -1;
        String cheapestCT = "";
        for(CarRentalCompany crc: rentals) {
            if (crc.getRegions().contains(region)) {
                for(CarType ct : crc.getAvailableCarTypes(start, end)) {
                    if (smallestPrice == -1  || ct.getRentalPricePerDay() < smallestPrice) {
                        smallestPrice = ct.getRentalPricePerDay();
                        cheapestCT = ct.getName();
                    }
                }
            }
        }
        return cheapestCT;
    }
}