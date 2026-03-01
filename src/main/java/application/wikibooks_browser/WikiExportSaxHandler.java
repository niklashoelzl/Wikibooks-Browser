package application.wikibooks_browser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SAX-Handler zum Parsen von Wikibooks-Export-XML-Dokumenten.
 *
 * <p>Extrahiert folgende Informationen:
 *
 * <ul>
 *   <li>Redirect-Ziele
 *   <li>Textinhalt der Seite
 *   <li>Contributor (Username)
 *   <li>Timestamp der letzten Änderung
 * </ul>
 *
 * <p>Wird von {@link WikiBooks} verwendet, um XML-Inhalte effizient zu verarbeiten.
 *
 * @author Niklas Hölzl
 */
public class WikiExportSaxHandler extends DefaultHandler {

  private boolean inRevision = false;
  private boolean inContributor = false;
  private boolean inUsername = false;
  private boolean inIP = false;
  private String timestamp = null;

  private boolean inText = false;
  private final StringBuilder fullText = new StringBuilder();
  private final StringBuilder text = new StringBuilder();

  private String contributor = null;
  private boolean gotContributor = false;
  private String redirectTitle = null;

  /**
   * Liefert den extrahierten Contributor zurück.
   *
   * @return Contributor Name
   */
  public String getContributor() {
    return contributor;
  }

  /**
   * Liefert den Titel des Redirects zurück, falls vorhanden.
   *
   * @return Zieltitel der Weiterleitung oder null
   */
  public String getRedirectTitle() {
    return redirectTitle;
  }

  /**
   * Liefert den Timestamp der letzten Änderung zurück.
   *
   * @return Timestamp als String im XML-Format (UTC)
   */
  public String getTimestamp() {
    return timestamp;
  }

  /**
   * Liefert den vollständigen Textinhalt der Wikibooks-Seite zurück.
   *
   * @return Text der Seite
   */
  public String getPageText() {
    return fullText.toString();
  }

  /**
   * Wird vom SAX-Parser aufgerufen, wenn ein neues XML-Element beginnt.
   *
   * <p>Setzt interne Flags für Elemente wie &lt;revision&gt; &lt;contributor&gt;
   * &lt;username&gt; &lt;ip&gt; &lt;text&gt; und speichert Redirect-Titel.
   *
   * @param uri Namespace-URI, falls vorhanden
   * @param localName lokaler Name des Elements
   * @param qName qualifizierter Name des Elements
   * @param attributes Attribute des Elements
   * @throws SAXException bei ungültigen Redirects
   */
  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes)
      throws SAXException {
    text.setLength(0);
    String name = qName != null ? qName : localName;

    switch (name.toLowerCase()) {
      case "revision":
        inRevision = true;
        break;
      case "contributor":
        inContributor = true;
        break;
      case "username":
        inUsername = true;
        break;
      case "ip":
        inIP = true;
        break;
      case "redirect":
        String t = attributes.getValue("title");
        if (t == null) throw new SAXException("Redirect-Element ohne 'title'-Attribut gefunden!");
        if (redirectTitle == null) redirectTitle = t;
        break;
      case "text":
        inText = true;
        break;
    }
  }

  /**
   * Wird vom SAX-Parser aufgerufen, wenn Text innerhalb eines XML-Elements gefunden wird.
   *
   * <p>Speichert Text in einem temporären StringBuilder und fügt bei &lt;text&gt;-Elementen den
   * Inhalt zum vollständigen Seitentext hinzu.
   *
   * @param chars Array mit Zeichen
   * @param start Startindex im Array
   * @param length Anzahl der zu lesenden Zeichen
   * @throws SAXException bei ungültigen Zeichen
   */
  @Override
  public void characters(char[] chars, int start, int length) throws SAXException {
    if (chars == null || length < 0) throw new SAXException("Ungültige Zeichen im XML gefunden");
    text.append(chars, start, length);
    if (inText) fullText.append(chars, start, length);
  }

  /**
   * Wird vom SAX-Parser aufgerufen, wenn ein XML-Element endet.
   *
   * <p>Verarbeitet den Text für Contributor, Timestamp, Textinhalt und setzt Flags zurück.
   *
   * @param uri Namespace-URI, falls vorhanden
   * @param localName lokaler Name des Elements
   * @param qName qualifizierter Name des Elements
   * @throws SAXException bei ungültigen oder leeren Inhalten
   */
  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    String name = qName != null ? qName : localName;

    if (inUsername && "username".equalsIgnoreCase(name)) {
      if (!gotContributor) {
        contributor = text.toString().trim();
        if (contributor.isEmpty()) throw new SAXException("Leerer Username gefunden");
        gotContributor = true;
      }
      inUsername = false;
    } else if (inIP && "ip".equalsIgnoreCase(name)) {
      if (!gotContributor) {
        contributor = text.toString().trim();
        if (contributor.isEmpty())
          throw new SAXException("Leere IP-Adresse als Contributor gefunden");
        gotContributor = true;
      }
      inIP = false;
    } else if (inContributor && "contributor".equalsIgnoreCase(name)) {
      inContributor = false;
    }

    if (inRevision && "timestamp".equalsIgnoreCase(name)) {
      timestamp = text.toString().trim();
      if (timestamp.isEmpty()) throw new SAXException("Leerer Timestamp in Revision gefunden");
    } else if (inRevision && "revision".equalsIgnoreCase(name)) {
      inRevision = false;
    }

    if ("text".equalsIgnoreCase(name)) inText = false;

    if ("page".equalsIgnoreCase(name) && !gotContributor) {
      contributor = "Unbekannt";
      gotContributor = true;
    }

    text.setLength(0);
  }
}
