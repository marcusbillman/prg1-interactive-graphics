import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;

/**
 * This is a class
 * Created 2020-03-25
 *
 * @author Magnus Silverdal
 */
public class Graphics extends Canvas implements Runnable {
    private String title = "Graphics";
    private int width;
    private int height;

    private JFrame frame;
    private BufferedImage image;
    private int[] pixels;
    private int scale;

    private Thread thread;
    private boolean running = false;
    private int fps = 60;

    private Sprite square;

    private int wSquare = 64;
    private int hSquare = 64;
    private int xSquare = 0;
    private int ySquare = 0;
    private int vxSquare = 0;
    private int vySquare = 0;

    public Graphics(int w, int h, int scale) {
        this.width = w;
        this.height = h;
        this.scale = scale;
        image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        Dimension size = new Dimension(scale*width, scale*height);
        setPreferredSize(size);
        frame = new JFrame();
        frame.setTitle(title);
        frame.add(this);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        this.addKeyListener(new MyKeyListener());
        this.addMouseListener(new MyMouseListener());
        this.addMouseMotionListener(new MyMouseMotionListener());
        this.requestFocus();

        square = new Sprite(wSquare, hSquare, 0xFF0000);
        square.setSize(32);
    }

    private void draw() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }

        java.awt.Graphics g = bs.getDrawGraphics();
        g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        g.dispose();
        bs.show();
    }

    private void update() {
        if (xSquare + vxSquare < 0 || xSquare + vxSquare > width - square.getWidth())
            vxSquare = 0;
        if (ySquare + vySquare < 0 || ySquare + vySquare > height - square.getHeight())
            vySquare = 0;

        xSquare += vxSquare;
        ySquare += vySquare;

        for (int i = 0 ; i < square.getHeight() ; i++) {
            for (int j = 0 ; j < square.getWidth() ; j++) {
                pixels[(ySquare+i)*width + xSquare+j] = square.getPixels()[i*square.getWidth()+j];
            }
        }
    }

    public synchronized void start() {
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    public synchronized void stop() {
        running = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        double frameUpdateInterval = 1000000000.0 / fps;
        double deltaFrame = 0;
        long lastTime = System.nanoTime();

        while (running) {
            long now = System.nanoTime();
            deltaFrame += (now - lastTime) / frameUpdateInterval;
            lastTime = now;

            while (deltaFrame >= 1) {
                draw();
                deltaFrame--;
            }
        }
        stop();
    }

    private void clear() {
        Arrays.fill(pixels, 0);
    }

    private class MyKeyListener implements KeyListener {
        @Override
        public void keyTyped(KeyEvent keyEvent) {

        }

        @Override
        public void keyPressed(KeyEvent keyEvent) {
            if (keyEvent.getKeyChar()=='q') {
                square.setColor(0xFF0000);
            } else if (keyEvent.getKeyChar()=='w') {
                square.setColor(0x00FF00);
            } else if (keyEvent.getKeyChar()=='e') {
                square.setColor(0x0000FF);
            } else if (keyEvent.getKeyChar()=='r') {
                square.setColor(0xFFFF00);
            } else if (keyEvent.getKeyChar()=='t') {
                square.setColor(0xFFFFFF);
            } else if (keyEvent.getKeyChar()=='y') {
                square.setColor(0x000000);
            } else if (keyEvent.getKeyChar()=='1') {
                square.setSize(8);
            } else if (keyEvent.getKeyChar()=='2') {
                square.setSize(32);
            } else if (keyEvent.getKeyChar()=='3') {
                square.setSize(64);
            } else if (keyEvent.getKeyChar()==' ') {
                clear();
            }
        }

        @Override
        public void keyReleased(KeyEvent keyEvent) {
            if (keyEvent.getKeyChar()=='a' || keyEvent.getKeyChar()=='d') {
                vxSquare = 0;
            } else if (keyEvent.getKeyChar()=='w' || keyEvent.getKeyChar()=='s') {
                vySquare = 0;
            }
        }
    }

    private class MyMouseListener implements MouseListener {
        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
        }

        @Override
        public void mousePressed(MouseEvent mouseEvent) {
            moveSquare(mouseEvent);
            update();
        }

        @Override
        public void mouseReleased(MouseEvent mouseEvent) {

        }

        @Override
        public void mouseEntered(MouseEvent mouseEvent) {

        }

        @Override
        public void mouseExited(MouseEvent mouseEvent) {

        }
    }

    private class MyMouseMotionListener implements MouseMotionListener {
        @Override
        public void mouseDragged(MouseEvent mouseEvent) {
            moveSquare(mouseEvent);
            update();
        }

        @Override
        public void mouseMoved(MouseEvent mouseEvent) {

        }
    }

    private void moveSquare(MouseEvent mouseEvent) {
        int x = mouseEvent.getX() - square.getWidth() / 2;
        int y = mouseEvent.getY() - square.getHeight() / 2;

        if (x < width / 2) {
            xSquare = Math.max(0, x / scale);
        } else {
            xSquare = Math.min(width - square.getWidth(), x / scale);
        }

        if (y < height / 2) {
            ySquare = Math.max(0, y / scale);
        } else {
            ySquare = Math.min(height - square.getHeight(), y / scale);
        }
    }

}
