
//https://www.freepik.com/free-vector/cartoon-computer-games-night-forest-landscape-plant-green-natural-environment-wood-grass_10600821.htm#query=2d%20forest%20background&position=7&from_view=keyword&track=ais">Image by macrovector on Freepik
import javax.swing.*;
import javax.swing.event.MouseInputAdapter;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class ScrollerGame extends JPanel {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 400;
    private static final int CHARACTER_SIZE = 50;
    private static final double OBJECT_SPEED = 3;
    private static final String GAME_OVER_TEXT = "Game Over! Press Enter to restart.";

    // Game variables
    private boolean blockDestroyedByUser = false;
    private boolean inAnimation = false;
    private int score;
    private int cannonRequirement;
    private int characterX;
    private int characterY;
    private List<Block> blocks;
    private Block blinkingObject;
    private List<ScorePopup> scorePopups;
    private List<ScorePopup> popupsToRemove;
    private boolean gameOver;
    private int destroyCollisionBoxX;
    private int blockCount;
    private int largerBlockCount;
    private Image character;
    private JLabel gameOverLabel;

    // Sounds
    private GameSound gameSound = new GameSound();
    private GameSound bonk = new GameSound();
    private GameSound woosh = new GameSound();
    private GameSound gameOverSound = new GameSound();
    

    public ScrollerGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        initializeGame();
        setupListeners();
        startTimer();
        setFocusable(true);
        gameSound.playSound("assets/gamesound.wav", 0.05f, true);
    }

    private void initializeGame() {
        characterX = 10;
        characterY = HEIGHT - CHARACTER_SIZE - 45;
        blocks = new ArrayList<>();
        blinkingObject = null;
        scorePopups = new ArrayList<>();
        popupsToRemove = new ArrayList<>();
        gameOver = false;
        destroyCollisionBoxX = 100 + Block.SIZE;
        score = 0;
        cannonRequirement = 9 * largerBlockCount;
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

        MouseInputAdapter touchAdapter = new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // if not in animation and game is not over
                if (!inAnimation && !gameOver) {
                    animateCharacter();
                    for (Block block : blocks) {
                        if (block.x <= destroyCollisionBoxX && block.x >= characterX + CHARACTER_SIZE) {
                            blockDestroyedByUser = true;
                            bonk.playSound("assets/bonk.wav", 0.6f, false);
                            woosh.playSound("assets/woosh.wav", 0.3f, false);
                            break;
                        }else {
                        woosh.playSound("assets/woosh.wav", 0.3f, false);
                        }
                    }
                }

            }
        };

        addMouseListener(touchAdapter);
        addMouseMotionListener(touchAdapter);
        addMouseWheelListener(touchAdapter);
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

    private void blockSpeeds() {
        for (Block block : blocks) {
            // speed of the blocks
            if (block.type == Block.Type.RED) {
                block.move(OBJECT_SPEED + (0.005 * score));
            }
            if (block.type == Block.Type.YELLOW) {
                block.move((OBJECT_SPEED * 0.8) + (0.005 * score));
            }

            if (block.type == Block.Type.BLUE && block.getX() <= 400) {
                block.move((OBJECT_SPEED * 1.2) + (0.005 * score));
                block.animateCharacter();
            } else if (block.type == Block.Type.BLUE && block.getX() > 400) {
                block.move((OBJECT_SPEED * 0.7) + (0.005 * score));
                block.animateCharacter();
            }
        }
    }

    private void spawnBlocks() {
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
    }

    private void moveBlocks() {
        blockSpeeds();
        spawnBlocks();
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
        inAnimation = true;
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
                    inAnimation = false;
                }
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
                        gameOverSound.playSound("assets/gameover.wav", 0.05f, false);
                        setGameOver(gameOver);
                        break;
                    }
                }
                if (block.x <= destroyCollisionBoxX && block.x >= characterX + CHARACTER_SIZE) {
                    if (block.getType() == Block.Type.YELLOW && score < cannonRequirement) {
                        break;
                    }
                    if (block.getType() == Block.Type.YELLOW && blockDestroyedByUser) {
                        blinkingObject = new Block(block.getX(), block.getY(), Block.SIZE, block.getType(), false);
                        blinkingObject.startBlinking();
                        blocks.remove(block);
                        blockDestroyedByUser = false;
                        score += 6;
                        scorePopups.add(new ScorePopup(block.getX(), block.getY(), 6, false));
                        break;
                    }
                    if (blockDestroyedByUser) {
                        blinkingObject = new Block(block.getX(), block.getY(), Block.SIZE, block.getType(), false);
                        blinkingObject.startBlinking();
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

    private void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
        if (gameOver) {
            gameSound.stopSound();
        } else {
            gameSound.playSound("assets/gamesound.wav", 0.05f, true);
        }
    }

    private void restartGame() {
        characterX = 10;
        gameOver = false;
        blinkingObject = null;
        blocks.clear();
        score = 0;
        blockCount = 0;
        largerBlockCount = 0;
        gameOverLabel.setVisible(false);
        gameSound.playSound("assets/gamesound.wav", 0.05f, true);
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

        // Mark Collisionbox
        Image collisionImage = new ImageIcon(getClass().getResource("assets/bone_pile.png")).getImage();
        int lineY = characterY + CHARACTER_SIZE;
        g.drawImage(collisionImage, destroyCollisionBoxX - 33, lineY - 37, 50, 50, this);


        // Draw the Moving Characters
        if (!gameOver) {
            for (Block block : blocks) {
                block.draw(g);
            }
        } else {
            gameOverLabel.setVisible(true);
        }

        // draw dead enemy
        if (blinkingObject != null) {
            blinkingObject.draw(g);
        }

        // Score Feedback
        for (ScorePopup popup : scorePopups) {
            popup.draw(g);
        }

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
