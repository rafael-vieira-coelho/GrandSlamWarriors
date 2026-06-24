package com.grandslamwarriors.game.entities.projectiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class FireProjectile {
    public Vector2 pos;
    public Rectangle bounds;
    public boolean active = true;

    private final TextureRegion[] animFrames;
    private float animTime = 0f;
    private static final float SPEED = 180f;
    private static final float WIDTH = 24f;
    private static final float HEIGHT = 32f;

    public FireProjectile(Texture texture, float x, float y) {
        this.pos = new Vector2(x, y);
        this.bounds = new Rectangle(x, y, WIDTH, HEIGHT);

        // 8 imagens horizontais
        TextureRegion[][] tmp = TextureRegion.split(texture, texture.getWidth() / 8, texture.getHeight());
        this.animFrames = tmp[0];
    }

    public void update(float delta) {
        animTime += delta;
        pos.y -= SPEED * delta;
        bounds.setPosition(pos.x, pos.y);

        if (pos.y < -50f) active = false;
    }

    public void draw(SpriteBatch batch) {
        if (!active) return;
        int frame = (int)(animTime * 12) % animFrames.length;
        batch.draw(animFrames[frame], pos.x, pos.y, WIDTH, HEIGHT);
    }
}
