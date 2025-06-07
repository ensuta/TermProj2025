package shootingspaceship;

public enum CharacterType {
    // 캐릭터 이름(이동속도, 총알발사간격(ms, 낮을수록 빠름), 총알데미지)
    SPEED("스피드", 5, 200, 1),      // 이동속도 빠름, 연사속도 빠름, 데미지 보통
    POWER("파워", 3, 1000, 10),    // 이동속도 느림, 연사속도 느림, 데미지 강력
    BALANCE("밸런스", 4, 500, 2);    // 모든 능력치 중간

    private final String displayName;
    public final int moveSpeed;
    public final int shotInterval;
    public final int bulletDamage;

    CharacterType(String displayName, int moveSpeed, int shotInterval, int bulletDamage) {
        this.displayName = displayName;
        this.moveSpeed = moveSpeed;
        this.shotInterval = shotInterval;
        this.bulletDamage = bulletDamage;
    }



    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName + " (이동: " + moveSpeed + ", 연사간격: " + shotInterval + "ms, 데미지: " + bulletDamage + ")";
    }
}