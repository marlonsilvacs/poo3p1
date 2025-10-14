package org.suaempresa.mainApp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.suaempresa.controller.MainController;
import org.suaempresa.service.ProdutoService;

import java.io.IOException;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        ProdutoService produtoService = new ProdutoService();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("src/main/java/org/suaempresa/view"));
        Scene scene = new Scene(loader.load(), 800, 600);
        MainController controller = loader.getController();
        controller.setProdutoService(produtoService);

        primaryStage.setTitle("Gerenciador de Produtos");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}