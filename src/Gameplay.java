import java.util.*;
import java.awt.event.*;

import javax.swing.*;

import java.awt.*;

import javax.swing.*;
import javax.swing.Timer;

public class Gameplay extends JPanel implements KeyListener, ActionListener
{
    private boolean play = false;

    private int brokenBricks = 0;
    private int totalBricks = 21;

    private Timer timer;
    private int delay=8;

    private int playerX = 310;

    private int ballposX = 120;
    private int ballposY = 350;
    private int ballXdir = -1;
    private int ballYdir = -2;

    private MapGenerator map;

    private int level = 1;

    public Gameplay()
    {
        map = new MapGenerator(3, 7);
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        timer=new Timer(delay,this);
        timer.start();
    }

    public void paint(Graphics g)
    {
        // background
        g.setColor(Color.black);
        g.fillRect(1, 1, 692, 592);

        // map
        map.draw((Graphics2D) g);

        // kenarlar
        g.setColor(Color.yellow);
        g.fillRect(0, 0, 3, 592);
        g.fillRect(0, 0, 692, 3);
        g.fillRect(691, 0, 3, 592);

        // skor
        g.setColor(Color.white);
        g.setFont(new Font("serif",Font.BOLD, 25));
        g.drawString("Total Bricks: "+totalBricks, 20,30);
        g.drawString("Level "+level, 280,30);
        g.drawString("Broken Bricks: "+brokenBricks, 480,30);

        // pedal
        g.setColor(Color.green);
        g.fillRect(playerX, 550, 100, 8);

        // top
        g.setColor(Color.yellow);
        g.fillOval(ballposX, ballposY, 20, 20);

        if(totalBricks <= 0) //Oyun kazanılınca
        {
            play = false;
            ballXdir = 0;
            ballYdir = 0;

            if(level == 3)
            {
                level = 1;
                g.setColor(Color.RED);
                g.setFont(new Font("serif",Font.BOLD, 30));
                g.drawString("You Won The Game!", 260,300);
                g.drawString("Press (Enter) to Restart..", 230,350);
            }
            else
            {
                level++;
                g.setColor(Color.RED);
                g.setFont(new Font("serif",Font.BOLD, 20));
                g.drawString("You Passed Level " + (level-1), 260,300);
                g.drawString("Press (Enter) to Level " + level, 230,350);
            }
        }

        if(ballposY > 570) //Oyun kaybedilince
        {
            play = false;
            ballXdir = 0;
            ballYdir = 0;
            level = 1;
            g.setColor(Color.RED);
            g.setFont(new Font("serif",Font.BOLD, 30));
            g.drawString("Game Over, Scores: "+brokenBricks, 190,300);

            g.setColor(Color.RED);
            g.setFont(new Font("serif",Font.BOLD, 20));
            g.drawString("Press (Enter) to Restart", 230,350);
        }

        g.dispose();
    }

    public void keyPressed(KeyEvent e)
    {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) //Sağ yön tuşuna basılınca
        {
            if(playerX >= 600)
            {
                playerX = 600;
            }
            else
            {
                moveRight();
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_LEFT) //Sol yön tuşuna basılınca
        {
            if(playerX < 10)
            {
                playerX = 10;
            }
            else
            {
                moveLeft();
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_ENTER) //Enter'a basılınca
        {
            if(!play)
            {
                play = true;
                ballposX = 120;
                ballposY = 350;
                ballXdir = -1;
                ballYdir = -2;
                playerX = 310;
                brokenBricks = 0;

                if(level == 1) {
                    totalBricks = 21;
                    map = new MapGenerator(3, 7);
                }
                else if(level == 2)
                {
                    totalBricks = 30;
                    map = new MapGenerator(3, 10);
                }
                else if(level == 3)
                {
                    totalBricks = 48;
                    map = new MapGenerator(4, 12);
                }

                repaint();
            }
        }
    }

    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}

    public void moveRight()
    {
        play = true;
        playerX+=20;
    }

    public void moveLeft()
    {
        play = true;
        playerX-=20;
    }

    public void actionPerformed(ActionEvent e)
    {
        timer.start();
        if(play)
        {
            if(new Rectangle(ballposX, ballposY, 20, 20).intersects(new Rectangle(playerX, 550, 30, 8)))
            {
                ballYdir = -ballYdir;
                ballXdir = -2;
            }
            else if(new Rectangle(ballposX, ballposY, 20, 20).intersects(new Rectangle(playerX + 70, 550, 30, 8)))
            {
                ballYdir = -ballYdir;
                ballXdir = ballXdir + 1;
            }
            else if(new Rectangle(ballposX, ballposY, 20, 20).intersects(new Rectangle(playerX + 30, 550, 40, 8)))
            {
                ballYdir = -ballYdir;
            }

            // map ve topun çalışması
            A: for(int i = 0; i<map.map.length; i++)
            {
                for(int j =0; j<map.map[0].length; j++)
                {
                    if(map.map[i][j] > 0)
                    {
                        int brickX = j * map.brickWidth + 80;
                        int brickY = i * map.brickHeight + 50;
                        int brickWidth = map.brickWidth;
                        int brickHeight = map.brickHeight;

                        Rectangle rect = new Rectangle(brickX, brickY, brickWidth, brickHeight);
                        Rectangle ballRect = new Rectangle(ballposX, ballposY, 20, 20);
                        Rectangle brickRect = rect;

                        if(ballRect.intersects(brickRect)) //Tuğlanın kırılması
                        {
                            map.setBrickValue(0, i, j);
                            brokenBricks++;
                            totalBricks--;

                            // top,  tuğlanın sağ veya soluna çarptığında
                            if(ballposX + 19 <= brickRect.x || ballposX + 1 >= brickRect.x + brickRect.width)
                            {
                                ballXdir = -ballXdir;
                            }
                            // top, tuğlanın üst veya altına çarptığında
                            else
                            {
                                ballYdir = -ballYdir;
                            }

                            break A;
                        }
                    }
                }
            }

            ballposX += ballXdir;
            ballposY += ballYdir;

            if(ballposX < 0)
            {
                ballXdir = -ballXdir;
            }
            if(ballposY < 0)
            {
                ballYdir = -ballYdir;
            }
            if(ballposX > 670)
            {
                ballXdir = -ballXdir;
            }

            repaint();
        }
    }
}
