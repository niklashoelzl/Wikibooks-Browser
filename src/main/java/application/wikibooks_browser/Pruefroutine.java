package application.wikibooks_browser;

import java.net.URL;

/**
 * Dienstklasse {@code Prüfroutine} mit statischen Methoden zur Validierung
 * von ISBN-Nummern (ISBN-10 und ISBN-13) und URLs.
 * <p>
 * Zusätzlich enthält die Klasse eine Methode zum Parsen von ISBNs aus Strings
 * in ein int-Array, um die Prüfroutinen zu unterstützen.
 * </p>
 *
 * @author Niklas Hölzl
 */
public class Pruefroutine {

  /**
   * Prüft eine ISBN-10 nach dem Modulo-11-Verfahren.
   *
   * @param isbn ISBN als int-Array (10 Ziffern)
   * @return {@code true}, wenn die ISBN-10 gültig ist, sonst {@code false}
   */
  public static boolean checkISBN10(int[] isbn) {
    int sum = 0;
    for (int i = 1; i <= isbn.length; i++) {
      sum += i * isbn[i - 1];
    }
    return sum % 11 == 0;
  }

  /**
   * Prüft eine ISBN-13 nach dem Modulo-10-Verfahren.
   *
   * @param isbn ISBN als int-Array (13 Ziffern)
   * @return {@code true}, wenn die ISBN-13 gültig ist, sonst {@code false}
   */
  public static boolean checkISBN13(int[] isbn) {
    int sum = 0;
    for (int i = 1; i < isbn.length; i++) {
      if (i % 2 == 0) {
        sum += isbn[i - 1] * 3;
      } else {
        sum += isbn[i - 1];
      }
    }
    int lastDigit = sum % 10;
    int check = (10 - lastDigit) % 10;
    return isbn[isbn.length - 1] == check;
  }

  /**
   * Prüft, ob eine URL formal gültig ist.
   *
   * @param urlString die URL als String
   * @return {@code true}, wenn die URL gültig ist, sonst {@code false}
   */
  public static boolean checkURL(String urlString) {
    try {
      URL url = new URL(urlString);
      url.toURI();
      return true;
    } catch (Exception exception) {
      return false;
    }
  }
  /**
   * Entfernt Bindestriche aus einer ISBN und konvertiert die Ziffern
   * in ein int-Array zur weiteren Verarbeitung.
   *
   * @param isbn ISBN als String
   * @return Array mit den Ziffern der ISBN
   */
  public static int[] parseISBN(String isbn) {
    isbn = isbn.replaceAll("-", "").trim();
    int[] digits = new int[isbn.length()];
    for (int i = 0; i < isbn.length(); i++) {
      digits[i] = Character.getNumericValue(isbn.charAt(i));
    }
    return digits;
  }
}
