package com.staticoyster.worldcupqualificationengine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration;

@SpringBootApplication(exclude = {
		DataSourceAutoConfiguration.class,
		HibernateJpaAutoConfiguration.class
})
public class WorldCupQualificationEngineApplication {

	public static void main(String[] args) {
		SpringApplication.run(WorldCupQualificationEngineApplication.class, args);
	}

}
