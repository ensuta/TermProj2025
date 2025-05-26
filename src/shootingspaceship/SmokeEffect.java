package shootingspaceship;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.ImageIcon;

public class SmokeEffect {
    private Image smokeImage;
    private int alpha = 0;
    private boolean increasing = true;

    public SmokeEffect() {
        smokeImage = new ImageIcon("image/smoke.png").getImage();
    }

    public void start() {
        alpha = 0;
    }

    public void update() {
        if (increasing) {
            alpha += 5;
            if (alpha >= 150) increasing = false;
        } else {
            alpha -= 5;
            if (alpha <= 0) increasing = true;
        }
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Composite original = g2d.getComposite();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha / 255f));
        g2d.drawImage(smokeImage, 0, 0, null);
        g2d.setComposite(original);
    }
}
