package session;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import rental.CarRentalCompany;
import rental.CarType;


@Stateless
@DeclareRoles("carManager")
public class ManagerSession implements ManagerSessionRemote {
    
    private static final Logger logger = Logger.getLogger(ManagerSession.class.getName());
    
    @PersistenceContext
    EntityManager em;
    
    @Override
    @RolesAllowed("carManager")
    public Set<CarType> getCarTypes(String company) {
        List<CarType> results = em.createQuery(""
                + "SELECT ct.name "
                + "FROM CarRentalCompany crc "
                + "INNER JOIN crc.carTypes ct "
                + "WHERE crc.name = :companyName")
                .setParameter("companyName", company)
                .getResultList();
        return new HashSet<>(results);
    }

    @Override
    @RolesAllowed("carManager")
    public Set<Integer> getCarIds(String company, String type) {
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

//    @Override
//    @RolesAllowed("carManager")
//    public int getNumberOfReservations(String company, String type, int id) {
//        List<Integer> results = em.createQuery("SELECT COUNT(r) FROM Car car, Company c WHERE c.name = :companyName AND c.id = :carId")
//                .setParameter("carId", id)
//                .setParameter("companyName", company)
//                .getResultList();
//        return results.get(0);
//    }

    @Override
    @RolesAllowed("carManager")
    public int getNumberOfReservations(String company, String type) {
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
    public void addCarRentalCompany(List<CarRentalCompany> companies) {
        logger.log(Level.INFO, "Persisting multiple car companies");
        // TODO: ADD LOCK FOR LOADING
        for(CarRentalCompany company: companies) {
            logger.log(Level.INFO, "Persisting company {0}", company.getName());
            em.persist(company);
        }
    }

    @Override
    public Set<String> getBestClients() {
        // TODO
        return new HashSet<>();
    }

    @Override
    public CarType getMostPopularCarTypeIn(String carRentalCompanyName, int year) {
        // TODO
        return null;
    }

    @Override
    public int getNumberOfReservationsBy(String clientName) {
        // TODO
        return 0;
    }
}
