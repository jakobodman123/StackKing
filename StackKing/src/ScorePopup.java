import java.awt.*;

public class ScorePopup {
    private static final int POPUP_SPEED = 2;
    private static final int POPUP_DURATION = 60;

    private int x;
    private int y;
    private int score;
    private Boolean stackFailed;
    private int frameCount;
    private boolean active;

    public ScorePopup(int x, int y, int score, Boolean stackFailed) {
        this.x = x;
        this.y = y;
        this.score = score;
        this.stackFailed = stackFailed;
        this.frameCount = 0;
        this.active = true;
    }

    public void update() {
        if (frameCount < POPUP_DURATION) {
            y -= POPUP_SPEED;
            frameCount++;
        } else {
            active = false;
        }
    }

    public void draw(Graphics g) {
        Font font;
        String value = "+";
        if (active) {
            g.setColor(Color.GREEN);
            // Bigger Font for higher score
            if (score == 6) {
                font = new Font("Arial", Font.BOLD, 26);
            } else {
                font = new Font("Arial", Font.BOLD, 20);
            }
            if (stackFailed) {
                g.setColor(Color.RED);
                value = "-";
            }
            g.setFont(font);
            g.drawString(value + score, x, y);
        }
    }

    public boolean isActive() {
        return active;
    }
}