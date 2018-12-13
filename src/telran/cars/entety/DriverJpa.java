package telran.cars.entety;

import javax.persistence.*;
import java.util.Set;

/**
 * Created by Сергей on 12.12.2018.
 */
@Table(name = "drivers")
@Entity
public class DriverJpa {
    @Id
    @Column(name = "license_id")
    private long licenseId;
    private String name;
    @Column(name = "birth_year")
    private int birthYear;
    private String phone;

    @OneToMany(mappedBy = "driver")
    private Set<RentRecordsJpa> records;

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

    public Set<RentRecordsJpa> getRecords() {
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
