import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) {
        JFrame mainMenuFrame = new JFrame("DaShooter Java Edition v0.1.1 Alpha");
        mainMenuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainMenuFrame.setSize(1060, 610);
        mainMenuFrame.setVisible(true);
        mainMenuFrame.setBackground(Color.GRAY);

        JButton playButton = new JButton("Play");
        playButton.setBounds(530, 155, 400, 50);
        playButton.setBackground(Color.LIGHT_GRAY);
        playButton.setFont(new Font("Arial", Font.BOLD, 30));
        mainMenuFrame.add(playButton);

        JButton quitButton = new JButton("Quit");
        quitButton.setBounds(530, 255, 400, 50);
        quitButton.setBackground(Color.LIGHT_GRAY);
        quitButton.setFont(new Font("Arial", Font.BOLD, 30));
        mainMenuFrame.add(quitButton);

        JLabel title = new JLabel("DaShooter", SwingConstants.CENTER);
        title.setBounds(350, 50, 800, 50);
        title.setForeground(Color.GRAY);
        title.setFont(new Font("Arial", Font.BOLD, 50));
        mainMenuFrame.add(title);

        JLabel subtitle = new JLabel("Java Edition", SwingConstants.CENTER);
        subtitle.setBounds(350, 100, 800, 50);
        subtitle.setForeground(Color.GRAY);
        subtitle.setFont(new Font("Arial", Font.BOLD, 20));
        mainMenuFrame.add(subtitle);

        JLabel versionLabel = new JLabel("Alpha 0.1.1");
        versionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        versionLabel.setForeground(Color.BLACK);
        versionLabel.setBounds(480, 180, 100, 20);
        mainMenuFrame.add(versionLabel);

        playButton.addActionListener(e -> {
            mainMenuFrame.dispose();
            new GameWindow(1060, 610).startGame();
        });

        quitButton.addActionListener(e -> System.exit(0));

        System.out.println("Main menu loaded");
    }
}

class GameWindow extends JFrame {
    private JPanel player;
    private List<Enemy> enemies;
    private List<Bullet> bullets;
    private List<Bullet> enemyBullets;
    private int score;
    private int health;
    private int bulletCount;
    private int time;
    private JLabel scoreLabel;
    private JLabel healthLabel;
    private JLabel bulletLabel;
    private JLabel timeLabel;
    private JPanel healthBar;

    public GameWindow(int frameX, int frameY) {
        super("DaShooter: Java Edition");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(frameX, frameY);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(Color.GRAY);

        player = new JPanel();
        player.setBackground(Color.BLUE);
        player.setBounds(getWidth() / 2 - 25, getHeight() - 100, 50, 50);
        add(player);

        enemies = new ArrayList<>();
        bullets = new ArrayList<>();
        enemyBullets = new ArrayList<>();
        score = 0;
        health = 100;
        bulletCount = 30; // Start with 30 bullets
        time = 0; // Time in some unit

        scoreLabel = new JLabel("Score: " + score);
        healthLabel = new JLabel("Health: " + health);
        bulletLabel = new JLabel("Bullets: " + bulletCount);
        timeLabel = new JLabel("Time: " + time);

        scoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
        healthLabel.setFont(new Font("Arial", Font.BOLD, 16));
        bulletLabel.setFont(new Font("Arial", Font.BOLD, 16));
        timeLabel.setFont(new Font("Arial", Font.BOLD, 16));

        scoreLabel.setForeground(Color.WHITE);
        healthLabel.setForeground(Color.GREEN);
        bulletLabel.setForeground(Color.BLUE);
        timeLabel.setForeground(Color.ORANGE);

        scoreLabel.setBounds(10, 10, 200, 30);
        healthLabel.setBounds(10, 40, 200, 30);
        bulletLabel.setBounds(10, 70, 200, 30);
        timeLabel.setBounds(10, 100, 200, 30);

        add(scoreLabel);
        add(healthLabel);
        add(bulletLabel);
        add(timeLabel);

        // Create the health bar
        healthBar = new JPanel();
        healthBar.setBackground(Color.GREEN);
        healthBar.setBounds(10, getHeight() - 50, 200, 20);
        add(healthBar);
    }

    public void startGame() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this::spawnEnemies, 0, 2, TimeUnit.SECONDS);
        executor.scheduleAtFixedRate(this::moveEnemies, 0, 1, TimeUnit.SECONDS);

        Timer timer = new Timer(30, e -> {
            time += 1;
            timeLabel.setText("Time: " + time);
            moveBullets();
            moveEnemyBullets();
            checkCollisions();
            printDebugInfo(); // Print debugging information
            repaint();
            updateHealthBar();
        });
        timer.start();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (bulletCount > 0) {
                    enemies.stream()
                        .filter(enemy -> new Rectangle(enemy.x, enemy.y, enemy.size, enemy.size).contains(e.getPoint()))
                        .findFirst()
                        .ifPresent(enemy -> {
                            bullets.add(new Bullet(player.getX() + 25, player.getY(), enemy.x + enemy.size / 2, enemy.y + enemy.size / 2, 30));
                            bulletCount--;
                            bulletLabel.setText("Bullets: " + bulletCount);
                            System.out.println("Bullet fired at enemy at (" + enemy.x + ", " + enemy.y + ")");
                        });
                }
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                movePlayer(e.getKeyCode());
            }
        });
        setFocusable(true);
        setVisible(true);
    }

    private void movePlayer(int keyCode) {
        int dx = 0, dy = 0;
        switch (keyCode) {
            case KeyEvent.VK_A: dx = -10; break;
            case KeyEvent.VK_D: dx = 10; break;
            case KeyEvent.VK_W: dy = -10; break;
            case KeyEvent.VK_S: dy = 10; break;
        }
        Rectangle bounds = player.getBounds();
        bounds.translate(dx, dy);

        // Restrict player movement within the screen bounds
        if (bounds.x < 0) bounds.x = 0;
        if (bounds.y < 0) bounds.y = 0;
        if (bounds.x + bounds.width > getWidth()) bounds.x = getWidth() - bounds.width;
        if (bounds.y + bounds.height > getHeight()) bounds.y = getHeight() - bounds.height;

        player.setBounds(bounds);
        System.out.println("Player moved to (" + bounds.x + ", " + bounds.y + ")");
    }

    private void moveBullets() {
        bullets.forEach(Bullet::move);
        bullets.removeIf(bullet -> !bullet.isOnScreen(getWidth(), getHeight()));
    }

    private void moveEnemyBullets() {
        enemyBullets.forEach(Bullet::move);
        enemyBullets.removeIf(bullet -> !bullet.isOnScreen(getWidth(), getHeight()));
    }

    private void checkCollisions() {
        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            if (new Rectangle(player.getX(), player.getY(), 50, 50).intersects(enemy.getBounds())) {
                System.out.println("Collision detected: Player and enemy at (" + enemy.x + ", " + enemy.y + ")");
                JOptionPane.showMessageDialog(this, "Game Over", "You collided with an enemy.", JOptionPane.OK_OPTION);
                System.exit(0);
            }

            Iterator<Bullet> bulletIterator = bullets.iterator();
            while (bulletIterator.hasNext()) {
                Bullet bullet = bulletIterator.next();
                if (bullet.hit(enemy)) {
                    score += 10;
                    bulletCount += 5; // Gain 5 bullets for hitting an enemy
                    health = Math.min(100, health + 10); // Regain health for killing an enemy, capped at 100
                    scoreLabel.setText("Score: " + score);
                    healthLabel.setText("Health: " + health);
                    bulletLabel.setText("Bullets: " + bulletCount);
                    System.out.println("Enemy hit at (" + enemy.x + ", " + enemy.y + "), Health left: " + (enemy.health - 5));
                    if (enemy.takeDamage(5)) { // Reduce enemy health by 5 per hit
                        enemyIterator.remove();
                        System.out.println("Enemy destroyed at (" + enemy.x + ", " + enemy.y + ")");
                    }
                    bulletIterator.remove();
                }
            }

            if (System.currentTimeMillis() % 500 < 30) { // Fire approximately once per half second
                enemy.fireAtPlayer(player.getX() + 25, player.getY() + 25, enemyBullets);
                System.out.println("Enemy fired at player from (" + enemy.x + ", " + enemy.y + ")");
            }
        }

        Iterator<Bullet> enemyBulletIterator = enemyBullets.iterator();
        while (enemyBulletIterator.hasNext()) {
            Bullet bullet = enemyBulletIterator.next();
            if (new Rectangle(player.getX(), player.getY(), 50, 50).intersects(bullet.getBounds())) {
                health -= 5; // Decrease health by 5 for each hit
                healthLabel.setText("Health: " + health);
                System.out.println("Player hit by enemy bullet. Health now: " + health);
                enemyBulletIterator.remove();
                if (health <= 0) {
                    JOptionPane.showMessageDialog(this, "Game Over", "Health reached 0.", JOptionPane.OK_OPTION);
                    System.exit(0);
                }
            }else if(time == 0){
                System.out.println("Time used up");
                JOptionPane.showMessageDialog(this, "Game Over", "You have used up all your time!", JOptionPane.OK_OPTION);
                System.exit(0);
            }
        }
    }

    private void spawnEnemies() {
        int frameWidth = getWidth();
        int frameHeight = getHeight();
        int playerX = player.getX();
        int playerY = player.getY();
        int safeDistance = 100; // Safe distance from the player

        if (enemies.size() < 10) {
            for (int attempts = 0; attempts < 50; attempts++) { // Try multiple times to find a safe location
                int x = (int) (Math.random() * (frameWidth - 50));
                int y = 30 + (int) (Math.random() * (frameHeight - 80)); // Ensure enemies do not spawn in the top 30 pixels

                // Ensure enemy does not spawn out of bounds
                if (x < 0) x = 0;
                if (y < 0) y = 0;
                if (x + 50 > frameWidth) x = frameWidth - 50;
                if (y + 50 > frameHeight) y = frameHeight - 50;

                double distance = Math.sqrt(Math.pow(playerX + 25 - x, 2) + Math.pow(playerY + 25 - y, 2));
                if (distance >= safeDistance) {
                    enemies.add(new Enemy(x, y, 20, 50));
                    System.out.println("Enemy spawned at (" + x + ", " + y + ")");
                    break;
                }
            }
        }
    }

    private void moveEnemies() {
        Random rand = new Random();
        for (Enemy enemy : enemies) {
            int dx = rand.nextInt(21) - 10; // Move randomly within -10 to 10 on x-axis
            int dy = rand.nextInt(21) - 10; // Move randomly within -10 to 10 on y-axis
            Rectangle bounds = enemy.getBounds();
            bounds.translate(dx, dy);

            // Restrict enemy movement within the screen bounds
            if (bounds.x < 0) bounds.x = 0;
            if (bounds.y < 0) bounds.y = 0;
            if (bounds.x + bounds.width > getWidth()) bounds.x = getWidth() - bounds.width;
            if (bounds.y + bounds.height > getHeight()) bounds.y = getHeight() - bounds.height;

            enemy.setBounds(bounds);
            System.out.println("Enemy moved to (" + bounds.x + ", " + bounds.y + ")");
        }
    }

    private void updateHealthBar() {
        int healthWidth = (int) (1000 * (health / 100.0));
        healthBar.setSize(healthWidth, 20);
        healthBar.setBackground(health > 50 ? Color.GREEN : health > 20 ? Color.ORANGE : Color.RED);
    }

    private void printDebugInfo() {
        System.out.println("Debug Info:");
        System.out.println("Score: " + score);
        System.out.println("Health: " + health);
        System.out.println("Bullets: " + bulletCount);
        System.out.println("Time: " + time);

        System.out.println("Enemies:");
        for (Enemy enemy : enemies) {
            System.out.println(" - Enemy at (" + enemy.x + ", " + enemy.y + ") with health: " + enemy.health);
        }

        System.out.println("Bullets:");
        for (Bullet bullet : bullets) {
            System.out.println(" - Bullet at (" + bullet.x + ", " + bullet.y + ")");
        }

        System.out.println("Enemy Bullets:");
        for (Bullet bullet : enemyBullets) {
            System.out.println(" - Enemy Bullet at (" + bullet.x + ", " + bullet.y + ")");
        }

        System.out.println("------------------------------------------------");
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        player.repaint();
        enemies.forEach(enemy -> enemy.draw(g));
        bullets.forEach(bullet -> bullet.draw(g));
        enemyBullets.forEach(bullet -> bullet.draw(g));
    }
}

class Enemy {
    int x, y, health, size;

    public Enemy(int x, int y, int health, int size) {
        this.x = x;
        this.y = y;
        this.health = health;
        this.size = size;
    }

    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(x, y, size, size);
        drawHealthBar(g);
    }

    private void drawHealthBar(Graphics g) {
        g.setColor(Color.GREEN);
        int healthWidth = (int) ((health / 20.0) * size);
        g.fillRect(x, y - 10, healthWidth, 5);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, size, size);
    }

    public void setBounds(Rectangle bounds) {
        x = bounds.x;
        y = bounds.y;
    }

    public boolean takeDamage(int damage) {
        health -= damage;
        return health <= 0;  // Return true if health is 0 or less
    }

    public void fireAtPlayer(int px, int py, List<Bullet> enemyBullets) {
        Bullet bullet = new Bullet(x + size / 2, y + size / 2, px, py, 20);
        bullet.setColor(Color.RED);  // Set enemy bullets to be red
        enemyBullets.add(bullet);
    }
}

class Bullet {
    double x, y;
    double dx, dy;
    Color color = Color.BLUE;  // Default color for player bullets

    public Bullet(int startX, int startY, int targetX, int targetY, double speedMultiplier) {
        this.x = startX;
        this.y = startY;
        double angle = Math.atan2(targetY - startY, targetX - startX);
        dx = speedMultiplier * Math.cos(angle);
        dy = speedMultiplier * Math.sin(angle);
    }

    public void move() {
        x += dx;
        y += dy;
    }

    public boolean isOnScreen(int width, int height) {
        return x >= 0 && x <= width && y >= 0 && y <= height;
    }

    public boolean hit(Enemy enemy) {
        return new Rectangle((int) x, (int) y, 10, 10).intersects(enemy.getBounds());
    }

    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, 10, 10);
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void draw(Graphics g) {
        g.setColor(color);
        g.fillOval((int) x, (int) y, 10, 10);
    }
}
