package co.programacionmaster.cenaclientes.service;

import co.programacionmaster.cenaclientes.model.TableClientPojo;
import co.programacionmaster.cenaclientes.model.TableFilterPojo;
import java.io.InputStream;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Client service contract.
 */
@ParametersAreNonnullByDefault
public interface ClientService {

  @Nonnull
  List<TableFilterPojo> processFile(InputStream inputStream);

  @Nonnull
  List<TableClientPojo> getGuestsPerTable(List<TableFilterPojo> list);

  @Nonnull
  String parseResponse(List<TableClientPojo> entry);
}
