package telran.cars.entety;



import javax.persistence.*;
import java.time.LocalDate;

/**
 * Created by Сергей on 12.12.2018.
 */
@Entity
@Table(name = "records", indexes = {@Index(name = "license_id_idx", columnList = "license_id", unique = false),
        @Index( columnList = "reg_number", unique = false),
        @Index(name = "rent_date_idx", columnList = "rent_date", unique = false),
        @Index(name = "return_date_idx", columnList = "return_date", unique = false)
})
public class RentRecordJpa {

    @Id
    @GeneratedValue
    private int id;

    @ManyToOne
    @JoinColumn(name = "reg_number")
    private CarJpa car;
    @ManyToOne
    @JoinColumn(name = "license_id")
    private DriverJpa driver;
    @Column(name = "rent_date")
    private LocalDate rentDate;
    @Column(name = "return_date")
    private LocalDate returnDate;
    private int gasTankPercent;
    private int rentDays;
    private float cost;
    private int damages;

    public RentRecordJpa() {
    }

    public RentRecordJpa(CarJpa car, DriverJpa driver, LocalDate rentDate, int rentDays) {
        this.car = car;
        this.driver = driver;
        this.rentDate = rentDate;
        this.rentDays = rentDays;
    }

    public int getId() {
        return id;
    }

    public CarJpa getCar() {
        return car;
    }

    public DriverJpa getDriver() {
        return driver;
    }

    public LocalDate getRentDate() {
        return rentDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public int getGasTankPercent() {
        return gasTankPercent;
    }

    public int getRentDays() {
        return rentDays;
    }

    public float getCost() {
        return cost;
    }

    public int getDamages() {
        return damages;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public void setGasTankPercent(int gasTankPercent) {
        this.gasTankPercent = gasTankPercent;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public void setDamages(int damages) {
        this.damages = damages;
    }
}
