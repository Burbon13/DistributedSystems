package session;

import java.util.List;
import java.util.Set;
import javax.ejb.Remote;
import rental.CarType;
import rental.CarRentalCompany;

@Remote
public interface ManagerSessionRemote {
    
    public Set<CarType> getCarTypes(String company);
    
    public Set<Integer> getCarIds(String company,String type);
    
    public int getNumberOfReservations(String company, String type, int carId);
    
    public int getNumberOfReservations(String company, String type);
      
    public void addCarRentalCompany(List<CarRentalCompany> companies);
}
