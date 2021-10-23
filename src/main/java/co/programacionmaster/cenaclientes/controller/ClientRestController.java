package co.programacionmaster.cenaclientes.controller;

import co.programacionmaster.cenaclientes.model.ClientAccountPojo;
import co.programacionmaster.cenaclientes.service.ClientService;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Client rest controller.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/clients")
public class ClientRestController {

  private final ClientService clientService;

  @GetMapping("/guests")
  public ResponseEntity<List<ClientAccountPojo>> guestsByTablee(
      @RequestParam(value = "type", required = false) Integer type,
      @RequestParam(value = "location", required = false) String location,
      @RequestParam(value = "initBalanceRange", required = false) BigDecimal initialBalanceRange,
      @RequestParam(value = "endBalanceRange", required = false) BigDecimal endBalanceRange
  ) {
    return ResponseEntity.ok(
        clientService.getGuestsPerTable(type, location, initialBalanceRange, endBalanceRange)
    );
  }
}