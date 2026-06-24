package com.grandslamwarriors.game.entities.weapons;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Trap {

    private static final int FRAME_COUNT = 6;
    private static final float FPS = 6f;

    private final TextureRegion[] frames;
    private float animTime = 0f;
    private final Rectangle bounds;
    private float drawX, drawY, drawWidth, drawHeight;
    private float damageCooldown = 0f;
    private static final float DAMAGE_INTERVAL = 0.5f;

    public Trap(float x, float y, float width, float height, Texture texture) {
        if (texture == null) throw new IllegalArgumentException("Trap texture cannot be null");

        TextureRegion[][] tmp = TextureRegion.split(texture, texture.getWidth() / FRAME_COUNT, texture.getHeight());
        frames = new TextureRegion[FRAME_COUNT];
        for (int i = 0; i < FRAME_COUNT; i++) frames[i] = tmp[0][i];

        this.drawWidth = width;
        this.drawHeight = height;
        this.drawX = x;
        this.drawY = y;
        // Hitbox TOTAL (a armadilha inteira)
        this.bounds = new Rectangle(drawX, drawY, drawWidth, drawHeight + 8f);
    }

    public void update(float delta) {
        animTime += delta;
        if (damageCooldown > 0) damageCooldown -= delta;
    }

    public void draw(SpriteBatch batch) {
        int frameIndex = (int)(animTime * FPS) % frames.length;
        batch.draw(frames[frameIndex], drawX, drawY, drawWidth, drawHeight);
    }

    public Rectangle getBounds() { return bounds; }

    /**
     * Causa dano se houver colisão e o cooldown estiver zerado.
     * (Hitbox total – você pode ajustar para meia altura se preferir)
     */
    public boolean canDamage(Rectangle playerBounds) {
        if (damageCooldown > 0) return false;
        if (!bounds.overlaps(playerBounds)) return false;
        damageCooldown = DAMAGE_INTERVAL;
        return true;
    }

    public float getX() { return drawX; }
}
