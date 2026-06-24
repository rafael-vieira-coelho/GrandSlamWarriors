package com.grandslamwarriors.game.entities.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.grandslamwarriors.game.entities.projectiles.BossProjectile;

public class WIM_Boss extends Boss {

    private Texture projectileTexture;
    private float burstTimer = 0f;

    public WIM_Boss(float x, float y, Texture texture, Texture projTexture, Array<BossProjectile> projectiles) {
        super(x, y, texture, projectiles);
        this.projectileTexture = projTexture;
        this.maxHealth = 80;
        this.health = maxHealth;
        setPatrolRangeSymmetric(200f);
    }

    @Override
    public void update(float delta) {
        if (dead) return;
        animTime += delta;
        stateTimer += delta;

        // Wimbledon Boss (Grass Cutter): Rápido, ataca em rajadas
        if (currentState == 0) {
            patrol(120f * difficultyFactor, delta);
            burstTimer += delta;
            if (burstTimer > 3f) {
                setState(1);
                burstTimer = 0;
            }
        } else if (currentState == 1) {
            if (stateTimer > 0.3f && stateTimer < 0.4f) shoot();
            if (stateTimer > 0.7f && stateTimer < 0.8f) shoot();

            if (stateTimer > 1.5f) {
                setState(0);
            }
        }
        updateBounds();
    }

    @Override
    protected void shoot() {
        if (projectileTexture == null) return;
        float dirX = movingRight ? 1f : -1f;
        BossProjectile p = new BossProjectile(projectileTexture, pos.x + drawWidth/2, pos.y + drawHeight/2, dirX * 400f, 0);
        bossProjectiles.add(p);
    }
}
