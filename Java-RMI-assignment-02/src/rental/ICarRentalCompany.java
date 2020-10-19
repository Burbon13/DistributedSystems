package rental;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;


public interface ICarRentalCompany {

	/**
	 * Retrieve all car types.
	 * 
	 * @return the collection of all the car types
	 */
	Collection<CarType> getAllCarTypes() ;
	
	/**
	 * Returns a type of CarType based on a given name (converts a String to CarType).
	 * 
	 * @param carTypeName   the name
	 * @return the equivalent CarType
	 */
	CarType getCarType(String carTypeName);
	
	/**
	 * Check which car types are available in the given period (across all companies
	 * and regions) and print this list of car types.
	 *
	 * @param start start time of the period
	 * @param end   end time of the period
	 * @return the set of the available car types
	 */
	Set<CarType> getAvailableCarTypes(Date date, Date end);
	
	/**
	 * Checks if there is any car of a given type available in a given period of time.
	 * 
	 * @param carTypeName	the desired car type
	 * @param start 		start time of the period
	 * @param end   		end time of the period
	 * @return true if available, false otherwise
	 */
	boolean isAvailable(String carTypeName, Date start, Date end);
	
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
	 */
	Quote createQuote(ReservationConstraints constraints, String client) throws ReservationException;
	
	/**
	 * Confirm the given quote to receive a final reservation of a car.
	 * 
	 * @param quote the quote to be confirmed
	 * @return the final reservation of a car
	 * 
	 * @throws ReservationException
	 */
	Reservation confirmQuote(Quote quote) throws ReservationException;
	
	/**
	 * Cancel a particular reservation.
	 * 
	 * @param res
	 */
	void cancelReservation(Reservation res) throws RemoteException;
	
	/**
	 * Get all reservations made by the given client.
	 *
	 * @param clientName name of the client
	 * @return the list of reservations of the given client
	 */
	List<Reservation> getReservationsByRenter(String clientName);
	
	/**
	 * Get the number of reservations for a particular car type.
	 * 
	 * @param carType name of the car type
	 * @return number of reservations for the given car type
	 * 
	 * @throws Exception
	 */
	int getNumberOfReservationsForCarType(String carType);
	
	String getName();
	
	List<String> getRegions();
	
	public double calculateRentalPrice(double rentalPricePerDay, Date start, Date end);
	
	public List<Reservation> getAllReservations();
	
	public List<Reservation> getReservationsForCarType(String carType);
}
