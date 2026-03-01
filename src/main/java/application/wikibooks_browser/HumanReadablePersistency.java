package application.wikibooks_browser;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

/**
 * Implementierung der Persistency-Schnittstelle für
 * menschenlesbare Speicherung (Textform, UTF-8).
 * Das Laden ist noch nicht implementiert und wirft eine Exception.
 *
 * @author Niklas Hölzl
 */
public class HumanReadablePersistency implements Persistency {

  private static final Logger LOGGER = Logger.getLogger(HumanReadablePersistency.class.getName());

  /**
   * Speichert alle Medien des Zettelkastens in einer Textdatei.
   * Jedes Medium wird mit calculateRepresentation() ausgegeben.
   *
   * @param zk         Zettelkasten, der gespeichert werden soll
   * @param dateiname  Ziel-Dateiname
   * @throws IOException falls beim Schreiben ein Fehler auftritt
   */
  @Override
  public void save(Zettelkasten zk, String dateiname) throws IOException {
    try (BufferedWriter writer = new BufferedWriter(
        new FileWriter(dateiname, StandardCharsets.UTF_8))) {

      for (Medium m : zk) {
        writer.write(m.calculateRepresentation());
        writer.newLine();
        writer.write("Status: " + m.getStatus().name());
        writer.newLine();
        writer.write("-------------------------------------------");
        writer.newLine();
      }
    }
    LOGGER.info("Zettelkasten erfolgreich als Textdatei gespeichert: " + dateiname);
  }
}
