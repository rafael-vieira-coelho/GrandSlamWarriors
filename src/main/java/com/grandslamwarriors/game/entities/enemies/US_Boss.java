package com.grandslamwarriors.game.entities.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.grandslamwarriors.game.entities.projectiles.BossProjectile;

public class US_Boss extends Boss {

    private Texture projectileTexture;
    private float mortarTimer = 0f;

    public US_Boss(float x, float y, Texture texture, Texture projTexture, Array<BossProjectile> projectiles) {
        super(x, y, texture, projectiles);
        this.projectileTexture = projTexture;
        this.maxHealth = 100;
        this.health = maxHealth;
        setPatrolRangeSymmetric(50f);
    }

    @Override
    public void update(float delta) {
        if (dead) return;
        animTime += delta;
        stateTimer += delta;

        // US Open Boss (Skyscraper Giant): Estático, atira como morteiro
        if (currentState == 0) {
            patrol(10f * difficultyFactor, delta);
            mortarTimer += delta;
            if (mortarTimer > 2f) {
                setState(1);
                mortarTimer = 0;
            }
        } else if (currentState == 1) {
            if (stateTimer > 0.5f && stateTimer < 0.6f) shoot();
            if (stateTimer > 1.5f) {
                setState(0);
            }
        }
        updateBounds();
    }

    @Override
    protected void shoot() {
        if (projectileTexture == null) return;

        // Alterna entre morteiro e horizontal
        if (MathUtils.randomBoolean(0.4f)) {
            // Horizontal
            float dirX = movingRight ? 1f : -1f;
            BossProjectile p = new BossProjectile(projectileTexture, pos.x + drawWidth/2, pos.y + drawHeight/2, dirX * 350f, 0);
            bossProjectiles.add(p);
        } else {
            // Morteiro
            float dirX = (playerRef != null && playerRef.pos.x < pos.x) ? -150f : 150f;
            BossProjectile p = new BossProjectile(projectileTexture, pos.x + drawWidth/2, pos.y + drawHeight/2, dirX, 500f);
            bossProjectiles.add(p);
        }
    }
}
