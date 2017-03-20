package snapshot;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import org.opencv.core.*;

import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;


import java.text.SimpleDateFormat;
import java.util.Date;
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
            resetCamera();
            Date date = new Date();
            String formattedDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(date);
            setRunning(true);
            bootCamera(this);
            Imgcodecs.imwrite("snapshot-" + formattedDate + ".png", getMat());
        } else {
            System.err.print("Camera is Off\n");
        }

    }

    @FXML
    protected void turnNormalFilter() {
        resetCamera();
        setColorId(Imgproc.COLOR_RGBA2RGB);
        setRunning(true);
        bootCamera(this);
    }

    @FXML
    protected void turnBlackWhiteFilter() {
        resetCamera();
        setColorId(Imgproc.COLOR_BGR2GRAY);
        setRunning(true);
        bootCamera(this);

    }

    @FXML
    protected void turnSuperFilter() {
        resetCamera();
        setRunning(true);
        setColorId(Imgproc.COLOR_RGB2HSV);
        bootCamera(this);
    }


    void bootCamera(Controller controller) {
        videoCapture.open(0);
        if (isRunning) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Mat image = getMat();
                    faceDetection();
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
        if (this.mat.channels() > 1) {
            Imgproc.cvtColor(mat, mat2, getColorId());
            return mat2;
        } else {

            Imgproc.cvtColor(mat, mat, getColorId());

        }
        return mat;
    }

    private void resetCamera() {
        setRunning(false);
        if (videoCapture.isOpened())
            bootCamera(this);
    }

    private void faceDetection() {
        MatOfRect faces = new MatOfRect();
        Mat mat3 = new Mat();
        Imgproc.cvtColor(getMat(), mat3, Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(mat3, mat3);
        CascadeClassifier cascadeClassifier = new CascadeClassifier();
        cascadeClassifier.load("haarcascade_frontalface_alt.xml");
        cascadeClassifier.detectMultiScale(mat3, faces, 1.1, 2, Objdetect.CASCADE_SCALE_IMAGE, new Size(30, 30), new Size());
        Rect[] facesArray = faces.toArray();
        for (int i = 0; i < facesArray.length; i++)
            Imgproc.rectangle(getMat(), facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0, 255), 3);

    }

    public ImageView getFrame() {
        return frame;
    }

    public int getColorId() {
        return colorId;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public void setColorId(int colorId) {
        this.colorId = colorId;
    }
}
