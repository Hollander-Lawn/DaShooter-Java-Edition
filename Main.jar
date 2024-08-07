import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) {

        System.out.println("Loading Window");

        //JFrame specification
        int frame_x = 1060;
        int frame_y = 610;
        JFrame Main_menu_frame = new JFrame("DaShooter Java Edition v0.1.1 Alpha");
        Main_menu_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Main_menu_frame.setSize(frame_x, frame_y);
        Main_menu_frame.setVisible(true);
        System.out.println("Window loaded!");

        // Making the Play button
        JButton play_button = new JButton("Play");
        play_button.setBounds(200,250,100,50); 
        Font font = new Font("Arial", Font.BOLD, 30); // Change font size as needed
        play_button.setFont(font);
        Main_menu_frame.add(play_button);
        System.out.println("Play button loaded!");

        // Get the screen size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;

        // Making the main menu text 
        JLabel t1 = new JLabel("DaShooter");
        t1.setFont(new Font("Arial", Font.BOLD, 48)); // Larger font for title
        t1.setForeground(Color.DARK_GRAY); // Set text color to dark gray
        t1.setBounds((screenWidth - t1.getPreferredSize().width) / 2, 100, 505, 300);
        Main_menu_frame.add(t1);


        JLabel t2 = new JLabel("Java Edition");
        t2.setFont(new Font("Arial", Font.BOLD, 24)); // Smaller font for subtitle
        t2.setForeground(Color.WHITE); // Set text color to white
        t2.setBounds(10, frame_y - t2.getPreferredSize().height - 10, t2.getPreferredSize().width, t2.getPreferredSize().height); // Bottom left
        Main_menu_frame.add(t2); 
        System.out.println("Loading");

        // Version Info Label
        JLabel versionLabel = new JLabel("Alpha 0.1.1");
        versionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        versionLabel.setForeground(Color.WHITE); 
        versionLabel.setBounds(screenWidth - versionLabel.getPreferredSize().width - 10, frame_y - versionLabel.getPreferredSize().height - 10, versionLabel.getPreferredSize().width, versionLabel.getPreferredSize().height); // Bottom right
        Main_menu_frame.add(versionLabel); 

        // Play Button ActionListener
        play_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main_menu_frame.dispose(); // Close Main Menu
                new GameWindow(frame_x, frame_y); // Create and Show Game Window
            }
        });

        System.out.println("All assets loaded!" + "\n" + "Starting game...");

        System.out.println("Loaded!");

    }
}

// Game Window class
class GameWindow extends JFrame {
    private JPanel player;
    private ArrayList<Enemy> enemies;
    private int gameWidth;
    private int gameHeight;

    public GameWindow(int frame_x, int frame_y) {
        super("DaShooter: Java Edition");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameWidth = frame_x;
        gameHeight = frame_y;
        setSize(gameWidth, gameHeight);
        setLocationRelativeTo(null); // Center the window
        setLayout(null); // Use absolute positioning
        setVisible(true);

        // Initialize Player
        player = new JPanel();
        player.setBackground(Color.BLUE); // Example
        player.setBounds(gameWidth / 2 - 25, gameHeight - 50, 50, 50);
        add(player);

        // Initialize Enemies
        enemies = new ArrayList<>();
        spawnEnemies(); // Initial spawn


        // Start Enemy Spawning (You can use a timer or a thread)
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this::spawnEnemies, 1000, 1000, TimeUnit.MILLISECONDS);
        System.out.println("Enemys spawned!");

        // Add Key Listener for Player Movement
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                if (keyCode == KeyEvent.VK_A) {
                    player.setLocation(player.getX() - 10, player.getY());
                } else if (keyCode == KeyEvent.VK_D) {
                    player.setLocation(player.getX() + 10, player.getY());
                } else if (keyCode == KeyEvent.VK_W) {
                    player.setLocation(player.getX(), player.getY() - 10);
                } else if (keyCode == KeyEvent.VK_S) {
                    player.setLocation(player.getX(), player.getY() + 10);
                }


                repaint();
            }
        });
        setFocusable(true); // Allow the JFrame to receive key events
    }

    private void spawnEnemies() {
        Random random = new Random();
        int enemyX = random.nextInt(gameWidth - 50); // 50 is example size of enemy
        int enemyY = random.nextInt(gameHeight - 50);
        int number_of_enemies = 0;

        if(number_of_enemies < 11){
            enemies.add(new Enemy(enemyX, enemyY));
            number_of_enemies++;
            repaint();
        } else{
            System.out.println("Max enemies reached!");
        }  
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g); // Call parent's paint method
        // Draw Enemies
        for (Enemy enemy : enemies) {
            enemy.draw(g);
            // Collision Check
            if (player.getBounds().intersects(enemy.getBounds())) {
                // Handle collision (e.g., display message)
                System.out.println("Collision!");
            }
        }
    }

    class Enemy {
        int x, y;

        public Enemy(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void draw(Graphics g) {
            g.setColor(Color.RED); // Example color
            g.fillRect(x, y, 50, 50); // Example size
        }

        public Rectangle getBounds() {
            return new Rectangle(x, y, 50, 50); // Example size
        }
    }
}
