package session;

import java.util.List;
import java.util.Set;
import javax.ejb.Remote;
import rental.CarType;
import rental.CarRentalCompany;

@Remote
public interface ManagerSessionRemote {
    
    public Set<CarType> getCarTypes(String company) throws Exception;
    
    public Set<Integer> getCarIds(String company,String type) throws Exception;
    
    // public int getNumberOfReservations(String company, String type, int carId);
    
    public int getNumberOfReservations(String company, String type) throws Exception;
      
    public void addCarRentalCompany(List<CarRentalCompany> companies) throws Exception;
    
    public Set<String> getBestClients() throws Exception;
    
    public CarType getMostPopularCarTypeIn(String carRentalCompanyName, int year) throws Exception;
    
    public int getNumberOfReservationsBy(String clientName) throws Exception;
}
