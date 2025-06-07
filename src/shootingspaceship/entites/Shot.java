package shootingspaceship.entites;
import java.awt.Graphics;
import java.awt.Color;

public class Shot {

    private int x_pos;
    private int y_pos;
    private boolean alive;
    private final int radius = 15;
    private int damage = 1; // 기본 데미지
    private int deltaX = 0; // X축 이동량
    private int deltaY = 0; // Y축 이동량

    public Shot(int x, int y, int damage, int deltaX, int deltaY) {
        x_pos = x;
        y_pos = y;
        alive = true;
        this.damage = damage;
        this.deltaX = deltaX;
        this.deltaY = deltaY;
    }

    public int getY() {
        return y_pos;
    }

    public int getX() {
        return x_pos;
    }
    public void moveShot() {
        x_pos += deltaX;
        y_pos += deltaY;
    }

    public void drawShot(Graphics g) {
        if (!alive) {
            return;
        }
        g.setColor(Color.RED);
        g.fillOval(x_pos, y_pos, radius, radius);
    }

    public void collided() {
        alive = false;
    }
    

    public boolean isAlive() { 
    	return alive;
    }

    public int getDamage() {
        return damage;
    }
}
