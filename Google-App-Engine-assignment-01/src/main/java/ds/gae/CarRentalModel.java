package ds.gae;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.PathElement;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;

import ds.gae.entities.Car;
import ds.gae.entities.CarRentalCompany;
import ds.gae.entities.CarType;
import ds.gae.entities.Quote;
import ds.gae.entities.Reservation;
import ds.gae.entities.ReservationConstraints;

public class CarRentalModel {
	
	private static final Logger logger = Logger.getLogger(CarRentalModel.class.getName());

    Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

    private static CarRentalModel instance;

    public static CarRentalModel get() {
        if (instance == null) {
            instance = new CarRentalModel();
        }
        return instance;
    }
    
    public void loadCarRentalCompany(String companyName) {
    	Key crcKey = datastore.newKeyFactory().setKind("CarRentalCompany") .newKey(companyName);
    	Entity crc = Entity.newBuilder(crcKey)
    			.set("name", companyName)
    			.build();
    	datastore.put(crc);
    }
    
    public void loadCarType(String companyName, CarType carType) {
    	Key carTypeKey = datastore
    			.newKeyFactory() 
    			.addAncestors(PathElement.of("CarRentalCompany", companyName)) 
    			.setKind("CarType")
    			.newKey(carType.getName());
    	Entity carTypeEntity = Entity.newBuilder(carTypeKey)
    			.set("name", carType.getName())
    			.set("numberOfSeats", carType.getNbOfSeats())
    			.set("trunkSpace", carType.getTrunkSpace())
    			.set("rentalPricePerDay", carType.getRentalPricePerDay())
    			.set("smokingAllowed", carType.isSmokingAllowed())
    			.build();
    	datastore.put(carTypeEntity);
    }
    
    public void loadCar(String companyName, String carType, int carId) {
    	Key carKey = datastore
    			.newKeyFactory() 
    			.addAncestors(PathElement.of("CarRentalCompany", companyName), PathElement.of("CarType", carType)) 
    			.setKind("Car")
    			.newKey(carId);
    	Entity carEntity = Entity.newBuilder(carKey)
    			.set("id", carId)
    			.build();
    	datastore.put(carEntity);
    }

    /**
     * Get the car types available in the given car rental company.
     *
     * @param companyName the car rental company
     * @return The list of car types (i.e. name of car type), available in the given
     * car rental company.
     */
    public Set<String> getCarTypesNames(String companyName) {
    	Query<Entity> query = Query.newEntityQueryBuilder()
    		    .setKind("CarType")
    		    .setFilter(
    		    		PropertyFilter.hasAncestor(
    		    				datastore
    		    				.newKeyFactory()
    		    				.setKind("CarRentalCompany")
    		    				.newKey(companyName)
    		    				)
    		    )
    		    .build();
    	QueryResults<Entity> carTypes = datastore.run(query);
    	Set<String> carTypesSet = new HashSet<>();
    	while (carTypes.hasNext()) {
    		  Entity carType = carTypes.next();
    		  carTypesSet.add(carType.getString("name"));
    		}
        return carTypesSet;
    }

    /**
     * Get the names of all registered car rental companies
     *
     * @return the list of car rental companies
     */
    public Collection<String> getAllRentalCompanyNames() {
    	Query<Entity> query = Query.newEntityQueryBuilder()
    		    .setKind("CarRentalCompany")
    		    .build();
    	QueryResults<Entity> carRentalCompanies = datastore.run(query);
    	Collection<String> companyCollection = new HashSet<>();
    	while (carRentalCompanies.hasNext()) {
    		  Entity company = carRentalCompanies.next();
    		  companyCollection.add(company.getString("name"));
    		}
        return companyCollection;
    }

    /**
     * Create a quote according to the given reservation constraints (tentative
     * reservation).
     *
     * @param companyName name of the car renter company
     * @param renterName  name of the car renter
     * @param constraints reservation constraints for the quote
     * @return The newly created quote.
     * @throws ReservationException No car available that fits the given
     *                              constraints.
     */
    public Quote createQuote(String companyName, String renterName, ReservationConstraints constraints)
            throws ReservationException {
        List<Car> cars = getCarsByCarType(companyName, constraints.getCarType());
        CarType desiredCarType = getCarType(companyName, constraints.getCarType());
        cars.forEach(car -> {
        	List<Reservation> reservations = getReservationsOfCar(companyName, car.getId());
        	reservations.forEach(r -> car.addReservation(r));
        	car.setCarType(desiredCarType);
        });
        CarRentalCompany crc = new CarRentalCompany(companyName, new HashSet<>(cars));
    	logger.info(crc.toString());
        return crc.createQuote(constraints, renterName);
    }

    /**
     * Confirm the given quote.
     *
     * @param quote Quote to confirm
     * @throws ReservationException Confirmation of given quote failed.
     */
    public void confirmQuote(Quote quote) throws ReservationException {
    	String companyName = quote.getRentalCompany();
    	String carType = quote.getCarType();
    	List<Car> cars = getCarsByCarType(companyName, carType);
        CarType desiredCarType = getCarType(companyName, carType);
        cars.forEach(car -> {
        	List<Reservation> reservations = getReservationsOfCar(companyName, car.getId());
        	reservations.forEach(r -> car.addReservation(r));
        	car.setCarType(desiredCarType);
        });
        CarRentalCompany crc = new CarRentalCompany(quote.getRentalCompany(), new HashSet<>(cars));
    	logger.info(crc.toString());
        Reservation r = crc.confirmQuote(quote);
        
        KeyFactory keyFactory = datastore.newKeyFactory() 
    			.addAncestors(
    					PathElement.of("CarRentalCompany", companyName),
    					PathElement.of("CarType", r.getCarType()),
    					PathElement.of("Car", r.getCarId())
    			) 
    			.setKind("Reservation");
        
        Key reservationKey = datastore.allocateId(keyFactory.newKey());
        
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");  
        String startDate = dateFormat.format(r.getStartDate());  
        String endDate = dateFormat.format(r.getEndDate());
        		
        Entity reservationEntity = Entity.newBuilder(reservationKey)
    			.set("carId", r.getCarId())
    			.set("renter", r.getRenter())
    			.set("startDate", startDate)
    			.set("endDate", endDate)
    			.set("rentalCompany", r.getRentalCompany())
    			.set("carType", r.getCarType())
    			.set("rentalPrice", r.getRentalPrice())
    			.build();
    	datastore.put(reservationEntity);
    }

    /**
     * Confirm the given list of quotes
     *
     * @param quotes the quotes to confirm
     * @return The list of reservations, resulting from confirming all given quotes.
     * @throws ReservationException One of the quotes cannot be confirmed. Therefore
     *                              none of the given quotes is confirmed.
     */
    public List<Reservation> confirmQuotes(List<Quote> quotes) throws ReservationException {
        // TODO: add implementation when time left, required for GAE2
        return null;
    }

    /**
     * Get all reservations made by the given car renter.
     *
     * @param renter name of the car renter
     * @return the list of reservations of the given car renter
     */
    public List<Reservation> getReservations(String renter) {
        // FIXME: use persistence instead
        List<Reservation> out = new ArrayList<>();
//        for (CarRentalCompany crc : CRCS.values()) {
//            for (Car c : crc.getCars()) {
//                for (Reservation r : c.getReservations()) {
//                    if (r.getRenter().equals(renter)) {
//                        out.add(r);
//                    }
//                }
//            }
//        }
        return out;
    }

    /**
     * Get the car types available in the given car rental company.
     *
     * @param companyName the given car rental company
     * @return The list of car types in the given car rental company.
     */
    public Collection<CarType> getCarTypesOfCarRentalCompany(String companyName) {
    	Query<Entity> query = Query.newEntityQueryBuilder()
    		    .setKind("CarType")
    		    .setFilter(
    		    		PropertyFilter.hasAncestor(
    		    				datastore
    		    				.newKeyFactory()
    		    				.setKind("CarRentalCompany")
    		    				.newKey(companyName)
    		    				)
    		    )
    		    .build();
    	QueryResults<Entity> carTypes = datastore.run(query);
    	Set<CarType> carTypesSet = new HashSet<>();
    	while (carTypes.hasNext()) {
    		  Entity carType = carTypes.next();
    		  carTypesSet.add(new CarType(
    				  carType.getString("name"),
    				  (int)carType.getLong("numberOfSeats"),
    				  (float)carType.getDouble("trunkSpace"),
    				  carType.getDouble("rentalPricePerDay"),
    				  carType.getBoolean("smokingAllowed")
    				 ));
    		}
        return carTypesSet;
    }

    /**
     * Get the list of cars of the given car type in the given car rental company.
     *
     * @param companyName name of the car rental company
     * @param carType     the given car type
     * @return A list of car IDs of cars with the given car type.
     */
    public Collection<Integer> getCarIdsByCarType(String companyName, CarType carType) {
        Collection<Integer> out = new ArrayList<>();
        Query<Entity> query = Query.newEntityQueryBuilder()
    		    .setKind("Car")
    		    .setFilter(
    		    		PropertyFilter.hasAncestor(
    	    		    		datastore
    	    		    			.newKeyFactory()
    	    		    			.addAncestors(PathElement.of("CarRentalCompany", companyName)) 
    	    		    			.setKind("CarType")
    	    		    			.newKey(carType.getName())
    	    		    	)		
    		    )
    		    .build();
    	QueryResults<Entity> cars = datastore.run(query);
    	while (cars.hasNext()) {
    		  Entity car = cars.next();
    		  out.add((int)car.getLong("id"));
    		}
        return out;
    }
    
    /**
     * Get the amount of cars of the given car type in the given car rental company.
     *
     * @param companyName name of the car rental company
     * @param carType     the given car type
     * @return A number, representing the amount of cars of the given car type.
     */
    public int getAmountOfCarsByCarType(String companyName, CarType carType) {
        return this.getCarsByCarType(companyName, carType).size();
    }

    /**
     * Get the list of cars of the given car type in the given car rental company.
     *
     * @param companyName name of the car rental company
     * @param carType     the given car type
     * @return List of cars of the given car type
     */
    private List<Car> getCarsByCarType(String companyName, CarType carType) {
        Query<Entity> query = Query.newEntityQueryBuilder()
    		    .setKind("Car")
    		    .setFilter(
    		    		PropertyFilter.hasAncestor(
    	    		    		datastore
    	    		    			.newKeyFactory()
    	    		    			.addAncestors(PathElement.of("CarRentalCompany", companyName)) 
    	    		    			.setKind("CarType")
    	    		    			.newKey(carType.getName())
    	    		    	)		
    		    )
    		    .build();
    	QueryResults<Entity> cars = datastore.run(query);
    	List<Car> out = new ArrayList<>();
    	while (cars.hasNext()) {
    		  Entity car = cars.next();
    		  Car newcar = new Car((int)car.getLong("id"));
    		  out.add(newcar);
    		}
        return out;

    }
    

    /**
     * Get the list of cars of the given car type in the given car rental company.
     *
     * @param companyName name of the car rental company
     * @param carType     the given car type
     * @return List of cars of the given car type
     */
    private List<Car> getCarsByCarType(String companyName, String carType) {
        Query<Entity> query = Query.newEntityQueryBuilder()
    		    .setKind("Car")
    		    .setFilter(
    		    		PropertyFilter.hasAncestor(
    	    		    		datastore
    	    		    			.newKeyFactory()
    	    		    			.addAncestors(PathElement.of("CarRentalCompany", companyName)) 
    	    		    			.setKind("CarType")
    	    		    			.newKey(carType)
    	    		    	)		
    		    )
    		    .build();
    	QueryResults<Entity> cars = datastore.run(query);
    	List<Car> out = new ArrayList<>();
    	while (cars.hasNext()) {
    		  Entity car = cars.next();
    		  Car newcar = new Car((int)car.getLong("id"));
    		  out.add(newcar);
    		}
        return out;

    }

    /**
     * Check whether the given car renter has reservations.
     *
     * @param renter the car renter
     * @return True if the number of reservations of the given car renter is higher
     * than 0. False otherwise.
     */
    public boolean hasReservations(String renter) {
        return this.getReservations(renter).size() > 0;
    }
    
    public List<Reservation> getReservationsOfCar(String companyName, int carId) {
    	Query<Entity> query = Query.newEntityQueryBuilder()
    		    .setKind("Reservation")
    		    .setFilter(
    		    		PropertyFilter.hasAncestor(
    	    		    		datastore
    	    		    			.newKeyFactory()
    	    		    			.addAncestors(PathElement.of("CarRentalCompany", companyName)) 
    	    		    			.setKind("Car")
    	    		    			.newKey(carId)
    	    		    	)		
    		    )
    		    .build();
    	QueryResults<Entity> results = datastore.run(query);
    	List<Reservation> out = new ArrayList<>();
    	while (results.hasNext()) {
    		  
			try {
				Entity r = results.next();
	    		String renter = r.getString("renter");
	    		String stringStartDate = r.getString("startDate");
	    		Date startDate = new SimpleDateFormat("dd/MM/yyyy").parse(stringStartDate);
	    		String stringEndDate = r.getString("endDate");
	     		Date endDate =new SimpleDateFormat("dd/MM/yyyy").parse(stringEndDate);
	     		String company = r.getString("rentalCompany");
	     		String carType = r.getString("carType");
	     		double rentalPrice = r.getDouble("rentalPrice");
	     		  
	     		Reservation reservation = new Reservation(carId, renter, startDate, endDate, company, carType, rentalPrice);
	     		out.add(reservation);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException("PARSING DATE ERROR");
			}
    		  
    	}
        return out;
    }
    
    public CarType getCarType(String companyName, String carTypeName) {
    	Entity carTypeEntity = datastore.get(datastore
    			.newKeyFactory()
    			.addAncestors(PathElement.of("CarRentalCompany", companyName)) 
    			.setKind("CarType")
    			.newKey(carTypeName));
    	
    	if(carTypeEntity == null) {
    		throw new RuntimeException("No car found " + companyName + "  " + carTypeName);
    	}
    	
    	return new CarType(
    			carTypeName,
				  (int)carTypeEntity.getLong("numberOfSeats"),
				  (float)carTypeEntity.getDouble("trunkSpace"),
				  carTypeEntity.getDouble("rentalPricePerDay"),
				  carTypeEntity.getBoolean("smokingAllowed")
				 );
    }
}
