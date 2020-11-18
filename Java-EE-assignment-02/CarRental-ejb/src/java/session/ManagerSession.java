package session;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import rental.CarType;
import rental.CarRentalCompany;

@Stateless
@DeclareRoles("carManager")
public class ManagerSession implements ManagerSessionRemote {
    
    private static final Logger LOG = Logger.getLogger(ManagerSession.class.getName());
    
    private final static AtomicBoolean LOAD_IN_PROCESS = new AtomicBoolean(false);
    
    public static boolean isLoading() {
        return LOAD_IN_PROCESS.get();
    }
    
    @PersistenceContext
    EntityManager em;
    
    @Override
    public void setLoadingInProgress(boolean inProgress) throws Exception {
        LOG.log(Level.INFO, "Setting loading boolean to {0}", inProgress);
        LOAD_IN_PROCESS.set(inProgress);
    }
    
    @Override
    @RolesAllowed("carManager")
    public void initializeNewCarRentalCompany(String name, List<String> regions) throws Exception {
        LOG.log(Level.INFO, "Initializing new car rental company name=<{0}> with nr. of regions=<{1}>", new Object[]{name, regions.size()});
        CarRentalCompany newCompany = new CarRentalCompany(name, regions, new ArrayList<>());
        em.persist(newCompany);
    }

    @Override
    @RolesAllowed("carManager")
    public void insertNewCar(String companyName, CarType carType, int nrOfCars) throws Exception {
        LOG.log(Level.INFO, "Inserting new car <{0}> in company <{1}>", new Object[]{carType, companyName});
        CarRentalCompany carRentalCompany = em.find(CarRentalCompany.class, companyName);
        carRentalCompany.addNewCar(carType, nrOfCars);
    }
    
    @Override
    @RolesAllowed("carManager")
    public Set<CarType> getCarTypes(String company) throws Exception{
        LOG.log(Level.INFO, "Retrieving car types for company {0}", company);
        List<CarType> results = em.createQuery(""
                + "SELECT t.name "
                + "FROM CarRentalCompany crc "
                + "INNER JOIN crc.cars c "
                + "INNER JOIN c.type t "
                + "WHERE crc.name = :companyName")
                .setParameter("companyName", company)
                .getResultList();
        return new HashSet<>(results);
    }

    @Override
    @RolesAllowed("carManager")
    public Set<Integer> getCarIds(String company, String type) throws Exception{
        LOG.log(Level.INFO, "Getting car ids for comany {0} and type {1}", new Object[]{company, type});
        List<Integer> results = em.createQuery(""
                + "SELECT c.id "
                + "FROM CarRentalCompany crc "
                + "INNER JOIN crc.cars c "
                + "WHERE crc.name = :companyName AND c.type = :carType")
                .setParameter("companyName", company)
                .setParameter("carType", type)
                .getResultList();
        return new HashSet<>(results);
    }

    @Override
    @RolesAllowed("carManager")
    public int getNumberOfReservations(int id) {
        LOG.log(Level.INFO, "Getting number of reservations car id {0}", id);
        TypedQuery<Long> query = em.createQuery(""
                + "SELECT COUNT(r) "
                + "FROM Car c "
                + "INNER JOIN c.reservations r "
                + "WHERE c.id = :carId", Long.class)
                .setParameter("carId", id);
        return query.getSingleResult().intValue();
    }

    @Override
    @RolesAllowed("carManager")
    public int getNumberOfReservations(String company, String type) throws Exception{
        LOG.log(Level.INFO, "Getting number of reservations for company {0} and type {1}", new Object[]{company, type});
        TypedQuery<Long> query = em.createQuery(""
                + "SELECT COUNT(r) "
                + "FROM CarRentalCompany crc "
                + "INNER JOIN crc.cars c "
                + "INNER JOIN c.reservations r "
                + "WHERE crc.name = :companyName AND c.type.name = :carType", Long.class)
                .setParameter("carType", type)
                .setParameter("companyName", company);
        return query.getSingleResult().intValue();
    }

    @Override
    @RolesAllowed("carManager")
    public Set<String> getBestClients() throws Exception{
        LOG.log(Level.INFO, "Retrieving best clients!");
        TypedQuery<Long> query1 =em.createQuery(""
            + "SELECT COUNT(r) "
            + "FROM CarRentalCompany crc "
            + "INNER JOIN crc.cars c "
            + "INNER JOIN c.reservations r "
            + "GROUP BY r.carRenter " 
            + "ORDER BY COUNT(r) DESC", Long.class);
        int max = query1.getResultList().get(0).intValue();
        List<String> query2 = em.createQuery(""
            + "SELECT r.carRenter "
            + "FROM CarRentalCompany crc "
            + "INNER JOIN crc.cars c "
            + "INNER JOIN c.reservations r "
            + "GROUP BY r.carRenter "
            + "HAVING COUNT(r) = :max_val")
            .setParameter("max_val", max)
            .getResultList();
        return new HashSet<>(query2);
    }

    @Override
    @RolesAllowed("carManager")
    public CarType getMostPopularCarTypeIn(String carRentalCompanyName, int year) throws Exception{
        LOG.log(Level.INFO, "Retrieving most popular car type {0} {1}", new Object[]{carRentalCompanyName, year});
        List<CarType> carTypes = em.createQuery(""
            + "SELECT c.type AS ctype "
            + "FROM CarRentalCompany crc "
            + "INNER JOIN crc.cars c "
            + "INNER JOIN c.reservations r "
            + "WHERE crc.name = :companyName AND EXTRACT(YEAR FROM r.startDate) = :definedYear "
            + "GROUP BY c.type "
            + "ORDER BY COUNT(r) DESC ", CarType.class)
            .setParameter("companyName", carRentalCompanyName)
            .setParameter("definedYear", year)
            .getResultList();
        return carTypes.get(0);
    }

    @Override
    @RolesAllowed("carManager")
    public int getNumberOfReservationsBy(String clientName) throws Exception{
        // TODO
        LOG.log(Level.INFO, "Getting number of reservations by {0}", clientName);
        TypedQuery<Long> query = em.createQuery(""
                + "SELECT COUNT(r) "
                + "FROM CarRentalCompany crc "
                + "INNER JOIN crc.cars c "
                + "INNER JOIN c.reservations r "
                + "WHERE r.carRenter = :client", Long.class)
                .setParameter("client", clientName);
        return query.getSingleResult().intValue();
    }
}
