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
import rental.A;
import rental.CarRentalCompany;
import rental.CarType;
import rental.Quote;
import rental.Reservation;
import rental.ReservationConstraints;
import rental.ReservationException;

@Stateful
public class ReservationSession implements ReservationSessionRemote {
    
    private static final Logger LOG = Logger.getLogger(ReservationSession.class.getName());
    
    @PersistenceContext
    EntityManager em;

    private String renter;
    private List<Quote> quotes = new LinkedList<>();

    @Override
    public Set<String> getAllRentalCompanies() {
        LOG.log(Level.INFO, "Retrieving all car rental companies");
        List<String> companies = em.createQuery("SELECT c.name FROM CarRentalCompany c")
                .getResultList();
        return new HashSet<>(companies);
    }
    
    @Override
    public List<CarType> getAvailableCarTypes(Date start, Date end) throws Exception {
        LOG.log(Level.INFO, "Retrieving available car types between {0} and {1}", new Object[]{start, end});
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
    public Quote createQuote(String name, ReservationConstraints constraints) throws ReservationException, Exception {
        /*
        LOG.log(Level.INFO, "Creating quote for company {0}", company);
        try {
            CarRentalCompany crc = em.find(CarRentalCompany.class, company);
            if (crc == null) {
                LOG.log(Level.WARNING, "Company {0} was not found", company);
                throw new ReservationException("Company " + company + " was not found");
            }
            Quote out = crc.createQuote(constraints, renter);
            quotes.add(out);
            LOG.log(Level.INFO, "Created quote for company {0}", company);
            return out;
        } catch(Exception e) {
            LOG.log(Level.WARNING, "Exception occurred on creating quote: {0}", e.getMessage());
            throw new ReservationException(e);
        }
        */
        List<CarRentalCompany> companies = em.createQuery("SELECT crc FROM CarRentalCompany crc", CarRentalCompany.class).getResultList();
        for(CarRentalCompany company: companies) {
            try {
                Quote createdQuote = company.createQuote(constraints, name);
                quotes.add(createdQuote);
                return createdQuote;
            } catch(Exception ignored) {
                LOG.info("Could not create quote: " + ignored.getMessage());
            }
        }
        throw new ReservationException("Unable to create quote"); 
    }

    @Override
    public List<Quote> getCurrentQuotes() {
        LOG.log(Level.INFO, "Retrieving current quotes");
        return quotes;
    }

    @Override
    public List<Reservation> confirmQuotes() throws ReservationException , Exception{
        LOG.log(Level.INFO, "Confirming quotes");
        List<Reservation> done = new LinkedList<>();
        try {
            for (Quote quote : quotes) {
                CarRentalCompany crc = em.find(CarRentalCompany.class, quote.getRentalCompany());
                done.add(crc.confirmQuote(quote));
            }
        } catch (Exception e) {
            // TODO Rollback
            LOG.log(Level.WARNING, "Exception occurred on creating quote: {0}", e.getMessage());
            throw new ReservationException(e);
        }
        LOG.log(Level.INFO, "Confirmed quotes");
        return done;
    }

    @Override
    public void setRenterName(String name) throws Exception {
        LOG.log(Level.INFO, "Setting renter name to {0}", name);
        if (renter != null) {
            LOG.log(Level.WARNING, "Renter name already set!");
            throw new IllegalStateException("name already set");
        }
        renter = name;
    }

    @Override
    public String getRenterName() {
        LOG.log(Level.INFO, "Retrieving rental name");
        return renter;
    }

    @Override
    public String getCheapestCarType(Date start, Date end, String region) throws Exception {
        LOG.log(Level.INFO, "Retreving cheapest car type {0} {1} {2}", new Object[]{start, end, region});
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
    
    // =============== TESTING PURPOSES FOR RMI ERRORS ===============

    @Override
    public void reveiveA(A a) throws Exception {
        LOG.log(Level.INFO, "Received A with value{0}", a.getName());
    }

    @Override
    public A sendA(String name) throws Exception {
        LOG.log(Level.INFO, "Sending A with name {0}", name);
        return new A(name);
    }

    @Override
    public void receiveSetOfA(Set<A> setA) throws Exception {
        LOG.log(Level.INFO, "Received set of A of size {0}", setA.size());
        setA.forEach((a) -> {
            LOG.info(a.getName());
        });
    }

    @Override
    public void receiveSetOfString(Set<String> setString) throws Exception {
        LOG.log(Level.INFO, "Received set of String of size {0}", setString.size());
        setString.forEach((s) -> {
            LOG.info(s);
        });
    }

    @Override
    public Set<A> sendSetA() throws Exception {
        LOG.info("Sending set<A>");
        Set<A> setA = new HashSet<>();
        setA.add(new A("a1-SERVER"));
        setA.add(new A("a2-SERVER"));
        setA.add(new A("a4-SERVER"));
        return setA;
    }
}