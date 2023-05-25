import java.awt.Graphics;
import java.awt.Image;
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

    public Block(int x, int y, int size, Type type, boolean isLethal) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.type = type;
        this.isLethal = isLethal;
        this.isDestroyed = false;
        this.blinkCount = 0;
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

    public void move(double speed) {
        x -= speed;
    }

    public void draw(Graphics g) {
        int adjustedY = y - 20;
        if (type == Type.YELLOW) {
            g.drawImage(image, x, adjustedY, SIZE * 3, SIZE * 3, null);
        } else if (type == Type.BLUE) {
            g.drawImage(image, x, y - 40, SIZE * 3, SIZE * 3, null);
        } else {
            g.drawImage(image, x, y, SIZE * 2, SIZE * 2, null);
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