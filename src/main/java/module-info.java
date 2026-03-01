module application.wikibooks_browser {
  requires javafx.controls;
  requires javafx.fxml;
  requires javafx.web;
  requires java.net.http;
  requires com.google.gson;
  requires java.logging;

  opens application.wikibooks_browser to javafx.fxml;
  exports application.wikibooks_browser;
}

