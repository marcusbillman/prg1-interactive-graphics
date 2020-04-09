import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * This is a class
 * Created 2020-03-26
 *
 * @author Magnus Silverdal
 */
public class Sprite {
    private int width;
    private int height;
    private int[] pixels;

    public Sprite(int w, int h, int col) {
        this.width = w;
        this.height = h;
        pixels = new int[w*h];
        Arrays.fill(pixels, col);
    }

    public Sprite(String path) {
        BufferedImage image = null;
        try {
            BufferedImage rawImage = ImageIO.read(new File(path));
            // Since the type of image is unknown it must be copied into an INT_RGB
            image = new BufferedImage(rawImage.getWidth(), rawImage.getHeight(),
                    BufferedImage.TYPE_INT_RGB);
            image.getGraphics().drawImage(rawImage, 0, 0, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.width = image.getWidth();
        this.height = image.getHeight();
        pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
    }

    public void setColor(int col) {
        Arrays.fill(pixels, col);
    }

    public void setSize(int size) {
        this.width = size;
        this.height = size;
    }

    public int[] getPixels() {
        return pixels;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
