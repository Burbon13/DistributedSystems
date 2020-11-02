package session;

import java.util.List;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import rental.Car;
import rental.RentalStore;


@DeclareRoles("carManager")
@Stateless
public class ManagerSession implements ManagerSessionRemote {

    @Override
    @RolesAllowed("carManager")
    public int getNumberOfReservationsForCarType(String carRentalName, String carType) {
        int total = 0;
        List<Car> cars = RentalStore.getRental(carRentalName).getCars();
        for(Car car: cars) {
            if(car.getType().getName().equals(carType)) {
                total += car.getAllReservations().size();
            }
        }
       return total;
    }

    @Override
    @RolesAllowed("carManager")
    public int getNumberOfReservationsBy(String clientName) {
        int total = 0;
        for(String crc: RentalStore.getRentals().keySet()) {
            total += RentalStore.getRental(crc).getReservationsBy(clientName).size();
        }
        return total;
    }   
}
