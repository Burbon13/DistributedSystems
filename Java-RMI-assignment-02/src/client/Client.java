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
import rental.IAgency;
import rental.ManagerSession;
import rental.Reservation;
import rental.ReservationSession;

public class Client extends AbstractTestManagement<ReservationSession, ManagerSession> {

	/********
	 * MAIN *
	 ********/

	private final static int LOCAL = 0;
	private final static int REMOTE = 1;
	private IAgency carAgencyServer;
	private final static Logger LOGGER = Logger.getLogger(Client.class.getName());
	private final static String REMOTE_SERVER_CLASS = "RentalAgencyServer";
	private final static String REMOTE_ADDRESS = "10.10.10.13";
	private final static int REMOTE_PORT = 54321;
	
	/**
	 * The `main` method is used to launch the client application and run the test
	 * script.
	 */
	public static void main(String[] args) throws Exception {
		System.setSecurityManager(null);
		// The first argument passed to the `main` method (if present)
		// indicates whether the application is run on the remote setup or not.
		int localOrRemote = (args.length == 1 && args[0].equals("REMOTE")) ? REMOTE : LOCAL;
		
		// Locate RMI registry
		Registry registry = null;
		try {
			if(localOrRemote == REMOTE) {
				LOGGER.log(
						Level.INFO, 
						"Server running remote on address {0} port {1}", 
						new Object[]{REMOTE_ADDRESS, REMOTE_PORT});
				registry = LocateRegistry.getRegistry(REMOTE_ADDRESS, REMOTE_PORT);
			} else {
				LOGGER.info("Server running localy");
				registry = LocateRegistry.getRegistry();
			}
		} catch(RemoteException e) {
			LOGGER.log(Level.SEVERE, "Could not locate RMI registry");
			System.exit(-1);
		}
		
		try {
			IAgency carAgency = (IAgency) registry.lookup(REMOTE_SERVER_CLASS);
			LOGGER.log(Level.INFO, "Found remote refference to {0}", REMOTE_SERVER_CLASS);
			// An example reservation scenario 
			String scriptFile = "trips";
			Client client = new Client(scriptFile, localOrRemote, carAgency);
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

	public Client(String scriptFile, int localOrRemote, IAgency carAgencyServer) {
		super(scriptFile);
		this.carAgencyServer = carAgencyServer;
	}

	@Override
	protected Set<String> getBestClients(ManagerSession ms) throws Exception {
		return carAgencyServer.getBestCustomers(ms);
	}

	@Override
	protected String getCheapestCarType(ReservationSession session, Date start, Date end, String region)
			throws Exception {
		return carAgencyServer.getCheapestCarType(session, start, end, region).getName();
	}

	@Override
	protected CarType getMostPopularCarTypeInCRC(ManagerSession ms, String carRentalCompanyName, int year)
			throws Exception {
		return carAgencyServer.getMostPopularCarType(ms, carRentalCompanyName, year);
	}

	@Override
	protected ReservationSession getNewReservationSession(String name) throws Exception {
		return carAgencyServer.getNewReservationSession(name);
	}

	@Override
	protected ManagerSession getNewManagerSession(String name) throws Exception {
		return carAgencyServer.getNewManagerSession(name);
	}

	@Override
	protected void checkForAvailableCarTypes(ReservationSession session, Date start, Date end) throws Exception {
		for(CarType carType: carAgencyServer.checkForAvailableCarTypes(session, start, end)) {
			LOGGER.log(Level.INFO, carType.getName());
		}
	}

	@Override
	protected void addQuoteToSession(ReservationSession session, String name, Date start, Date end, String carType,
			String region) throws Exception {
		carAgencyServer.addQuoteToSession(session, name, start, end, carType, region);
	}

	@Override
	protected List<Reservation> confirmQuotes(ReservationSession session, String name) throws Exception {
		return carAgencyServer.confirmQuotes(session, name);
	}

	@Override
	protected int getNumberOfReservationsByRenter(ManagerSession ms, String clientName) throws Exception {
		return carAgencyServer.getNumberOfReservationsByRenter(ms, clientName);
	}

	@Override
	protected int getNumberOfReservationsForCarType(ManagerSession ms, String carRentalName, String carType)
			throws Exception {
		return carAgencyServer.getNumberOfReservationsForCarType(ms, carRentalName, carType);
	}
}
