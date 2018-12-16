package telran.cars.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import telran.cars.entety.DriverJpa;

/**
 * Created by Gran1 on 13/12/2018.
 */
public interface DriverRepository extends JpaRepository<DriverJpa, Long> {
}
