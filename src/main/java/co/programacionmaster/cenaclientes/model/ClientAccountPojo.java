package co.programacionmaster.cenaclientes.model;

import java.math.BigDecimal;
import javax.annotation.Nonnull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class ClientAccountPojo {

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

  @Nonnull
  private BigDecimal totalBalance;
}
