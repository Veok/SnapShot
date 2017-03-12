package snapshot;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;

public class Controller {

    @FXML
    private ImageView frame;
    @FXML
    private Button snapShot;
    @FXML
    private MenuItem normalFilter;
    @FXML
    private MenuItem blackWhiteFilter;
    @FXML
    private MenuItem superFilter;


    public ImageView getFrame() {
        return frame;
    }

    @FXML
    protected void takeSnapShot(){}
    @FXML
    protected void turnNormalFilter(){}
    @FXML
    protected void turnBlackWhiteFilter(){}
    @FXML
    protected void turnSuperFilter(){}


}
