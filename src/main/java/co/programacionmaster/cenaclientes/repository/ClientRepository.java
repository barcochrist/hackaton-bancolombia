package co.programacionmaster.cenaclientes.repository;

import co.programacionmaster.cenaclientes.entity.ClientJpa;
import java.math.BigDecimal;
import java.util.List;
import javax.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Client Jpa Repository.
 */
public interface ClientRepository extends JpaRepository<ClientJpa, Integer> {

  @Nonnull
  @Query(value = "select DISTINCT c from ClientJpa c "
      + "inner join AccountJpa a on c.id = a.clientId "
      + "where c.type = :type "
      + "and c.location = :location "
      + "and a.balance >= :initialBalanceRange "
      + "and a.balance <= :endBalanceRange")
  List<ClientJpa> filter(
      @Param("type") Integer type,
      @Param("location") String location,
      @Param("initialBalanceRange") BigDecimal initialBalanceRange,
      @Param("endBalanceRange") BigDecimal endBalanceRange
  );
}