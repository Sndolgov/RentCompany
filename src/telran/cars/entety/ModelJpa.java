package telran.cars.entety;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.Set;

/**
 * Created by Gran1 on 09/12/2018.
 */

@Entity
@Table(name = "car_models")
public class ModelJpa {
    @Id
    @Column(name = "model_name")
    private String modelName;
    @Min(0)
    private int gasTank;
    @NotBlank
    private String company;
    @NotBlank
    private String country;
    @Column(name = "price_day")
    @Min(0)
    private int priceDay;


    public ModelJpa() {
    }

    public ModelJpa(String modelName, int gasTank, String company, String country, int priceDay) {
        this.modelName = modelName;
        this.gasTank = gasTank;
        this.company = company;
        this.country = country;
        this.priceDay = priceDay;
    }

    public String getModelName() {
        return modelName;
    }

    public int getGasTank() {
        return gasTank;
    }

    public String getCompany() {
        return company;
    }

    public String getCountry() {
        return country;
    }

    public int getPriceDay() {
        return priceDay;
    }



    public void setPriceDay(int priceDay) {
        this.priceDay = priceDay;
    }


}
