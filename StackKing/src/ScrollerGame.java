
//https://www.freepik.com/free-vector/cartoon-computer-games-night-forest-landscape-plant-green-natural-environment-wood-grass_10600821.htm#query=2d%20forest%20background&position=7&from_view=keyword&track=ais">Image by macrovector on Freepik
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class ScrollerGame extends JPanel {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 400;
    private static final int CHARACTER_SIZE = 50;
    private static final int OBJECT_SIZE = 30;
    private static final double OBJECT_SPEED = 3.5;
    private static final String GAME_OVER_TEXT = "Game Over! Press Enter to restart.";

    // Game variables
    private boolean blockDestroyedByUser = false;
    private int score;
    private int cannonRequirement;
    private int characterX;
    private int characterY;
    private List<Block> blocks;
    private List<ScorePopup> scorePopups;
    private List<ScorePopup> popupsToRemove;
    private boolean gameOver;
    private int destroyCollisionBoxX;
    private int blockCount;
    private int largerBlockCount;
    private Image character;
    private JLabel gameOverLabel;

    public ScrollerGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        initializeGame();
        setupListeners();
        startTimer();
        setFocusable(true);
    }

    private void initializeGame() {
        characterX = 10;
        characterY = HEIGHT - CHARACTER_SIZE - 45;
        blocks = new ArrayList<>();
        scorePopups = new ArrayList<>();
        popupsToRemove = new ArrayList<>();
        gameOver = false;
        destroyCollisionBoxX = 100 + Block.SIZE;
        score = 0;
        cannonRequirement = 9;
        blockCount = 0;
        largerBlockCount = 0;
        character = new ImageIcon(getClass().getResource("assets/not_nasus.png")).getImage();
    }

    private void setupListeners() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (gameOver && e.getKeyCode() == KeyEvent.VK_ENTER) {
                    restartGame();
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                animateCharacter();
                if (!gameOver) {
                    for (Block block : blocks) {
                        if (block.x <= destroyCollisionBoxX && block.x >= characterX + CHARACTER_SIZE) {
                            blockDestroyedByUser = true;
                            break;
                        }
                    }
                }
            }
        });
    }

    private void startTimer() {
        Timer timer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gameOver) {
                    moveBlocks();
                    checkCollision();
                    repaint();
                }
            }
        });
        timer.start();
    }

    private void moveBlocks() {
        for (Block block : blocks) {
            block.move(OBJECT_SPEED);
        }

        if (blocks.isEmpty() || blocks.get(blocks.size() - 1).x <= WIDTH - (WIDTH / 3)) {
            blockCount++;
            if (blockCount % 11 == 0) {
                blocks.add(new Block(WIDTH, characterY, Block.SIZE * 3, Block.Type.BLUE, true));
            } else if (blockCount % 5 == 0) {
                if (largerBlockCount < blockCount / 5) {
                    blocks.add(new Block(WIDTH, characterY, Block.SIZE * 2, Block.Type.YELLOW, true));
                    largerBlockCount++;
                    cannonRequirement = 9 * largerBlockCount;
                    System.out.println(cannonRequirement);
                } else {
                    blocks.add(new Block(WIDTH, characterY, Block.SIZE, Block.Type.RED, false));
                }
            } else {
                blocks.add(new Block(WIDTH, characterY, Block.SIZE, Block.Type.RED, false));
            }
        }
        for (ScorePopup popup : scorePopups) {
            popup.update();
            if (!popup.isActive()) {
                popupsToRemove.add(popup);
            }
        }

        scorePopups.removeAll(popupsToRemove);
        popupsToRemove.clear();
    }

    private void animateCharacter() {
        Timer timer = new Timer(15, new ActionListener() {
            int frameCount = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                frameCount++;
                if (frameCount <= 5) {
                    character = new ImageIcon(getClass().getResource("assets/cane_up.png")).getImage();
                } else if (frameCount <= 10) {
                    character = new ImageIcon(getClass().getResource("assets/cane_middle.png")).getImage();
                } else if (frameCount <= 15) {
                    character = new ImageIcon(getClass().getResource("assets/cane_down.png")).getImage();
                } else {
                    character = new ImageIcon(getClass().getResource("assets/not_nasus.png")).getImage();
                    ((Timer) e.getSource()).stop();
                }
                repaint();
            }
        });
        timer.start();
    }

    private void checkCollision() {
        if (!gameOver) {
            for (Block block : blocks) {
                if (block.x <= characterX + CHARACTER_SIZE && block.y == characterY) {
                    if (block.isLethal) {
                        gameOver = true;
                        break;
                    }
                }
                if (block.x <= destroyCollisionBoxX && block.x >= characterX + CHARACTER_SIZE) {
                    if (block.getType() == Block.Type.YELLOW && score < cannonRequirement) {
                        break;
                    }
                    if (block.getType() == Block.Type.YELLOW && blockDestroyedByUser) {
                        blocks.remove(block);
                        blockDestroyedByUser = false;
                        score += 6;
                        scorePopups.add(new ScorePopup(block.getX(), block.getY(), 6, false));
                        break;
                    }
                    if (blockDestroyedByUser) {
                        blocks.remove(block);
                        blockDestroyedByUser = false;
                        score += 3;
                        scorePopups.add(new ScorePopup(block.getX(), block.getY(), 3, false));
                        break;
                    }
                }
            }
        }
    }

    private void restartGame() {
        characterX = 10;
        gameOver = false;
        blocks.clear();
        score = 0;
        blockCount = 0;
        largerBlockCount = 0;
        gameOverLabel.setVisible(false);
        requestFocus();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the background image
        Image backgroundImage = new ImageIcon(getClass().getResource("assets/background.jpg")).getImage();
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

        // Draw The character
        g.drawImage(character, characterX - 35, characterY - 90, 140, 140, this);

        // Draw the Moving Characters
        if (!gameOver) {
            for (Block block : blocks) {
                block.draw(g);
            }
        } else {
            gameOverLabel.setVisible(true);
        }

        // Score Feedback
        for (ScorePopup popup : scorePopups) {
            popup.draw(g);
        }

        // Draw a red line to mark destroyCollisionBoxX
        g.setColor(Color.RED);
        int destroyCollisionBoxX = characterX + CHARACTER_SIZE + OBJECT_SIZE;
        int lineY = characterY + CHARACTER_SIZE;
        g.drawLine(destroyCollisionBoxX, lineY, destroyCollisionBoxX + 2 * OBJECT_SIZE, lineY);

        // Draw the score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        String scoreText = "Score: " + score;
        int scoreX = WIDTH - g.getFontMetrics().stringWidth(scoreText) - 10;
        int scoreY = 20;
        g.drawString(scoreText, scoreX, scoreY);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("2D Scroller Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        ScrollerGame game = new ScrollerGame();
        frame.getContentPane().add(game);

        game.gameOverLabel = new JLabel(GAME_OVER_TEXT);
        game.gameOverLabel.setForeground(Color.WHITE);
        game.gameOverLabel.setFont(new Font("Arial", Font.BOLD, 20));
        game.gameOverLabel.setHorizontalAlignment(SwingConstants.CENTER);
        game.gameOverLabel.setBounds(0, HEIGHT / 2, WIDTH, 50);
        game.gameOverLabel.setVisible(false);
        game.add(game.gameOverLabel);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        game.requestFocus();
    }
}
