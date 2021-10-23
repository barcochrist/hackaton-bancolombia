package co.programacionmaster.cenaclientes.entity;

import javax.annotation.Nonnull;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Client type Jpa implementation.
 */
@Data
@Entity
@AllArgsConstructor
@Table(name = "client")
public class ClientJpa {

  @Id
  @Nonnull
  private Integer id;

  @Nonnull
  private String code;

  @Nonnull
  private Integer male;

  @Nonnull
  private Integer type;

  @Nonnull
  private String location;

  @Nonnull
  private String company;

  @Nonnull
  private Integer encrypt;

  private ClientJpa() {
  }
}
