package snapshot;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static snapshot.VideoHandler.onFXThread;
import static snapshot.VideoHandler.toFxImage;

public class Controller {


    @FXML
    private ImageView frame;

    private VideoCapture videoCapture = new VideoCapture();
    private ScheduledExecutorService timer;
    private Mat mat = new Mat();
    private Mat mat2 = new Mat();
    private volatile boolean isRunning;
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
        setColorId(Imgproc.COLOR_BGR2GRAY);
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
        videoCapture.read(mat);
        if(this.mat.channels() > 1){
        Imgproc.cvtColor(mat, mat2, getColorId());
        return mat2; }
        else{
            Imgproc.cvtColor(mat, mat,getColorId());
        }
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
