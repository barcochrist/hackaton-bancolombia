package co.programacionmaster.cenaclientes.controller;

import co.programacionmaster.cenaclientes.model.ClientAccountPojo;
import co.programacionmaster.cenaclientes.model.TableClientPojo;
import co.programacionmaster.cenaclientes.model.TableFilterPojo;
import co.programacionmaster.cenaclientes.service.ClientService;
import com.google.common.io.Files;
import io.vavr.control.Try;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Client rest controller.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/clients")
public class ClientRestController {

  private static final String TXT_EXTENSION = "txt";

  private final ClientService clientService;

  @GetMapping("/guests")
  public ResponseEntity<List<ClientAccountPojo>> guestsByTable(
      @RequestParam(value = "type", required = false) Integer type,
      @RequestParam(value = "location", required = false) String location,
      @RequestParam(value = "initBalanceRange", required = false) BigDecimal initialBalanceRange,
      @RequestParam(value = "endBalanceRange", required = false) BigDecimal endBalanceRange
  ) {
    return ResponseEntity.ok(
        clientService.getGuestsPerTable(type, location, initialBalanceRange, endBalanceRange)
    );
  }

  @PostMapping("/upload")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<List<TableFilterPojo>> upload(
      @RequestParam("file") MultipartFile file
  ) {
    if (file.isEmpty()) {
      throw new IllegalArgumentException(
          "The file should not be empty, please upload a valid file");
    }

    var extension = Files.getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));
    if (!TXT_EXTENSION.equals(extension)) {
      throw new IllegalArgumentException("The file is not a txt file, please upload a valid file");
    }

    return ResponseEntity.ok(clientService.processFile(Try.of(file::getInputStream).get()));
  }

  @PostMapping("/process")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<List<TableClientPojo>> processFile(
      @RequestParam("file") MultipartFile file
  ) {
    if (file.isEmpty()) {
      throw new IllegalArgumentException(
          "The file should not be empty, please upload a valid file");
    }

    var extension = Files.getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));
    if (!TXT_EXTENSION.equals(extension)) {
      throw new IllegalArgumentException("The file is not a txt file, please upload a valid file");
    }

    var response = clientService.processFile(Try.of(file::getInputStream).get());
    return ResponseEntity.ok(clientService.getGuestsPerTable(response));
  }
}