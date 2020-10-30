package session;

import java.util.List;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import rental.Car;
import rental.RentalStore;


@DeclareRoles("manager")
@Stateless
public class ManagerSession implements ManagerSessionRemote {

    @Override
    @RolesAllowed("manager")
    public int getNumberOfReservationsForCarType(String carRentalName, String carType) {
        int total = 0;
        List<Car> cars = RentalStore.getRental(carRentalName).getCars();
        for(Car car: cars) {
            if(car.getType().toString().equals(carType)) {
                total ++;
            }
        }
       return total;
    }

    @Override
    @RolesAllowed("manager")
    public int getNumberOfReservationsBy(String clientName) {
        int total = 0;
        for(String crc: RentalStore.getRentals().keySet()) {
            total += RentalStore.getRental(crc).getReservationsBy(clientName).size();
        }
        return total;
    }   
}
