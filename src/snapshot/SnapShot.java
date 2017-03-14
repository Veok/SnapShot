package snapshot;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.opencv.core.Core;

public class SnapShot extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainStage.fxml"));
        primaryStage.setTitle("SnapShot");
        primaryStage.setScene(new Scene(fxmlLoader.load(), 620, 455));
        primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Controller controller = fxmlLoader.getController();
                if (controller.isRunning()) {
                    controller.setRunning(false);
                    controller.bootCamera(controller);
                }
                System.exit(0);
            }
        });

    }


    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        launch(args);
    }
}
