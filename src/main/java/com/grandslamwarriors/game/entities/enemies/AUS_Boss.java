package com.grandslamwarriors.game.entities.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.grandslamwarriors.game.entities.projectiles.BossProjectile;

public class AUS_Boss extends Boss {

    private float attackTimer = 0f;
    private float jumpTimer = 0f;
    private Texture projectileTexture;

    public AUS_Boss(float x, float y, Texture texture, Texture projTexture, Array<BossProjectile> projectiles) {
        super(x, y, texture, projectiles);
        this.projectileTexture = projTexture;
        this.maxHealth = 60;
        this.health = maxHealth;
        setPatrolRangeSymmetric(150f);
    }

    @Override
    public void update(float delta) {
        if (dead) return;
        animTime += delta;
        stateTimer += delta;

        // Lógica de Estado
        if (currentState == 0) { // IDLE / MOVE
            patrol(60f * difficultyFactor, delta);
            attackTimer += delta;
            if (attackTimer > 2.5f) {
                setState(1); // Mudar para ATAQUE
                attackTimer = 0;
            }
        } else if (currentState == 1) { // ATTACK
            if (stateTimer > 0.5f && stateTimer < 0.6f) {
                shoot();
            }
            if (stateTimer > 1.2f) {
                setState(0);
            }
        }

        // Pulo aleatório (Diferencial do Kanguru Gigante)
        jumpTimer += delta;
        if (jumpTimer > 4f) {
            vel.y = 400f;
            jumpTimer = 0;
        }

        updateBounds();
    }

    @Override
    protected void shoot() {
        if (projectileTexture == null) return;
        // Atira 1 bola direto ou 2 em leque menor
        float dirX = movingRight ? 1f : -1f;
        BossProjectile p = new BossProjectile(projectileTexture, pos.x + drawWidth/2, pos.y + drawHeight/2, dirX * 300f, 0);
        bossProjectiles.add(p);
    }
}
