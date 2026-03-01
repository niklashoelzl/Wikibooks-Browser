package application.wikibooks_browser;

import java.util.logging.Logger;

/**
 * Die Klasse {@code Buch} repräsentiert ein Buch als Medium und erweitert die abstrakte
 * Basisklasse {@link Medium}. Sie enthält spezifische Eigenschaften eines Buches wie
 * Erscheinungsjahr, Verlag, ISBN, Verfasser, Auflage und Seitenanzahl.
 * <p>
 * Über die Methode {@link #calculateRepresentation()} wird eine formatierte textuelle
 * Darstellung aller Buchinformationen erzeugt.
 * </p>
 *
 * @author Niklas Hölzl
 */
public class Buch extends Medium {

  private static final Logger LOGGER = Logger.getLogger(Buch.class.getName());
  private int erscheinungsjahr;
  private String verlag;
  private String isbn;
  private String verfasser;
  private int auflage;
  private int seitenanzahl;

  /**
   * Konstruktor für die Klasse {@code Buch}.
   * <p>
   * Erstellt ein neues Buchobjekt mit allen relevanten bibliografischen Angaben
   * und führt eine Validierung der angegebenen ISBN durch.
   * </p>
   *
   * @param titel            der Titel des Buches
   * @param erscheinungsjahr das Erscheinungsjahr des Buches
   * @param verlag           der Verlag des Buches
   * @param isbn             die ISBN-Nummer des Buches
   * @param verfasser        der Verfasser des Buches
   * @param auflage          die Auflage des Buches
   * @param seitenanzahl     die Seitenanzahl des Buches
   */
  public Buch(String titel, int erscheinungsjahr, String verlag, String isbn, String verfasser, int auflage, int seitenanzahl) {
    super(titel);
    this.erscheinungsjahr = erscheinungsjahr;
    this.verlag = verlag;
    setIsbn(isbn);
    this.verfasser = verfasser;
    this.auflage = auflage;
    this.seitenanzahl = seitenanzahl;
  }

  /**
   * Erzeugt eine textuelle Darstellung aller Eigenschaften eines Buches.
   *
   * @return String mit formatierten Informationen über das Buch
   */
  @Override
  public String calculateRepresentation() {
    StringBuilder sb = new StringBuilder();
    sb.append("Titel: ").append(getTitel()).append("\n");
    sb.append("Erscheinungsjahr: ").append(erscheinungsjahr).append("\n");
    sb.append("Verlag: ").append(verlag).append("\n");
    sb.append("ISBN: ").append(isbn != null ? isbn : "-").append("\n");
    sb.append("Verfasser: ").append(verfasser != null ? verfasser : "-").append("\n");
    sb.append("Auflage: ").append(auflage).append("\n");
    sb.append("Seitenanzahl: ").append(seitenanzahl);
    return sb.toString();
  }

  /**
   * Gibt das Erscheinungsjahr zurück
   *
   * @return Erscheinungsjahr
   */
  public int getErscheinungsjahr() {
    return erscheinungsjahr;
  }

  /**
   * Setzt das Erscheinungsjahr des Buches.
   *
   * @param erscheinungsjahr neues Erscheinungsjahr
   */
  public void setErscheinungsjahr(int erscheinungsjahr) {
    this.erscheinungsjahr = erscheinungsjahr;
  }

  /**
   * Gibt den Verlag zurück
   *
   * @return Verlag
   */
  public String getVerlag() {
    return verlag;
  }

  /**
   * Setzt den Verlag des Buches.
   *
   * @param verlag neuer Verlag
   */
  public void setVerlag(String verlag) {
    this.verlag = verlag;
  }

  /**
   * Gibt die ISBN-Nummer oder null zurück, wenn die ISBN ungültig ist.
   *
   * @return ISBN-Nummer oder null
   */
  public String getIsbn() {
    return isbn;
  }

  /**
   * Setzt die ISBN-Nummer des Buches nach Validierung. Unterstützt ISBN-10 und ISBN-13.
   *
   * @param isbn zu prüfende ISBN-Nummer
   */
  public void setIsbn(String isbn) {
    int[] digits = Pruefroutine.parseISBN(isbn);
    boolean valid = false;

    if (digits.length == 10) {
      valid = Pruefroutine.checkISBN10(digits);
    } else if (digits.length == 13) {
      valid = Pruefroutine.checkISBN13(digits);
    }

    if (valid) {
      this.isbn = isbn;
    } else {
      LOGGER.warning("Ungültige ISBN: " + isbn);
      this.isbn = null;
    }
  }

  /**
   * Gibt den Verfasser zurück
   *
   * @return Verfasser
   */
  public String getVerfasser() {
    return verfasser;
  }

  /**
   * Setzt den Verfasser des Buches.
   *
   * @param verfasser neuer Verfasser
   */
  public void setVerfasser(String verfasser) {
    this.verfasser = verfasser;
  }

  /**
   * Gibt die Auflage des Buches zurück.
   *
   * @return die Auflage
   */
  public int getAuflage() {
    return auflage;
  }

  /**
   * Setzt die Auflage des Buches neu.
   *
   * @param auflage die neue Auflage
   */
  public void setAuflage(int auflage) {
    this.auflage = auflage;
  }

  /**
   * Gibt die Seitenanzahl des Buches zurück.
   *
   * @return die Seitenanzahl
   */
  public int getSeitenanzahl() {
    return seitenanzahl;
  }

  /**
   * Setzt die Seitenanzahl des Buches neu.
   *
   * @param seiten die neue Seitenanzahl
   */
  public void setSeitenanzahl(int seiten) {
    seitenanzahl = seiten;
  }

}
