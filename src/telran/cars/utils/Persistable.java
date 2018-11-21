package telran.cars.utils;

import java.io.Serializable;

/**
 * Created by Gran1 on 11/10/2018.
 */
public interface Persistable extends Serializable{
    void save(String fileName);
}
