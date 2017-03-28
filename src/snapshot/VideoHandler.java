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
import java.util.*;

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

    public static void drawImageOverSecondImage(Mat srcMat, Mat dstMat, MatOfPoint2f dst) {

        MatOfPoint2f src = new MatOfPoint2f();
        ArrayList<Mat> rgbaChannels = new ArrayList<>(4);
        Mat cpyImg = new Mat(dstMat.rows(), dstMat.cols(), dstMat.type());
        Mat negImg = new Mat(dstMat.rows(), dstMat.cols(), dstMat.type());
        Mat blank = new Mat(srcMat.rows(), srcMat.cols(), srcMat.type());

        MatOfPoint2f helpVariable = new MatOfPoint2f();
        helpVariable.get(0, 0);
        src.push_back(helpVariable);
        helpVariable.get(srcMat.cols(), 0);
        src.push_back(helpVariable);
        helpVariable.get(srcMat.cols(), srcMat.rows());
        src.push_back(helpVariable);
        helpVariable.get(0, srcMat.rows());
        src.push_back(helpVariable);

        Mat warpMatrix = new Mat();
        warpMatrix = Imgproc.getPerspectiveTransform(src, dst);
        Core.split(srcMat, rgbaChannels);

        rgbaChannels.set(0, rgbaChannels.get(3));
        rgbaChannels.set(1, rgbaChannels.get(3));
        rgbaChannels.set(2, rgbaChannels.get(3));
        rgbaChannels.remove(rgbaChannels.size() - 1);
        Core.merge(rgbaChannels, blank);

        Imgproc.warpPerspective(srcMat, negImg, warpMatrix, new Size(negImg.cols(), negImg.rows()));
        Imgproc.warpPerspective(blank, cpyImg, warpMatrix, new Size(cpyImg.cols(), negImg.rows()));
        dstMat.setTo(cpyImg);
        rgbaChannels = new ArrayList<>(3);
        Core.merge(rgbaChannels, negImg);
        negImg.mul(cpyImg);
        dstMat.setTo(negImg);
    }
}
