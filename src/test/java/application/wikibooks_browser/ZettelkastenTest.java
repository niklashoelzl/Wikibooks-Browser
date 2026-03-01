package application.wikibooks_browser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ZettelkastenTest {

  private Zettelkasten zk;
  private Buch buch1;
  private Buch buch2;

  @BeforeEach
  void setUp() {
    zk = new Zettelkasten();
    buch1 = new Buch("Java Grundlagen", 2020, "Springer",
        "9783765727818", "Max Mustermann", 1, 300);
    buch2 = new Buch("Design Patterns", 2019, "Addison-Wesley",
        "3598215088", "Gang of Four", 1, 395);
    new CD("Abbey Road", "Apple", "Beatles", 47, 0);
  }

  // addMedium Tests
  @Test
  void testAddMedium_gueltig() {
    zk.addMedium(buch1);
    List<Medium> result = zk.findMedium("Java Grundlagen", true);
    assertEquals(1, result.size());
  }

  @Test
  void testAddMedium_ungueltigeIsbn() {
    Buch ungueltig = new Buch("Test", 2020, "Verlag", "1234567891", "Autor", 1, 100);
    zk.addMedium(ungueltig);
    List<Medium> result = zk.findMedium("Test", true);
    assertTrue(result.isEmpty());
  }

  // findMedium Tests
  @Test
  void testFindMedium_gefunden() {
    zk.addMedium(buch1);
    List<Medium> result = zk.findMedium("Java Grundlagen", true);
    assertEquals(1, result.size());
    assertEquals("Java Grundlagen", result.getFirst().getTitel());
  }

  @Test
  void testFindMedium_nichtGefunden() {
    List<Medium> result = zk.findMedium("Nicht vorhanden", true);
    assertTrue(result.isEmpty());
  }

  @Test
  void testFindMedium_caseInsensitive() {
    zk.addMedium(buch1);
    List<Medium> result = zk.findMedium("java grundlagen", true);
    assertEquals(1, result.size());
  }

  // sort Tests
  @Test
  void testSort_aufsteigend() {
    zk.addMedium(buch2);
    zk.addMedium(buch1);
    zk.sort(true);

    List<Medium> alle = zk.findMedium("Design Patterns", true);
    assertFalse(alle.isEmpty());
  }

  // removeAllByTitle Tests
  @Test
  void testRemoveAllByTitle() {
    zk.addMedium(buch1);
    zk.addMedium(buch1);
    int removed = zk.removeAllByTitle("Java Grundlagen");
    assertEquals(2, removed);
    assertTrue(zk.findMedium("Java Grundlagen", true).isEmpty());
  }

  @Test
  void testRemoveAllByTitle_nichtVorhanden() {
    int removed = zk.removeAllByTitle("Nicht vorhanden");
    assertEquals(0, removed);
  }

  @Test
  void testRemoveAllByTitle_null() {
    int removed = zk.removeAllByTitle(null);
    assertEquals(0, removed);
  }

  // dropMedium Tests
  @Test
  void testDropMedium_erfolgreich() throws DuplicateEntryException {
    zk.addMedium(buch1);
    zk.dropMedium("Java Grundlagen");
    assertTrue(zk.findMedium("Java Grundlagen", true).isEmpty());
  }

  @Test
  void testDropMedium_duplikat() {
    zk.addMedium(buch1);
    zk.addMedium(buch1);
    assertThrows(DuplicateEntryException.class,
        () -> zk.dropMedium("Java Grundlagen"));
  }
}
