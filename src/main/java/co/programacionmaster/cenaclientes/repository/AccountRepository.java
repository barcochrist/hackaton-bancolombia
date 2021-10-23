package co.programacionmaster.cenaclientes.repository;

import co.programacionmaster.cenaclientes.entity.AccountJpa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<AccountJpa, Integer> {

}
