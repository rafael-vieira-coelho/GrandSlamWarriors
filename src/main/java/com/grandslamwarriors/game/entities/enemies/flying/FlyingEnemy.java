package com.grandslamwarriors.game.entities.enemies.flying;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.grandslamwarriors.game.entities.enemies.Enemy;

public abstract class FlyingEnemy extends Enemy {

    protected float fireTimer = 0f;
    protected float fireInterval = 2.5f;
    protected Texture fireTexture;
    protected boolean readyToFire = false;

    public FlyingEnemy(float x, float y, Texture texture, Texture fireTexture) {
        super(x, y, texture);
        this.fireTexture = fireTexture;

        // Divisão padrão: 6 frames horizontais (pode ser sobrescrito)
        if (texture != null) {
            int frameWidth = texture.getWidth() / 6;
            int frameHeight = texture.getHeight();
            this.regions = TextureRegion.split(texture, frameWidth, frameHeight)[0];
        }
        this.fireInterval = (2f + MathUtils.random(1.5f)) / difficultyFactor;
        this.animationSpeed = 6f;
    }

    // Método para sobrescrever se a textura tiver outra disposição
    protected void fillTexture() {
        // Já feito no construtor, mas pode ser sobrescrito
    }

    @Override
    public void update(float delta) {
        if (dead) return;

        animTime += delta;
        pos.y = spawnY + MathUtils.sin(animTime * 2f) * 20f;

        patrol(40f * difficultyFactor, delta);

        fireTimer += delta;
        if (fireTimer >= fireInterval) {
            fireTimer = 0;
            readyToFire = true;
        }

        updateBounds();
    }

    public boolean checkAndResetFire() {
        if (readyToFire) {
            readyToFire = false;
            return true;
        }
        return false;
    }

    public Texture getFireTexture() {
        return fireTexture;
    }

    @Override
    public boolean isSolid() {
        return false;
    }
}
