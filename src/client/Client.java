package client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import rental.CarType;
import rental.ICarRentalCompany;
import rental.Quote;
import rental.RentalServer;
import rental.Reservation;
import rental.ReservationConstraints;

public class Client extends AbstractTestBooking {

	/********
	 * MAIN *
	 ********/

	private final static int LOCAL = 0;
	private final static int REMOTE = 1;
	private ICarRentalCompany carRentalCompany;
	private final static Logger LOGGER = Logger.getLogger(RentalServer.class.getName());
	private final static String REMOTE_SERVER_CLASS = "CarRentalCompany";
	
	/**
	 * The `main` method is used to launch the client application and run the test
	 * script.
	 */
	public static void main(String[] args) throws Exception {
		System.setSecurityManager(null);
		// The first argument passed to the `main` method (if present)
		// indicates whether the application is run on the remote setup or not.
		int localOrRemote = (args.length == 1 && args[0].equals("REMOTE")) ? REMOTE : LOCAL;
		
		if(localOrRemote == REMOTE) {
			throw new UnsupportedOperationException("Remote implementation not available");
		}
		
		// Locate RMI registry
		Registry registry = null;
		try {
			registry = LocateRegistry.getRegistry();
		} catch(RemoteException e) {
			LOGGER.log(Level.SEVERE, "Could not locate RMI registry");
			System.exit(-1);
		}
		
		try {
			ICarRentalCompany carRentalCompany = (ICarRentalCompany) registry.lookup(REMOTE_SERVER_CLASS);
			LOGGER.log(Level.INFO, "Found remote refference to {0}", REMOTE_SERVER_CLASS);
			// An example reservation scenario on car rental company 'Hertz' would be...
			String carRentalCompanyName = "Hertz";
			Client client = new Client("simpleTrips", carRentalCompanyName, localOrRemote, carRentalCompany);
			client.run();
		} catch(NotBoundException e) {
			LOGGER.log(Level.SEVERE, "Cound not find remote class {0}", REMOTE_SERVER_CLASS);
			System.exit(-1);
		} catch (RemoteException e) {
			LOGGER.log(Level.SEVERE, "Remote error: {0}", e.getMessage());
			System.exit(-1);
		}
	}

	/***************
	 * CONSTRUCTOR *
	 ***************/

	public Client(String scriptFile, String carRentalCompanyName, int localOrRemote, ICarRentalCompany carRentalCompany) {
		super(scriptFile);
		this.carRentalCompany = carRentalCompany;
	}

	/**
	 * Check which car types are available in the given period (across all companies
	 * and regions) and print this list of car types.
	 *
	 * @param start start time of the period
	 * @param end   end time of the period
	 * @throws Exception if things go wrong, throw exception
	 */
	@Override
	protected void checkForAvailableCarTypes(Date start, Date end) throws Exception {
		Set<CarType> carTypes = carRentalCompany.getAvailableCarTypes(start, end);
		for (CarType carType: carTypes) {
			System.out.println(carType);
		}
	}

	/**
	 * Retrieve a quote for a given car type (tentative reservation).
	 * 
	 * @param clientName name of the client
	 * @param start      start time for the quote
	 * @param end        end time for the quote
	 * @param carType    type of car to be reserved
	 * @param region     region in which car must be available
	 * @return the newly created quote
	 * 
	 * @throws Exception if things go wrong, throw exception
	 */
	@Override
	protected Quote createQuote(String clientName, Date start, Date end, String carType, String region)
			throws Exception {
		return carRentalCompany.createQuote(new ReservationConstraints(start, end, carType, region), clientName);
	}

	/**
	 * Confirm the given quote to receive a final reservation of a car.
	 * 
	 * @param quote the quote to be confirmed
	 * @return the final reservation of a car
	 * 
	 * @throws Exception if things go wrong, throw exception
	 */
	@Override
	protected Reservation confirmQuote(Quote quote) throws Exception {
		return carRentalCompany.confirmQuote(quote);
	}

	/**
	 * Get all reservations made by the given client.
	 *
	 * @param clientName name of the client
	 * @return the list of reservations of the given client
	 * 
	 * @throws Exception if things go wrong, throw exception
	 */
	@Override
	protected List<Reservation> getReservationsByRenter(String clientName) throws Exception {
		return carRentalCompany.getReservationsByRenter(clientName);
	}

	/**
	 * Get the number of reservations for a particular car type.
	 * 
	 * @param carType name of the car type
	 * @return number of reservations for the given car type
	 * 
	 * @throws Exception if things go wrong, throw exception
	 */
	@Override
	protected int getNumberOfReservationsForCarType(String carType) throws Exception {
		return carRentalCompany.getNumberOfReservationsForCarType(carType);
	}
}
