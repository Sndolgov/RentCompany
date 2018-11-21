package telran.cars.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import telran.cars.service.IRentCompany;
import telran.cars.service.RentCompanyEmbedded;
import telran.cars.utils.Persistable;

import javax.annotation.PreDestroy;

/**
 * Created by Сергей on 19.11.2018.
 */

@Configuration
public class RentCompanyConfigurator {
    @Value("${fileName}")
    private String fileName;

    @Autowired
    private IRentCompany company;

    @Bean
    public IRentCompany company() {
        return RentCompanyEmbedded.restoreFromFile(fileName);
    }

    @PreDestroy
    public void save() {
        if (company instanceof Persistable)
            ((Persistable) company).save(fileName);
   }

}
