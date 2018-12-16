package telran.cars.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import telran.cars.entety.CarJpa;

/**
 * Created by Gran1 on 13/12/2018.
 */
public interface CarRepository extends JpaRepository<CarJpa, String> {
}
