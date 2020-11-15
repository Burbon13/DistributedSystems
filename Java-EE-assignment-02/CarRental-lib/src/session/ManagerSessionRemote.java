package session;

import java.util.Set;
import javax.ejb.Remote;
import rental.CarType;

@Remote
public interface ManagerSessionRemote {
    
    public Set<CarType> getCarTypes(String company) throws Exception;
    
    public Set<Integer> getCarIds(String company,String type) throws Exception;
    
    // TODO: Decide if to implement or remove
    // public int getNumberOfReservations(String company, String type, int carId);
    
    public int getNumberOfReservations(String company, String type) throws Exception;
      
    // TODO: Change to a parameterized method 
    public void loadCarRentalCompanies(/*List<CarRentalCompany> companies*/) throws Exception;
    
    public Set<String> getBestClients() throws Exception;
    
    public CarType getMostPopularCarTypeIn(String carRentalCompanyName, int year) throws Exception;
    
    public int getNumberOfReservationsBy(String clientName) throws Exception;
}
