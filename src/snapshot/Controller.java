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
    private volatile boolean isRunning;
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
            isRunning = false;
            bootCamera(this);
            Date date = new Date();
            String formattedDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(date);
            isRunning = true;
            bootCamera(this);
            Imgcodecs.imwrite("snapshot-" + formattedDate + ".png", getMat());
        } else {
            System.err.print("Camera is Off\n");
        }

    }

    @FXML
    protected void turnNormalFilter() {
        isRunning = false;
        if (videoCapture.isOpened()) {
            bootCamera(this);
        }
        setColorId(Imgproc.COLOR_RGBA2RGB);
        isRunning = true;
        bootCamera(this);


    }

    @FXML
    protected void turnBlackWhiteFilter() {
        isRunning = false;
        if (videoCapture.isOpened()) {
            bootCamera(this);
        }
        setColorId(Imgproc.COLOR_RGBA2GRAY);
        isRunning = true;
        bootCamera(this);


    }

    @FXML
    protected void turnSuperFilter() {
        isRunning = false;
        if (videoCapture.isOpened()) {
            bootCamera(this);
        }
        isRunning = true;
        setColorId(Imgproc.COLOR_RGB2HSV);
        bootCamera(this);


    }

    public ImageView getFrame() {
        return frame;
    }

    protected void bootCamera(Controller controller) {
        videoCapture.open(0);
        runTask(controller);

    }


    private void runTask(Controller controller) {
        if (isRunning) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Mat image = getMat();
                    toFxImage(image);
                    onFXThread(controller.getFrame().imageProperty(), toFxImage(image));
                }
            };
            timer = Executors.newSingleThreadScheduledExecutor();
            timer.scheduleAtFixedRate(runnable, 0, 33, TimeUnit.MILLISECONDS);
        } else {
            timer.shutdown();
            videoCapture.release();
        }
    }


    private Mat getMat() {
        videoCapture.read(this.mat);
        Imgproc.cvtColor(this.mat, this.mat, getColorId());
        return mat;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public int getColorId() {
        return colorId;
    }

    public void setColorId(int colorId) {
        this.colorId = colorId;
    }
}
