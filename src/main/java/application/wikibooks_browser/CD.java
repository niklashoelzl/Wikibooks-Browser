package application.wikibooks_browser;

/**
 * Die Klasse {@code CD} repräsentiert eine Musik-CD als Medium und erweitert die abstrakte
 * Basisklasse {@link Medium}. Sie enthält spezifische Informationen wie Label, Künstler,
 * Spieldauer und Altersfreigabe.
 * <p>
 * Über die Methode {@link #calculateRepresentation()} wird eine formatierte textuelle
 * Darstellung aller CD-Informationen erzeugt.
 * </p>
 *
 * @author Niklas Hölzl
 */
public class CD extends Medium {

  private String label;
  private String kuenstler;
  private int dauer;
  private int altersfreigabe;

  /**
   * Konstruktor für ein {@code CD}-Objekt.
   * <p>
   * Erstellt eine neue CD mit Titel, Label, Künstler, Spieldauer und Altersfreigabe.
   * </p>
   *
   * @param titel           der Titel der CD
   * @param label           das Label bzw. die Plattenfirma
   * @param kuenstler       der Künstler oder die Band
   * @param dauer           die Spieldauer in Minuten
   * @param altersfreigabe  die Altersfreigabe der CD
   */
  public CD(String titel, String label, String kuenstler, int dauer, int altersfreigabe) {
    super(titel);
    this.label = label;
    this.kuenstler = kuenstler;
    this.dauer = dauer;
    this.altersfreigabe = altersfreigabe;
  }

  /**
   * Erzeugt eine String-Darstellung der CD.
   *
   * @return Titel, Label und Künstler
   */
  @Override
  public String calculateRepresentation() {
    StringBuilder sb = new StringBuilder();
    sb.append("Titel: ").append(getTitel()).append("\n");
    sb.append("Label: ").append(label).append("\n");
    sb.append("Künstler: ").append(kuenstler).append("\n");
    sb.append("Dauer: ").append(dauer).append(" min").append("\n");
    sb.append("Altersfreigabe: ").append(altersfreigabe);
    return sb.toString();
  }

  /**
   * Gibt das Label zurück
   *
   * @return Label
   */
  public String getLabel() {
    return label;
  }

  /**
   * Setzt das Label neu.
   *
   * @param label neues Label
   */
  public void setLabel(String label) {
    this.label = label;
  }

  /**
   * Gibt den Künstler zurück
   *
   * @return Künstler
   */
  public String getKuenstler() {
    return kuenstler;
  }

  /**
   * Setzt den Künstler neu.
   *
   * @param kuenstler neuer Künstler
   */
  public void setKuenstler(String kuenstler) {
    this.kuenstler = kuenstler;
  }

  /**
   * Gibt die Gesamtdauer der CD in Minuten zurück.
   *
   * @return die Dauer in Minuten
   */
  public int getDauer() {
    return dauer;
  }

  /**
   * Setzt die Gesamtdauer der CD neu.
   *
   * @param dauer die neue Dauer in Minuten
   */
  public void setDauer(int dauer) {
    this.dauer = dauer;
  }

  /**
   * Gibt die Altersfreigabe der CD zurück.
   *
   * @return die Altersfreigabe
   */
  public int getAltersfreigabe() {
    return altersfreigabe;
  }

  /**
   * Setzt die Altersfreigabe der CD neu.
   *
   * @param altersfreigabe die neue Altersfreigabe
   */
  public void setAltersfreigabe(int altersfreigabe) {
    this.altersfreigabe = altersfreigabe;
  }

}
