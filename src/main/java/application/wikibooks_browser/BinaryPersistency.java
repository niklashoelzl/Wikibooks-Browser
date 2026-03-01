package application.wikibooks_browser;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Logger;

/**
 * Implementierung der Persistency-Schnittstelle zur
 * binären Speicherung (Serialisierung) des Zettelkastens.
 * <p>
 * Diese Klasse speichert und lädt vollständige Objekte.
 *
 * @author Niklas Hölzl
 */
public class BinaryPersistency implements Persistency {

  private static final Logger LOGGER = Logger.getLogger(BinaryPersistency.class.getName());

  /**
   * Speichert den gesamten Zettelkasten serialisiert in einer Datei.
   *
   * @param zk         Zettelkasten, der gespeichert werden soll
   * @param dateiname  Ziel-Dateiname
   * @throws IOException falls ein Fehler beim Schreiben auftritt
   */
  @Override
  public void save(Zettelkasten zk, String dateiname) throws IOException {
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dateiname))) {
      oos.writeObject(zk);
    }
    LOGGER.info("Zettelkasten erfolgreich binär gespeichert: " + dateiname);
  }

  /**
   * Lädt einen gespeicherten Zettelkasten aus einer Datei.
   *
   * @param dateiname Dateiname der gespeicherten Datei
   * @return das geladene Zettelkasten-Objekt
   * @throws IOException falls ein Fehler beim Lesen auftritt
   * @throws ClassNotFoundException falls die Klasse nicht gefunden wird
   */
  @Override
  public Zettelkasten load(String dateiname) throws IOException, ClassNotFoundException {
    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dateiname))) {
      Object obj = ois.readObject();
      if (obj instanceof Zettelkasten zk) {
        LOGGER.info("Zettelkasten erfolgreich geladen: " + dateiname);
        return zk;
      } else {
        throw new IOException("Datei enthält kein gültiges Zettelkasten-Objekt.");
      }
    }
  }
}

