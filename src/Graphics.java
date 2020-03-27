import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

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
    private int ups = 60;

    private Sprite square;
    private double t;

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

        square = new Sprite(32,32, 0x00FF00);
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
        /*
        for (int i = 0 ; i < pixels.length ; i++) {
            pixels[i] = 0;
        }
         */

        int x = (int)(width/2+(width/2- square.getWidth())*Math.sin(t));
        int y = (int)(height/2+(height/2- square.getHeight())*Math.cos(t));

        t += Math.PI/180;

        for (int i = 0; i < square.getHeight() ; i++) {
            for (int j = 0; j < square.getWidth() ; j++) {
                pixels[(y+i)*width + x+j] = square.getPixels()[i* square.getWidth()+j];
            }
        }

        // The moving magenta square
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

        // The mouse-controlled square
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
        double frameUpdateinteval = 1000000000.0 / fps;
        double stateUpdateinteval = 1000000000.0 / ups;
        double deltaFrame = 0;
        double deltaUpdate = 0;
        long lastTime = System.nanoTime();

        while (running) {
            long now = System.nanoTime();
            deltaFrame += (now - lastTime) / frameUpdateinteval;
            deltaUpdate += (now - lastTime) / stateUpdateinteval;
            lastTime = now;

            while (deltaUpdate >= 1) {
                update();
                deltaUpdate--;
            }

            while (deltaFrame >= 1) {
                draw();
                deltaFrame--;
            }
        }
        stop();
    }

    private class MyKeyListener implements KeyListener {
        @Override
        public void keyTyped(KeyEvent keyEvent) {

        }

        @Override
        public void keyPressed(KeyEvent keyEvent) {
            if (keyEvent.getKeyChar()=='a') {
                vxSquare = -5;
            } else if (keyEvent.getKeyChar()=='d') {
                vxSquare = 5;
            } else if (keyEvent.getKeyChar()=='w') {
                vySquare = -5;
            } else if (keyEvent.getKeyChar()=='s') {
                vySquare = 5;
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
            xSquare = mouseEvent.getX()/scale;
            ySquare = mouseEvent.getY()/scale;
        }

        @Override
        public void mouseMoved(MouseEvent mouseEvent) {

        }
    }

}
