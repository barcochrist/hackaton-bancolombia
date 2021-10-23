package co.programacionmaster.cenaclientes;

import co.programacionmaster.cenaclientes.controller.ClientRestController;
import co.programacionmaster.cenaclientes.entity.ClientJpa;
import co.programacionmaster.cenaclientes.repository.ClientRepository;
import co.programacionmaster.cenaclientes.service.impl.ClientServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackageClasses = {
    ClientServiceImpl.class,
    ClientRestController.class
})
@EntityScan(basePackageClasses = {
    ClientJpa.class
})
@EnableJpaRepositories(basePackageClasses = {
    ClientRepository.class
})
public class CenaClientesApplication {

  public static void main(String[] args) {
    SpringApplication.run(CenaClientesApplication.class, args);
  }
}
