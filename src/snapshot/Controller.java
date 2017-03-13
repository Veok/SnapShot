package snapshot;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfInt;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static snapshot.VideoHandler.matToBufferedImage;
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

    @FXML
    protected void takeSnapShot() {
        Date date = new Date();
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(date);
        videoCapture.open(0);
        Imgcodecs.imwrite("snapshot-" + formattedDate + ".png", getMat());

    }

    @FXML
    protected void turnNormalFilter() {
    }

    @FXML
    protected void turnBlackWhiteFilter() {
    }

    @FXML
    protected void turnSuperFilter() {
    }

    public ImageView getFrame() {
        return frame;
    }

    protected void bootCamera(Controller controller) {

        videoCapture.open(0);

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

    }

    private Mat getMat() {
        videoCapture.read(this.mat);
        return mat;
    }


    protected void stopCamera() throws InterruptedException {
        this.timer.shutdown();
        this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
        this.videoCapture.release();

    }

}
