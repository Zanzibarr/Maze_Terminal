module it.rickezanzi.terminal_maze {
    requires javafx.controls;
    requires javafx.fxml;


    opens it.rickezanzi.terminal_maze to javafx.fxml;
    exports it.rickezanzi.terminal_maze;
}