package edu.ntnu.iir.bidata.laddergame.view.menu;

import edu.ntnu.iir.bidata.laddergame.controller.menu.MainMenuController;
import edu.ntnu.iir.bidata.laddergame.file.SaveFileTracker;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class LoadGameDialog {
    private final MainMenuController controller;
    private final Stage primaryStage;

    public LoadGameDialog(MainMenuController controller, Stage primaryStage) {
        this.controller = controller;
        this.primaryStage = primaryStage;
    }

    public void show() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Saved Mission");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );

        String savesDirectory = System.getProperty("user.home") + File.separator + "cosmicladder" + File.separator + "saves";
        File savesDir = new File(savesDirectory);

        if (savesDir.exists() && savesDir.isDirectory()) {
            fileChooser.setInitialDirectory(savesDir);

            if (SaveFileTracker.getInstance().wasLoadedFromSave()) {
                String currentFileName = SaveFileTracker.getInstance().getCurrentSaveFileName();
                if (currentFileName != null) {
                    File initialFile = new File(savesDir, currentFileName);
                    if (initialFile.exists()) {
                        fileChooser.setInitialFileName(currentFileName);
                    }
                }
            }
        }

        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile != null) {
            controller.loadGame(selectedFile);
        }
    }
}