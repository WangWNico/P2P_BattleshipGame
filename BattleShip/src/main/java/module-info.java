module edu.rpi.cs.csci4963.u24.rcsid.hw02.gol_gui.battleship {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires java.desktop;
    requires org.jetbrains.annotations;


    opens edu.rpi.cs.csci4963.u24.wangn4.hw03.battleship to javafx.fxml;
    exports edu.rpi.cs.csci4963.u24.wangn4.hw03.battleship;
}