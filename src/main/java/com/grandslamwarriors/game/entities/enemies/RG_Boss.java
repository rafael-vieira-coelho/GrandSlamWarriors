package com.grandslamwarriors.game.entities.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.grandslamwarriors.game.entities.projectiles.BossProjectile;

public class RG_Boss extends Boss {

    private float shootTimer = 0f;
    private Texture projectileTexture;

    public RG_Boss(float x, float y, Texture texture, Texture projTexture, Array<BossProjectile> projectiles) {
        super(x, y, texture, projectiles);
        this.projectileTexture = projTexture;
        this.maxHealth = 70;
        this.health = maxHealth;
        setPatrolRangeSymmetric(80f);
    }

    @Override
    public void update(float delta) {
        if (dead) return;
        animTime += delta;
        stateTimer += delta;

        // RG Boss (Clay Monster): Lento, mas atira constantemente
        if (currentState == 0) {
            patrol(30f * difficultyFactor, delta);
            shootTimer += delta;
            if (shootTimer > 1.5f) {
                setState(1);
                shootTimer = 0;
            }
        } else if (currentState == 1) {
            if (stateTimer > 0.4f && stateTimer < 0.5f) {
                shoot();
            }
            if (stateTimer > 0.8f) {
                setState(0);
            }
        }
        updateBounds();
    }

    @Override
    protected void shoot() {
        if (projectileTexture == null) return;
        // Atira rajada de 3 projéteis (reduzido de 5)
        for (int i = 0; i < 3; i++) {
            float vx = (movingRight ? 1f : -1f) * (200f + i * 40f);
            BossProjectile p = new BossProjectile(projectileTexture, pos.x + drawWidth/2, pos.y + drawHeight/2, vx, 0);
            bossProjectiles.add(p);
        }
    }
}
