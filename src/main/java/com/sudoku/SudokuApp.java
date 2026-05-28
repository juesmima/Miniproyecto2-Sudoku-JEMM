package com.sudoku;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class SudokuApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {

            URL fxmlLocation = getClass().getResource("/com/sudoku/SudokuView.fxml");

            if (fxmlLocation == null) {
                throw new IOException("No se encontró el archivo FXML. Verifica que 'mi-pantalla.fxml' esté en src/main/resources/com/sudoku/");
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();


            Scene scene = new Scene(root);



            URL cssLocation = getClass().getResource("/com/sudoku/sudoku.css");

            if (cssLocation != null) {
                scene.getStylesheets().add(cssLocation.toExternalForm());
                System.out.println("✅ ¡Estilos CSS cargados correctamente!");
            } else {
                System.out.println("⚠️ Advertencia: No se encontró 'mi-estilo.css'. La app cargará con el diseño base.");
            }

            primaryStage.setTitle("Sudoku");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            System.err.println("❌ Error al inicializar la interfaz de usuario:");
            System.err.println(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ Ocurrió un error inesperado:");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}