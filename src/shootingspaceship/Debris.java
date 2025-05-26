package shootingspaceship;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

public class Debris {
    private int x, y;
    private int speed = 5;
    private Image debrisImage;

    public Debris(int x, int y) {
        this.x = x;
        this.y = y;
        this.debrisImage = new ImageIcon("image/debris.png").getImage();
    }

    public void moveDown() {
        y += speed;
    }

    public void draw(Graphics g) {
        g.drawImage(debrisImage, x, y, null);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, debrisImage.getWidth(null), debrisImage.getHeight(null));
    }
}
