package telran.cars.entety;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Created by Сергей on 12.12.2018.
 */
@Entity
@Table(name = "rentrecords",  indexes = {@Index(name = "license_id_idx",  columnList="driver_license_id", unique = false),
        @Index(name = "car_number_idx", columnList="car_car_number",     unique = false), @Index(name = "rent_date_idx", columnList="rent_date",     unique = false)
})
public class RentRecordsJpa
{
    /*@Column(name = "license_id")
    private long licenseId;
    @Column(name = "car_number")
    private String carNumber;*/

    @ManyToOne(cascade = CascadeType.ALL)
    private CarJpa car;
    @ManyToOne(cascade = CascadeType.ALL)
    private DriverJpa driver;
    @Column(name = "rent_date")
    private LocalDate rentDate;
    @Column(name = "return_date")
    private LocalDate returnDate;
    private int gasTankPercent;
    private int rentDays;
    private float cost;
    private int damages;
}
