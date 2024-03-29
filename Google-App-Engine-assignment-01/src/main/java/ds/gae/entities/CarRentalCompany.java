package ds.gae.entities;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import ds.gae.ReservationException;

public class CarRentalCompany {

    private static final Logger logger = Logger.getLogger(CarRentalCompany.class.getName());

    private String name;
    private Set<Car> cars;
    private Map<String, CarType> carTypes = new HashMap<>();

    /***************
     * CONSTRUCTOR *
     ***************/

    public CarRentalCompany(String name, Set<Car> cars) {
        setName(name);
        this.cars = cars;
        for(Car c : cars) {
            this.carTypes.put(c.getType().getName(), c.getType());
        }
    }

    /********
     * NAME *
     ********/

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    /*************
     * CAR TYPES *
     *************/

    public Collection<CarType> getAllCarTypes() {
        return carTypes.values();
    }

    public CarType getCarType(String carTypeName) {
        return carTypes.get(carTypeName);
    }

    public boolean isAvailable(String carTypeName, Date start, Date end) {
        logger.log(Level.INFO, "<{0}> Checking availability for car type {1}", new Object[] { name, carTypeName });
        return getAvailableCarTypes(start, end).contains(getCarType(carTypeName));
    }

    public Set<CarType> getAvailableCarTypes(Date start, Date end) {
        Set<CarType> availableCarTypes = new HashSet<>();
        for (Car car : getCars()) {
            if (car.isAvailable(start, end)) {
                availableCarTypes.add(car.getType());
            }
        }
        return availableCarTypes;
    }

    /*********
     * CARS *
     *********/

    private Car getCar(int uid) {
        for (Car car : cars) {
            if (car.getId() == uid) {
                return car;
            }
        }
        throw new IllegalArgumentException("<" + name + "> No car with uid " + uid);
    }

    public Set<Car> getCars() {
        return cars;
    }

    private List<Car> getAvailableCars(String carType, Date start, Date end) {
        List<Car> availableCars = new LinkedList<>();
        for (Car car : cars) {
            if (car.getType().getName().equals(carType) && car.isAvailable(start, end)) {
                availableCars.add(car);
            }
        }
        return availableCars;
    }

    /****************
     * RESERVATIONS *
     ****************/

    public Quote createQuote(ReservationConstraints constraints, String client) throws ReservationException {
        logger.log(Level.INFO, "<{0}> Creating tentative reservation for {1} with constraints {2}",
                new Object[] { name, client, constraints.toString() });

        CarType type = getCarType(constraints.getCarType());

        if (!isAvailable(constraints.getCarType(), constraints.getStartDate(), constraints.getEndDate())) {
            throw new ReservationException("<" + name + "> No cars available to satisfy the given constraints.");
        }

        double price = calculateRentalPrice(
                type.getRentalPricePerDay(),
                constraints.getStartDate(),
                constraints.getEndDate()
        );

        return new Quote(
                client,
                constraints.getStartDate(),
                constraints.getEndDate(),
                getName(),
                constraints.getCarType(),
                price
        );
    }

    // Implementation can be subject to different pricing strategies
    private double calculateRentalPrice(double rentalPricePerDay, Date start, Date end) {
        return rentalPricePerDay * Math.ceil((end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24D));
    }

    public Reservation confirmQuote(Quote quote) throws ReservationException {
        logger.log(Level.INFO, "<{0}> Reservation of {1}", new Object[] { name, quote.toString() });
        List<Car> availableCars = getAvailableCars(quote.getCarType(), quote.getStartDate(), quote.getEndDate());
        if (availableCars.isEmpty()) {
            throw new ReservationException("Reservation failed, all cars of type " + quote.getCarType()
                    + " are unavailable from " + quote.getStartDate() + " to " + quote.getEndDate());
        }
        Car car = availableCars.get((int) (Math.random() * availableCars.size()));

        Reservation res = new Reservation(quote, car.getId());
        car.addReservation(res);
        return res;
    }

    public void cancelReservation(Reservation res) {
        logger.log(Level.INFO, "<{0}> Cancelling reservation {1}", new Object[] { name, res.toString() });
        getCar(res.getCarId()).removeReservation(res);
    }
    
    public String toString() {
    	StringBuilder st = new StringBuilder();
    	
    	st.append(this.name).append("\n");
    	this.carTypes.values().forEach(ct -> {
    		st.append(ct.toString()).append("\n");
    	});
    	
    	this.cars.forEach(c -> {
    		st.append(c.getId()).append("\n");
    	});
    	
    	return st.toString();
    }
}
