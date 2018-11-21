package telran.cars;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import telran.cars.service.RentCompanyEmbedded;


import java.io.IOException;
import java.util.Scanner;

/**
 * Created by Сергей on 30.10.2018.
 */

@SpringBootApplication
@ManagedResource
public class RentCompanyAppl {
    private static ConfigurableApplicationContext ctx;

    @ManagedOperation
    public static void stop() {
        ctx.close();
    }

    public static void main(String[] args) throws IOException {
        ctx = SpringApplication.run(RentCompanyAppl.class, args);
        /*for(String s: ctx.getBeanDefinitionNames())
            System.out.println(s);*/

    }
}
