package shootingspaceship;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class Bomb extends Enemy{
	private List<Bomb> activeBombs = new ArrayList<>();
	private long lastBombTime = 0;
	private long bombInterval = 1000; // 1초에 한 번

	private boolean Parry;
	private Color color= Color.RED;
    private boolean active;
    private int speed;
    private boolean isParryable = false;
    private static BufferedImage bombImage;
    private int speedY;
	private int x;
	private int y;
	
	int scaledBWidth = 1;  //폭탄 원하는 너비
	int scaledBHeight = 150; //폭탄 원하는 높이
	
    public static final int WIDTH = 32;
    public static final int HEIGHT = 32;
    private static final int SCREEN_HEIGHT = 800;
    
    public Bomb(float x, float y, int speedY) {
    	super((int) x, (int) y, y, y, speedY, speedY, y);
    	this.x = (int)x;
    	this.y = (int)y;
    	this.speed = speed;
    	this.speedY = speedY;
    	this.Parry = false;
    	this.Parry = false; //기본 상태
    	this.color = Color.RED;
    	this.active = true;
    	this.speedY = speedY;
    	
    	try {
            bombImage = ImageIO.read(new File("src/shootingspaceship/image/firstBomb.png"));
        } catch (IOException e) {
            System.out.println("Bomb 이미지 로드 실패");
            e.printStackTrace();
        }
    
    //이미지 로딩 (한 번만)
    	if (bombImage == null) {
            try {
                bombImage = ImageIO.read(getClass().getResource("/shootingspaceship/Image/firstBomb.png"));
                if (bombImage == null) {
                    System.err.println("Bomb 이미지 경로 잘못됨: 파일이 존재하지 않음");
                }
            } catch (IOException e) {
                System.err.println("Bomb 이미지 로딩 실패");
                e.printStackTrace();
            }
        }
    }
    public void update() {
    	y += speedY;
    	if (y > SCREEN_HEIGHT) {
    		active = false;
    	}
    }
    
//    @Override
//    public Rectangle getBounds() {
//    	int width = (bombImage != null) ? bombImage.getWidth() : 32;   // 기본값 32
//        int height = (bombImage != null) ? bombImage.getHeight() : 32;
//        return new Rectangle(x, y, width, height);
//    }
    
    @Override
    public boolean isCollidedWithPlayer(Player player) {
        if (bombImage == null) {
        	return false;
        }

        Rectangle bombBounds = new Rectangle((int)x, (int)y, bombImage.getWidth(), bombImage.getHeight());
        Rectangle playerBounds = player.getBounds();

        
        return false;
    }
    
    public void move() {
        y += speed;
    }
    
    public void moveBomb(int delta) {
        this.y += delta;
    }
    
    public void drawBomb(Graphics g) {
    	if (!active) return;
    	
    	if(bombImage != null) {
    		g.drawImage(bombImage, (int)x, (int)y, scaledBWidth, scaledBHeight, null);
    	} else {
    		g.setColor(Color.YELLOW);
            g.fillOval((int)x, (int)y, WIDTH, HEIGHT);
    	}
    }
    
    public void setColor(Color color) {
    	this.color = color;
    }
    
    //비활성
    public void deactivate() {
    	this.active = false;
    }
    //활성화
    public boolean isActive() {
    	return active;
    }   
}
