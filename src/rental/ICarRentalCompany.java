package rental;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;


public interface ICarRentalCompany extends Remote{

	Collection<CarType> getAllCarTypes() throws RemoteException;
	
	CarType getCarType(String carTypeName) throws RemoteException;
	
	Set<CarType> getAvailableCarTypes(Date date, Date end) throws RemoteException;
	
	boolean isAvailable(String carTypeName, Date start, Date end) throws RemoteException;
	
	Quote createQuote(ReservationConstraints constraints, String client) throws ReservationException, RemoteException;
	
	Reservation confirmQuote(Quote quote) throws ReservationException, RemoteException;
	
	void cancelReservation(Reservation res) throws RemoteException;
	
	List<Reservation> getReservationsByRenter(String clientName) throws RemoteException;
	
	int getNumberOfReservationsForCarType(String carType) throws Exception, RemoteException;
}
