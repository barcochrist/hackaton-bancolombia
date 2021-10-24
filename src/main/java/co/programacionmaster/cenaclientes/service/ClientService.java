package co.programacionmaster.cenaclientes.service;

import co.programacionmaster.cenaclientes.model.ClientAccountPojo;
import co.programacionmaster.cenaclientes.model.TableFilterPojo;
import java.io.InputStream;
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

  @Nonnull
  List<TableFilterPojo> processFile(InputStream inputStream);
}
