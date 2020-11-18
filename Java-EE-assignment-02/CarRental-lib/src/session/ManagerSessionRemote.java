package session;

import java.util.List;
import java.util.Set;
import javax.ejb.Remote;
import rental.CarType;

@Remote
public interface ManagerSessionRemote {
    
    public void initializeNewCarRentalCompany(String name, List<String> regions) throws Exception;
    
    public void insertNewCar(String companyName, CarType carType, int nrOfCars) throws Exception;
    
    public Set<CarType> getCarTypes(String company) throws Exception;
    
    public Set<Integer> getCarIds(String company,String type) throws Exception;
    
     public int getNumberOfReservations(int carId);
    
    public int getNumberOfReservations(String company, String type) throws Exception;
    
    public Set<String> getBestClients() throws Exception;
    
    public CarType getMostPopularCarTypeIn(String carRentalCompanyName, int year) throws Exception;
    
    public int getNumberOfReservationsBy(String clientName) throws Exception;
}
