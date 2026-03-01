package application.wikibooks_browser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

/**
 * Implementierung des Persistency-Interfaces für BibTeX-Dateien.
 * <p>
 * Diese Klasse speichert und lädt Medien im BibTeX-ähnlichen Format.
 * Zum Einlesen wird die Methode {@link BibTexPersistency#parseBibTex(String)} verwendet.
 *
 * @author Niklas Hölzl
 */
public class BibTexPersistency implements Persistency {

  private static final Logger LOGGER = Logger.getLogger(BibTexPersistency.class.getName());

  /**
   * Speichert den gegebenen Zettelkasten im BibTeX-Format in eine Datei.
   *
   * @param zk         Zettelkasten mit allen Medien
   * @param dateiname  Name der Zieldatei (z. B. "bibtex.txt")
   * @throws IOException bei Schreibfehlern
   */
  @Override
  public void save(Zettelkasten zk, String dateiname) throws IOException {
    try (BufferedWriter writer = new BufferedWriter(
        new OutputStreamWriter(new FileOutputStream(dateiname), StandardCharsets.UTF_8))) {

      for (Medium m : zk) {
        String entry = convertMediumToBibTex(m);
        writer.write(entry);
        writer.newLine();
        writer.newLine();
      }
    }
  }

  /**
   * Lädt Medien aus einer BibTeX-ähnlichen Datei und erzeugt daraus einen neuen Zettelkasten.
   *
   * @param dateiname Name der Datei, aus der geladen wird
   * @return Neuer Zettelkasten mit geladenen Medien
   * @throws IOException bei Datei- oder Leseproblemen
   */
  @Override
  public Zettelkasten load(String dateiname) throws IOException {
    Zettelkasten zk = new Zettelkasten();

    StringBuilder currentEntry = new StringBuilder();
    try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(new FileInputStream(dateiname), StandardCharsets.UTF_8))) {

      String line;
      while ((line = reader.readLine()) != null) {
        line = line.trim();
        if (line.isEmpty()) {
          // Ende eines Eintrags → verarbeiten
          if (!currentEntry.isEmpty()) {
            try {
              Medium medium = parseBibTex(currentEntry.toString());
              zk.addMedium(medium);
            } catch (Exception e) {
              LOGGER.warning("Fehler beim Parsen eines Eintrags: " + e.getMessage());
            }
            currentEntry.setLength(0);
          }
        } else {
          currentEntry.append(line).append(" ");
        }
      }

      // Letzten Eintrag verarbeiten
      if (!currentEntry.isEmpty()) {
        try {
          Medium medium = parseBibTex(currentEntry.toString());
          zk.addMedium(medium);
        } catch (Exception e) {
          LOGGER.warning("Fehler beim Parsen eines Eintrags: " + e.getMessage());
        }
      }
    }

    return zk;
  }

  /**
   * Parst einen BibTeX-ähnlichen String und erzeugt das passende Medium-Objekt.
   *
   * @param input String in BibTeX-ähnlichem Format, z.B. "@book{...}"
   * @return Medium-Objekt (Buch, CD, Zeitschrift oder ElektronischesMedium)
   * @throws IllegalArgumentException bei ungültiger Eingabe oder Klammerung
   */
  public static Medium parseBibTex(String input) {
    if (input == null || input.isBlank()) {
      throw new IllegalArgumentException("Eingabe darf nicht leer sein.");
    }

    input = input.trim();

    // Typ des Mediums erkennen (@book, @cd, etc.)
    int startBrace = input.indexOf('{');
    if (startBrace == -1 || !input.trim().endsWith("}")) {
      throw new IllegalArgumentException("Ungültige Klammerung im String: " + input);
    }

    String type = input.substring(1, startBrace).trim().toLowerCase(); // z.B. @book -> book
    String content = input.substring(startBrace + 1, input.length() - 1).trim();

    // Variablen für mögliche Felder initialisieren
    String titel = "-", verlag = "-", isbn = "-", verfasser = "-", kuenstler = "-", label = "-", url = "-", issn = "-", dateiformat = "-";
    int jahr = 0, volume = 0, nummer = 0, auflage = 0, seitenanzahl = 0, dauer = 0, altersfreigabe = 0, groesse = 0;

    int index = 0;
    while (index < content.length()) {
      int eq = content.indexOf('=', index);
      if (eq == -1) break;

      String key = content.substring(index, eq).trim().toLowerCase();

      int valStart = content.indexOf('{', eq);
      int valEnd;
      String value;

      if (valStart != -1 && (valStart < content.indexOf(',', eq) || content.indexOf(',', eq) == -1)) {
        // Wert in geschweiften Klammern {...}
        int braceCount = 0;
        valEnd = valStart;
        for (; valEnd < content.length(); valEnd++) {
          if (content.charAt(valEnd) == '{') braceCount++;
          else if (content.charAt(valEnd) == '}') braceCount--;
          if (braceCount == 0) break;
        }
        if (braceCount != 0)
          throw new IllegalArgumentException("Unbalancierte Klammern für Feld " + key);
        value = content.substring(valStart + 1, valEnd).trim();
        index = valEnd + 1;
      } else {
        // kein { }, z.B. "year = 2004"
        int commaPos = content.indexOf(',', eq);
        if (commaPos == -1) commaPos = content.length();
        value = content.substring(eq + 1, commaPos).trim();
        index = commaPos + 1;
      }

      if (value.endsWith(",")) value = value.substring(0, value.length() - 1).trim();

      // Value der passenden Variablen zuweisen
      switch (key) {
        case "title": titel = value; break;
        case "author": verfasser = value; break;
        case "publisher": verlag = value; break;
        case "year":
          try { jahr = Integer.parseInt(value); } catch (NumberFormatException e) { jahr = 0; }
          break;
        case "isbn": isbn = value; break;
        case "artist": kuenstler = value; break;
        case "label": label = value; break;
        case "url": url = value; break;
        case "issn": issn = value; break;
        case "volume":
          try { volume = Integer.parseInt(value); } catch (NumberFormatException e) { volume = 0; }
          break;
        case "number":
          try { nummer = Integer.parseInt(value); } catch (NumberFormatException e) { nummer = 0; }
          break;
        case "auflage":
          try { auflage = Integer.parseInt(value); } catch (NumberFormatException e) { auflage = 0; }
          break;
        case "seitenanzahl":
          try { seitenanzahl = Integer.parseInt(value); } catch (NumberFormatException e) { seitenanzahl = 0; }
          break;
        case "dauer":
          try { dauer = Integer.parseInt(value); } catch (NumberFormatException e) { dauer = 0; }
          break;
        case "altersfreigabe":
          try { altersfreigabe = Integer.parseInt(value); } catch (NumberFormatException e) { altersfreigabe = 0; }
          break;
        case "dateiformat": dateiformat = value; break;
        case "groesse":
          try { groesse = Integer.parseInt(value); } catch (NumberFormatException e) { groesse = 0; }
          break;
      }

      // Nächster Startpunkt (nach Komma)
      if (index < content.length() && content.charAt(index) == ',') index++;
    }

    // Passendes Medium erzeugen basierend auf Typ
    return switch (type) {
      case "book" -> new Buch(titel, jahr, verlag, isbn, verfasser, auflage, seitenanzahl);
      case "cd" -> new CD(titel, label, kuenstler, dauer, altersfreigabe);
      case "journal" -> new Zeitschrift(titel, issn, volume, nummer, auflage, seitenanzahl);
      case "elmed" -> new ElektronischesMedium(titel, url, dateiformat, groesse);
      default -> throw new IllegalArgumentException("Unbekannter Medientyp: " + type);
    };
  }

  /**
   * Hilfsmethode zur Umwandlung eines Medium-Objekts in einen BibTeX-ähnlichen String.
   *
   * @param m Medium-Objekt
   * @return String im BibTeX-ähnlichen Format
   */
  private String convertMediumToBibTex(Medium m) {
    return switch (m) {
      case Buch buch -> String.format(
          "@book{author = {%s}, title = {%s}, publisher = {%s}, year = {%d}, isbn = {%s}, auflage = {%d}, seitenanzahl = {%d}}",
          buch.getVerfasser(), buch.getTitel(), buch.getVerlag(), buch.getErscheinungsjahr(),
          buch.getIsbn(), buch.getAuflage(), buch.getSeitenanzahl());
      case CD cd -> String.format(
          "@cd{artist = {%s}, title = {%s}, label = {%s}, dauer = {%d}, altersfreigabe = {%d}}",
          cd.getKuenstler(), cd.getTitel(), cd.getLabel(), cd.getDauer(), cd.getAltersfreigabe());
      case Zeitschrift z -> String.format(
          "@journal{title = {%s}, issn = {%s}, volume = {%d}, number = {%d}, auflage = {%d}, seitenanzahl = {%d}}",
          z.getTitel(), z.getIssn(), z.getVolume(), z.getNummer(), z.getAuflage(),
          z.getSeitenanzahl());
      case ElektronischesMedium em -> String.format(
          "@elmed{title = {%s}, url = {%s}, dateiformat = {%s}, groesse = {%d}}",
          em.getTitel(), em.getURL(), em.getDateiformat(), em.getGroesse());
      default -> "@unknown{title = {" + m.getTitel() + "}}";
    };
  }
}

