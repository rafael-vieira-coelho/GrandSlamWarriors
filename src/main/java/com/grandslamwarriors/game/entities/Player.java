package com.grandslamwarriors.game.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Player {

    public static final int MAX_HEALTH = 100;

    public GameCharacter character;

    public Vector2 pos;
    public Vector2 vel;
    public Rectangle bounds;

    public int health;
    public int lives;
    public int score;
    public int tennisBallsCollected;

    public boolean grounded;
    public boolean facingRight;
    public boolean meleeActive;
    public boolean crouching;
    public boolean sprinting;
    public boolean aiming;

    public float meleeTimer;
    public float invincibilityTimer;
    public float animTime;

    // --- POWER-UP ---
    public float powerupTimer = 0f;
    public boolean isPowerupActive = false;
    private static final float POWERUP_SPEED_MULTIPLIER = 1.4f;

    // Constantes
    private static final float BASE_MOVE_SPEED = 200f;
    private static final float SPRINT_SPEED_MULTIPLIER = 1.5f;
    private static final float AIM_SPEED_REDUCTION = 0.5f;
    private static final float JUMP_SPEED = 550f;
    private static final float GRAVITY = -1100f;
    private static final float MAX_FALL_SPEED = -800f;
    private static final float MELEE_DURATION = 0.16f;
    private static final float INVINCIBILITY_DURATION = 0.90f;

    public Player(GameCharacter character, float x, float y) {
        this.character = character;

        this.pos = new Vector2(x, y);
        this.vel = new Vector2();

        this.bounds = new Rectangle(x + 12f, y, 26f, 54f);

        this.health = MAX_HEALTH;
        this.lives = 3;
        this.score = 0;
        this.tennisBallsCollected = 5;

        this.grounded = false;
        this.facingRight = true;
        this.meleeActive = false;
        this.sprinting = false;
        this.aiming = false;

        this.meleeTimer = 0f;
        this.invincibilityTimer = 0f;
        this.animTime = 0f;
    }

    public void update(float delta) {
        // Power-up
        if (powerupTimer > 0) {
            powerupTimer -= delta;
            if (powerupTimer < 0) powerupTimer = 0;
            if (powerupTimer == 0) isPowerupActive = false;
        }

        // Animação
        if (Math.abs(vel.x) > 0.1f || !grounded || meleeActive) {
            animTime += delta;
        } else {
            animTime = 0;
        }

        // Invulnerabilidade
        if (invincibilityTimer > 0f) {
            invincibilityTimer -= delta;
            if (invincibilityTimer < 0f) invincibilityTimer = 0f;
        }

        // Melee
        if (meleeActive) {
            meleeTimer -= delta;
            if (meleeTimer <= 0f) {
                meleeTimer = 0f;
                meleeActive = false;
            }
        }

        // Física
        vel.y += GRAVITY * delta;
        if (vel.y < MAX_FALL_SPEED) vel.y = MAX_FALL_SPEED;

        pos.x += vel.x * delta;
        pos.y += vel.y * delta;

        updateBounds();
    }

    public void moveLeft() {
        float speed = getCurrentSpeed();
        if (sprinting) speed *= SPRINT_SPEED_MULTIPLIER;
        if (aiming) speed *= AIM_SPEED_REDUCTION;
        vel.x = -speed;
        facingRight = false;
    }

    public void moveRight() {
        float speed = getCurrentSpeed();
        if (sprinting) speed *= SPRINT_SPEED_MULTIPLIER;
        if (aiming) speed *= AIM_SPEED_REDUCTION;
        vel.x = speed;
        facingRight = true;
    }

    public void stop() {
        vel.x = 0f;
    }

    public void jump() {
        if (!grounded || crouching) return;
        vel.y = JUMP_SPEED * character.getJumpMultiplier();
        grounded = false;
    }

    public void crouch(boolean active) {
        if (!grounded && active) return;
        crouching = active;
        updateBounds();
    }

    public void setSprint(boolean active) {
        sprinting = active;
    }

    public void setAim(boolean active) {
        aiming = active;
    }

    public void meleeAttack() {
        meleeActive = true;
        meleeTimer = MELEE_DURATION;
    }

    public void collectTennisBall() {
        tennisBallsCollected += 3;
        score += 10;
    }

    public void heal(int amount) {
        health = Math.min(MAX_HEALTH, health + amount);
    }

    public void activatePowerup(float duration) {
        powerupTimer = duration;
        isPowerupActive = true;
    }

    private float getCurrentSpeed() {
        float base = BASE_MOVE_SPEED * character.getMoveSpeedMultiplier();
        if (isPowerupActive) {
            return base * POWERUP_SPEED_MULTIPLIER;
        }
        return base;
    }

    public void takeDamage(int amount, float sourceX, float knockbackX, float knockbackY) {
        if (isPowerupActive) return;
        if (invincibilityTimer > 0f) return;

        // Aplica o fator de dificuldade
        float factor = com.grandslamwarriors.game.data.SettingsManager.getDifficultyFactor();
        int finalDamage = Math.max(1, (int)(amount * factor));

        if (com.grandslamwarriors.game.data.SettingsManager.isSoundEnabled()) {
            com.badlogic.gdx.Gdx.input.vibrate(200);
        }

        health -= finalDamage;
        if (health < 0) health = 0;
        invincibilityTimer = INVINCIBILITY_DURATION;

        if (pos.x < sourceX) {
            vel.x = -Math.abs(knockbackX);
        } else {
            vel.x = Math.abs(knockbackX);
        }
        vel.y = Math.abs(knockbackY);
        grounded = false;

        if (health <= 0) loseLife();
    }

    public void takeDamage(int amount) {
        if (isPowerupActive) return;
        if (invincibilityTimer > 0f) return;

        // Aplica o fator de dificuldade (1.0 fácil, 1.4 difícil)
        float factor = com.grandslamwarriors.game.data.SettingsManager.getDifficultyFactor();
        int finalDamage = Math.max(1, (int)(amount * factor));

        if (com.grandslamwarriors.game.data.SettingsManager.isSoundEnabled()) {
            com.badlogic.gdx.Gdx.input.vibrate(200);
        }

        health -= finalDamage;
        if (health < 0) health = 0;
        invincibilityTimer = INVINCIBILITY_DURATION;

        if (health <= 0) loseLife();
    }

    private void loseLife() {
        lives--;
        health = MAX_HEALTH;
        if (lives >= 0) {
            respawnAt(com.grandslamwarriors.game.data.CheckpointManager.getCheckpointX(),
                com.grandslamwarriors.game.data.CheckpointManager.getCheckpointY());
        }
    }

    public void respawnAt(float x, float y) {
        pos.set(x, y);
        vel.set(0f, 0f);
        grounded = false;
        meleeActive = false;
        meleeTimer = 0f;
        invincibilityTimer = 1.0f;
        updateBounds();
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public Vector2 getPosition() {
        return pos;
    }

    public void updateBounds() {
        float width = 26f;
        float height = crouching ? 30f : 54f;
        bounds.setSize(width, height);
        bounds.setPosition(pos.x + 12f, pos.y);
    }
}
