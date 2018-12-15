package telran.cars.utils;

import telran.cars.dto.CarDto;
import telran.cars.dto.DriverDto;
import telran.cars.dto.ModelDto;
import telran.cars.dto.RentRecordDto;
import telran.cars.entety.CarJpa;
import telran.cars.entety.DriverJpa;
import telran.cars.entety.ModelJpa;
import telran.cars.entety.RentRecordJpa;

/**
 * Created by Gran1 on 13/12/2018.
 */
public class Converter {
    public static ModelJpa modelDtoToJpa(ModelDto model) {
        if (model == null)
            return null;
        return new ModelJpa(model.getModelName(), model.getGasTank(), model.getCompany(), model.getCountry(), model.getPriceDay());
    }

    public static ModelDto modelJpaToDto(ModelJpa model) {
        if (model == null)
            return null;
        return new ModelDto(model.getModelName(), model.getGasTank(), model.getCompany(), model.getCountry(), model.getPriceDay());
    }

    public static CarJpa carDtoToJpa(CarDto car, ModelJpa model) {
        if (car == null || model == null)
            return null;
        return new CarJpa(car.getRegNumber(), car.getColor(), car.getState(), model, car.isFlRemoved());
    }

    public static CarDto carJpaToDto(CarJpa car, RentRecordJpa record) {
        if (car == null)
            return null;
        CarDto carDto = new CarDto(car.getRegNumber(), car.getColor(), car.getModel().getModelName());
        carDto.setState(car.getState());
        carDto.setInUse(record != null);
        carDto.setFlRemoved(car.isFlRemoved());
        return carDto;
    }

    public static DriverJpa driverDtoToJpa(DriverDto driver) {
        if (driver == null)
            return null;
        return new DriverJpa(driver.getLicenseId(), driver.getName(), driver.getBirthYear(), driver.getPhone());
    }

    public static DriverDto driverJpaToDto(DriverJpa driver) {
        if (driver == null)
            return null;
        return new DriverDto(driver.getLicenseId(), driver.getName(), driver.getBirthYear(), driver.getPhone());
    }

    public static RentRecordJpa recordsDtoToJpa(RentRecordDto record, CarJpa car, DriverJpa driver) {
        RentRecordJpa recordJpa = new RentRecordJpa(car, driver, record.getRentDate(), record.getRentDays());
        recordJpa.setCost(record.getCost());
        recordJpa.setDamages(record.getDamages());
        recordJpa.setReturnDate(record.getReturnDate());
        recordJpa.setGasTankPercent(record.getGasTankPercent());

        return recordJpa;
    }

    public static RentRecordDto recordsJpaToDto(RentRecordJpa record) {
        RentRecordDto recordDto = new RentRecordDto(record.getDriver().getLicenseId(), record.getCar().getRegNumber(), record.getRentDate(), record.getRentDays());
        recordDto.setCost(record.getCost());
        recordDto.setDamages(record.getDamages());
        recordDto.setReturnDate(record.getReturnDate());
        recordDto.setGasTankPercent(record.getGasTankPercent());
        return recordDto;
    }


}
