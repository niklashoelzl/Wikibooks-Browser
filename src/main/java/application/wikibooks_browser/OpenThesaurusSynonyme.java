package application.wikibooks_browser;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

/**
 * Hilfsklasse zum Abrufen von Synonymen über OpenThesaurus.
 * <p>
 * Diese Klasse stellt statische Methoden zur Verfügung, um zu einem gegebenen
 * Suchbegriff passende Synonyme von der öffentlichen OpenThesaurus API
 * abzurufen, auszuwerten und sortiert zurückzugeben.
 * </p>
 * <p>
 * Die Kommunikation erfolgt über HTTP. Die Antwort wird im JSON Format
 * verarbeitet. Das Originalwort selbst wird aus der Ergebnisliste entfernt,
 * sodass dieses nicht als Synonym angezeigt wird.
 * </p>
 *
 * @author Niklas Hölzl
 */
public class OpenThesaurusSynonyme {

  /**
   * Liefert eine sortierte Liste von Synonymen zu einem Begriff von OpenThesaurus.
   * <p>
   * Der Suchbegriff wird URL kodiert an die OpenThesaurus API gesendet. Die JSON Antwort wird geparst
   * und alle gefundenen Synonyme werden gesammelt, alphabetisch sortiert und ohne das Originalwort zurückgegeben.
   * </p>
   *
   * @param suchbegriff der Begriff, zu dem Synonyme abgefragt werden sollen
   * @return sortierte Liste von Synonymen ohne den eigentlichen Suchbegriff
   * @throws MyWebException bei Netzwerkfehlern oder Fehlern beim Parsen der JSON Antwort
   */
  public static List<String> sucheSynonyme(String suchbegriff) throws MyWebException {
    String basisUrl = "https://www.openthesaurus.de/synonyme/search";
    String parameter = "?q="
        + URLEncoder.encode(suchbegriff.trim(), StandardCharsets.UTF_8)
        + "&format=application/json";

    try {
      URL myURL = new URL(basisUrl + parameter);
      String jsonResponse = streamToString(myURL.openStream());

      JsonObject root = JsonParser.parseString(jsonResponse).getAsJsonObject();
      JsonArray synsets = root.getAsJsonArray("synsets");

      Set<String> synonyme = getStrings(suchbegriff, synsets);
      return new ArrayList<>(synonyme);

    } catch (IOException e) {
      throw new MyWebException("Netzwerkfehler beim Laden der Synonyme.", e);
    } catch (Exception e) {
      throw new MyWebException("Fehler beim Parsen der JSON-Antwort.", e);
    }
  }

  /**
   * Extrahiert alle Synonyme aus den gelieferten Synsets.
   * <p>
   * Die Synonyme werden in einer {@link TreeSet} Struktur gesammelt,
   * wodurch sie automatisch alphabetisch sortiert wird und doppelte Einträge vermieden werden.
   * </p>
   *
   * @param suchbegriff der Begriff, zu dem Synonyme abgefragt werden sollen
   * @param synsets die von OpenThesaurus gelieferten Synsets
   * @return eine sortierte Menge von Synonymen
   */
  private static Set<String> getStrings(String suchbegriff, JsonArray synsets) {
    Set<String> synonyme = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

    for (var el : synsets) {
      JsonArray terms = el.getAsJsonObject().getAsJsonArray("terms");

      for (var t : terms) {
        String synonym = t.getAsJsonObject().get("term").getAsString();

        if (synonym != null && !synonym.equalsIgnoreCase(suchbegriff.trim())) {
          synonyme.add(synonym);
        }
      }
    }
    return synonyme;
  }

  /**
   * Liest den gesamten Inhalt eines {@link InputStream} in einen String ein.
   * <p>
   * Diese Methode wird verwendet, um die HTTP Antwort der OpenThesaurus API
   * vollständig zu erfassen und als String weiterzuverarbeiten.
   * </p>
   *
   * @param is der zu lesende InputStream
   * @return der vollständige Inhalt des Streams als String
   * @throws IOException falls ein Fehler beim Lesen des Streams auftritt
   */
  public static String streamToString(InputStream is) throws IOException {
    try (Scanner s = new Scanner(is)) {
      s.useDelimiter("\\A");
      return s.hasNext() ? s.next() : "";
    }
  }
}
