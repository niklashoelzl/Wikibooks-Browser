package application.wikibooks_browser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * Die Klasse {@code Zettelkasten} verwaltet eine Sammlung von Medienobjekten
 * ({@link Medium} und deren Unterklassen). Sie bietet Funktionen zum Hinzufügen,
 * Entfernen, Suchen und Sortieren von Medien.
 * <p>
 * {@code Zettelkasten} implementiert {@link Iterable}, sodass alle Medien direkt
 * in einer foreach-Schleife durchlaufen werden können.
 * </p>
 *
 * @author Niklas Hölzl
 */
public class Zettelkasten implements Iterable<Medium>, Serializable {

  private static final Logger LOGGER = Logger.getLogger(Zettelkasten.class.getName());

  /** Interne Liste zur Speicherung aller Medienobjekte. */
  private final ArrayList<Medium> medienListe;

  private boolean isSorted = false;
  private boolean ascendingOrder = true; // merkt sich die letzte Sortierrichtung

  /**
   * Konstruktor für {@code Zettelkasten}.
   * <p>
   * Erstellt eine leere Medienliste.
   * </p>
   */
  public Zettelkasten() {
    medienListe = new ArrayList<>();
  }

  /**
   * Fügt ein Medium zur Liste hinzu, wenn es gültig ist.
   * <p>
   * Validierungen:
   * <ul>
   *   <li>Titel darf nicht leer sein</li>
   *   <li>Bücher müssen gültige ISBN, Verlag und Verfasser haben</li>
   *   <li>Elektronische Medien müssen eine gültige URL besitzen</li>
   * </ul>
   *
   * @param m das hinzuzufügende Medium
   */
  public void addMedium(Medium m) {
    boolean valid = true;

    // Titel muss mindestens 1 Zeichen haben
    if (m.getTitel() == null || m.getTitel().trim().isEmpty()) {
      valid = false;
      LOGGER.warning("Fehler: Titel darf nicht leer sein.");
    }

    // Spezifische Validierungen
    if (m instanceof Buch b) {
      if (b.getIsbn() == null || b.getVerfasser() == null || b.getVerlag() == null) {
        valid = false;
        LOGGER.warning("Fehler: Buch '" + b.getTitel() + "' ist unvollständig oder ISBN ungültig.");
      }
    } else if (m instanceof ElektronischesMedium e) {
      if (e.getURL() == null) {
        valid = false;
        LOGGER.warning("Fehler: Elektronisches Medium '" + e.getTitel() + "' hat keine gültige URL.");
      }
    }

    if (valid) {
      medienListe.add(m);
    }
  }

  /**
   * Löscht ein Medium anhand seines Titels.
   * Falls mehrere Medien mit gleichem Titel existieren, wird eine DuplicateEntryException geworfen.
   *
   * @param titel der Titel des zu löschenden Mediums
   * @throws DuplicateEntryException wenn mehrere Medien mit gleichem Titel existieren
   */
  public void dropMedium(String titel) throws DuplicateEntryException {
    List<Medium> treffer = findMedium(titel, true);

    if (treffer.isEmpty()) {
      LOGGER.info("Kein Medium mit Titel '" + titel + "' gefunden.");
      return;
    }

    if (treffer.size() > 1) {
      // mehrere Treffer → Exception werfen
      throw new DuplicateEntryException("Mehrere Medien mit dem Titel '" + titel + "' gefunden!");
    }

    // nur ein Treffer → löschen
    medienListe.remove(treffer.getFirst());
    LOGGER.info("Medium '" + titel + "' wurde gelöscht.");
  }

  /**
   * Löscht gezielt ein bestimmtes Medium anhand von Titel und Typ.
   *
   * @param titel der Titel des Mediums
   * @param typ   der Typ (z.B. "Buch", "CD", "Zeitschrift", "ElektronischesMedium")
   */
  public void dropMedium(String titel, String typ) {
    Iterator<Medium> it = medienListe.iterator();
    boolean found = false;

    while (it.hasNext()) {
      Medium m = it.next();
      if (m.getTitel().equalsIgnoreCase(titel) &&
          m.getClass().getSimpleName().equalsIgnoreCase(typ)) {
        it.remove();
        found = true;
        LOGGER.info("Medium '" + titel + "' vom Typ '" + typ + "' wurde gelöscht.");
        break;
      }
    }

    if (!found) {
      LOGGER.info("Kein Medium mit Titel '" + titel + "' und Typ '" + typ + "' gefunden.");
    }
  }

  /**
   * Erweiterte Suchfunktion:
   * Sucht alle Medien mit dem gegebenen Titel und gibt sie sortiert zurück.
   * Bei mehreren Treffern werden sie nach Typ (Buch, CD, ElektronischesMedium, Zeitschrift)
   * sortiert. Die Sortierrichtung (a-z oder z-a) kann gewählt werden.
   *
   * @param titel       Der Titel der zu suchenden Medien
   * @param aufsteigend true für aufsteigend, false für absteigend
   * @return Liste der gefundenen Medien (kann leer sein)
   */
  public List<Medium> findMedium(String titel, boolean aufsteigend) {
    List<Medium> treffer = new ArrayList<>();

    // Alle Medien mit passendem Titel sammeln
    for (Medium m : medienListe) {
      if (m.getTitel().equalsIgnoreCase(titel)) {
        treffer.add(m);
      }
    }

    // Keine Treffer → Meldung und Rückgabe einer leeren Liste
    if (treffer.isEmpty()) {
      LOGGER.info("Keine Medien mit Titel '" + titel + "' gefunden.\n");
      return treffer;
    }

    // Sortieren nach Typname
    treffer.sort((m1, m2) -> {
      int cmp = m1.getClass().getSimpleName().compareTo(m2.getClass().getSimpleName());
      if (!aufsteigend) {
        cmp = -cmp;
      }
      return cmp;
    });

    LOGGER.info(treffer.size() + " Medium/Medien mit Titel '" + titel + "' gefunden und sortiert.\n");
    return treffer;
  }

  /**
   * Sortiert die Medien nach Titel.
   *
   * @param ascending true für A->Z, false für Z->A
   */
  public void sort(boolean ascending) {
    // Prüfen, ob Sortierung schon vorhanden
    if (isSorted && ascendingOrder == ascending) {
      LOGGER.info("\nListe ist bereits sortiert, keine Sortierung notwendig.\n");
      return;
    }

    if (ascending) {
      Collections.sort(medienListe); // nutzt compareTo von Medium
    } else {
      medienListe.sort(Collections.reverseOrder());
    }

    // Status aktualisieren
    isSorted = true;
    ascendingOrder = ascending;
  }

  /**
   * Entfernt alle Medien mit dem angegebenen Titel aus dem Zettelkasten.
   *
   * @param titel der Titel der zu entfernenden Medien
   * @return Anzahl der entfernten Einträge
   */
  public int removeAllByTitle(String titel) {
    if (titel == null) {
      return 0;
    }

    int removed = 0;
    Iterator<Medium> it = medienListe.iterator();
    while (it.hasNext()) {
      Medium m = it.next();
      if (m.getTitel().equalsIgnoreCase(titel)) {
        it.remove();
        removed++;
      }
    }

    if (removed > 0) {
      isSorted = false;
    }

    return removed;
  }

  /**
   * Standard-Sortierung (aufsteigend)
   */
  public void sort() {
    sort(true);
  }

  /**
   * Liefert einen Iterator über alle Medien zur Unterstützung von foreach-Schleifen.
   *
   * @return Iterator über {@link Medium}-Objekte
   */
  @Override
  public Iterator<Medium> iterator() {
    return medienListe.iterator();
  }
}