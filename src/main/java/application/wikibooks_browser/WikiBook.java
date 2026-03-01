package application.wikibooks_browser;

import java.util.List;

/**
 * Die Klasse {@code WikiBook} repräsentiert ein Buch aus Wikibooks als elektronisches Medium. Sie
 * enthält Informationen über Titel, Contributor, Zeitpunkt der letzten Änderung, Kategorien
 * (Regale) und Kapitel.
 *
 * <p>Die Klasse erbt von {@link ElektronischesMedium} und erweitert dieses um spezifische
 * Informationen aus Wikibooks.
 *
 * @author Niklas Hölzl
 */
public class WikiBook extends ElektronischesMedium {

  /** Name des letzten Contributors */
  private final String contributor;

  /** Zeitpunkt der letzten Änderung in lokaler Zeit (String-Format: "dd.MM.yyyy um HH:mm") */
  private final String lastChange;

  /** Liste der Kategorien bzw. Regale, denen das Buch zugeordnet ist */
  private final List<String> categories;

  /** Liste der Kapitel im Buch */
  private final List<String> chapters;

  /**
   * Erstellt ein neues {@code WikiBook} Objekt mit allen relevanten Informationen.
   *
   * @param title Titel des Buches
   * @param contributor Name oder IP-Adresse des letzten Contributors
   * @param lastChange Zeitpunkt der letzten Änderung in lokaler Zeit
   * @param categories Liste der Kategorien (Regale)
   * @param chapters Liste der Kapitel
   */
  public WikiBook(
      String title,
      String contributor,
      String lastChange,
      List<String> categories,
      List<String> chapters) {
    super(title, "https://de.wikibooks.org/wiki/" + title.replace(" ", "_"), "xml", 0);
    this.contributor = contributor;
    this.lastChange = lastChange;
    this.categories = categories;
    this.chapters = chapters;
  }

  /**
   * Liefert den Namen des Contributors zurück.
   *
   * @return Name des Contributors
   */
  public String getContributor() {
    return contributor;
  }

  /**
   * Liefert den Zeitpunkt der letzten Änderung zurück.
   *
   * @return Datum/Zeit der letzten Änderung als String
   */
  public String getLastChange() {
    return lastChange;
  }

  /**
   * Liefert die Kategorien bzw. Regale zurück, denen das Buch zugeordnet ist.
   *
   * @return Liste der Kategorien
   */
  public List<String> getCategories() {
    return categories;
  }

  /**
   * Liefert die Kapitel des Buches zurück.
   *
   * @return Liste der Kapitel
   */
  public List<String> getChapters() {
    return chapters;
  }

  /**
   * Berechnet eine textuelle Repräsentation des {@code WikiBook} Objekts inklusive Titel,
   * Contributor, URL, letzte Änderung, Kategorien und Kapitel.
   *
   * @return formatierter String mit allen Informationen
   */
  @Override
  public String calculateRepresentation() {
    StringBuilder sb = new StringBuilder();
    sb.append("Titel: ").append(getTitel()).append("\n");
    sb.append("Urheber: ").append(contributor != null ? contributor : "Unbekannt").append("\n");
    sb.append("URL: ").append(getURL() != null ? getURL() : "-").append("\n");
    sb.append("Letzte Änderung: ")
        .append(lastChange != null ? lastChange + " Uhr" : "Unbekannt")
        .append("\n");

    sb.append("Regal: ");
    if (categories != null && !categories.isEmpty()) {
      sb.append(String.join(", ", categories));
    } else {
      sb.append("Unbekannt");
    }
    sb.append("\n");

    sb.append("Kapitel:\n");
    if (chapters != null) {
      for (int i = 0; i < chapters.size(); i++) {
        sb.append(i + 1).append(" ").append(chapters.get(i)).append("\n");
      }
    }
    return sb.toString();
  }
}
