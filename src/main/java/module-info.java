module org.suaempresa.gerenciadorprodutos {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;

    opens org.suaempresa.mainApp to javafx.fxml;
    opens org.suaempresa.controller to javafx.fxml;

    exports org.suaempresa.mainApp;
    exports org.suaempresa.controller;
    exports org.suaempresa.model;
    exports org.suaempresa.service;
}