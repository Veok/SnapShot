package snapshot;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import org.opencv.core.Mat;

import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static snapshot.VideoHandler.onFXThread;
import static snapshot.VideoHandler.toFxImage;

public class Controller {

    private VideoCapture videoCapture = new VideoCapture();
    private ScheduledExecutorService timer;
    private Mat mat = new Mat();
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

    private int colorId;

    @FXML
    protected void takeSnapShot() {
        if (videoCapture.isOpened()) {
            Date date = new Date();
            String formattedDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(date);
            videoCapture.open(0);
            Imgcodecs.imwrite("snapshot-" + formattedDate + ".png", getMat());
        } else {

        }

    }

    @FXML
    protected void turnNormalFilter() throws InterruptedException {
        if (videoCapture.isOpened()) {
            videoCapture.release();
        }
        setColorId(Imgproc.COLOR_RGBA2RGB);
        bootCamera(this);


    }

    @FXML
    protected void turnBlackWhiteFilter() throws InterruptedException {
        if (videoCapture.isOpened()) {
            videoCapture.release();
        }
        setColorId(Imgproc.COLOR_RGBA2GRAY);
        bootCamera(this);


    }

    @FXML
    protected void turnSuperFilter() throws InterruptedException {


        if (videoCapture.isOpened()) {
            videoCapture.release();
        }
        setColorId(Imgproc.COLOR_RGB2HSV);
        bootCamera(this);


    }

    public ImageView getFrame() {
        return frame;
    }

    protected void bootCamera(Controller controller) throws InterruptedException {
        videoCapture.open(0);
        setTimer(runTask(controller));

    }


    private Runnable runTask(Controller controller) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Mat image = getMat();
                toFxImage(image);
                onFXThread(controller.getFrame().imageProperty(), toFxImage(image));
            }
        };
        return runnable;
    }

    private void setTimer(Runnable runnable) {
        timer = Executors.newSingleThreadScheduledExecutor();
        timer.scheduleAtFixedRate(runnable, 0, 33, TimeUnit.MILLISECONDS);
    }


    private Mat getMat() {
        videoCapture.read(this.mat);
        Imgproc.cvtColor(this.mat, this.mat, getColorId());
        return mat;
    }


    public int getColorId() {
        return colorId;
    }

    public void setColorId(int colorId) {
        this.colorId = colorId;
    }
}
