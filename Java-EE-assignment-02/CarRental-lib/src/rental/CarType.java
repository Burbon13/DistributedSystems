package rental;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class CarType implements Serializable{
    
    private int id;
    private String name;
    private int nbOfSeats;
    private boolean smokingAllowed;
    private double rentalPricePerDay;
    //trunk space in liters
    private float trunkSpace;
    
    /***************
     * CONSTRUCTOR *
     ***************/
    
    public CarType() {
        
    }
    
    public CarType(String name, int nbOfSeats, float trunkSpace, double rentalPricePerDay, boolean smokingAllowed) {
        this.name = name;
        this.nbOfSeats = nbOfSeats;
        this.trunkSpace = trunkSpace;
        this.rentalPricePerDay = rentalPricePerDay;
        this.smokingAllowed = smokingAllowed;
    }

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column
    public String getName() {
    	return name;
    }
    
    public void setName(String name) {
        this.name = name;
    } 
    
    @Column
    public int getNbOfSeats() {
        return nbOfSeats;
    }
    
    public boolean isSmokingAllowed() {
        return smokingAllowed;
    }

    @Column
    public double getRentalPricePerDay() {
        return rentalPricePerDay;
    }
    
    @Column
    public float getTrunkSpace() {
    	return trunkSpace;
    }

    public void setNbOfSeats(int nbOfSeats) {
        this.nbOfSeats = nbOfSeats;
    }

    public void setSmokingAllowed(boolean smokingAllowed) {
        this.smokingAllowed = smokingAllowed;
    }

    public void setRentalPricePerDay(double rentalPricePerDay) {
        this.rentalPricePerDay = rentalPricePerDay;
    }

    public void setTrunkSpace(float trunkSpace) {
        this.trunkSpace = trunkSpace;
    }
    
    
    
    /*************
     * TO STRING *
     *************/
    
    @Override
    public String toString() {
    	return String.format("Car type: %s \t[seats: %d, price: %.2f, smoking: %b, trunk: %.0fl]" , 
                getName(), getNbOfSeats(), getRentalPricePerDay(), isSmokingAllowed(), getTrunkSpace());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
	int result = 1;
	result = prime * result + Integer.hashCode(id);
	return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
	if (obj == null)
            return false;
	if (getClass() != obj.getClass())
            return false;
	CarType other = (CarType) obj;
	return other.getId() == this.id;
    }
}
