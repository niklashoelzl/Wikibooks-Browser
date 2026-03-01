package application.wikibooks_browser;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BibTexPersistencyTest {

  @Test
  void testParseBibTex_buch() {
    String input = "@book{author = {Max Mustermann}, title = {Java Grundlagen}, " +
        "publisher = {Springer}, year = {2020}, isbn = {9783765727818}, " +
        "auflage = {1}, seitenanzahl = {300}}";
    Medium m = BibTexPersistency.parseBibTex(input);
    assertInstanceOf(Buch.class, m);
    assertEquals("Java Grundlagen", m.getTitel());
  }

  @Test
  void testParseBibTex_cd() {
    String input = "@cd{artist = {Beatles}, title = {Abbey Road}, " +
        "label = {Apple}, dauer = {47}, altersfreigabe = {0}}";
    Medium m = BibTexPersistency.parseBibTex(input);
    assertInstanceOf(CD.class, m);
    assertEquals("Abbey Road", m.getTitel());
  }

  @Test
  void testParseBibTex_zeitschrift() {
    String input = "@journal{title = {Nature}, issn = {0028-0836}, " +
        "volume = {1}, number = {2}, auflage = {1}, seitenanzahl = {100}}";
    Medium m = BibTexPersistency.parseBibTex(input);
    assertInstanceOf(Zeitschrift.class, m);
    assertEquals("Nature", m.getTitel());
  }

  @Test
  void testParseBibTex_elektronischesMedium() {
    String input = "@elmed{title = {Java Docs}, url = {https://docs.oracle.com}, " +
        "dateiformat = {html}, groesse = {0}}";
    Medium m = BibTexPersistency.parseBibTex(input);
    assertInstanceOf(ElektronischesMedium.class, m);
    assertEquals("Java Docs", m.getTitel());
  }

  @Test
  void testParseBibTex_unbekannterTyp() {
    String input = "@unknown{title = {Test}}";
    assertThrows(IllegalArgumentException.class,
        () -> BibTexPersistency.parseBibTex(input));
  }

  @Test
  void testParseBibTex_leerString() {
    assertThrows(IllegalArgumentException.class,
        () -> BibTexPersistency.parseBibTex(""));
  }

  @Test
  void testParseBibTex_null() {
    assertThrows(IllegalArgumentException.class,
        () -> BibTexPersistency.parseBibTex(null));
  }

  @Test
  void testParseBibTex_ungueltigeKlammerung() {
    assertThrows(IllegalArgumentException.class,
        () -> BibTexPersistency.parseBibTex("@book{title = {Test}"));
  }
}
