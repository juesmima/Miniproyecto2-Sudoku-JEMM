module com.sudoku {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.sudoku to javafx.fxml;
    exports com.sudoku;


    opens com.sudoku.model to javafx.fxml;
    exports com.sudoku.model;


    opens com.sudoku.controller to javafx.fxml;
    exports com.sudoku.controller;
}