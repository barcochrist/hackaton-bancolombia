package co.programacionmaster.cenaclientes.model;

import java.math.BigDecimal;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.Data;

@Data
public class TableFilterPojo {

  @Nonnull
  private String name;

  @Nullable
  private Integer clientType;

  @Nullable
  private String location;

  @Nullable
  private BigDecimal initBalance;

  @Nullable
  private BigDecimal endBalance;
}
