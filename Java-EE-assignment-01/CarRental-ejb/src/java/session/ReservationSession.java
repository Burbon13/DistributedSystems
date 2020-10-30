package session;

import java.util.HashSet;
import java.util.Set;
import javax.ejb.Stateful;
import rental.RentalStore;

@Stateful
public class ReservationSession implements ReservationSessionRemote {

    @Override
    public Set<String> getAllRentalCompanies() {
        return new HashSet<String>(RentalStore.getRentals().keySet());
    }

    
    
}
