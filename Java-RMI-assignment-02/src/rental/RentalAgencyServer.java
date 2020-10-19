package rental;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;


public class RentalAgencyServer implements IAgency{
	
	private final static int LOCAL = 0;
	private final static int REMOTE = 1;
	private final static Logger LOGGER = Logger.getLogger(RentalAgencyServer.class.getName());
	private final static String REMOTE_SERVER_CLASS = "RentalAgencyServer";
	private final static String REMOTE_ADDRESS = "10.10.10.13";
	private final static int REMOTE_PORT = 54321;

	public static void main(String[] args) throws ReservationException,
			NumberFormatException, IOException {
		// The first argument passed to the `main` method (if present)
		// indicates whether the application is run on the remote setup or not.
		int localOrRemote = (args.length == 1 && args[0].equals("REMOTE")) ? REMOTE : LOCAL;

		CrcData hertzData = loadData("hertz.csv");
		ICarRentalCompany hertzCompany = new CarRentalCompany(hertzData.name, hertzData.regions, hertzData.cars);
		
		CrcData dockxData = loadData("dockx.csv");
		ICarRentalCompany dockxCompany = new CarRentalCompany(dockxData.name, dockxData.regions, dockxData.cars);
		
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
				System.setSecurityManager(null); // Throws some security exception if called when the server is REMOTE
				registry = LocateRegistry.getRegistry();
			}
		} catch(RemoteException e) {
			LOGGER.log(Level.SEVERE, "Could not locate RMI registry");
			System.exit(-1);
		}
		
		ICarRentalCompany stub;
		try {
			stub = (ICarRentalCompany) UnicastRemoteObject.exportObject(carRentalCompany, 0);
			registry.rebind(REMOTE_SERVER_CLASS, stub);
		} catch (RemoteException e) {
			LOGGER.log(Level.SEVERE, "Could not rebind {0}", REMOTE_SERVER_CLASS);
			System.exit(-1);
		}
		
		LOGGER.log(Level.INFO, "Server ready");
	}

	public static CrcData loadData(String datafile)
			throws ReservationException, NumberFormatException, IOException {

		CrcData out = new CrcData();
		int nextuid = 0;

		// open file
		InputStream stream = MethodHandles.lookup().lookupClass().getClassLoader().getResourceAsStream(datafile);
		if (stream == null) {
			System.err.println("Could not find data file " + datafile);
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(stream));
		StringTokenizer csvReader;
		
		try {
			// while next line exists
			while (in.ready()) {
				String line = in.readLine();
				
				if (line.startsWith("#")) {
					// comment -> skip					
				} else if (line.startsWith("-")) {
					csvReader = new StringTokenizer(line.substring(1), ",");
					out.name = csvReader.nextToken();
					out.regions = Arrays.asList(csvReader.nextToken().split(":"));
				} else {
					// tokenize on ,
					csvReader = new StringTokenizer(line, ",");
					// create new car type from first 5 fields
					CarType type = new CarType(csvReader.nextToken(),
							Integer.parseInt(csvReader.nextToken()),
							Float.parseFloat(csvReader.nextToken()),
							Double.parseDouble(csvReader.nextToken()),
							Boolean.parseBoolean(csvReader.nextToken()));
					System.out.println(type);
					// create N new cars with given type, where N is the 5th field
					for (int i = Integer.parseInt(csvReader.nextToken()); i > 0; i--) {
						out.cars.add(new Car(nextuid++, type));
					}
				}
			}
		} finally {
			in.close();
		}

		return out;
	}
	
	static class CrcData {
		public List<Car> cars = new LinkedList<Car>();
		public String name;
		public List<String> regions =  new LinkedList<String>();
	}

}
