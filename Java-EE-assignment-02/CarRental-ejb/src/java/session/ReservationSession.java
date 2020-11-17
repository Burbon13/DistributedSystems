package session;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import rental.CarRentalCompany;
import rental.CarType;
import rental.Quote;
import rental.Reservation;
import rental.ReservationConstraints;
import rental.ReservationException;

@Stateful
public class ReservationSession implements ReservationSessionRemote {
    
    private static final Logger LOG = Logger.getLogger(ReservationSession.class.getName());
    
    @Resource
    SessionContext ctx; 
    
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
        List<CarType> availableCarTypes = em.createQuery(""
                + "SELECT DISTINCT c.type " 
                + "FROM Car c " 
                + "WHERE " 
                + ":startDate > ALL (SELECT r.endDate FROM Reservation r WHERE r.carId = c.id AND r.endDate <= :endDate) " 
                + "AND " 
                + ":endDate < ALL (SELECT r.startDate FROM Reservation r WHERE r.carId = c.id AND r.endDate > :endDate) ", 
                CarType.class)
                .setParameter("startDate", start)
                .setParameter("endDate", end)
                .getResultList();
        return availableCarTypes;
    }

    @Override
    public Quote createQuote(String name, ReservationConstraints constraints) throws ReservationException, Exception {
        List<CarRentalCompany> companies = em.createQuery("SELECT crc FROM CarRentalCompany crc", CarRentalCompany.class).getResultList();
        for(CarRentalCompany company: companies) {
            try {
                Quote createdQuote = company.createQuote(constraints, name);
                quotes.add(createdQuote);
                return createdQuote;
            } catch(Exception ignored) {
                LOG.info("Could not create quote: " + ignored.getMessage());
                if(ignored.getMessage() == null) {
                    ignored.printStackTrace();
                }
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
                Reservation res = crc.confirmQuote(quote);
                done.add(res);
                em.persist(res);
            }
        } catch (Exception e) {
            LOG.log(Level.WARNING, "ROLLBACK: Exception occurred on creating quote: {0}", e.getMessage());
            ctx.setRollbackOnly();
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
        List<CarType> carTypesByPrice = em.createQuery(""
            + "SELECT c.type " 
            + "FROM CarRentalCompany crc " 
            + "INNER JOIN crc.cars c "    
            + "INNER JOIN crc.regions r "
            + "WHERE " 
            + "r = :region AND "
            + ":startDate > ALL (SELECT r.endDate FROM Reservation r WHERE r.carId = c.id AND r.endDate <= :endDate) " //?
            + "AND " 
            + ":endDate < ALL (SELECT r.startDate FROM Reservation r WHERE r.carId = c.id AND r.endDate > :endDate) " //?
            + "ORDER BY c.type.rentalPricePerDay ASC",
            CarType.class)
            .setParameter("startDate", start)
            .setParameter("endDate", end)
            .setParameter("region", region)
            .getResultList();
        return carTypesByPrice.get(0).getName();
    }
}