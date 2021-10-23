package co.programacionmaster.cenaclientes.entity;

import java.math.BigDecimal;
import javax.annotation.Nonnull;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Account type Jpa implementation.
 */
@Data
@Entity
@AllArgsConstructor
@Table(name = "account")
public class AccountJpa {

  @Id
  @Nonnull
  private Integer id;

  @Nonnull
  private Integer clientId;

  @Nonnull
  private BigDecimal balance;

  private AccountJpa() {
  }
}
