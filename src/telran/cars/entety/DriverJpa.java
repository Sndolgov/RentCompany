package telran.cars.entety;


import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.Set;

/**
 * Created by Сергей on 12.12.2018.
 */
@Table(name = "drivers")
@Entity
public class DriverJpa {

    private final static  long MIN_BIRTH_YEAR= LocalDate.now().getYear()-70;
    private static final int MAX_BIRTH_YEAR= LocalDate.now().getYear();

    @Id
    @Column(name = "license_id")
    @Min(0)
    private long licenseId;
    @NotBlank
    private String name;
    @Column(name = "birth_year")
    private int birthYear;
    @NotBlank
    private String phone;
    @OneToMany(mappedBy = "driver", cascade = CascadeType.REMOVE)
    private Set<RentRecordJpa> records;

    public DriverJpa() {
    }

    public DriverJpa(long licenseId, String name, int birthYear, String phone) {
        this.licenseId = licenseId;
        this.name = name;
        this.birthYear = birthYear;
        this.phone = phone;
    }

    public long getLicenseId() {
        return licenseId;
    }

    public String getName() {
        return name;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public String getPhone() {
        return phone;
    }

    public Set<RentRecordJpa> getRecords() {
        return records;
    }

    public void setLicenseId(long licenseId) {
        this.licenseId = licenseId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
