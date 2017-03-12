package snapshot;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Lelental on 12.03.2017.
 */
public class VideoHandler {

    private VideoCapture videoCapture = new VideoCapture();
    private ScheduledExecutorService timer;
    private Mat mat = new Mat();

    protected static Image toFxImage(Mat mat) {
        return SwingFXUtils.toFXImage(matToBufferedImage(mat), null);
    }


    protected static <T> void onFXThread(final ObjectProperty<T> property, final T value) {
        Platform.runLater(() -> property.set(value));
    }

    protected static BufferedImage matToBufferedImage(Mat original) {
        BufferedImage image;
        int width = original.width(), height = original.height(), channels = original.channels();
        byte[] sourcePixels = new byte[width * height * channels];
        original.get(0, 0, sourcePixels);

        if (original.channels() > 1) {
            image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        } else {
            image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        }
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);

        return image;
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
