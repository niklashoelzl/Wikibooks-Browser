package application.wikibooks_browser;

/**
 * Die Klasse {@code Zeitschrift} repräsentiert eine periodisch erscheinende Publikation
 * (z. B. Magazin, Fachzeitschrift oder Journal) und erweitert die abstrakte Basisklasse
 * {@link Medium}.
 * <p>
 * Sie enthält spezifische Eigenschaften wie ISSN, Volume (Jahrgang), Ausgabenummer,
 * Auflage und Seitenanzahl. Über die Methode {@link #calculateRepresentation()} wird
 * eine formatierte textuelle Darstellung aller Zeitschriften informationen erzeugt.
 * </p>
 *
 * @author Niklas Hölzl
 */
public class Zeitschrift extends Medium {

  private String issn;
  private int volume;
  private int nummer;
  private int auflage;
  private int seitenanzahl;

  /**
   * Konstruktor für ein {@code Zeitschrift}-Objekt.
   * <p>
   * Erstellt eine neue Zeitschrift mit allen relevanten bibliografischen Angaben.
   * </p>
   *
   * @param titel         der Titel der Zeitschrift
   * @param issn          die internationale Standardnummer für Zeitschriften
   * @param volume        der Jahrgang (Volume)
   * @param nummer        die Ausgabenummer
   * @param auflage       die Auflage der Zeitschrift
   * @param seitenanzahl  die Seitenanzahl der Ausgabe
   */
  public Zeitschrift(String titel, String issn, int volume, int nummer, int auflage, int seitenanzahl) {
    super(titel);
    this.issn = issn;
    this.volume = volume;
    this.nummer = nummer;
    this.auflage = auflage;
    this.seitenanzahl = seitenanzahl;
  }

  /**
   * Erzeugt eine String-Darstellung der Zeitschrift.
   *
   * @return Titel, ISSN, Volume und Nummer
   */
  @Override
  public String calculateRepresentation() {
    StringBuilder sb = new StringBuilder();
    sb.append("Titel: ").append(getTitel()).append("\n");
    sb.append("ISSN: ").append(issn).append("\n");
    sb.append("Volume: ").append(volume).append("\n");
    sb.append("Nummer: ").append(nummer).append("\n");
    sb.append("Auflage: ").append(auflage).append("\n");
    sb.append("Seitenanzahl: ").append(seitenanzahl);
    return sb.toString();
  }

  /**
   * Gibt die ISSN Zurück
   *
   * @return issn
   */
  public String getIssn() {
    return issn;
  }

  /**
   * Setzt die ISSN neu.
   *
   * @param issn neue ISSN
   */
  public void setIssn(String issn) {
    this.issn = issn;
  }

  /**
   * Gibt das Volume zurück
   *
   * @return volume
   */
  public int getVolume() {
    return volume;
  }

  /**
   * Setzt das Volume neu.
   *
   * @param volume neues Volume
   */
  public void setVolume(int volume) {
    this.volume = volume;
  }

  /**
   * Gibt die Nummer zurück
   *
   * @return Nummer
   */
  public int getNummer() {
    return nummer;
  }

  /**
   * Setzt die Nummer neu.
   *
   * @param nummer neue Nummer
   */
  public void setNummer(int nummer) {
    this.nummer = nummer;
  }

  /**
   * Gibt die Auflage der Zeitschrift zurück.
   *
   * @return die Auflage
   */
  public int getAuflage() {
    return auflage;
  }

  /**
   * Setzt die Auflage der Zeitschrift neu.
   *
   * @param auflage die neue Auflage
   */
  public void setAuflage(int auflage) {
    this.auflage = auflage;
  }

  /**
   * Gibt die Seitenanzahl der Zeitschrift zurück.
   *
   * @return die Seitenanzahl
   */
  public int getSeitenanzahl() {
    return seitenanzahl;
  }

  /**
   * Setzt die Seitenanzahl der Zeitschrift neu.
   *
   * @param seitenanzahl die neue Seitenanzahl
   */
  public void setSeitenanzahl(int seitenanzahl) {
    this.seitenanzahl = seitenanzahl;
  }

}
