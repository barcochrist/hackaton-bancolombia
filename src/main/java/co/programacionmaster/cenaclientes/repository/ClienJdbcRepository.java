package co.programacionmaster.cenaclientes.repository;

import co.programacionmaster.cenaclientes.model.ClientAccountPojo;
import java.math.BigDecimal;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class ClienJdbcRepository {

  private static final RowMapper<ClientAccountPojo> row =
      (rs, rowNum) -> ClientAccountPojo.builder()
          .id(rs.getInt("id"))
          .code(rs.getString("code"))
          .male(rs.getInt("male"))
          .type(rs.getInt("type"))
          .location(rs.getString("location"))
          .company(rs.getString("company"))
          .encrypt(rs.getInt("encrypt"))
          .totalBalance(rs.getBigDecimal("total_balance"))
          .build();

  private final JdbcOperations jdbc;

  /**
   * Get filtered {@link ClientAccountPojo} instances order by {@link
   * ClientAccountPojo#getTotalBalance()} and {@link ClientAccountPojo#getId()} ascending.
   *
   * @param type                Optional type filter
   * @param location            Optional location filter
   * @param initialBalanceRange Optional initialBalanceRange filter
   * @param endBalanceRange     Optional endBalanceRange filter
   * @return A collection of {@link ClientAccountPojo}
   */
  @Nonnull
  public List<ClientAccountPojo> filter(
      @Nullable Integer type,
      @Nullable String location,
      @Nullable BigDecimal initialBalanceRange,
      @Nullable BigDecimal endBalanceRange
  ) {
    StringBuilder sql = new StringBuilder(
        "select * from ("
            + "select c.*, sum(a.balance) total_balance "
            + "from client c "
            + "inner join account a on c.id = a.client_id "
            + "group by c.id) as x "
            + "where 1 = 1 "
    );

    if (type != null) {
      sql.append("and x.type = ").append(type).append(" ");
    }
    if (location != null) {
      sql.append("and x.location = '").append(location).append("' ");
    }
    if (initialBalanceRange != null) {
      sql.append("and x.total_balance >= ").append(initialBalanceRange.toPlainString()).append(" ");
    }
    if (endBalanceRange != null) {
      sql.append("and x.total_balance <= ").append(endBalanceRange.toPlainString()).append(" ");
    }

    sql.append("order by x.total_balance desc, x.code asc");
    return jdbc.query(sql.toString(), row);
  }
}
