package application.wikibooks_browser;

import java.util.Objects;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Hauptklasse der JavaFX-Anwendung.
 * <p>
 * Diese Klasse wird von {@link Application} erweitert und stellt den Einstiegspunkt
 * dar. Sie ist verantwortlich für das Laden der FXML Datei, das Erzeugen der {@link Scene} und das
 * Initialisieren und Anzeigen des Hauptfensters ({@link Stage}).
 * </p>
 * <p>
 * Zusätzlich wird hier der Titel der Anwendung und das Anwendungsicon gesetzt.
 * Kommt es während des Startvorgangs zu einem Fehler, wird eine Fehlermeldung
 * in Form eines {@link Alert} Dialogs angezeigt und die Anwendung wird beendet.
 * </p>
 *
 * @author Niklas Hölzl
 */
public class Main extends Application {

  private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

  /**
   * Startet die JavaFX Anwendung.
   * <p>
   * Diese Methode wird automatisch von der JavaFX-Laufzeitumgebung
   * aufgerufen. Sie lädt die Benutzeroberfläche aus der FXML Datei
   * {@code View.fxml}. Außerdem wird hier die Szene und Konfiguration des
   * Hauptfensters der Anwendung erstellt.
   * </p>
   * <p>
   * Wenn es beim Laden der FXML Datei oder beim Initialisieren der Oberfläche
   * einen Fehler gibt, wird ein Fehlerdialog angezeigt und die Anwendung
   * anschließend beendet.
   * </p>
   *
   * @param primaryStage das Hauptfenster der JavaFX Anwendung
   */
  @Override
  public void start(Stage primaryStage) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource(
          "/application/wikibooks_browser/View.fxml"));
      Parent root = loader.load();
      Scene scene = new Scene(root, 1300, 700);

      primaryStage.setTitle("Mein Wikibooks Browser");

      Image icon =
          new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Images/WikibooksLogo.png")));
      primaryStage.getIcons().add(icon);

      primaryStage.setScene(scene);

      primaryStage.setMinWidth(1000);
      primaryStage.setMinHeight(650);

      primaryStage.show();
    } catch (Exception e) {
      LOGGER.severe("Fehler beim Laden der Benutzeroberfläche: " + e.getMessage());

      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Fehler beim Starten");
      alert.setHeaderText("Anwendung konnte nicht gestartet werden");
      alert.setContentText(
          "Fehler beim Laden der Benutzeroberfläche: "
              + e.getMessage()
              + "\n\nDie Anwendung wird beendet.");
      alert.showAndWait();

      Platform.exit();
    }
  }
}
