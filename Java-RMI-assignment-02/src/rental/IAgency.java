package rental;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

public interface IAgency extends Remote{
	ReservationSession getNewReservationSession(String clientName) throws RemoteException;
	
	void endReservationSession(ReservationSession session) throws RemoteException;
	
	ManagerSession getNewManagerSession(String clientName) throws RemoteException;
	
	void endManagerSession(ManagerSession session) throws RemoteException;
	
	Set<CarType> checkForAvailableCarTypes(ReservationSession session, Date start, Date end) throws RemoteException;
	
	void addQuoteToSession(ReservationSession session, String name, Date start, Date end, String carType, String region) throws RemoteException, ReservationException;
	
	List<Reservation> confirmQuotes(ReservationSession session, String name) throws RemoteException, ReservationException ;
	
	List<Quote> getCurrentQuotes(ReservationSession session) throws RemoteException;
	
	CarType getCheapestCarType(ReservationSession session, Date start, Date end, String region) throws RemoteException;
	
	int getNumberOfReservationsByRenter(ManagerSession managerSession, String clientName) throws RemoteException;
	
	int getNumberOfReservationsForCarType(ManagerSession managerSession, String carRentalName, String carType) throws RemoteException;
	
	Set<String> getBestCustomers(ManagerSession managerSession) throws RemoteException;
	
	CarType getMostPopularCarType(ManagerSession managerSession, String carRentalCompany, int year) throws RemoteException;
	
	void registerCRC(ManagerSession managerSession, String crcName) throws RemoteException;
	
	void unregisterCRC(ManagerSession managerSession, String crcName) throws RemoteException;
	
	Collection<ICarRentalCompany> getRegisteredCRC(ManagerSession managerSession) throws RemoteException;
}
