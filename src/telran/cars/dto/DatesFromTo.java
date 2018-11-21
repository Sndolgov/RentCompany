package telran.cars.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Created by Сергей on 30.10.2018.
 */
public class DatesFromTo implements Serializable {
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate from;
    LocalDate to;

    public DatesFromTo(LocalDate from, LocalDate to) {
        this.from = from;
        this.to = to;
    }

    public DatesFromTo() {
    }

    public LocalDate getFrom() {
        return from;
    }

    public LocalDate getTo() {
        return to;
    }
}
