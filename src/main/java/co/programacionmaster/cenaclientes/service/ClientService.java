package co.programacionmaster.cenaclientes.service;

import co.programacionmaster.cenaclientes.model.ClientAccountPojo;
import java.math.BigDecimal;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Client service contract.
 */
@ParametersAreNonnullByDefault
public interface ClientService {

  @Nonnull
  List<ClientAccountPojo> getGuestsPerTable(
      @Nullable Integer type,
      @Nullable String location,
      @Nullable BigDecimal initialBalanceRange,
      @Nullable BigDecimal endBalanceRange
  );
}
