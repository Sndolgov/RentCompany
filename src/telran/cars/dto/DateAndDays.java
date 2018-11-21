package telran.cars.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Created by Сергей on 30.10.2018.
 */
public class DateAndDays implements Serializable{
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate localDate;
    int days;

    public DateAndDays(LocalDate localDate, int days) {
        this.localDate = localDate;
        this.days = days;
    }

    public DateAndDays() {
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public int getDays() {
        return days;
    }
}
