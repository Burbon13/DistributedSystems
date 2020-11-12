package session;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import rental.Car;
import rental.CarRentalCompany;
import rental.CarType;
import rental.RentalStore;
import rental.Reservation;

@Stateless
public class ManagerSession implements ManagerSessionRemote {
    
    @PersistenceContext
    EntityManager em;
    
    @Override
    public Set<CarType> getCarTypes(String company) {
        List<CarType> results = em.createQuery("SELECT carType FROM CarRentalCompany company WHERE company = :companyName")
                .setParameter("companyName", company)
                .getResultList();
        return new HashSet<>(results);
    }

    @Override
    public Set<Integer> getCarIds(String company, String type) {
        List<Integer> results = em.createQuery("SELECT c.id FROM CarRentalCompany company WHERE company = :companyName AND CarType = :carType")
                .setParameter("companyName", company)
                .setParameter("carType", type)
                .getResultList();
        return new HashSet<>(results);
    }

    @Override
    public int getNumberOfReservations(String company, String type, int id) {
        List<Integer> results = em.createQuery("SELECT COUNT(r) FROM Car car, Company c WHERE c.name = :companyName AND c.id = :carId")
                .setParameter("carId", id)
                .setParameter("companyName", company)
                .getResultList();
        return results.get(0);
    }

    @Override
    public int getNumberOfReservations(String company, String type) {
        List<Integer> results = em.createQuery("SELECT COUNT(r) FROM Car car, Company c WHERE c.name = :companyName AND c.carType = :type")
                .setParameter("carType", type)
                .setParameter("companyName", company)
                .getResultList();
        return results.get(0);
    }

    @Override
    public void addCarRentalCompany(List<CarRentalCompany> companies) {
        for(CarRentalCompany company: companies) {
            em.persist(company);
        }
    }
}
