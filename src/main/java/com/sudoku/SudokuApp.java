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

            // 2. CREAR LA ESCENA CON EL CONTENIDO DEL FXML
            Scene scene = new Scene(root);

            // 3. CARGAR EL ARCHIVO CSS Y APLICARLO A LA ESCENA
            // IMPORTANTE: Cambia "mi-estilo.css" por el nombre real de tu archivo CSS si es diferente
            URL cssLocation = getClass().getResource("/com/sudoku/mi-estilo.css");

            if (cssLocation != null) {
                scene.getStylesheets().add(cssLocation.toExternalForm());
            } else {
                System.out.println("⚠️ Advertencia: No se encontró el archivo CSS. La app cargará sin estilos personalizados.");
            }

            // 4. MOSTRAR LA VENTANA
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