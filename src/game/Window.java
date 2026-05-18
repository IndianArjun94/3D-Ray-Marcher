package game;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Dimension2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import static game.Shader.rayCounter;

public class Window {
    public JFrame frame;
    public final int WIDTH;
    public final int HEIGHT;
    public final int FOV = 60;

    public InnerGameRenderer innerGameRenderer;

    public Window(Dimension2D dimension2D) {
        WIDTH = (int) dimension2D.getWidth();
        HEIGHT = (int) dimension2D.getHeight();
        initJFrame();
        frame.setName("Grant Johnsrud");
    }

    private void initJFrame() {
        frame = new JFrame();
        frame.setSize(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public void initRenderSystem() {
        this.innerGameRenderer = new InnerGameRenderer(WIDTH, HEIGHT);
        frame.add(innerGameRenderer);
        frame.pack();
        innerGameRenderer.startRenderThread();
    }

    public class InnerGameRenderer extends Canvas implements Runnable {
        private final int WIDTH;
        private final int HEIGHT;

        public BufferStrategy bufferStrategy;
        public BufferedImage image;

        public int[] pixels;

        public Thread renderThread;
        public boolean running = false;

        public int targetFPS = 120;

        double counter = 0;

        public InnerGameRenderer(int width, int height) {
            WIDTH = width;
            HEIGHT = height;

            this.setPreferredSize(new Dimension(width, height));

//            Creates the off-screen pixels
            image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
            pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        }

        public synchronized int[] getPixel(int x, int y) {
            int pixelValue = pixels[(WIDTH*y)+x];
            int r = (pixelValue >> 16) & 0xFF;
            int g = (pixelValue >> 8) & 0xFF;
            int b = pixelValue & 0xFF;

            return new int[]{r,g,b};
        }

        public synchronized void setPixel(int x, int y, Vec3 rgb) {
            pixels[(WIDTH*y)+x] = ((int)rgb.x << 16) | ((int)rgb.y << 8) | (int) rgb.z;
        }

        public synchronized void setPixel(int x, int y, int pixel) {
            pixels[(WIDTH*y)+x] = pixel;
        }

        public synchronized void render() {
            Graphics graphics = bufferStrategy.getDrawGraphics();

            graphics.setColor(Color.BLACK);
            graphics.fillRect(0, 0, WIDTH, HEIGHT);
            graphics.drawImage(image, 0, 0, getWidth(), getHeight(), null);
            graphics.dispose();

            bufferStrategy.show();

//            reset
        }

        private synchronized void startRenderThread() {
            if (running) return;
            running = true;
            renderThread = new Thread(this, "renderGame");
            renderThread.setPriority(1);
            renderThread.start();
        }

        private synchronized void stopRenderThread() {
            if (!running) return;
            running = false;
            try {
                renderThread.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        @Override
        public void run() {
            createBufferStrategy(2);
            bufferStrategy = getBufferStrategy();

            long frameStartTime;
            long frameEndTime;

            while (running) {
                frameStartTime = System.currentTimeMillis();

                render();

                frameEndTime = System.currentTimeMillis();
                if ((1000/targetFPS)-(frameEndTime-frameStartTime) > 0) {
                    try {
                        Thread.sleep((1000/targetFPS) - (frameEndTime-frameStartTime));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

}
