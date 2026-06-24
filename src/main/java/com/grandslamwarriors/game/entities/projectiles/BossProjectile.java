package com.grandslamwarriors.game.entities.projectiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class BossProjectile {
    public Vector2 pos;
    public Vector2 vel;
    public Rectangle bounds;
    public boolean active = true;

    private final TextureRegion[] animFrames;
    private float animTime = 0f;

    private static final float SIZE = 32f;

    public BossProjectile(Texture texture, float x, float y, float vx, float vy) {
        this.pos = new Vector2(x, y);
        this.vel = new Vector2(vx, vy);
        this.bounds = new Rectangle(x, y, SIZE, SIZE);

        // Assume 6 frames horizontais conforme imagem fornecida
        if (texture != null) {
            TextureRegion[][] tmp = TextureRegion.split(texture, texture.getWidth() / 6, texture.getHeight());
            this.animFrames = tmp[0];
        } else {
            this.animFrames = null;
        }
    }

    public void update(float delta) {
        animTime += delta;
        pos.x += vel.x * delta;
        pos.y += vel.y * delta;
        bounds.setPosition(pos.x, pos.y);

        if (pos.y < -100f || pos.y > 1000f || pos.x < -100f || pos.x > 6000f) {
            active = false;
        }
    }

    public void draw(SpriteBatch batch) {
        if (!active || animFrames == null) return;
        // Inverte a animação (direita para esquerda) usando (length - 1 - frame)
        int frame = (int)(animTime * 10) % animFrames.length;
        int invertedFrame = animFrames.length - 1 - frame;
        batch.draw(animFrames[invertedFrame], pos.x, pos.y, SIZE, SIZE);
    }
}
