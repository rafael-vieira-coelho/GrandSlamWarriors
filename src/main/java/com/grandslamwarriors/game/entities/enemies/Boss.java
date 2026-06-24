package com.grandslamwarriors.game.entities.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.grandslamwarriors.game.entities.projectiles.BossProjectile;

public abstract class Boss extends Enemy {

    protected TextureRegion[][] grid;
    protected int currentState = 0; // 0: Idle/Move, 1: Attack, 2: Special/Hurt
    protected float stateTimer = 0f;
    protected int maxHealth;

    protected Array<BossProjectile> bossProjectiles;

    public Boss(float x, float y, Texture texture, Array<BossProjectile> projectiles) {
        super(x, y, texture);
        this.bossProjectiles = projectiles;

        if (texture != null) {
            // Se a imagem for larga (como a da Estátua da Liberdade), tratamos como 1 linha e várias colunas
            // Se for quadrada, tratamos como grid 3x3
            if (texture.getWidth() > texture.getHeight() * 2) {
                int frames = 6; // Padrão para a tira horizontal de chefes
                int frameWidth = texture.getWidth() / frames;
                int frameHeight = texture.getHeight();
                this.grid = TextureRegion.split(texture, frameWidth, frameHeight);
            } else {
                // Divide em 3 colunas e 3 linhas (Total 9 imagens)
                int frameWidth = texture.getWidth() / 3;
                int frameHeight = texture.getHeight() / 3;
                this.grid = TextureRegion.split(texture, frameWidth, frameHeight);
            }
        }

        this.maxHealth = 40;
        this.health = maxHealth;
        this.animationSpeed = 6f;

        // Chefes são geralmente maiores
        setDrawSize(128f, 128f);
        setBoundsSize(80f, 110f);
        setDrawOffset(-24f, 0f);
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (dead || grid == null) return;

        // Linha do grid baseada no estado atual (0, 1 ou 2)
        TextureRegion[] row = grid[Math.min(currentState, 2)];
        int frame = (int)(animTime * animationSpeed) % 3;
        TextureRegion region = row[frame];

        drawRegion(batch, region);
    }

    public void setState(int state) {
        if (this.currentState != state) {
            this.currentState = state;
            this.animTime = 0f;
            this.stateTimer = 0f;
        }
    }

    protected abstract void shoot();

    @Override
    public void takeDamage(float amount) {
        super.takeDamage(amount);
    }
}
