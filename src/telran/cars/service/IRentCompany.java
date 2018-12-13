package telran.cars.service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

import telran.cars.dto.*;
public interface IRentCompany {

	int getFinePercent();
	void setFinePercent(int finePercent);
	int getGasPrice();
	void setGasPrice(int gasPrice);

	CarsReturnCode addModel(ModelDto model);//(OK,MODEL_EXISTS)
	CarsReturnCode addCar(CarDto car);//(OK,CAR_EXISTS,NO_MODEL)
	CarsReturnCode addDriver(DriverDto driver);//(OK,DRIVER_EXISTS)
	ModelDto getModel(String modelName);
	CarDto getCar(String carNumber);
	DriverDto getDriver(long licenseId);
	CarsReturnCode rentCar(String carNumber, long licenseId,
                           LocalDate rentDate, int rentDays);//(OK,CAR_IN_USE,NO_CAR,NO_DRIVER)
	    
	CarsReturnCode returnCar(String carNumber,
                             LocalDate returnDate, int gasTankPercent,
                             int damages);//(OK,CAR_NOT_RENTED,
	      // RETURN_DATE_WRONG) In the case of damages up to 10% state is GOOD, 
	       // up to 30% state BAD more than 30% - remove car (flRemoved)
	CarsReturnCode removeCar(String carNumber);//(OK,CAR_IN_USE,CAR_NOT_EXISTS)
	//removing car is setting flRemoved in true



	List<CarDto> clear(LocalDate currentDate, int days);
	//all cars for which the returnDate before currentDate - days with flRemoved=true
	//are deleted from an information model along with all related records
	//it returns list of the deleted cars
	List<DriverDto> getCarDrivers(String carNumber); //returns
	//all drivers that have been renting the car
	List<CarDto> getDriverCars(long licenseId); //returns list of
	//all cars that have been rented by the driver
	Stream<RentRecordDto> getAllRecords();
	Stream <RentRecordDto> getReturnedRecords(LocalDate from, LocalDate to);
	Stream<CarDto> getAllCars();
	Stream<DriverDto> getAllDrivers();
	
	List<String> getAllModelNames();
	List<String> getMostPopularModelNames(); //returns list of
	// the model names the cars of which have been rented most times
	double getModelProfit(String modelName); //returns value of money received from
	//the renting cars of a given model name
	List<String> getMostProfitModelNames(); //returns list of most
	//proftable model names

}
