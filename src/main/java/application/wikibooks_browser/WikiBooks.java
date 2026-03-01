package application.wikibooks_browser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;

/**
 * Hilfsklasse für alle Wikibooks Funktionen.
 * <p>
 * Diese Klasse kapselt den Zugriff auf die Wikibooks Website und stellt
 * Methoden bereit, um Wikibooks Einträge abzurufen, Redirects aufzulösen,
 * Inhalte zu parsen und Metadaten wie z.b. Kapitel, Kategorien bzw. Regale,
 * letzte Bearbeitung und Bearbeiter zu extrahieren.
 * </p>
 * <p>
 * Die Daten werden über den Wikibooks Export als XML geladen und anschließend
 * mithilfe eines SAX Parsers weiter verarbeitet.
 * </p>
 *
 * @author Niklas Hölzl
 */
public class WikiBooks {

  /**
   * Basis URL für reguläre Wikibooks Einträge.
   */
  private static final String BASE_URL = "https://de.wikibooks.org/wiki/";

  /**
   * Basis URL für den XML-Export von Wikibooks Einträgen.
   */
  private static final String EXPORT_BASE = "https://de.wikibooks.org/wiki/Spezial:Exportieren/";

  /**
   * Erzeugt die vollständige Wikibooks URL zu einem vom Benutzer eingegebenen Begriff.
   * <p>
   * Leerzeichen im Titel werden durch Unterstriche ersetzt.
   * Ist der Titel null oder leer, wird die Startseite von Wikibooks angezeigt.
   * </p>
   *
   * @param title der Titel des Wikibooks Eintrags
   * @return die vollständige URL zum entsprechenden Wikibooks Eintrag
   */
  public static String getUrlForTitle(String title) {
    if (title == null || title.trim().isEmpty()) {
      return "https://de.wikibooks.org";
    }
    String normalized = title.trim().replace(" ", "_");
    return BASE_URL + normalized;
  }

  /**
   * Lädt ein Wiki-book inklusive Redirect Auflösung.
   * <p>
   * Diese Methode ist der öffentliche Einstiegspunkt und ruft rekursive
   * die Klasse mit der eigentlichen Implementierung auf.
   * </p>
   *
   * @param pageTitle der Titel des Wikibooks Eintrags
   * @return ein {@link WikiBook} Objekt samt allen extrahierten Informationen
   * @throws MyWebException bei Netzwerk, Parse oder Redirect Fehlern
   */
  public static WikiBook fetchWikiBookWithRedirectHandling(String pageTitle) throws MyWebException {
    return fetchWikiBookWithRedirectHandling(pageTitle, 0);
  }

  /**
   * Lädt ein Wiki-book und verarbeitet Redirects.
   * <p>
   * Die Methode lädt den XML-Export der Seite, prüft auf Redirects.
   * Anschließend wird der Seiteninhalt durch einen SAX-Parser weiter verarbeitet.
   * </p>
   *
   * @param pageTitle der Titel des Wikibooks Eintrags
   * @param depth     aktuelle Redirect Tiefe
   * @return ein {@link WikiBook} Objekt mit den extrahierten Daten
   * @throws MyWebException bei zu vielen Redirects, Netzwerk oder Parse Fehlern
   */
  private static WikiBook fetchWikiBookWithRedirectHandling(String pageTitle, int depth) throws MyWebException {
    if (depth > 5) {
      throw new MyWebException("Zu viele Redirects");
    }

    String normalizedTitle = pageTitle.trim().replace(" ", "_");
    String exportUrl = EXPORT_BASE + URLEncoder.encode(normalizedTitle, StandardCharsets.UTF_8);

    String xml;
    try {
      URL url = URI.create(exportUrl).toURL();
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
      conn.setConnectTimeout(10_000);
      conn.setReadTimeout(15_000);

      int code = conn.getResponseCode();
      if (code < 200 || code >= 300) {
        throw new MyWebException("HTTP-Fehler " + code + " beim Laden der Export Seite.");
      }

      try (InputStream in = conn.getInputStream();
          BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
          sb.append(line).append('\n');
        }
        xml = sb.toString();
      }
    } catch (IOException e) {
      throw new MyWebException("Netzwerkfehler beim Laden der Export Seite.", e);
    }

    // Prüfung auf Redirects
    Pattern redirectPattern = Pattern.compile("<redirect\\s+title=\"([^\"]+)\"", Pattern.CASE_INSENSITIVE);

    Matcher redirectMatcher = redirectPattern.matcher(xml);
    if (redirectMatcher.find()) {
      return fetchWikiBookWithRedirectHandling(redirectMatcher.group(1), depth + 1);
    }

    // SAX Parsing
    WikiExportSaxHandler handler = new WikiExportSaxHandler();
    try {
      SAXParserFactory factory = SAXParserFactory.newInstance();
      SAXParser parser = factory.newSAXParser();
      parser.parse(new InputSource(new StringReader(xml)), handler);
    } catch (Exception e) {
      throw new MyWebException("Fehler beim Parsen der XML-Daten", e);
    }

    String pageText = handler.getPageText();
    if (pageText == null || pageText.trim().isEmpty()) {
      throw new MyWebException("Das Buch \"" + pageTitle + "\" konnte nicht gefunden werden oder hat keinen Inhalt.");
    }

    // Erkennung der Regale durch verschiedene Regal Vorlagen
    List<String> categories = new ArrayList<>();

    List<String> relevantKeywords = List.of("regal", "Regalhinweis", "Infoleiste Schule", "Bücherregal", "reihe", "infoleiste");

    Pattern templatePattern = Pattern.compile("\\{\\{([^}|]+)([^}]*)}}");
    Matcher templateMatcher = templatePattern.matcher(pageText);

    while (templateMatcher.find()) {
      String templateName = templateMatcher.group(1).trim().toLowerCase();
      String paramsStr = templateMatcher.group(2).trim();

      boolean isRelevant = false;
      for (String keyword : relevantKeywords) {
        if (templateName.contains(keyword)) {
          isRelevant = true;
          break;
        }
      }
      if (!isRelevant) continue;

      String[] params = paramsStr.split("\\|");
      for (String param : params) {
        param = param.trim();
        if (param.isEmpty()) continue;

        String value;
        if (param.contains("=")) {
          String[] split = param.split("=", 2);
          String key = split[0].trim().toLowerCase();
          value = split[1].trim();

          if (key.equals("icon") || key.equals("position") || key.equals("icon-size") || key.equals("background")
              || key.matches("[a-z]")
              || key.isEmpty()) {
            continue;
          }
        } else {
          value = param;
        }

        value = value.replaceAll("\\[\\[.*?]]", "").replaceAll("\\{\\{.*?}}", "")
            .replaceAll("\\|", "").trim();

        if (!value.isEmpty()
            && !value.matches("\\d+|\\w")
            && !categories.contains(value)) {
          categories.add(value);
        }
      }
      if (templateName.contains("infoleiste") && templateName.contains("schule")) {
        if (!categories.contains("Schule")) categories.add("Schule");
      }
    }

    Pattern categoryPattern = Pattern.compile("\\[\\[Kategorie:(.*?(?:Regal|Reihe|Schule).*?):([^]|]+)]]", Pattern.CASE_INSENSITIVE);

    Matcher catMatcher = categoryPattern.matcher(pageText);
    while (catMatcher.find()) {
      String cat = catMatcher.group(2).trim();
      if (!categories.contains(cat)) categories.add(cat);
    }

    if (categories.isEmpty()) categories.add("Unbekannt");

    // Erkennung der Kapitel (werden durch Überschriften in der Form "== Kapitel ==" extrahiert)
    List<String> chapters = new ArrayList<>();
    Pattern kapitelPattern = Pattern.compile("==\\s*([^=]+?)\\s*==");
    Matcher kapitelMatcher = kapitelPattern.matcher(pageText);
    while (kapitelMatcher.find()) {
      chapters.add(kapitelMatcher.group(1).trim());
    }

    // Lesen der Metadaten wie letzter bearbeiter und zeitstempel
    String contributor = handler.getContributor();
    if (contributor == null || contributor.isEmpty()) {
      contributor = "Unbekannt";
    }

    String localTime = "Unbekannt";
    String rawTs = handler.getTimestamp();
    if (rawTs != null && !rawTs.isEmpty()) {
      try {
        Instant instant = Instant.parse(rawTs);
        ZonedDateTime local = instant.atZone(ZoneId.systemDefault());
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd.MM.yyyy 'um' HH:mm");
        localTime = local.format(fmt);
      } catch (DateTimeParseException e) {
        localTime = "Ungültiges Datum";
      }
    }
    return new WikiBook(pageTitle, contributor, localTime, categories, chapters);
  }
}
