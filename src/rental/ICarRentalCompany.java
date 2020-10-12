package rental;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;


public interface ICarRentalCompany extends Remote{

	/**
	 * Retrieve all car types.
	 * 
	 * @return the collection of all the car types
	 * 
	 * @throws RemoteException
	 */
	Collection<CarType> getAllCarTypes() throws RemoteException;
	
	/**
	 * Returns a type of CarType based on a given name (converts a String to CarType).
	 * 
	 * @param carTypeName   the name
	 * @return the equivalent CarType
	 * 
	 * @throws RemoteException
	 */
	CarType getCarType(String carTypeName) throws RemoteException;
	
	/**
	 * Check which car types are available in the given period (across all companies
	 * and regions) and print this list of car types.
	 *
	 * @param start start time of the period
	 * @param end   end time of the period
	 * @return the set of the available car types
	 * 
	 * @throws RemoteException
	 */
	Set<CarType> getAvailableCarTypes(Date date, Date end) throws RemoteException;
	
	/**
	 * Checks if there is any car of a given type available in a given period of time.
	 * 
	 * @param carTypeName	the desired car type
	 * @param start 		start time of the period
	 * @param end   		end time of the period
	 * @return true if available, false otherwise
	 * 
	 * @throws RemoteException
	 */
	boolean isAvailable(String carTypeName, Date start, Date end) throws RemoteException;
	
	/**
	 * 
	 * Retrieve a quote for a given car type (tentative reservation).
	 * 
	 * @param clientName name of the client
	 * @param start      start time for the quote
	 * @param end        end time for the quote
	 * @param carType    type of car to be reserved
	 * @param region     region in which car must be available
	 * @return the newly created quote
	 * 
	 * @throws ReservationException
	 * @throws RemoteException
	 */
	Quote createQuote(ReservationConstraints constraints, String client) throws ReservationException, RemoteException;
	
	/**
	 * Confirm the given quote to receive a final reservation of a car.
	 * 
	 * @param quote the quote to be confirmed
	 * @return the final reservation of a car
	 * 
	 * @throws ReservationException
	 * @throws RemoteException
	 */
	Reservation confirmQuote(Quote quote) throws ReservationException, RemoteException;
	
	/**
	 * Cancel a particular reservation.
	 * 
	 * @param res
	 * @throws RemoteException
	 */
	void cancelReservation(Reservation res) throws RemoteException;
	
	/**
	 * Get all reservations made by the given client.
	 *
	 * @param clientName name of the client
	 * @return the list of reservations of the given client
	 * 
	 * @throws RemoteException
	 */
	List<Reservation> getReservationsByRenter(String clientName) throws RemoteException;
	
	/**
	 * Get the number of reservations for a particular car type.
	 * 
	 * @param carType name of the car type
	 * @return number of reservations for the given car type
	 * 
	 * @throws Exception
	 * @throws RemoteException
	 */
	int getNumberOfReservationsForCarType(String carType) throws Exception, RemoteException;
}
