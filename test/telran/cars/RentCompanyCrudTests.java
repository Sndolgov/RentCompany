package telran.cars;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import telran.cars.dto.*;
import telran.cars.service.IRentCompany;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.*;

@SpringBootApplication
public class RentCompanyCrudTests {

	private static final String REG_NUMBER1 = "123";
	private static final String COLOR1 = "green";
	private static final long LICENSEID1 = 123;
	private static final String NAME1 = "Moshe";
	private static final int BIRTH_YEAR1 = 1970;
	private static final String PHONE1 = "1111111";
	private static final LocalDate RENT_DATE1 = LocalDate.of(2018, 10, 15);

	private static final int DELAY_DAYS = 2;
	private static final int RENT_DAYS1 = 5;
	private static final LocalDate RETURN_DATE_TRUE = RENT_DATE1.plusDays(RENT_DAYS1);
	private static final LocalDate RETURN_DATE_DELAY = RETURN_DATE_TRUE.plusDays(DELAY_DAYS);
	private static final LocalDate RETURN_DATE_WRONG = RENT_DATE1.minusDays(1);
	ConfigurableApplicationContext ctx;
	IRentCompany rentCompany;
	String modelName1 = "modelName_1";
	static final int PRICE1 = 100;
	int gasPrice;
	static final int GAS_TANK1 = 30;
	private static final int DAMAGE20 = 20;
	private static final int DAMAGES50 = 50;

	private String company = "company1";
	private String country = "contry1";
	ModelDto model1 = new ModelDto(modelName1, GAS_TANK1, company, country, PRICE1);
	private CarDto car1 = new CarDto(REG_NUMBER1, COLOR1, modelName1);
	private DriverDto driver1 = new DriverDto(LICENSEID1, NAME1, BIRTH_YEAR1, PHONE1);
	private int finePercent;

	@Before
	public void setUp() throws Exception {
		ctx=SpringApplication.run
				(RentCompanyCrudTests.class);
			rentCompany=ctx.getBean(IRentCompany.class);
		rentCompany.addModel(model1);
		rentCompany.addCar(car1);
		rentCompany.addDriver(driver1);
		rentCompany.rentCar(REG_NUMBER1, LICENSEID1, RENT_DATE1, RENT_DAYS1);
		gasPrice = rentCompany.getGasPrice();
		finePercent = rentCompany.getFinePercent();

	}
@After
public void tearDown() {
	ctx.close();
}
	@Test
	public void ModelsTest() {
		int gasTank = 50;
		int price = 50;
		ModelDto model_1 = new ModelDto("modelName_2", gasTank, "company_1", "country_1", price);
		ModelDto model_2 = new ModelDto("modelName_3", gasTank, "company_1", "country_1", price);

		assertEquals(CarsReturnCode.OK, rentCompany.addModel(model_1));
		assertEquals(CarsReturnCode.MODEL_EXISTS, rentCompany.addModel(model_1));

		assertEquals(CarsReturnCode.OK, rentCompany.addModel(model_2));

		assertEquals(null, rentCompany.getModel("NotExistingModel"));
		assertEquals(model_1, rentCompany.getModel("modelName_2"));
	}

	@Test
	public void CarsTest() {
		String color = "color1";
		CarDto car1 = new CarDto("111 111 111", color, modelName1);
		CarDto car2 = new CarDto("111 111 112", color, modelName1 + "notExisted");

		assertEquals(CarsReturnCode.OK, rentCompany.addCar(car1));
		assertEquals(CarsReturnCode.CAR_EXISTS, rentCompany.addCar(car1));
		assertEquals(CarsReturnCode.NO_MODEL, rentCompany.addCar(car2));

		assertEquals(car1, rentCompany.getCar("111 111 111"));
	}

	@Test
	public void DriversTest() {
		DriverDto driver = new DriverDto(1231l, "name1", 1970, "phone1");
		assertEquals(CarsReturnCode.OK, rentCompany.addDriver(driver));
		assertEquals(CarsReturnCode.DRIVER_EXISTS, rentCompany.addDriver(driver));
	}

	@Test
	public void rentTest() {
		LocalDate ld_2018_10_14 = LocalDate.of(2018, 10, 14);
		int rentDays = 10;
		CarDto car_2 = new CarDto("555555", null, null);
		DriverDto driver_2 = new DriverDto(1234567, null, 0, null);
		LocalDate ld_2018_10_15 = LocalDate.of(2018, 10, 15);
		LocalDate ld_2019_10_14 = LocalDate.of(2019, 10, 14);
		CarDto car2 = new CarDto("124", "red", modelName1);
		CarDto car3 = new CarDto("125", "red", modelName1);
		rentCompany.addCar(car2);
		car3.setFlRemoved(true);
		assertEquals(CarsReturnCode.OK,
				rentCompany.rentCar(car2.getRegNumber(), driver1.getLicenseId(), ld_2018_10_14, rentDays));

		assertEquals(CarsReturnCode.CAR_IN_USE,
				rentCompany.rentCar(car1.getRegNumber(), driver1.getLicenseId(), ld_2018_10_15, rentDays));
		// car_2 doesn't exist
		assertEquals(CarsReturnCode.NO_CAR,
				rentCompany.rentCar(car3.getRegNumber(), driver1.getLicenseId(), ld_2019_10_14, rentDays));
		// car2 exists but in the state removed;
		assertEquals(CarsReturnCode.NO_CAR,
				rentCompany.rentCar(car3.getRegNumber(), driver1.getLicenseId(), ld_2019_10_14, rentDays));

		assertEquals(CarsReturnCode.NO_DRIVER,
				rentCompany.rentCar(car1.getRegNumber(), driver_2.getLicenseId(), ld_2019_10_14, rentDays));
	}

	@Test
	public void returnCarFullTankOnTime() {

		returnCodesTest();
		assertEquals(CarsReturnCode.OK, rentCompany.returnCar(REG_NUMBER1, RETURN_DATE_TRUE, 100, 0));
		getAllRecords();
		RentRecordDto actual = rentCompany.getAllRecords().findFirst().get();
		RentRecordDto expected = new RentRecordDto(LICENSEID1, REG_NUMBER1, RENT_DATE1, RENT_DAYS1);
		expected.setReturnDate(RETURN_DATE_TRUE);
		expected.setGasTankPercent(100);
		expected.setDamages(0);
		expected.setCost(getCostFullTankOnTime());
		assertEquals(expected, actual);
	}

	private int getCostFullTankOnTime() {
		return PRICE1 * RENT_DAYS1;
	}

	private void returnCodesTest() {
		assertEquals(CarsReturnCode.CAR_NOT_RENTED, rentCompany.returnCar(REG_NUMBER1 + "1", RETURN_DATE_TRUE, 100, 0));
		assertEquals(CarsReturnCode.RETURN_DATE_WRONG, rentCompany.returnCar(REG_NUMBER1, RETURN_DATE_WRONG, 100, 0));
	}

	@Test
	public void getAllRecords() {
		List<RentRecordDto> allRecords = rentCompany.getAllRecords().collect(Collectors.toList());

		assertEquals(1, allRecords.size());
		assertEquals(allRecords.get(0).getCarNumber(),
				REG_NUMBER1);
		assertEquals(allRecords.get(0).getLicenseId(),
				LICENSEID1);
		assertEquals(allRecords.get(0).getRentDate(),
				RENT_DATE1);
		assertEquals(allRecords.get(0).getRentDays(),
				RENT_DAYS1);

	}

	@Test
	public void returnCarEmptyTankOnTime() {
		assertEquals(CarsReturnCode.OK, rentCompany.returnCar(REG_NUMBER1, RETURN_DATE_TRUE, 0, 0));
		RentRecordDto actual = rentCompany.getAllRecords().findFirst().get();
		assertEquals(getCostFullTankOnTime() + gasPrice * GAS_TANK1, actual.getCost());
		assertEquals(0, actual.getGasTankPercent());

	}

	@Test
	public void returnCarHalfTankDelay() {
		assertEquals(CarsReturnCode.OK, rentCompany.returnCar(REG_NUMBER1, RETURN_DATE_DELAY, 50, 0));
		RentRecordDto actual = rentCompany.getAllRecords().findFirst().get();
		assertEquals(
				getCostFullTankOnTime() + gasPrice * GAS_TANK1 / 2 + DELAY_DAYS * (PRICE1 + PRICE1 * finePercent / 100),
				actual.getCost());
	}

	@Test
	public void returnCarWithDamages20() {
		assertEquals(CarsReturnCode.OK, rentCompany.returnCar(REG_NUMBER1, RENT_DATE1, GAS_TANK1, DAMAGE20));
		RentRecordDto actual = rentCompany.getAllRecords().findFirst().get();
		assertEquals(DAMAGE20, actual.getDamages());
		assertEquals(State.BAD, car1.getState());
		assertFalse(car1.isFlRemoved());
	}

	@Test
	public void returnCarWithDamages50() {
		assertEquals(CarsReturnCode.OK, rentCompany.returnCar(REG_NUMBER1, RETURN_DATE_TRUE, 100, DAMAGES50));
		RentRecordDto actual = rentCompany.getAllRecords().findFirst().get();
		assertEquals(DAMAGES50, actual.getDamages());
		assertTrue(car1.isFlRemoved());
	}

	@Test
	public void getAllCars() {

	}

	@Test
	public void clear() {
		assertEquals(0, rentCompany.clear(RENT_DATE1, 0).size());
		assertEquals(1, rentCompany.getAllRecords().count());

		rentCompany.returnCar(car1.getRegNumber(),
				RETURN_DATE_TRUE, 100, DAMAGES50);

		assertEquals(0, rentCompany.clear(RENT_DATE1, 0).size());

		List<CarDto> list = rentCompany.clear(RETURN_DATE_TRUE
				.plusDays(10), 5);

		assertEquals(1, list.size());
		assertEquals(car1, list.get(0));
		assertEquals(0, rentCompany.getAllRecords().count());
		assertNull(rentCompany.getCar(REG_NUMBER1));
		assertTrue
		(rentCompany.getDriverCars(LICENSEID1).isEmpty());
		assertTrue
		(rentCompany.getCarDrivers(REG_NUMBER1).isEmpty());
		
	}
	@Test
	public void getCarDriversTest() {
		List<DriverDto> list_expected = Arrays.asList(driver1);
		
		List<DriverDto> list_actual = rentCompany.getCarDrivers(car1.getRegNumber());
		assertEquals(1, list_actual.size());
		assertEquals(list_expected, list_actual);	  
		
		List<DriverDto> list_actual_not_existed =
				rentCompany.getCarDrivers("not_existed_number");
		assertEquals(new ArrayList<DriverDto>(), list_actual_not_existed);
		assertNotNull(list_actual_not_existed);
	}
	
	
	@Test
	public void getDriverCarsTest() {
		List<CarDto> list_expected = Arrays.asList(car1);
		
		List<CarDto> list_actual = rentCompany.getDriverCars(driver1.getLicenseId());
		assertEquals(1, list_actual.size());
		assertEquals(list_expected, list_actual);	 
		
		List<CarDto> list_actual_not_existed = rentCompany.getDriverCars(1234567890);
		assertEquals(new ArrayList<CarDto>(), list_actual_not_existed);
		assertNotNull(list_actual_not_existed);
	}
	

}
