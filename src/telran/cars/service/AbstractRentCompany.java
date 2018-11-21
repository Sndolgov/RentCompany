package telran.cars.service;

import telran.cars.dto.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Created by Gran1 on 14/10/2018.
 */
public abstract class AbstractRentCompany implements IRentCompany {
    protected int finePercent;
    protected int gasPrice;

    public AbstractRentCompany() {
        finePercent = 15;
        gasPrice = 10;
    }


    protected float getCost(Model model, RentRecord record, int gasTankPercent, LocalDate returnDate) {
        float cost = 0;
        int priceDay = model.getPriceDay();
        int gasTank = model.getGasTank();
        int rentDays = record.getRentDays();
        cost += priceDay * rentDays;
        long days = ChronoUnit.DAYS.between(record.getRentDate(), returnDate);
        if (days > rentDays)
            cost += (days - rentDays) * priceDay * (1 + (double) getFinePercent() / 100);
        if (gasTankPercent < 100)
            cost += (gasTank - gasTank * (double) gasTankPercent / 100) * getGasPrice();
        return cost;
    }

    @Override
    public int getFinePercent() {
        return finePercent;
    }

    @Override
    public void setFinePercent(int finePercent) {
        this.finePercent = finePercent;
    }

    @Override
    public int getGasPrice() {
        return gasPrice;
    }

    @Override
    public void setGasPrice(int gasPrice) {
        this.gasPrice = gasPrice;
    }

    @Override
    public String toString() {
        return "AbstractRentCompany{" +
                "finePercent=" + finePercent +
                ", gasPrice=" + gasPrice +
                '}';
    }
}
