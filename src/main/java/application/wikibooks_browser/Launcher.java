package application.wikibooks_browser;

import javafx.application.Application;

/**
 * Einstiegspunkt der Anwendung.
 * <p>
 * Diese Klasse dient ausschließlich als separater Launcher für die JavaFX-Applikation.
 * Sie startet die eigentliche Anwendung, indem sie die {@link Main} Klasse über
 * {@link Application#launch(Class, String...)} aufruft.
 * </p>
 * <p>
 * Wird verwendet, um Probleme beim Starten von
 * JavaFX-Anwendungen in bestimmten Umgebungen oder IDEs zu vermeiden.
 * </p>
 *
 * @author Niklas Hölzl
 */
public class Launcher {

  /**
   * Startpunkt des Programms.
   * <p>
   * Übergibt die Kommandozeilenargumente an JavaFX und
   * startet die {@link Main} Klasse als JavaFX Applikation.
   * </p>
   *
   * @param args Kommandozeilenargumente, die an die Anwendung übergeben werden
   */
  public static void main(String[] args) {
    Application.launch(Main.class, args);
  }
}
