package me.darkinfect.entityes;
public class Boss {
    private int hp;
    private int maxHp;
    private float timeLeft;
    private boolean isShielded;
    private boolean isEnraged;

    public Boss(int level) {
        this.maxHp = 100 + 50 * level;
        this.hp = maxHp;
        this.timeLeft = 30f; // 30 секунд на бой
        this.isShielded = false;
        this.isEnraged = false;
    }

    public void takeDamage(int clicks) {
        if (isShielded) {
            if (clicks >= 20) { // Нужно 20 кликов, чтобы сломать щит
                isShielded = false;
            }
            return;
        }

        hp -= clicks;
        if (hp <= maxHp * 0.1f && !isEnraged) {
            isEnraged = true; // Фаза ярости!
        }
    }

    public void update(float delta) {
        timeLeft -= delta;
        if (timeLeft <= 0) {
            escape(); // Босс убежал!
        }

        // Каждые 10 секунд босс ставит щит
        if ((int)timeLeft % 10 == 0 && !isShielded) {
            isShielded = true;
        }
    }

    public boolean isDefeated() {
        return hp <= 0;
    }

    public void escape() {
        // Босс убежал, игрок не получил награду
    }
}
