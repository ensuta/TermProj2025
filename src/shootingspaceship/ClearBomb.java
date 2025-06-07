package shootingspaceship;

import java.awt.Graphics;
import java.awt.Image;
import java.util.List;
import javax.swing.ImageIcon;

public class ClearBomb {
    private Image effectImage;
    private boolean active = false;
    private long startTime;
    private final int DURATION = 1000; // 이펙트 지속 시간 (ms)

    public ClearBomb() {
        effectImage = new ImageIcon("image/screen_bomb_effect.png").getImage();
    }

    public void activate(List<Enemy> enemies, List<Bomb> clearbombs) {
        // 모든 적과 적의 폭탄 제거
        enemies.clear();
        clearbombs.clear();
        System.out.println("화면 정리 폭탄 발동");

        // 이펙트 상태 시작
        active = true;
        startTime = System.currentTimeMillis();
    }

    public void update() {
        // 지속 시간이 지나면 비활성화
        if (active && System.currentTimeMillis() - startTime > DURATION) {
            active = false;
        }
    }

    public void draw(Graphics g) {
        if (active) {
            g.drawImage(effectImage, 0, 0, null); // 전체 화면에 이펙트 출력
        }
    }

    public boolean isActive() {
        return active;
    }
}
