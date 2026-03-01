package application.wikibooks_browser;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;

/**
 * Controller Klasse für die WikiBooks-Browser-Anwendung.
 *
 * <p>
 * Diese Klasse verbindet die grafische Benutzeroberfläche
 * mit der Anwendungslogik. Sie verarbeitet Benutzereingaben, steuert die Navigation im
 * integrierten Browser, lädt Metadaten von Wikibooks-Seiten, verwaltet den Zettelkasten
 * sowie Import- und Exportfunktionen und bietet eine Synonymsuche über OpenThesaurus an.
 * </p>
 *
 * @author Niklas Hölzl
 */
public class WikiBooksController {

  /** ObservableList für die gefundenen Synonyme. Zeigt diese in der ListView an. */
  private final ObservableList<String> synonymItems = FXCollections.observableArrayList();

  /** ObservableList für die Anzeige der Titel im Zettelkasten. */
  private final ObservableList<String> zettelkastenItems = FXCollections.observableArrayList();

  /** ObservableList für die Anzeige der Such History in der ComboBox. */
  private final ObservableList<String> historyItems = FXCollections.observableArrayList();

  /** Interne Liste für die handhabung der Navigation's History (Vor und Zurück Button). */
  private final List<String> history = new ArrayList<>();

  @FXML private BorderPane rootPane;
  @FXML private TextField tfSearchTerm;
  @FXML private WebView browser;
  @FXML private Label lblUrheber;
  @FXML private Label lblLetztesUpdate;
  @FXML private Label lblRegal;
  @FXML private Label lblKapitelAnzahl;
  @FXML private Label lblSynonymStatus;
  @FXML private ListView<String> lvSynonyms;
  @FXML private ListView<String> lvTitles;
  @FXML private Button btnSearch;
  @FXML private Button btnSynonymSearch;
  @FXML private Button btnUseSynonym;
  @FXML private Button btnHinzufuegen;
  @FXML private Button btnSortieren;
  @FXML private Button btnLoeschen;
  @FXML private Button btnSpeichern;
  @FXML private Button btnLaden;
  @FXML private Button btnImport;
  @FXML private Button btnExport;
  @FXML private Button btnBack;
  @FXML private Button btnForward;
  @FXML private Button btnShowLastChange;
  @FXML private ComboBox<String> cbHistory;
  @FXML private MenuBar menuBar;


  private WebEngine engine;
  private Zettelkasten zettelkasten;
  private WikiBook currentWikiBook;
  private boolean sortAscending = true;
  private List<Node> tabOrder;

  /** Für das binäre Speichern und Laden des Zettelkastens. */
  private Persistency binaryPersistency;

  /** Für BibTeX Import und Export. */
  private Persistency bibTexPersistency;

  /** Aktueller Index in der Navigation's History. */
  private int historyIndex = -1;

  /** Variable zur Vermeidung rekursiver Updates bei History Änderungen. */
  private boolean historyUpdating = false;

  /**
   * Initialisiert den Controller und alle UI Komponenten.
   *
   * <p>
   * Diese Methode wird automatisch von JavaFX nach dem Laden der FXML Datei aufgerufen.
   * Sie richtet die WebEngine ein, initialisiert Datenstrukturen und
   * verknüpft ObservableLists mit den UI Elementen.
   * </p>
   */
  @FXML
  public void initialize() {
    // WebEngine initialisieren
    engine = browser.getEngine();
    engine.load("https://de.wikibooks.org");

    // Datenstrukturen initialisieren
    zettelkasten = new Zettelkasten();
    binaryPersistency = new BinaryPersistency();
    bibTexPersistency = new BibTexPersistency();

    // ObservableLists mit UI verbinden
    lvSynonyms.setItems(synonymItems);
    lvTitles.setItems(zettelkastenItems);
    cbHistory.setItems(historyItems);

    // Buttons initial deaktivieren
    btnSynonymSearch.setDisable(true);
    btnUseSynonym.setDisable(true);
    btnBack.setDisable(true);
    btnForward.setDisable(true);

    // Event Handler registrieren
    setupEventHandlers();
    setupTabOrder();
  }

  /** Registriert Event-Handler für die UI Komponenten. */
  private void setupEventHandlers() {
    // Enter Taste im TextField
    tfSearchTerm.setOnKeyPressed(
        evt -> {
          if (evt.getCode() == KeyCode.ENTER) {
            performSearch(tfSearchTerm.getText());
            evt.consume();
          }
        });

    // TextField Änderungen für Synonym Button
    tfSearchTerm
        .textProperty()
        .addListener(
            (obs, oldVal, newVal) ->
                btnSynonymSearch.setDisable(newVal == null || newVal.isBlank()));

    // Doppelklick auf Synonym
    lvSynonyms.setOnMouseClicked(
        evt -> {
          if (evt.getClickCount() == 2) {
            handleUseSynonym();
          }
        });

    // Enter auf Synonym Liste
    lvSynonyms.setOnKeyPressed(
        evt -> {
          if (evt.getCode() == KeyCode.ENTER) {
            handleUseSynonym();
            evt.consume();
          }
        });

    // Doppelklick auf Zettelkasten Eintrag
    lvTitles.setOnMouseClicked(
        evt -> {
          if (evt.getClickCount() == 2) {
            showSelectedMediumDetails();
          }
        });

    // Reagiere erst, wenn Dropdown geschlossen wird bzw. eine Auswahl bestätigt worden ist
    cbHistory.showingProperty().addListener((obs, wasShowing, isNowShowing) -> {
      if (wasShowing && !isNowShowing) {
        String selected = cbHistory.getSelectionModel().getSelectedItem();
        if (selected != null) {
          jumpToHistory(selected);
        }
      }
    });

    // Enter Taste bei geöffneter ComboBox
    cbHistory.setOnKeyPressed(
        evt -> {
          if (evt.getCode() == KeyCode.ENTER && cbHistory.isShowing()) {
            cbHistory.hide();
            Platform.runLater(() -> focusNext(true));
            evt.consume();
          }
        });
  }

  /**
   * Navigiert den integrierten Browser zur Wikibooks Seite des vom Benutzer eingegebenen Titels.
   *
   * @param searchTerm Titel des gesuchten WikiBooks
   */
  private void navigateBrowser(String searchTerm) {
    String url = WikiBooks.getUrlForTitle(searchTerm);
    engine.load(url);
  }

  /**
   * Führt eine vollständige Suche durch.
   *
   * <p>
   * Validiert die Eingabe, navigiert zum entsprechenden Wikibooks Eintrag,
   * lädt asynchron die Metadaten und aktualisiert die Navigation's History.
   * </p>
   *
   * @param searchTerm der eingegebene Suchbegriff
   */
  private void performSearch(String searchTerm) {
    if (searchTerm == null || searchTerm.trim().isEmpty()) {
      showAlert(Alert.AlertType.ERROR, "Fehler", "Ein Fehler ist aufgetreten", "Bitte geben Sie einen Suchbegriff ein.");
      return;
    }

    String normalized = searchTerm.trim();
    tfSearchTerm.setText(normalized);

    // Cursor ans Ende setzten
    tfSearchTerm.positionCaret(normalized.length());

    // UML konforme Methode aufrufen
    navigateBrowser(normalized);

    // Metadaten laden
    loadMetadata(normalized);

    // Verlauf aktualisieren
    recordHistoryEntry(normalized);
  }

  /** Handler für den Such Button. Ruft {@link #performSearch(String)} auf. */
  @FXML
  private void handleSearchButton() {
    performSearch(tfSearchTerm.getText());
  }

  /**
   * Fügt einen neuen Eintrag zur Navigation's History hinzu.
   *
   * @param term der Suchbegriff, der in die History aufgenommen werden soll
   */
  private void recordHistoryEntry(String term) {
    // Vorwärts Verlauf entfernen, wenn wir neu suchen
    while (history.size() - 1 > historyIndex) {
      history.removeLast();
    }

    if (history.isEmpty() || !history.getLast().equalsIgnoreCase(term)) {
      history.add(term);
    }
    historyIndex = history.size() - 1;

    refreshHistoryUi();
  }

  /**
   * Springt direkt zum Eintrag in der Navigation's History.
   *
   * @param term der Suchbegriff, zu dem navigiert werden soll
   */
  private void jumpToHistory(String term) {
    if (historyUpdating) return;

    int idx = -1;
    for (int i = history.size() - 1; i >= 0; i--) {
      if (history.get(i).equalsIgnoreCase(term)) {
        idx = i;
        break;
      }
    }

    if (idx >= 0) {
      historyIndex = idx;
      loadHistoryEntry();
    }
  }

  /** Aktualisiert die Anzeige der History ComboBox und die Aktivierung der Vor und Zurück Buttons. */
  private void refreshHistoryUi() {
    historyUpdating = true;
    try {
      List<String> reversed = new ArrayList<>(history);
      Collections.reverse(reversed);
      historyItems.setAll(reversed);

      if (historyIndex >= 0 && historyIndex < history.size()) {
        int reversedIndex = reversed.size() - 1 - historyIndex;
        cbHistory.getSelectionModel().select(reversedIndex);
      } else {
        cbHistory.getSelectionModel().clearSelection();
      }

      btnBack.setDisable(historyIndex <= 0);
      btnForward.setDisable(historyIndex < 0 || historyIndex >= history.size() - 1);
    } finally {
      historyUpdating = false;
    }
  }

  /** Springt einen Schritt zurück in der History. */
  @FXML
  private void handleBack() {
    if (historyIndex > 0) {
      historyIndex--;
      loadHistoryEntry();
    }
  }

  /** Springt einen Schritt vor in der History. */
  @FXML
  private void handleForward() {
    if (historyIndex < history.size() - 1) {
      historyIndex++;
      loadHistoryEntry();
    }
  }

  /** Lädt den aktuell durch {@link #historyIndex} referenzierten History Eintrag (Browser + Metadaten). */
  private void loadHistoryEntry() {
    if (historyIndex < 0 || historyIndex >= history.size()) return;

    String term = history.get(historyIndex);
    tfSearchTerm.setText(term);

    // UML-konforme Methode verwenden
    navigateBrowser(term);

    loadMetadata(term);
    refreshHistoryUi();
  }

  /**
   * Lädt die Metadaten zu einem WikiBook asynchron in einem separaten Thread.
   *
   * @param searchTerm Titel des WikiBooks
   */
  private void loadMetadata(String searchTerm) {
    // UI sofort aktualisieren
    lblUrheber.setText("Urheber: Lade...");
    lblLetztesUpdate.setText("Letzte Änderung: Lade...");
    lblRegal.setText("Regal: Lade...");
    lblKapitelAnzahl.setText("Anzahl Kapitel: Lade...");

    // Hintergrund Thread für Netzwerk Zugriff
    new Thread(
        () -> {
          try {
            // Netzwerk Zugriff im Hintergrund
            WikiBook wikiBook = WikiBooks.fetchWikiBookWithRedirectHandling(searchTerm);

            Platform.runLater(
                () -> {
                  currentWikiBook = wikiBook;
                  updateMetadataLabels(wikiBook);
                });

          } catch (MyWebException webEx) {
            Platform.runLater(
                () -> {
                  currentWikiBook = null;
                  resetMetadataLabels();
                  showAlert(Alert.AlertType.ERROR,"Fehler","Ein Fehler ist aufgetreten",webEx.getMessage());
                });
          } catch (Exception e) {
            Platform.runLater(
                () -> {
                  currentWikiBook = null;
                  resetMetadataLabels();
                  showAlert(Alert.AlertType.ERROR,"Fehler","Ein Fehler ist aufgetreten","Unerwarteter Fehler beim Laden der Metadaten: " + e.getMessage());
                });
          }
        })
        .start();
  }

  /**
   * Aktualisiert die Metadaten Labels mit den Daten des geladenen WikiBooks.
   *
   * @param wikiBook das geladene WikiBook
   */
  private void updateMetadataLabels(WikiBook wikiBook) {
    lblUrheber.setText(
        "Urheber: "
            + (wikiBook.getContributor() != null ? wikiBook.getContributor() : "Unbekannt"));
    lblLetztesUpdate.setText(
        "Letzte Änderung: "
            + (wikiBook.getLastChange() != null ? wikiBook.getLastChange() : "Unbekannt"));

    String regale =
        wikiBook.getCategories() != null && !wikiBook.getCategories().isEmpty()
            ? String.join(", ", wikiBook.getCategories())
            : "Unbekannt";
    lblRegal.setText("Regal: " + regale);

    int kapitelAnzahl = wikiBook.getChapters() != null ? wikiBook.getChapters().size() : 0;
    lblKapitelAnzahl.setText("Anzahl Kapitel: " + kapitelAnzahl);
  }

  /** Setzt die Metadaten Labels auf den fehlgeschlagenen Zustand zurück. */
  private void resetMetadataLabels() {
    lblUrheber.setText("Urheber: Fehler beim Laden");
    lblLetztesUpdate.setText("Letzte Änderung: Fehler beim Laden");
    lblRegal.setText("Regal: Fehler beim Laden");
    lblKapitelAnzahl.setText("Anzahl Kapitel: Fehler beim Laden");
  }

  /**
   * Zeigt Informationen zur letzten Änderung des aktuellen WikiBooks an.
   */
  @FXML
  private void handleShowLastChange() {
    if (currentWikiBook == null) {
      showAlert(Alert.AlertType.WARNING,"Warnung",
          "Kein Buch geladen", "Bitte laden Sie zuerst ein Buch, um Informationen anzuzeigen.");
      return;
    }

    String msg =
        """
                Titel: %s
                Letzte Änderung: %s
                Urheber: %s
                """
            .formatted(
                currentWikiBook.getTitel(),
                currentWikiBook.getLastChange(),
                currentWikiBook.getContributor());

    showAlert(Alert.AlertType.INFORMATION,"Information","Information zur letzten Bearbeitung", msg);
  }

  /**
   * Startet die asynchrone Synonymsuche über OpenThesaurus für den vom Benutzer eingegebenen Suchbegriff.
   */
  @FXML
  private void handleSynonymSearch() {
    String suchbegriff = tfSearchTerm.getText().trim();

    if (suchbegriff.isEmpty()) {
      synonymItems.clear();
      lblSynonymStatus.setText("Synonyme: -");
      btnSynonymSearch.setDisable(true);
      btnUseSynonym.setDisable(true);
      lvSynonyms.setDisable(false);
      return;
    }

    lblSynonymStatus.setText("Synonyme: Suche läuft...");
    btnSynonymSearch.setDisable(true);
    btnUseSynonym.setDisable(true);

    // Asynchroner Thread => UI bleibt responsiv
    new Thread(
        () -> {
          try {
            List<String> synonyme = OpenThesaurusSynonyme.sucheSynonyme(suchbegriff);

            Platform.runLater(
                () -> {
                  synonymItems.clear();
                  if (synonyme.isEmpty()) {
                    synonymItems.add("<keine>");
                    lblSynonymStatus.setText("Synonyme: <keine>");
                    btnSynonymSearch.setDisable(true);
                    btnUseSynonym.setDisable(true);
                    lvSynonyms.setDisable(true);
                  } else {
                    // Elemente wieder aktivieren bei gefundenen Synonymen
                    synonymItems.addAll(synonyme);
                    lblSynonymStatus.setText("Synonyme: " + synonyme.size() + " gefunden");
                    btnSynonymSearch.setDisable(false);
                    btnUseSynonym.setDisable(false);
                    lvSynonyms.setDisable(false);
                  }
                });

          } catch (Exception e) {
            Platform.runLater(
                () -> {
                  synonymItems.clear();
                  synonymItems.add("<keine>");
                  lblSynonymStatus.setText("Synonyme: Fehler");
                  btnSynonymSearch.setDisable(true);
                  btnUseSynonym.setDisable(true);
                  lvSynonyms.setDisable(true);
                  showAlert(Alert.AlertType.ERROR,"Fehler","Ein Fehler ist aufgetreten","Fehler beim Abruf der Synonyme:\n" + e.getMessage());
                });
          }
        })
        .start();
  }

  /**
   * Übernahme des markierten Synonyms aus der ListView als Suchbegriff. Startet anschließend eine neue Suche.
   */
  @FXML
  private void handleUseSynonym() {
    String sel = lvSynonyms.getSelectionModel().getSelectedItem();
    if (sel == null || sel.isBlank()) {
      showAlert(Alert.AlertType.WARNING,"Warnung","Kein Synonym ausgewählt", "Bitte wählen Sie ein Synonym aus der Liste.");
      return;
    }

    tfSearchTerm.setText(sel);
    performSearch(sel);
  }

  /** Fügt das aktuell angezeigte WikiBook zum Zettelkasten hinzu. */
  @FXML
  private void handleHinzufuegen() {
    if (currentWikiBook == null) {
      showAlert(Alert.AlertType.WARNING,"Warnung","Kein WikiBook geladen", "Bitte suchen Sie zuerst nach einem Buch.");
      return;
    }

    zettelkasten.addMedium(currentWikiBook);
    refreshZettelkastenView();
    showAlert(Alert.AlertType.INFORMATION,"Information",
        "Erfolgreich hinzugefügt",
        "Das Buch '" + currentWikiBook.getTitel() + "' wurde zum Zettelkasten hinzugefügt.");
  }

  /** Sortiert den Zettelkasten abwechselnd auf- bzw. absteigend und aktualisiert die Anzeige. */
  @FXML
  private void handleSortieren() {
    zettelkasten.sort(sortAscending);
    sortAscending = !sortAscending;
    refreshZettelkastenView();

    String direction = sortAscending ? "Z-A" : "A-Z";
    showAlert(Alert.AlertType.INFORMATION,"Information","Sortiert", "Zettelkasten wurde sortiert (" + direction + ")");
  }

  /** Löscht alle Einträge mit dem Titel des aktuell geladenen WikiBooks aus dem Zettelkasten. */
  @FXML
  private void handleLoeschen() {
    if (currentWikiBook == null) {
      showAlert(Alert.AlertType.WARNING,"Warnung","Kein WikiBook geladen", "Bitte suchen Sie zuerst nach einem Buch.");
      return;
    }

    String titel = currentWikiBook.getTitel();
    int removed = zettelkasten.removeAllByTitle(titel);
    refreshZettelkastenView();

    if (removed == 0) {
      showAlert(Alert.AlertType.INFORMATION,"Information","Nichts zu löschen", "Kein Medium mit Titel '" + titel + "' gefunden.");
    } else {
      showAlert(Alert.AlertType.INFORMATION,"Information","Gelöscht", removed + " Einträge mit Titel '" + titel + "' wurden entfernt.");
    }
  }

  /** Speichert den aktuellen Zettelkasten binär in einer vom Benutzer gewählten Datei. */
  @FXML
  private void handleSpeichern() {
    FileChooser fc = new FileChooser();
    fc.setTitle("Zettelkasten speichern");
    fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Binary Files", "*.dat"));
    fc.setInitialFileName("zettelkasten.dat");

    File file = fc.showSaveDialog(browser.getScene().getWindow());
    if (file != null) {
      try {
        binaryPersistency.save(zettelkasten, file.getAbsolutePath());
        showAlert(Alert.AlertType.INFORMATION,"Information",
            "Gespeichert",
            "Zettelkasten wurde erfolgreich gespeichert in: " + file.getAbsolutePath());
      } catch (Exception e) {
        showAlert(Alert.AlertType.ERROR,"Fehler","Ein Fehler ist aufgetreten","Fehler beim Speichern: " + e.getMessage());
      }
    }
  }

  /** Lädt einen zuvor binär gespeicherten Zettelkasten aus einer Datei. */
  @FXML
  private void handleLaden() {
    FileChooser fc = new FileChooser();
    fc.setTitle("Zettelkasten laden");
    fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Binary Files", "*.dat"));

    File file = fc.showOpenDialog(browser.getScene().getWindow());
    if (file != null) {
      try {
        zettelkasten = binaryPersistency.load(file.getAbsolutePath());
        refreshZettelkastenView();
        showAlert(Alert.AlertType.INFORMATION,"Information",
            "Geladen", "Zettelkasten wurde erfolgreich geladen aus: " + file.getAbsolutePath());
      } catch (Exception e) {
        showAlert(Alert.AlertType.ERROR,"Fehler","Ein Fehler ist aufgetreten","Fehler beim Laden: " + e.getMessage());
      }
    }
  }

  /** Importiert Medien aus einer BibTeX-Datei und fügt sie dem aktuellen Zettelkasten hinzu. */
  @FXML
  private void handleImport() {
    FileChooser fc = new FileChooser();
    fc.setTitle("BibTeX importieren");
    fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("BibTeX Files", "*.bib", "*.txt"));

    File file = fc.showOpenDialog(browser.getScene().getWindow());
    if (file != null) {
      try {
        Zettelkasten imported = bibTexPersistency.load(file.getAbsolutePath());
        for (Medium m : imported) {
          zettelkasten.addMedium(m);
        }
        refreshZettelkastenView();
        showAlert(Alert.AlertType.INFORMATION,"Information",
            "Importiert", "Daten wurden erfolgreich importiert aus: " + file.getAbsolutePath());
      } catch (Exception e) {
        showAlert(Alert.AlertType.ERROR,"Fehler","Ein Fehler ist aufgetreten","Fehler beim Import: " + e.getMessage());
      }
    }
  }

  /** Exportiert den aktuellen Zettelkasten als BibTeX-Datei. */
  @FXML
  private void handleExport() {
    FileChooser fc = new FileChooser();
    fc.setTitle("BibTeX exportieren");
    fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("BibTeX Files", "*.bib"));
    fc.setInitialFileName("zettelkasten.bib");

    File file = fc.showSaveDialog(browser.getScene().getWindow());
    if (file != null) {
      try {
        bibTexPersistency.save(zettelkasten, file.getAbsolutePath());
        showAlert(Alert.AlertType.INFORMATION, "Information",
            "Exportiert",
            "Zettelkasten wurde erfolgreich exportiert in: " + file.getAbsolutePath());
      } catch (Exception e) {
        showAlert(Alert.AlertType.ERROR,"Fehler","Ein Fehler ist aufgetreten","Fehler beim Export: " + e.getMessage());
      }
    }
  }

  /** Aktualisiert die ListView mit den aktuellen Titeln aus dem Zettelkasten. */
  private void refreshZettelkastenView() {
    List<String> entries = new ArrayList<>();
    for (Medium m : zettelkasten) {
      entries.add(m.getTitel() + " [" + m.getClass().getSimpleName() + "]");
    }
    zettelkastenItems.setAll(entries);
  }

  /** Zeigt einen Dialog mit den detaillierten Informationen des markierten Zettelkasten-Eintrags an. */
  private void showSelectedMediumDetails() {
    int idx = lvTitles.getSelectionModel().getSelectedIndex();
    if (idx < 0 || idx >= zettelkastenItems.size()) return;

    Medium selected = null;
    int counter = 0;
    for (Medium m : zettelkasten) {
      if (counter == idx) {
        selected = m;
        break;
      }
      counter++;
    }

    if (selected != null) {
      showAlert(Alert.AlertType.INFORMATION,"Information",selected.getTitel(), selected.calculateRepresentation());
    }
  }

  /** Zeigt den About-Dialog mit Lizenz- und Nutzungshinweisen an. */
  @FXML
  private void handleAbout() {
    String text =
        """
                Alle redaktionellen Inhalte stammen von den Internetseiten der Projekte Wikibooks und Wortschatz.

                Die von Wikibooks bezogenen Inhalte unterliegen seit dem 22. Juni 2009 unter der Lizenz CC-BY-SA 3.0
                Unported zur Verfügung. Eine deutschsprachige Dokumentation für Weiternutzer findet man in den
                Nutzungsbedingungen der Wikimedia Foundation. Für alle Inhalte von Wikibooks galt bis zum 22. Juni
                2009 standardmäßig die GNU FDL (GNU Free Documentation License, engl. für GNU-Lizenz für freie
                Dokumentation). Der Text der GNU FDL ist unter
                http://de.wikipedia.org/wiki/Wikipedia:GNU_Free_Documentation_License verfügbar

                Die von Wortschatz[](http://wortschatz.uni-leipzig.de/) oder Wikipedia (www.wikipedia.de) bezogenen
                Inhalte sind urheberrechtlich geschützt. Sie werden hier für wissenschaftliche Zwecke eingesetzt und
                dürfen darüber hinaus in keiner Weise genutzt werden.

                Dieses Programm ist nur zur Nutzung durch den Programmierer selbst gedacht. Dieses Programm dient
                der Demonstration und dem Erlernen von Prinzipien der Programmierung mit Java. Eine Verwendung
                des Programms für andere Zwecke verletzt möglicherweise die Urheberrechte der Originalautoren der
                redaktionellen Inhalte und ist daher untersagt.
                """;
    showAlert(Alert.AlertType.INFORMATION,"Information","Über dieses Programm", text);
  }

  /** Definiert und aktiviert eine benutzerdefinierte Tabulator-Reihenfolge für bessere Tastaturbedienbarkeit. */
  private void setupTabOrder() {
    tabOrder =
        List.of(
            tfSearchTerm,
            btnSearch,
            btnShowLastChange,
            btnBack,
            cbHistory,
            btnForward,
            btnSynonymSearch,
            btnUseSynonym,
            lvSynonyms,
            lvTitles,
            btnHinzufuegen,
            btnSortieren,
            btnLoeschen,
            btnLaden,
            btnSpeichern,
            btnImport,
            btnExport,
            menuBar);

    rootPane.addEventFilter(
        KeyEvent.KEY_PRESSED,
        evt -> {
          // F1 überall → Info-Dialog
          if (evt.getCode() == KeyCode.F1) {
            handleAbout();
            evt.consume();
            return;
          }

          // Tab bzw. Shift+Tab
          if (evt.getCode() == KeyCode.TAB) {
            evt.consume();
            focusNext(!evt.isShiftDown());
            return;
          }

          // Enter auf ComboBox => Dropdown öffnen/schließen
          if (evt.getCode() == KeyCode.ENTER) {
            if (rootPane.getScene().getFocusOwner() == cbHistory) {
              if (!cbHistory.isShowing()) {
                cbHistory.show();
              } else {
                cbHistory.hide();
              }
              evt.consume();
            }
          }
        });

    // Enter in offener ComboBox-Liste
    cbHistory.setOnKeyPressed(
        evt -> {
          if (evt.getCode() == KeyCode.ENTER && cbHistory.isShowing()) {
            String selected = cbHistory.getSelectionModel().getSelectedItem();
            if (selected != null) {
              jumpToHistory(selected);
            }
            cbHistory.hide();
            Platform.runLater(() -> focusNext(true));
            evt.consume();
          }
        });
  }

  /**
   * Setzt den Fokus auf das nächste (bzw. vorherige) aktivierte UI-Element gemäß der definierten Tab-Reihenfolge.
   *
   * @param forward true für Vorwärts, false für Rückwärts
   */
  private void focusNext(boolean forward) {
    Node currentFocus = rootPane.getScene() != null ? rootPane.getScene().getFocusOwner() : null;

    int currentIndex = tabOrder.indexOf(currentFocus);
    if (currentIndex == -1) {
      currentIndex = forward ? -1 : tabOrder.size();
    }

    int nextIndex = currentIndex;
    int steps = 0;
    final int maxSteps = tabOrder.size();

    do {
      nextIndex =
          forward
              ? (nextIndex + 1) % tabOrder.size()
              : (nextIndex - 1 + tabOrder.size()) % tabOrder.size();

      steps++;
      if (steps > maxSteps) break;

    } while (tabOrder.get(nextIndex).isDisabled());

    Node target = tabOrder.get(nextIndex);
    if (target != null && !target.isDisabled()) {
      target.requestFocus();
    }
  }

  /**
   * Zeigt einen Alert-Dialog mit dem angegebenen Typ an.
   *
   * @param type    Der Typ des Alerts (ERROR, WARNING, INFORMATION)
   * @param title   Titel des Fensters
   * @param header  Header-Text (fett gedruckt, kann null sein)
   * @param message Der eigentliche Nachrichtentext
   */
  private void showAlert(Alert.AlertType type, String title, String header, String message) {
    Alert alert = new Alert(type);
    alert.setTitle(title);
    alert.setHeaderText(header);
    alert.setContentText(message);
    alert.showAndWait();
  }
}
