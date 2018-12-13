package telran.cars.service;

import telran.cars.dto.*;
import telran.cars.utils.Persistable;

import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Created by Gran1 on 14/10/2018.
 */


public class RentCompanyEmbedded extends AbstractRentCompany implements Persistable {


    private Map<String, CarDto> cars = new HashMap<>();
    private Map<Long, DriverDto> drivers = new HashMap<>();
    private Map<String, ModelDto> models = new HashMap<>();
    private Map<String, List<RentRecordDto>> carRecords = new HashMap<>();
    private Map<Long, List<RentRecordDto>> driverRecords = new HashMap<>();
    private TreeMap<LocalDate, List<RentRecordDto>> returnedRecords = new TreeMap<>();

    @Override
    public CarsReturnCode addModel(ModelDto model) {
        if (model == null)
            throw new IllegalArgumentException("model is null");
        return models.putIfAbsent(model.getModelName(), model) == null ? CarsReturnCode.OK : CarsReturnCode.MODEL_EXISTS;
    }

    @Override
    public ModelDto getModel(String modelName) {
        return models.get(modelName);
    }

    @Override
    public CarsReturnCode addCar(CarDto car) {
        if (car == null)
            throw new IllegalArgumentException("car is null");
        if (models.get(car.getModelName()) == null)
            return CarsReturnCode.NO_MODEL;
        return cars.putIfAbsent(car.getRegNumber(), car) == null ? CarsReturnCode.OK : CarsReturnCode.CAR_EXISTS;
    }

    @Override
    public CarDto getCar(String carNumber) {
        return cars.get(carNumber);
    }

    @Override
    public CarsReturnCode addDriver(DriverDto driver) {
        if (driver == null)
            throw new IllegalArgumentException("driver is null");
        return drivers.putIfAbsent(driver.getLicenseId(), driver) == null ? CarsReturnCode.OK : CarsReturnCode.DRIVER_EXISTS;
    }

    @Override
    public DriverDto getDriver(long licenseId) {
        return drivers.get(licenseId);
    }

    @Override
    public CarsReturnCode rentCar(String carNumber, long licenseId, LocalDate rentDate, int rentDays) {
        CarsReturnCode code = checkRentCar(carNumber, licenseId);
        if (code != CarsReturnCode.OK)
            return code;
        RentRecordDto record = new RentRecordDto(licenseId, carNumber, rentDate, rentDays);
        addRecordCarRecords(record);
        addRecordDriverRecords(record);

        cars.get(carNumber).setInUse(true);
        return CarsReturnCode.OK;
    }

    private void addRecordDriverRecords(RentRecordDto record) {
        driverRecords.putIfAbsent(record.getLicenseId(), new ArrayList<>());
        List<RentRecordDto> list = driverRecords.get(record.getLicenseId());
        list.add(record);

      /*  List<RentRecordDto> list = new ArrayList<>();
        list.add(record);
        driverRecords.merge(record.getLicenseId(), list, (oldV, newV) ->
        {
            oldV.addAll(newV);
            return oldV;
        });*/
    }

    private void addRecordCarRecords(RentRecordDto record) {
        carRecords.putIfAbsent(record.getCarNumber(), new ArrayList<>());
        List<RentRecordDto> list = carRecords.get(record.getCarNumber());
        list.add(record);

        /*carRecords.merge(record.getCarNumber(), Collections.singletonList(record), (oldV, newV) ->
        {
            List <RentRecordDto> list = new ArrayList<>(oldV);
            list.addAll(newV);
            return list;
        });*/
    }

    private CarsReturnCode checkRentCar(String carNumber, long licenseId) {
        CarDto car = cars.get(carNumber);
        if (car == null || car.isFlRemoved())
            return CarsReturnCode.NO_CAR;
        if (car.isInUse())
            return CarsReturnCode.CAR_IN_USE;
        if (!drivers.containsKey(licenseId))
            return CarsReturnCode.NO_DRIVER;
        return CarsReturnCode.OK;
    }

    @Override
    public CarsReturnCode returnCar(String carNumber, LocalDate returnDate, int gasTankPercent, int damages) {

        CarDto car = getCar(carNumber);
        if (car == null || !car.isInUse())
            return CarsReturnCode.CAR_NOT_RENTED;

        RentRecordDto record = getRentRecordLast(carNumber);
        if (record.getRentDate().isAfter(returnDate))
            return CarsReturnCode.RETURN_DATE_WRONG;

        updateRecord(record, returnDate, gasTankPercent, damages);
        addRecordReturnedRecords(record);
        setCarDamages(car, damages);
        car.setInUse(false);
        return CarsReturnCode.OK;
    }

    private RentRecordDto getRentRecordLast(String carNumber) {
        List<RentRecordDto> records = carRecords.get(carNumber);
        RentRecordDto record = null;
        if (records != null) {
            record = records.get(records.size() - 1);
        }
        return record;
    }

    private void updateRecord(RentRecordDto record, LocalDate returnDate, int gasTankPercent, int damages) {
        CarDto car = getCar(record.getCarNumber());
        ModelDto model = getModel(car.getModelName());
        float cost = getCost(model, record, gasTankPercent, returnDate);
        setReturnFields(record, cost, returnDate, gasTankPercent, damages);
    }

    private void setReturnFields(RentRecordDto record, float cost, LocalDate returnDate, int gasTankPercent, int damages) {
        record.setCost(cost);
        record.setDamages(damages);
        record.setGasTankPercent(gasTankPercent);
        record.setReturnDate(returnDate);
    }

    private void addRecordReturnedRecords(RentRecordDto record) {
        returnedRecords.putIfAbsent(record.getReturnDate(), new ArrayList<>());
        returnedRecords.get(record.getReturnDate()).add(record);
    }

    private void setCarDamages(CarDto car, int damages) {
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
    public CarsReturnCode removeCar(String carNumber) {
        CarDto car = getCar(carNumber);
        if (car == null || car.isFlRemoved())
            return CarsReturnCode.NO_CAR;
        if (car.isInUse())
            return CarsReturnCode.CAR_IN_USE;
        car.setFlRemoved(true);
        return CarsReturnCode.OK;
    }

    @Override
    public List<CarDto> clear(LocalDate currentDate, int days) {
        LocalDate removeDate = currentDate.minusDays(days);

        NavigableMap<LocalDate, List<RentRecordDto>> recordsBefore = returnedRecords.headMap(removeDate, true);

        List<RentRecordDto> records = recordsBefore.values().stream()
                .flatMap(List::stream

                )
                .filter(r -> getCar(r.getCarNumber()).isFlRemoved())
                .collect(Collectors.toList());

        Set<Long> licenseId = new TreeSet<>();
        Set<String> regNumbers = new TreeSet<>();

        records.forEach(r -> addToSet(r, licenseId, regNumbers));

        List<CarDto> carsForDelete = new ArrayList<>();
        regNumbers.forEach(n -> carsForDelete.add(cars.remove(n)));

        removeRecordsFromAllMap(licenseId, regNumbers);
        return carsForDelete;
    }

    private void addToSet(RentRecordDto record, Set<Long> licenseId, Set<String> regNumbers) {
        licenseId.add(record.getLicenseId());
        regNumbers.add(record.getCarNumber());
    }

    private void removeRecordsFromAllMap(Set<Long> licenseId, Set<String> regNumbers) {
        removeFromCars(regNumbers);
        removeFromDrivers(licenseId, regNumbers);
        removeFromReturn(regNumbers);
    }

    private void removeFromReturn(Set<String> regNumbers) {

        Iterator<List<RentRecordDto>> iterator = returnedRecords.values().iterator();
        while (iterator.hasNext()) {
            List<RentRecordDto> next = iterator.next();
            next.removeIf(r -> regNumbers.contains(r.getCarNumber()));
            if (next.isEmpty())
                iterator.remove();
        }
    }

    private void removeFromDrivers(Set<Long> licenseId, Set<String> regNumbers) {
//        licenseId.forEach(li-> driverRecords.get(li).removeIf(r-> regNumbers.contains(r.getCarNumber())));

        licenseId.forEach(li -> driverRecords.compute(li, (k, v) ->
        {
            v.removeIf(r -> regNumbers.contains(r.getCarNumber()));
            return v.isEmpty() ? null : v;
        }));
    }

    private void removeFromCars(Set<String> regNumbers) {
        regNumbers.forEach(c -> carRecords.remove(c));
    }

    @Override
    public List<DriverDto> getCarDrivers(String carNumber) {
        List<RentRecordDto> records = carRecords.get(carNumber);
        if (records != null)
            return carRecords.get(carNumber).stream()
                    .map(r -> getDriver(r.getLicenseId()))
                    .distinct()
                    .collect(Collectors.toList());
        return Collections.emptyList();
    }

    @Override
    public List<CarDto> getDriverCars(long licenseId) {
        List<RentRecordDto> records = driverRecords.get(licenseId);
        if (records != null)
            return records.stream()
                    .map(r -> getCar(r.getCarNumber()))
                    .distinct()
                    .collect(Collectors.toList());
        return Collections.emptyList();
    }

    @Override
    public Stream<RentRecordDto> getAllRecords() {
        return carRecords.values().stream()
                .flatMap(Collection::stream);
    }

    @Override
    public Stream<RentRecordDto> getReturnedRecords(LocalDate from, LocalDate to) {
        return returnedRecords.subMap(from, to).values().stream().flatMap(List::stream);
    }

    @Override
    public Stream<CarDto> getAllCars() {
        return cars.values().stream();

    }

    @Override
    public Stream<DriverDto> getAllDrivers() {
        return drivers.values().stream();
    }

    @Override
    public List<String> getAllModelNames() {
        return new ArrayList<>(models.keySet());
    }

    @Override
    public List<String> getMostPopularModelNames() {
        Map<String, Long> models = carRecords.values().stream()
                .flatMap(List::stream)
                .map(r -> getCar(r.getCarNumber()).getModelName())
                .collect(Collectors.groupingBy(m -> m, Collectors.counting()));

        long max = models.values().stream()
                .max(Long::compareTo).orElse(0L);

        List<String> modelsPopular = new ArrayList<>();
        models.forEach((k, v) -> {
            if (v.equals(max))
                modelsPopular.add(k);
        });
        return modelsPopular;
    }

    @Override
    public double getModelProfit(String modelName) {
        ModelDto model = getModel(modelName);
        if (model == null)
            return -1;
        int priceDay = model.getPriceDay();
        Integer days = getAllRecords()
                .filter(r -> getCar(r.getCarNumber()).getModelName().equals(modelName))
                .mapToInt(RentRecordDto::getRentDays)
                .sum();

        return days * priceDay;
    }

    @Override
    public List<String> getMostProfitModelNames() {

        Map<String, Double> profitModels = getAllRecords()
                .collect(Collectors.groupingBy(r -> getCar(r.getCarNumber()).getModelName(), Collectors.summingDouble(this::getSum)));

        double max = profitModels.values().stream()
                .max(Double::compareTo)
                .orElse(0.0);

        List<String> models = new ArrayList<>();
        profitModels.forEach((k, v) -> {
            if (v == max && v != 0) models.add(k);
        });
        return models;
    }

    private double getSum(RentRecordDto record) {
        String modelName = getCar(record.getCarNumber()).getModelName();
        return getModel(modelName).getPriceDay() * record.getRentDays();
    }

    @Override
    public void save(String fileName) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            out.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static RentCompanyEmbedded restoreFromFile(String fileName) {
        RentCompanyEmbedded rentCompany = null;
        File file = new File(fileName);
        if (file.exists()) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {
                rentCompany = (RentCompanyEmbedded) in.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return rentCompany == null ? new RentCompanyEmbedded() : rentCompany;
    }
}
