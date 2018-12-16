package telran.cars.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import telran.cars.entety.RentRecordJpa;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by Gran1 on 13/12/2018.
 */
public interface RecordsRepository extends JpaRepository<RentRecordJpa, String> {
    RentRecordJpa findByCarRegNumberAndReturnDateIsNull(String regNumber);
    List<RentRecordJpa> findByReturnDateBefore(LocalDate returnDate);
    Stream<RentRecordJpa> findByReturnDateBetween(LocalDate from, LocalDate to);
}
