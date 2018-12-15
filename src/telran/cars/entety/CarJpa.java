package telran.cars.entety;

import telran.cars.dto.State;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * Created by Сергей on 12.12.2018.
 */
@Table(name = "cars", indexes = @Index(columnList = "model_name"))
@Entity
public class CarJpa {
    @Id
    @Column(name = "reg_number")
    @NotBlank
    private String regNumber;
    @NotBlank
    private String color;
    @NotNull
    @Enumerated(EnumType.STRING)
    private State state;
    @ManyToOne
    @JoinColumn(name = "model_name")
    @NotNull
    private ModelJpa model;
    @Column(name = "fl_removed")
    private boolean flRemoved;
    @OneToMany(mappedBy = "car", cascade = CascadeType.REMOVE)
    private Set<RentRecordJpa> records;

    public CarJpa() {
    }

    public CarJpa(String regNumber, String color, State state, ModelJpa model, boolean flRemoved) {
        this.regNumber = regNumber;
        this.color = color;
        this.state = state;
        this.model = model;
        this.flRemoved = flRemoved;
    }

    public String getRegNumber() {
        return regNumber;
    }

    public String getColor() {
        return color;
    }

    public State getState() {
        return state;
    }

    public ModelJpa getModel() {
        return model;
    }

    public boolean isFlRemoved() {
        return flRemoved;
    }

    public Set<RentRecordJpa> getRecords() {
        return records;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setFlRemoved(boolean flRemoved) {
        this.flRemoved = flRemoved;
    }
}
