package org;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class MinCraft extends JPanel implements Runnable, KeyListener, MouseListener 
{

    static final int WORLD_W = 40;
    static final int WORLD_H = 28;
    //static final int WINDOW_W = WORLD_W * TILE_SIZE;
    //static final int WINDOW_H = WORLD_H * TILE_SIZE;
    //int changeX;
    //int changeY;

    int[][] world = new int[WORLD_W][WORLD_H];

    int px = WORLD_W / 2;
    int py = 5;

    boolean left, right, up, down;

    Thread thread;
    boolean running = true;

    public static void main(String[] args) 
    {
        SwingUtilities.invokeLater(() -> 
        {
            JFrame frame = new JFrame("MinCraft");
            MinCraft game = new MinCraft();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setContentPane(game);
            frame.setVisible(true);
            game.requestFocusInWindow();
        });
    }

    public MinCraft() 
    {
        setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);
        // setPreferredSize(new Dimension(WINDOW_W, WINDOW_H));
        generateWorld();

        thread = new Thread(this);
        thread.start();
    }

    //world
    private void generateWorld() 
    {
        Random rand = new Random();

        for (int x = 0; x < WORLD_W; x++) 
        {
            int ground = WORLD_H / 2 + rand.nextInt(3) - 1;

            for (int y = 0; y < WORLD_H; y++) {
                if (y < ground - 3) world[x][y] = 0; 
                else if (y < ground) world[x][y] = 1; 
                else world[x][y] = 2; 
            }

            for (int y = 0; y < WORLD_H; y++) {
                if (world[x][y] == 1) {
                    world[x][y] = 3; 
                    break;
                }
            }
        }

        // enemies
        for (int i = 0; i < 12; i++) {
            int ex = rand.nextInt(WORLD_W);
            int ey = rand.nextInt(WORLD_H);
            if (world[ex][ey] == 0) {
                world[ex][ey] = 4;
            }
        }
    }

    private boolean inBounds(int x, int y) {
        return x >= 0 && x < WORLD_W && y >= 0 && y < WORLD_H;
    }

   

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int tsX = getWidth() / WORLD_W;
        int tsY = getHeight() / WORLD_H;

        for (int x = 0; x < WORLD_W; x++) {
            for (int y = 0; y < WORLD_H; y++) {
                switch (world[x][y]) {
                    case 0 -> g.setColor(new Color(135, 206, 235)); // sky
                    case 1 -> g.setColor(new Color(155, 118, 83));  // dirt
                    case 2 -> g.setColor(new Color(120, 120, 120)); // stone
                    case 3 -> g.setColor(new Color(103, 146, 63));  // grass
                    case 4 -> g.setColor(Color.BLACK);              // enemy
                }

                g.fillRect(x * tsX, y * tsY, tsX, tsY);
                g.setColor(new Color(0, 0, 0, 40));
                g.drawRect(x * tsX, y * tsY, tsX, tsY);
            }
        }

        // PLAYER
        g.setColor(Color.RED);
        g.fillRect(
                px * tsX + tsX / 6,
                py * tsY + tsY / 6,
                tsX * 2 / 3,
                tsY * 2 / 3
        );
    }


    private void update() 
    {
        int nx = px + (right ? 1 : 0) - (left ? 1 : 0);
        int ny = py + (down ? 1 : 0) - (up ? 1 : 0);

        if (!inBounds(nx, ny)) return;

        if (world[nx][ny] == 0) 
        {
            px = nx;
            py = ny;
        }

        // enemy collision
        if (world[nx][ny] == 4) 
        {
            world[nx][ny] = 0;
            px = nx;
            py = ny;
        }
    }

    @Override
    public void run() 
    {
        while (running) 
        {
            update();
            repaint();
            try {
                Thread.sleep(33);
                } 
            catch (InterruptedException ignored) {}
        }
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        switch (e.getKeyCode()) 
        {
            case KeyEvent.VK_A -> left = true;
            case KeyEvent.VK_D -> right = true;
            case KeyEvent.VK_W -> up = true;
            case KeyEvent.VK_S -> down = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) 
    {
        switch (e.getKeyCode()) 
        {
            case KeyEvent.VK_A -> left = false;
            case KeyEvent.VK_D -> right = false;
            case KeyEvent.VK_W -> up = false;
            case KeyEvent.VK_S -> down = false;
        }
    }

    @Override public void keyTyped(KeyEvent e) {}

    // CLICK TO KILL ENEMIES
    @Override
    public void mousePressed(MouseEvent e) 
    {
        int tsX = getWidth() / WORLD_W;
        int tsY = getHeight() / WORLD_H;

        int gx = e.getX() / tsX;
        int gy = e.getY() / tsY;

        if (inBounds(gx, gy) && world[gx][gy] == 4) 
        {
            world[gx][gy] = 0;
        }
    }

    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}