package telran.cars.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.web.bind.annotation.*;
import telran.cars.dto.*;
import telran.cars.service.IRentCompany;
import telran.cars.service.RentCompanyEmbedded;
import telran.cars.utils.Persistable;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.stream.Collectors;

import static telran.cars.dto.RentCompanyApiConstants.*;

/**
 * Created by Сергей on 19.11.2018.
 */

@RestController
@ManagedResource
public class RentCompanyController {
    @Value("${fileName:data}")
    private String fileName;
    @Value("${finePercent:15}")
    private int finePercent;
    @Value("${gasPrice:10}")
    private int gasPrice;

    @PostConstruct
    public void  settings(){
        company.setGasPrice(gasPrice);
        company.setFinePercent(finePercent);
    }

    @ManagedAttribute
    public int getFinePercent() {
        return finePercent;
    }

    @ManagedAttribute
    public void setFinePercent(int finePercent) {
        this.finePercent = finePercent;
        company.setFinePercent(finePercent);
    }

    @ManagedAttribute
    public int getGasPrice() {
        return gasPrice;
    }

    @ManagedAttribute
    public void setGasPrice(int gasPrice) {
        this.gasPrice = gasPrice;
        company.setGasPrice(gasPrice);
    }

    @Autowired
    private IRentCompany company;


    @PostMapping(ADD_MODEL)
    public CarsReturnCode addModel(@RequestBody Model model) {
        return company.addModel(model);
    }

    @PostMapping(ADD_CAR)
    public CarsReturnCode addCar(@RequestBody Car car) {
        return company.addCar(car);
    }

    @PostMapping(ADD_DRIVER)
    public CarsReturnCode addDriver(@RequestBody Driver driver) {
        return company.addDriver(driver);
    }

    @GetMapping(GET_MODEL + "/{modelName}")
    public Model getModel(@PathVariable("modelName") String modelName) {
        return company.getModel(modelName);
    }

    @GetMapping(GET_CAR + "/{carNumber}")
    public Car getCar(@PathVariable("carNumber") String carNumber) {
        return company.getCar(carNumber);
    }

    @GetMapping(GET_DRIVER + "/{licenseId}")
    public Driver getDriver(@PathVariable("licenseId") long licenseId) {
        return company.getDriver(licenseId);
    }

    @PostMapping(RENT_CAR)
    public CarsReturnCode rentCar(@RequestBody RentRecord record) {
        return company.rentCar(record.getCarNumber(), record.getLicenseId(), record.getRentDate(), record.getRentDays());
    }

    @PostMapping(RETURN_CAR)
    public CarsReturnCode returnCar(@RequestBody RentRecord record) {
        return company.returnCar(record.getCarNumber(), record.getReturnDate(), record.getGasTankPercent(), record.getDamages());
    }

    @PostMapping(CLEAR)
    public List<Car> clear(@RequestBody DateAndDays dateAndDays) {
        return company.clear(dateAndDays.getLocalDate(), dateAndDays.getDays());
    }

    @GetMapping(GET_CAR_DRIVERS + "/{carNumber}")
    public List<Driver> getCarDrivers(@PathVariable("carNumber") String carNumber) {
        return company.getCarDrivers(carNumber);
    }

    @GetMapping(GET_DRIVER_CARS + "/{licenseId}")
    public List<Car> getDriverCars(@PathVariable("licenseId") long licenseId) {
        return company.getDriverCars(licenseId);
    }

    @GetMapping(GET_ALL_RECORDS)
    public List<RentRecord> getAllRecords() {
        return company.getAllRecords().collect(Collectors.toList());
    }

    @GetMapping(GET_ALL_CARS)
    public List<Car> getAllCars() {
        return company.getAllCars().collect(Collectors.toList());
    }

    @GetMapping(GET_ALL_DRIVERS)
    public List<Driver> getAllDrivers() {
        return company.getAllDrivers().collect(Collectors.toList());
    }

    @GetMapping(GET_ALL_MODEL_NAMES)
    public List<String> getAllModelNames() {
        return company.getAllModelNames();
    }

    @GetMapping(GET_MOST_POPULAR_MODEL_NAMES)
    public List<String> getMostPopularModelNames() {
        return company.getMostPopularModelNames();
    }

    @GetMapping(GET_MODEL_PROFIT + "/{modelName}")
    public double getModelProfit(@PathVariable("modelName") String modelName) {
        return company.getModelProfit(modelName);
    }

    @GetMapping(GET_MOST_PROFIT_MODEL_NAMES)
    public List<String> getMostProfitModelNames() {
        return company.getMostProfitModelNames();
    }

    @GetMapping(SAVE)
    public void save() {
        if (company instanceof Persistable)
            ((Persistable) company).save(fileName);
    }

    @GetMapping(LOAD)
    public void load(){
        company= RentCompanyEmbedded.restoreFromFile(fileName);
    }

    @PostMapping(GET_RETURNED_RECORDS)
    public List<RentRecord> getReturnedRecords(@RequestBody DatesFromTo datesFromTo) {
        return company.getReturnedRecords(datesFromTo.getFrom(), datesFromTo.getTo()).collect(Collectors.toList());
    }

    @DeleteMapping(REMOVE_CAR+"/{carNumber}")
    public CarsReturnCode removeCar(@PathVariable("carNumber") String carNumber) {
        return company.removeCar(carNumber);
    }
}
