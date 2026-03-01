package application.wikibooks_browser;

import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Abstrakte Basisklasse für verschiedene Medientypen wie {@code Buch}, {@code CD},
 * {@code Zeitschrift} und {@code ElektronischesMedium}.
 * <p>
 * Diese Klasse definiert gemeinsame Eigenschaften wie Titel und Ausleihstatus
 * sowie Methoden zur Verwaltung des Ausleihvorgangs. Außerdem enthält sie eine
 * abstrakte Methode {@link #calculateRepresentation()}, die von Unterklassen
 * implementiert werden muss, um eine textuelle Darstellung des Mediums zu liefern.
 * </p>
 *
 * @author Niklas Hölzl
 */
public abstract class Medium implements Comparable<Medium>, Serializable {

  private static final Logger LOGGER = Logger.getLogger(Medium.class.getName());
  private String titel;
  private MediumStatus status;

  /**
   * Konstruktor für die Klasse {@code Medium}.
   * Initialisiert das Medium mit einem Titel und setzt den Status standardmäßig auf "verfügbar".
   *
   * @param titel der Titel des Mediums
   */
  public Medium(String titel) {
    this.titel = titel;
    status = MediumStatus.VERFUEGBAR;
  }

  /**
   * Gibt den Titel zurück
   *
   * @return titel
   */
  public String getTitel() {
    return titel;
  }

  /**
   * Setzt den Titel neu.
   *
   * @param titel neuer Titel
   */
  public void setTitel(String titel) {
    this.titel = titel;
  }

  /**
   * Gibt den aktuellen Ausleihstatus zurück.
   *
   * @return der aktuelle Status des Mediums
   */
  public MediumStatus getStatus() {
    return status;
  }

  /**
   * Setzt den Ausleihstatus des Mediums neu.
   *
   * @param status der neue Status (z. B. "verfügbar", "ausgeliehen")
   */
  public void setStatus(MediumStatus status) {
    this.status = status;
  }

  /**
   * Versucht, das Medium auszuleihen.
   * <ul>
   *   <li>Wenn das Medium verfügbar ist, wird der Status auf "ausgeliehen" gesetzt.</li>
   *   <li>Wenn das Medium bereits ausgeliehen ist, wird eine entsprechende Meldung ausgegeben.</li>
   * </ul>
   */
  public void ausleihen() {
    if (status == MediumStatus.VERFUEGBAR) {
      status = MediumStatus.AUSGELIEHEN;
    } else {
      LOGGER.info("\"" + titel + "\" ist bereits ausgeliehen.");
    }
  }

  /**
   * Gibt das Medium zurück.
   * <ul>
   *   <li>Wenn das Medium ausgeliehen war, wird der Status auf "verfügbar" gesetzt.</li>
   *   <li>Wenn das Medium nicht ausgeliehen war, wird eine Meldung ausgegeben.</li>
   * </ul>
   */
  public void rueckgabe() {
    if (status == MediumStatus.AUSGELIEHEN) {
      status = MediumStatus.VERFUEGBAR;
    } else {
      LOGGER.info("\"" + titel + "\" war nicht ausgeliehen.");
    }
  }

  /**
   * Verlängert die Leihfrist des Mediums, falls es derzeit ausgeliehen ist.
   * Gibt eine entsprechende Meldung in der Konsole aus.
   */
  public void verlaengern() {
    if (status != MediumStatus.AUSGELIEHEN) {
      LOGGER.info("\"" + titel + "\" ist nicht ausgeliehen.");
    }
  }

  /**
   * Abstrakte Methode zur Berechnung der textuellen Repräsentation des Mediums.
   *
   * @return String mit den wichtigsten Informationen zum Medium
   */
  public abstract String calculateRepresentation();

  /**
   * Vergleicht dieses Medium mit einem anderen Medium nach Titel.
   *
   * @param other das andere Medium
   * @return negativer Wert, 0 oder positiver Wert, wie im Comparable-Contract
   */
  @Override
  public int compareTo(Medium other) {
    return this.getTitel().compareToIgnoreCase(other.getTitel());
  }
}

