package telran.cars.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import telran.cars.dto.*;
import telran.cars.entety.CarJpa;
import telran.cars.entety.DriverJpa;
import telran.cars.entety.ModelJpa;
import telran.cars.entety.RentRecordJpa;
import telran.cars.repository.CarRepository;
import telran.cars.repository.DriverRepository;
import telran.cars.repository.ModelRepository;
import telran.cars.repository.RecordsRepository;
import telran.cars.utils.Converter;

import static telran.cars.utils.Converter.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Gran1 on 13/12/2018.
 */

@Service
@Transactional(readOnly = true)
public class RentCompanyJpa extends AbstractRentCompany implements IRentCompany {
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private ModelRepository modelRepository;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private CarRepository carRepository;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private DriverRepository driverRepository;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private RecordsRepository recordsRepository;

    @Override
    public int getFinePercent() {
        return 0;
    }

    @Override
    public void setFinePercent(int finePercent) {

    }

    @Override
    public int getGasPrice() {
        return 0;
    }

    @Override
    public void setGasPrice(int gasPrice) {

    }

    @Override
    @Transactional
    public CarsReturnCode addModel(ModelDto model) {
        if (modelRepository.findById(model.getModelName()).orElse(null) != null)
            return CarsReturnCode.MODEL_EXISTS;
        modelRepository.save(modelDtoToJpa(model));
        return CarsReturnCode.OK;
    }

    @Override
    @Transactional
    public CarsReturnCode addCar(CarDto car) {
        if (carRepository.findById(car.getRegNumber()).orElse(null) != null)
            return CarsReturnCode.CAR_EXISTS;
        ModelJpa model = modelRepository.findById(car.getModelName()).orElse(null);
        if (model == null)
            return CarsReturnCode.NO_MODEL;
        carRepository.save(carDtoToJpa(car, model));
        return CarsReturnCode.OK;
    }

    @Override
    @Transactional
    public CarsReturnCode addDriver(DriverDto driver) {
        if (driverRepository.findById(driver.getLicenseId()).orElse(null) != null)
            return CarsReturnCode.DRIVER_EXISTS;
        driverRepository.save(driverDtoToJpa(driver));
        return CarsReturnCode.OK;
    }

    @Override
    public ModelDto getModel(String modelName) {
        return modelJpaToDto(modelRepository.findById(modelName).orElse(null));
    }

    @Override
    public CarDto getCar(String carNumber) {
        RentRecordJpa record = recordsRepository.findByCarRegNumberAndReturnDateIsNull(carNumber);
        return carJpaToDto(carRepository.findById(carNumber).orElse(null), record);
    }

    @Override
    public DriverDto getDriver(long licenseId) {
        return driverJpaToDto(driverRepository.findById(licenseId).orElse(null));
    }

    @Override
    @Transactional
    public CarsReturnCode rentCar(String carNumber, long licenseId, LocalDate rentDate, int rentDays) {
        CarJpa car = carRepository.findById(carNumber).orElse(null);
        DriverJpa driver = driverRepository.findById(licenseId).orElse(null);
        CarsReturnCode code = checkRentCar(car, driver);
        if (code != CarsReturnCode.OK)
            return code;
        recordsRepository.save(new RentRecordJpa(car, driver, rentDate, rentDays));
        return CarsReturnCode.OK;
    }

    private CarsReturnCode checkRentCar(CarJpa car, DriverJpa driver) {
        if (car == null || car.isFlRemoved())
            return CarsReturnCode.NO_CAR;
        RentRecordJpa record = recordsRepository.findByCarRegNumberAndReturnDateIsNull(car.getRegNumber());
        if (record != null)
            return CarsReturnCode.CAR_IN_USE;
        if (driver == null)
            return CarsReturnCode.NO_DRIVER;
        return CarsReturnCode.OK;
    }

    @Override
    @Transactional
    public CarsReturnCode returnCar(String carNumber, LocalDate returnDate, int gasTankPercent, int damages) {
        CarJpa car = carRepository.findById(carNumber).orElse(null);
        RentRecordJpa record = recordsRepository.findByCarRegNumberAndReturnDateIsNull(carNumber);
        CarsReturnCode code = checkReturnCar(car, returnDate, record);
        if (code != CarsReturnCode.OK)
            return code;
        updateRecord(record, car, returnDate, gasTankPercent, damages);
        setCarDamages(damages);

        return CarsReturnCode.OK;
    }

    private CarsReturnCode checkReturnCar(CarJpa car, LocalDate returnDate, RentRecordJpa record) {
        if (car == null)
            return CarsReturnCode.CAR_EXISTS;
        if (record == null)
            return CarsReturnCode.CAR_NOT_RENTED;
        if (record.getRentDate().isAfter(returnDate))
            return CarsReturnCode.RETURN_DATE_WRONG;
        return CarsReturnCode.OK;
    }

    private void updateRecord(RentRecordJpa record, CarJpa car, LocalDate returnDate, int gasTankPercent, int damages) {
        ModelDto model = modelJpaToDto(modelRepository.findById(car.getModel().getModelName()).orElse(null));
        float cost = getCost(model, recordsJpaToDto(record), gasTankPercent, returnDate);
        setReturnFields(record, cost, returnDate, gasTankPercent, damages);
    }

    private void setReturnFields(RentRecordJpa record, float cost, LocalDate returnDate, int gasTankPercent, int damages) {
        record.setCost(cost);
        record.setDamages(damages);
        record.setGasTankPercent(gasTankPercent);
        record.setReturnDate(returnDate);
    }

    private void setCarDamages(CarJpa car, int damages) {
        if (damages == 0)
            return;
        if (damages > 0 && damages < 10)
            car.setState(State.GOOD);
        else {
            car.setState(State.BAD);
            if (damages >= 30)
                car.setFlRemoved(true);
        }
    }

    @Override
    @Transactional
    public CarsReturnCode removeCar(String carNumber) {
        CarJpa car = carRepository.findById(carNumber).orElse(null);
        if (car == null || car.isFlRemoved())
            return CarsReturnCode.NO_CAR;
        if (recordsRepository.findByCarRegNumberAndReturnDateIsNull(carNumber) != null)
            return CarsReturnCode.CAR_IN_USE;
        car.setFlRemoved(true);
        return CarsReturnCode.OK;
    }

    @Override
    @Transactional
    public List<CarDto> clear(LocalDate currentDate, int days) {
        List<RentRecordJpa> records = recordsRepository.findByReturnDateBefore(currentDate.minusDays(days));
        return records.stream()
                .filter(r-> r.getCar().isFlRemoved())
                .map(r -> carJpaToDto(r.getCar(), null))
                .distinct()
                .map(c -> {
                    carRepository.deleteById(c.getRegNumber());
                    return c;
                }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DriverDto> getCarDrivers(String carNumber) {
        CarJpa car = carRepository.findById(carNumber).orElse(null);
        if (car == null)
            return Collections.emptyList();
        return car.getRecords().stream()
                .map(r -> driverJpaToDto(r.getDriver()))
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarDto> getDriverCars(long licenseId) {
        DriverJpa driver = driverRepository.findById(licenseId).orElse(null);
        if (driver == null)
            return Collections.emptyList();
        return driver.getRecords().stream()
                .map(r -> carJpaToDto(r.getCar(), r))
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public Stream<RentRecordDto> getAllRecords() {
        return recordsRepository.findAll().stream().map(Converter::recordsJpaToDto);
    }

    @Override
    public Stream<RentRecordDto> getReturnedRecords(LocalDate from, LocalDate to) {
        return recordsRepository.findByReturnDateBetween(from, to).map(Converter::recordsJpaToDto);
    }

    @Override
    public Stream<CarDto> getAllCars() {
        return carRepository.findAll().stream().map(c -> carJpaToDto(c, recordsRepository.findByCarRegNumberAndReturnDateIsNull(c.getRegNumber())));
    }

    @Override
    public Stream<DriverDto> getAllDrivers() {
        return driverRepository.findAll().stream().map(Converter::driverJpaToDto);
    }

    @Override
    public List<String> getAllModelNames() {
        return modelRepository.findAll().stream().map(ModelJpa::getModelName).collect(Collectors.toList());
    }

    @Override
    public List<String> getMostPopularModelNames() {
        return null;
    }

    @Override
    public double getModelProfit(String modelName) {
        return 0;
    }

    @Override
    public List<String> getMostProfitModelNames() {
        return null;
    }
}
