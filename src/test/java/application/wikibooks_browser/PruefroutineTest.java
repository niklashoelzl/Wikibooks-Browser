package application.wikibooks_browser;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PruefroutineTest {

  // ISBN-10 Tests
  @Test
  void testCheckISBN10_gueltig() {
    int[] isbn = Pruefroutine.parseISBN("3598215088");
    assertTrue(Pruefroutine.checkISBN10(isbn));
  }

  @Test
  void testCheckISBN10_ungueltig() {
    int[] isbn = Pruefroutine.parseISBN("1234567890");
    assertFalse(Pruefroutine.checkISBN10(isbn));
  }

  @Test
  void testCheckISBN10_mitBindestrichen() {
    int[] isbn = Pruefroutine.parseISBN("3-598-21508-8");
    assertTrue(Pruefroutine.checkISBN10(isbn));
  }

  // ISBN-13 Tests
  @Test
  void testCheckISBN13_gueltig() {
    int[] isbn = Pruefroutine.parseISBN("9783765727818");
    assertTrue(Pruefroutine.checkISBN13(isbn));
  }

  @Test
  void testCheckISBN13_ungueltig() {
    int[] isbn = Pruefroutine.parseISBN("9783765727819");
    assertFalse(Pruefroutine.checkISBN13(isbn));
  }

  @Test
  void testCheckISBN13_mitBindestrichen() {
    int[] isbn = Pruefroutine.parseISBN("978-3-76572-781-8");
    assertTrue(Pruefroutine.checkISBN13(isbn));
  }

  // URL Tests
  @Test
  void testCheckURL_gueltig() {
    assertTrue(Pruefroutine.checkURL("https://de.wikibooks.org"));
  }

  @Test
  void testCheckURL_ungueltig() {
    assertFalse(Pruefroutine.checkURL("kein-url"));
  }

  @Test
  void testCheckURL_null() {
    assertFalse(Pruefroutine.checkURL(null));
  }

  @Test
  void testCheckURL_leer() {
    assertFalse(Pruefroutine.checkURL(""));
  }
}