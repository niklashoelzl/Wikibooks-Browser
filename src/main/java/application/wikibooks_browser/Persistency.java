package application.wikibooks_browser;

/**
 * Interface für die Persistenz von Zettelkasten-Objekten.
 * Definiert Methoden zum Speichern und Laden von Zettelkasten-Daten
 * in bzw. aus Dateien.
 *
 * @author Niklas Hölzl
 */
public interface Persistency {

  /**
   * Speichert den angegebenen Zettelkasten in einer Datei.
   *
   * @param zk         Zettelkasten-Objekt, das gespeichert werden soll
   * @param dateiname  Name der Datei, in die gespeichert wird
   * @throws Exception falls beim Speichern ein Fehler auftritt
   */
  void save(Zettelkasten zk, String dateiname) throws Exception;

  /**
   * Lädt einen Zettelkasten aus einer Datei.
   *
   * @param dateiname Name der Datei, aus der geladen wird
   * @return das geladene Zettelkasten-Objekt
   * @throws UnsupportedOperationException wenn die Implementierung kein Laden unterstützt
   */
  default Zettelkasten load(String dateiname) throws Exception {
    throw new UnsupportedOperationException(
        "Diese Persistenz-Implementierung unterstützt kein Laden.");
  }
}

