package session;

import javax.ejb.Remote;

@Remote
public interface ManagerSessionRemote {
    
    int getNumberOfReservationsForCarType(String carRentalName, String carType);
    
    int getNumberOfReservationsBy(String clientName);
}
