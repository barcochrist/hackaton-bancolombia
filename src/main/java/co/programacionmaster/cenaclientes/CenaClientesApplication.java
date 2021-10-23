package co.programacionmaster.cenaclientes;

import co.programacionmaster.cenaclientes.controller.ClientRestController;
import co.programacionmaster.cenaclientes.repository.ClienJdbcRepository;
import co.programacionmaster.cenaclientes.service.impl.ClientServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@ComponentScan(basePackageClasses = {
    ClientServiceImpl.class,
    ClientRestController.class,
    ClienJdbcRepository.class
})
public class CenaClientesApplication {

  public static void main(String[] args) {
    SpringApplication.run(CenaClientesApplication.class, args);
  }

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}
