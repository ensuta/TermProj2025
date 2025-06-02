package shootingspaceship;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

public class Debris {
    private int x, y;
    private int width, height;
    private Image image;

    public Debris(int x, int y, int width, int height, Image image) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = image;
    }

    public void draw(Graphics g) {
        g.drawImage(image, x, y, width, height, null);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    // 충돌 체크용
    public boolean collidesWith(Player player) {
        return player.getBounds().intersects(this.getBounds());
    }
}
