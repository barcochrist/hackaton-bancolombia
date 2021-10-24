package co.programacionmaster.cenaclientes.controller;

import co.programacionmaster.cenaclientes.service.ClientService;
import com.google.common.io.Files;
import io.vavr.control.Try;
import java.util.Objects;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

  /**
   * Process entry file and create tables with guests codes.
   *
   * @param file Specific input format
   * @return Formated string
   */
  @PostMapping("/process")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<String> processFile(
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

    var fileProcessed = clientService.processFile(Try.of(file::getInputStream).get());
    var guestsPerTable = clientService.getGuestsPerTable(fileProcessed);
    return ResponseEntity.ok(clientService.parseResponse(guestsPerTable));
  }
}