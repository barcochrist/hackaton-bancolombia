package co.programacionmaster.cenaclientes.service.impl;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.maxBy;

import co.programacionmaster.cenaclientes.model.ClientAccountPojo;
import co.programacionmaster.cenaclientes.model.TableClientPojo;
import co.programacionmaster.cenaclientes.model.TableFilterPojo;
import co.programacionmaster.cenaclientes.repository.ClienJdbcRepository;
import co.programacionmaster.cenaclientes.service.ClientService;
import io.vavr.control.Try;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Client service implementation.
 */
@Service
@AllArgsConstructor
@ParametersAreNonnullByDefault
public class ClientServiceImpl implements ClientService {

  private static final String CLIENT_TYPE_FIELD = "TC";
  private static final String LOCATION_CODE_FIELD = "UG";
  private static final String INITIAL_BALANCE_FIELD = "RI";
  private static final String END_BALANCE_FIELD = "RF";

  private final ClienJdbcRepository clienJdbcRepository;
  private final RestTemplate restTemplate;

  @Nonnull
  @Override
  public List<TableClientPojo> getGuestsPerTable(List<TableFilterPojo> list) {
    return list
        .stream()
        .map(filter -> {
          var codes = this
              .getGuestsPerTable(
                  filter.getClientType(),
                  filter.getLocation(),
                  filter.getInitBalance(),
                  filter.getEndBalance())
              .stream()
              .map(ClientAccountPojo::getCode)
              .collect(Collectors.toList());

          return TableClientPojo.from(
              filter.getName(),
              codes.size() < 4 ? "CANCELADA" : String.join(",", codes)
          );
        })
        .sorted(Comparator.comparing(TableClientPojo::getTableName))
        .collect(Collectors.toList());
  }

  @Nonnull
  @Override
  public String parseResponse(List<TableClientPojo> entry) {
    var stringBuilder = new StringBuilder();
    entry
        .stream()
        .forEach(tcp -> {
          stringBuilder.append("<").append(tcp.getTableName()).append(">").append("\n");
          stringBuilder.append(tcp.getResponse()).append("\n");
        });
    return stringBuilder.toString();
  }

  @Nonnull
  @Override
  public List<ClientAccountPojo> getGuestsPerTable(
      @Nullable Integer type,
      @Nullable String location,
      @Nullable BigDecimal initialBalanceRange,
      @Nullable BigDecimal endBalanceRange
  ) {
    var response = clienJdbcRepository.filter(type, location, initialBalanceRange, endBalanceRange);
    response = getClientMaxBalanceByCompany(response);
    response = homologateClientsByGender(response);

    if (response.size() > 8) {
      response = response.stream().limit(8).collect(Collectors.toList());
    }

    response = decryptClientCodes(response);
    return response;
  }

  /**
   * get a guest list where there are no two or more from the same company.
   *
   * @param entry A collection of {@link ClientAccountPojo}
   * @return A collection of {@link ClientAccountPojo}
   */
  private List<ClientAccountPojo> getClientMaxBalanceByCompany(List<ClientAccountPojo> entry) {
    return entry
        .stream()
        .collect(groupingBy(ClientAccountPojo::getCompany,
            maxBy(comparing(ClientAccountPojo::getTotalBalance))))
        .values()
        .stream()
        .map(optional -> optional.orElseThrow(NoSuchElementException::new))
        .collect(Collectors.toList());
  }

  /**
   * Get a guest list with the same number of people of both genders.
   *
   * @param entry A collection of {@link ClientAccountPojo}
   * @return A collection of {@link ClientAccountPojo}
   */
  private List<ClientAccountPojo> homologateClientsByGender(
      List<ClientAccountPojo> entry
  ) {
    var clients = entry
        .stream()
        .collect(groupingBy(ClientAccountPojo::getMale));

    var men = clients.getOrDefault(1, List.of())
        .stream()
        .sorted(Comparator.comparing(ClientAccountPojo::getTotalBalance).reversed())
        .limit(4)
        .collect(Collectors.toList());

    var women = clients.getOrDefault(0, List.of())
        .stream()
        .sorted(Comparator.comparing(ClientAccountPojo::getTotalBalance).reversed())
        .limit(4)
        .collect(Collectors.toList());

    if (men.size() < women.size()) {
      women = women.subList(0, men.size());
    } else {
      men = men.subList(0, women.size());
    }

    //TODO ordenar según el monto de sus cuentas de mayor a menor (y en caso de coincidir el monto, ordenado por código.
    Comparator<ClientAccountPojo> comparator = Comparator
        .comparing(ClientAccountPojo::getTotalBalance).reversed()
        .thenComparing(ClientAccountPojo::getCode);

    return Stream
        .concat(men.stream(), women.stream())
        .sorted(comparator)
        .collect(Collectors.toList());
  }

  /**
   * Decrypt all client codes.
   *
   * @param entry A collection of {@link ClientAccountPojo}
   * @return A collection of {@link ClientAccountPojo}
   */
  private List<ClientAccountPojo> decryptClientCodes(List<ClientAccountPojo> entry) {
    return entry
        .stream()
        .map(clientAccountValue -> {
          if (1 == clientAccountValue.getEncrypt()) {
            String decrypted = decryptClientCode(clientAccountValue.getCode());
            return ClientAccountPojo.builder()
                .id(clientAccountValue.getId())
                .code(decrypted)
                .male(clientAccountValue.getMale())
                .type(clientAccountValue.getType())
                .location(clientAccountValue.getLocation())
                .company(clientAccountValue.getCompany())
                .encrypt(clientAccountValue.getEncrypt())
                .totalBalance(clientAccountValue.getTotalBalance())
                .build();
          }
          return clientAccountValue;
        })
        .collect(Collectors.toList());
  }

  /**
   * Decrypt a client code.
   *
   * @param encryptedClientCode Encrypted client code
   * @return Client code decrypted
   */
  private String decryptClientCode(String encryptedClientCode) {
    ResponseEntity<String> response = restTemplate.exchange(
        "https://test.evalartapp.com/extapiquest/code_decrypt/".concat(encryptedClientCode),
        HttpMethod.GET,
        null,
        String.class
    );

    if (HttpStatus.OK.equals(response.getStatusCode())) {
      return response.getBody().replace("\"", "");
    } else {
      throw new RuntimeException("Error decrypting client code");
    }
  }

  @Nonnull
  @Override
  public List<TableFilterPojo> processFile(InputStream inputStream) {
    return Try.of(() -> {
      var scannedTables = new StringBuilder();
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
        String line;
        while ((line = reader.readLine()) != null) {
          scannedTables.append(line).append("\n");
        }
      }
      return processTables(scannedTables.toString());
    }).get();
  }

  /**
   * Process string generated by file and generate tables with filters.
   *
   * @param scanned String generated from File
   * @return A collection of {@link TableFilterPojo}
   */
  private List<TableFilterPojo> processTables(String scanned) {
    MultiValuedMap<String, String> map = new ArrayListValuedHashMap<>();
    AtomicReference<String> nameIndex = new AtomicReference<>("");
    scanned
        .lines()
        .forEach(line -> {
          if (StringUtils.isNotBlank(line)) {
            if (line.startsWith("<")) {
              String name = StringUtils.substringBetween(line, "<", ">");
              nameIndex.set(name);
            } else if (StringUtils.startsWithAny(line, CLIENT_TYPE_FIELD, LOCATION_CODE_FIELD,
                INITIAL_BALANCE_FIELD, END_BALANCE_FIELD)) {
              map.put(nameIndex.get(), line);
            }
          }
        });

    List<TableFilterPojo> tables = new ArrayList<>();
    map
        .asMap()
        .forEach((item, list) -> {
              var pojo = new TableFilterPojo(item);

              list.forEach(listItem -> {
                var splitted = listItem.split(":");
                if (StringUtils.equalsAny(splitted[0], CLIENT_TYPE_FIELD, LOCATION_CODE_FIELD,
                    INITIAL_BALANCE_FIELD, END_BALANCE_FIELD)) {
                  switch (splitted[0]) {
                    case CLIENT_TYPE_FIELD:
                      pojo.setClientType(Integer.valueOf(splitted[1]));
                      break;
                    case LOCATION_CODE_FIELD:
                      pojo.setLocation(splitted[1]);
                      break;
                    case INITIAL_BALANCE_FIELD:
                      pojo.setInitBalance(new BigDecimal(splitted[1]));
                      break;
                    case END_BALANCE_FIELD:
                      pojo.setEndBalance(new BigDecimal(splitted[1]));
                      break;
                  }
                }
              });
              tables.add(pojo);
            }
        );

    return tables;
  }
}
