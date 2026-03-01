package application.wikibooks_browser;

import java.util.logging.Logger;
/**
 * Die Klasse {@code ElektronischesMedium} repräsentiert ein Medium, das über eine URL zugänglich ist
 * (z. B. Online-Artikel, Webseite) und erweitert die abstrakte Basisklasse {@link Medium}.
 * <p>
 * Sie enthält spezifische Eigenschaften wie URL, Dateiformat und Dateigröße. Die URL wird
 * mittels {@link Pruefroutine} validiert. Über die Methode {@link #calculateRepresentation()}
 * wird eine formatierte textuelle Darstellung erzeugt.
 * </p>
 *
 * @author Niklas Hölzl
 */
public class ElektronischesMedium extends Medium {

  private static final Logger LOGGER = Logger.getLogger(ElektronischesMedium.class.getName());
  private String url;
  private String dateiformat;
  private int groesse;

  /**
   * Konstruktor für ein {@code ElektronischesMedium}-Objekt.
   * <p>
   * Erstellt ein neues elektronisches Medium mit Titel, URL, Dateiformat und Dateigröße.
   * Die URL wird validiert; ungültige URLs werden als {@code null} gesetzt.
   * </p>
   *
   * @param titel        der Titel des Mediums
   * @param url          die URL des Mediums
   * @param dateiformat  das Dateiformat
   * @param groesse      die Größe in KB
   */
  public ElektronischesMedium(String titel, String url, String dateiformat, int groesse) {
    super(titel);
    setURL(url);
    this.dateiformat = dateiformat;
    this.groesse = groesse;
  }

  /**
   * Erzeugt eine textuelle Darstellung der Eigenschaften eines elektronischen Mediums.
   *
   * @return String mit Titel und URL
   */
  @Override
  public String calculateRepresentation() {
    StringBuilder sb = new StringBuilder();
    sb.append("Titel: ").append(getTitel()).append("\n");
    sb.append("URL: ").append(url != null ? url : "-").append("\n");
    sb.append("Dateiformat: ").append(dateiformat).append("\n");
    sb.append("Größe: ").append(groesse).append(" KB");
    return sb.toString();
  }

  /**
   * Gibt die URL zurück
   *
   * @return URL
   */
  public String getURL() {
    return url;
  }

  /**
   * Setzt die URL des Mediums nach Validierung. Nur gültige URLs werden übernommen, andernfalls
   * bleibt die URL null.
   *
   * @param url neue URL des Mediums
   */
  public void setURL(String url) {
    if (Pruefroutine.checkURL(url)) {
      this.url = url;
    } else {
      LOGGER.warning("Ungültige URL: " + url);
      this.url = null;
    }
  }

  /**
   * Gibt das Dateiformat zurück.
   *
   * @return das Dateiformat
   */
  public String getDateiformat() {
    return dateiformat;
  }

  /**
   * Setzt das Dateiformat neu.
   *
   * @param dateiformat das neue Dateiformat
   */
  public void setDateiformat(String dateiformat) {
    this.dateiformat = dateiformat;
  }

  /**
   * Gibt die Dateigröße in Kilobyte zurück.
   *
   * @return die Größe in KB
   */
  public int getGroesse() {
    return groesse;
  }

  /**
   * Setzt die Dateigröße neu.
   *
   * @param groesse die neue Größe in KB
   */
  public void setGroesse(int groesse) {
    this.groesse = groesse;
  }

}
