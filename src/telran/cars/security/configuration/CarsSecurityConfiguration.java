package telran.cars.security.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import telran.security.accounting.Authenticator;
import telran.security.accounting.service.AccountingMongo;

import static telran.cars.dto.RentCompanyApiConstants.*;

/**
 * Created by Сергей on 02.12.2018.
 */

@Configuration
@EnableMongoRepositories("telran.security.repository")
@EnableWebSecurity
public class CarsSecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Bean
    Authenticator getAuthenticator() {
        return new Authenticator();
    }

    @Bean
    AccountingMongo getAccountingMongo() {
        return new AccountingMongo();
    }

    @Bean
    PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic();
        http.csrf().disable();

        http.authorizeRequests().antMatchers(ADD_MODEL, ADD_CAR, REMOVE_CAR, CLEAR).hasRole("MANAGER");
        http.authorizeRequests().antMatchers(ADD_DRIVER, RENT_CAR, RETURN_CAR).hasRole("CLERK");
        http.authorizeRequests().antMatchers(GET_CAR_DRIVERS, GET_DRIVER_CARS, GET_ALL_DRIVERS).hasRole("DRIVER");
        http.authorizeRequests().antMatchers(GET_ALL_RECORDS, GET_RETURNED_RECORDS).hasRole("TECHNICIAN");
        http.authorizeRequests().antMatchers(GET_MODEL_PROFIT, GET_MOST_POPULAR_MODEL_NAMES, GET_MOST_PROFIT_MODEL_NAMES)
                .hasRole("STATIST");
        http.authorizeRequests().antMatchers(GET_CAR).authenticated();
        http.authorizeRequests().anyRequest().permitAll();
        http.authorizeRequests().antMatchers("/actuator/shutdown").hasRole("ADMIN");

        //TODO
        //configuration
        //MANAGER (addModel, addCar, removeCar, clear)
        //CLERK (addDriver, rentCar, returnCar, getDriver)
        //DRIVER (getCarsDriver, getDriversCar, getAllDrivers,getDriver)
        //TECHNICIAN (getAllRecords, getAllReturnedRecords)
        //STATIST (all statistic method)
        //method getCar all authenticated users
        //getCars all even not authenticated)

        /*addModel - MANAGER
        addCar - MANAGER
        removeCar - MANAGER
        clear - MANGER
        getDriver - CLERK, DRIVER

        */
    }
}
