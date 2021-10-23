package co.programacionmaster.cenaclientes.service.impl;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.maxBy;

import co.programacionmaster.cenaclientes.model.ClientAccountPojo;
import co.programacionmaster.cenaclientes.repository.ClienJdbcRepository;
import co.programacionmaster.cenaclientes.service.ClientService;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.AllArgsConstructor;
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

  private final ClienJdbcRepository clienJdbcRepository;
  private final RestTemplate restTemplate;

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

    var men = clients.get(1)
        .stream()
        .sorted(Comparator.comparing(ClientAccountPojo::getTotalBalance).reversed())
        .limit(4)
        .collect(Collectors.toList());

    var women = clients.get(0)
        .stream()
        .sorted(Comparator.comparing(ClientAccountPojo::getTotalBalance).reversed())
        .limit(4)
        .collect(Collectors.toList());

    if (men.size() < women.size()) {
      women = women.subList(0, men.size());
    } else {
      men = men.subList(0, women.size());
    }

    return Stream
        .concat(men.stream(), women.stream())
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
      return response.getBody();
    } else {
      throw new RuntimeException("Error decrypting client code");
    }
  }
}
