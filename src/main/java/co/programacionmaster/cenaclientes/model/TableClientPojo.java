package co.programacionmaster.cenaclientes.model;

import javax.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TableClientPojo {

  @Nonnull
  private String tableName;

  @Nonnull
  private String response;

  @Nonnull
  public static TableClientPojo from(
      @Nonnull String tableName,
      @Nonnull String response
  ) {
    return new TableClientPojo(tableName, response);
  }
}
