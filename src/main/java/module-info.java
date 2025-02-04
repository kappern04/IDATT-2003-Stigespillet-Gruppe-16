module org.example.stigespillet {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.stigespillet to javafx.fxml;
    exports org.example.stigespillet;
}