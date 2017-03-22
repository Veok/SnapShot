package snapshot;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.List;

/**
 * @author Lelental on 12.03.2017.
 */
public class VideoHandler {


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

    protected static void imageOverImageBGRA(Mat sourceMat, Mat destinationMat, MatOfPoint2f destination){

        MatOfPoint2f source = new MatOfPoint2f();
        Mat mat = new Mat();
        mat.checkVector(4);
        Mat cpyImg = new Mat(destinationMat.rows(),destinationMat.cols(),destinationMat.type());
        Mat negImg = new Mat(destinationMat.rows(),destinationMat.cols(),destinationMat.type());
        Mat blank = new Mat(sourceMat.rows(), sourceMat.cols(), sourceMat.type());
        Mat warpMatrix= Imgproc.getPerspectiveTransform(sourceMat,destinationMat);
        Core.split(sourceMat, (List<Mat>) mat);
        Imgproc.warpPerspective(sourceMat,negImg,warpMatrix,new Size(negImg.cols(),negImg.rows()));
        Imgproc.warpPerspective(blank,cpyImg,warpMatrix,new Size(cpyImg.cols(),cpyImg.rows()));

    }


}
