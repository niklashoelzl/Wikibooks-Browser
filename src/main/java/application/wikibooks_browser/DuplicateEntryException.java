package application.wikibooks_browser;

/**
 * Wird geworfen, wenn mehrere Medien mit demselben Titel existieren.
 *
 * @author Niklas Hölzl
 */
public class DuplicateEntryException extends Exception {
  public DuplicateEntryException(String message) {
    super(message);
  }
}

