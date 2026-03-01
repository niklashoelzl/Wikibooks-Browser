package application.wikibooks_browser;

/**
 * Exception Klasse für Fehler bei Webzugriffen auf WikiBooks.
 *
 * <p>Diese wird verwendet, wenn beim Laden, Abrufen oder Verarbeiten
 * von WikiBooks Daten ein Fehler auftritt, z.B.:
 *
 * <ul>
 *   <li>Netzwerkprobleme wie z.B. HTTP-Fehler und Timeouts</li>
 *   <li>Ungültige oder nicht existierende WikiBooks Einträgen</li>
 *   <li>Fehler beim Parsen der XML-Daten</li>
 * </ul>
 *
 * @author Niklas Hölzl
 */
public class MyWebException extends Exception {

  /**
   * Erstellt eine neue {@code MyWebException} mit einer Fehlermeldung.
   *
   * @param message die Beschreibung des aufgetretenen Fehlers
   */
  public MyWebException(String message) {
    super(message);
  }

  /**
   * Erstellt eine neue {@code MyWebException} mit einer Fehlermeldung und der
   * Ursache des Fehlers.
   *
   * <p>Diese Variante wird verwendet, um eine niedrigere
   * Exception (z.B. {@link java.io.IOException}) weiterzureichen, ohne die
   * ursprüngliche Fehlerursache zu verlieren.
   *
   * @param message die Beschreibung des aufgetretenen Fehlers
   * @param cause   die ursprüngliche Exception, die den Fehler verursacht hat
   */
  public MyWebException(String message, Throwable cause) {
    super(message, cause);
  }
}
