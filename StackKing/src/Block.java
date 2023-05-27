import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

import javax.swing.ImageIcon;

class Block {
    public static final int SIZE = 30;

    public enum Type {
        RED, YELLOW, BLUE
    }

    public int x;
    public int y;
    public int size;
    public Type type;
    public boolean isLethal;
    private Image image;
    public boolean isDestroyed;
    public int blinkCount;
    private boolean visible;
    private int blinkInterval;

    public Block(int x, int y, int size, Type type, boolean isLethal) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.type = type;
        this.isLethal = isLethal;
        this.isDestroyed = false;
        this.blinkCount = 0;
        this.blinkInterval = 100;
        this.visible = true;
        loadImage();
        
    }

    private void loadImage() {
        String imagePath = "";

        switch (type) {
            case RED:
                imagePath = "assets/mascot.png";
                break;
            case YELLOW:
                imagePath = "assets/cannon_minion.png";
                break;
            case BLUE:
                imagePath = "assets/not_garen.png";
                break;
        }

        ImageIcon icon = new ImageIcon(getClass().getResource(imagePath));
        image = icon.getImage();
    }

    public void startBlinking() {
        Timer blinkTimer = new Timer(blinkInterval, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                visible = !visible;
                blinkCount++;
                //needs to be uneven for it to remain invis until next block arrives
                if (blinkCount >= 5) {
                    ((Timer) e.getSource()).stop();
                }
            }
        });
        blinkTimer.start();
    }

    public void move(double speed) {
        x -= speed;
    }

    public void animateCharacter() {
        Timer timer = new Timer(15, new ActionListener() {
            int frameCount = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                frameCount++;
                if (frameCount <= 5) {
                    image = new ImageIcon(getClass().getResource("assets/not_garen1.png")).getImage();
                } else if (frameCount <= 10) {
                    image = new ImageIcon(getClass().getResource("assets/not_garen2.png")).getImage();
             
                } else {
                    image = new ImageIcon(getClass().getResource("assets/not_garen.png")).getImage();
                    ((Timer) e.getSource()).stop();
                    
                }
                //repaint();
            }
        });
        timer.start();

    }

    public void draw(Graphics g) {
        if (visible) {
            //padding in y direction to lift from ground
            int adjustedY = y - 20;
            if (type == Type.YELLOW) {
                g.drawImage(image, x, adjustedY, SIZE * 3, SIZE * 3, null);
            } else if (type == Type.BLUE) {
                g.drawImage(image, x, y - 40, SIZE * 3, SIZE * 3, null);
            } else {
                g.drawImage(image, x, y, SIZE * 2, SIZE * 2, null);
            }
        }
    }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Type getType() {
        return type;
    }

    public void setDestroyed(boolean destroyed) {
        this.isDestroyed = destroyed;
    }

    public void setBlinkCount(int blinkCount) {
        this.blinkCount = blinkCount;
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }

    public int getBlinkCount() {
        return blinkCount;
    }
}