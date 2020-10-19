package rental;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RentalAgency implements IAgency {
	// TODO: do we need to store sessions??   Set of sessions, throw Exception if not saved
	private Map<String, ICarRentalCompany> carRentalCompanies; // Transform this into Map
	private Map<ReservationSession, List<Quote>> quotesBySession;
	
	RentalAgency(List<ICarRentalCompany> carRentalCompanies) {
		this.carRentalCompanies = new HashMap<>();
		for(ICarRentalCompany company: carRentalCompanies) {
			this.carRentalCompanies.put(company.getName(), company);
		}
		this.quotesBySession = new HashMap<>();
	}

	@Override
	synchronized public ReservationSession getNewReservationSession(String clientName) throws RemoteException {
		ReservationSession newReservationSession = new ReservationSession(clientName);
		quotesBySession.put(newReservationSession, new ArrayList<>());
		return newReservationSession;
	}

	@Override
	synchronized public ManagerSession getNewManagerSession(String clientName) throws RemoteException {
		ManagerSession newManagerSession = new ManagerSession(clientName);
		return newManagerSession;
	}

	@Override
	synchronized public Set<CarType> checkForAvailableCarTypes(ReservationSession session, Date start, Date end) throws RemoteException {
		Set<CarType> availableCarTypes = new HashSet<CarType>();
		for(ICarRentalCompany company: carRentalCompanies.values()) {
			availableCarTypes.addAll(company.getAvailableCarTypes(start,  end));
		}
		return availableCarTypes;
	}

	@Override
	synchronized public void addQuoteToSession(ReservationSession session, String name, Date start, Date end, String carType,
			String region) throws RemoteException, ReservationException {
		ReservationConstraints contraints = new ReservationConstraints(start, end, carType, region);
		for(ICarRentalCompany company: carRentalCompanies.values()) {
			try {
				Quote newQuote = company.createQuote(contraints, session.getClientName());
				quotesBySession.get(session).add(newQuote);
				return;
			} catch (ReservationException e) {
				
			}
		}
		throw new ReservationException("No available car");
	}

	@Override
	synchronized public List<Reservation> confirmQuotes(ReservationSession session, String name) throws RemoteException {
		List<Reservation> reservations = new ArrayList<>();
		try {
			for(Quote quote: quotesBySession.get(session)) {
				String companyName = quote.getRentalCompany();
				reservations.add(carRentalCompanies.get(companyName).confirmQuote(quote));
			}
		} catch(ReservationException e) {
			for(Reservation reservation: reservations) {
				String companyName = reservation.getRentalCompany();
				carRentalCompanies.get(companyName).cancelReservation(reservation);
			}
		}
		return reservations;
	}

	@Override
	synchronized public List<Quote> getCurrentQuotes(ReservationSession session) throws RemoteException {
		return quotesBySession.get(session);
	}

	@Override
	synchronized public CarType getCheapestCarType(ReservationSession session, Date start, Date end, String region)  throws RemoteException {
		CarType cheapestCarType = null;
		double cheapestPrice = -1;
		
		for(ICarRentalCompany company: carRentalCompanies.values()) {
			if(company.getRegions().contains(region)) {
				Set<CarType> available = company.getAvailableCarTypes(start, end);
				for(CarType carType: available) {
					double price = company.calculateRentalPrice(carType.getRentalPricePerDay(), start, end);
					if (cheapestPrice == -1 || cheapestPrice > price) {
						cheapestCarType = carType;
						cheapestPrice = price;
					}
				}
			}
		}
		
		return cheapestCarType;
	}
	
	@Override
	synchronized public int getNumberOfReservationsByRenter(ManagerSession managerSession, String clientName)
			throws RemoteException {
		int numberReservations = 0;
		for(ICarRentalCompany company: carRentalCompanies.values()) {
			numberReservations += company.getReservationsByRenter(clientName).size();
		}
		return numberReservations;
	}

	@Override
	synchronized public int getNumberOfReservationsForCarType(ManagerSession managerSession, String carRentalName, String carType)
			throws RemoteException {
		ICarRentalCompany company = carRentalCompanies.get(carRentalName);
		return company.getNumberOfReservationsForCarType(carType);
	}

	@Override
	synchronized public List<String> getBestCustomers(ManagerSession managerSession) throws RemoteException {
		Map<String, Integer> customers = new HashMap<>();
		
		for(ICarRentalCompany company: carRentalCompanies.values()) {
			for(Reservation reservation: company.getAllReservations()) {
				if (!customers.containsKey(reservation.getCarRenter())) {
					customers.put(reservation.getCarRenter(), 1);
				} else {
					int actualNr = customers.get(reservation.getCarRenter());
					customers.put(reservation.getCarRenter(), actualNr + 1);
				}
			}
		}
		
		int maximum = -1;
		List<String> bestCustomers = new ArrayList<>();
		for(String customer: customers.keySet()) {
			int nrOfRevs = customers.get(customer);
			if (nrOfRevs > maximum) {
				maximum = nrOfRevs;
				bestCustomers = new ArrayList<String>();
				bestCustomers.add(customer);
			} else if(nrOfRevs == maximum) {
				bestCustomers.add(customer);
			}
		}
		return bestCustomers;
	}

	@Override
	synchronized public CarType getMostPopularCarType(ManagerSession managerSession, String carRentalCompany, int year)
			throws RemoteException {
		ICarRentalCompany company = carRentalCompanies.get(carRentalCompany);
		int maximum = -1;
		CarType bestCarType = null;
		for(CarType carType: company.getAllCarTypes()) {
			int counter = 0;
			List<Reservation> reservationList = company.getReservationsForCarType(carType.getName());
			for(Reservation reservation: reservationList) {
				if(reservation.getStartDate().getYear() == year) {
					counter ++;
				}
			}
			if(counter > maximum) {
				maximum = counter;
				bestCarType = carType;
			}
		}
		return bestCarType;
	}
}
