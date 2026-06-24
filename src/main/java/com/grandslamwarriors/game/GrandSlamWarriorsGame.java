package com.grandslamwarriors.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.grandslamwarriors.game.data.CharacterFactory;
import com.grandslamwarriors.game.data.CharacterRepository;
import com.grandslamwarriors.game.data.GrandSlam;
import com.grandslamwarriors.game.screens.GameScreen;
import com.grandslamwarriors.game.screens.MenuScreen;

public class GrandSlamWarriorsGame extends Game {
    public SpriteBatch batch;
    public AssetManager assetManager;

    private final java.util.Map<String, Integer> slamScores = new java.util.HashMap<>();
    private int totalScore = 0;

    public static final int V_WIDTH = 800;
    public static final int V_HEIGHT = 480;

    @Override
    public void create() {
        batch = new SpriteBatch();
        assetManager = new AssetManager();

        // Registra os assets dos personagens
        assetManager.load("characters/nadal.png", Texture.class);
        assetManager.load("characters/federer.png", Texture.class);
        assetManager.load("characters/djokovic.png", Texture.class);
        assetManager.load("characters/agassi.png", Texture.class);

        // Carregar texturas de uso comum
        assetManager.load("collectables/tennis_ball.png", Texture.class);
        assetManager.load("collectables/trophy.png", Texture.class);

        // Inimigos e fogos
        assetManager.load("enemies/aus_enemy1.png", Texture.class);
        assetManager.load("enemies/aus_enemy2.png", Texture.class);
        assetManager.load("enemies/aus_enemy3.png", Texture.class);
        assetManager.load("weapons/aus_fire.png", Texture.class);
        assetManager.load("enemies/rg_enemy1.png", Texture.class);
        assetManager.load("enemies/rg_enemy2.png", Texture.class);
        assetManager.load("enemies/rg_enemy3.png", Texture.class);
        assetManager.load("weapons/rg_fire.png", Texture.class);
        assetManager.load("enemies/wim_enemy1.png", Texture.class);
        assetManager.load("enemies/wim_enemy2.png", Texture.class);
        assetManager.load("enemies/wim_enemy3.png", Texture.class);
        assetManager.load("weapons/wim_fire.png", Texture.class);
        assetManager.load("enemies/us_enemy1.png", Texture.class);
        assetManager.load("enemies/us_enemy2.png", Texture.class);
        assetManager.load("enemies/us_enemy3.png", Texture.class);
        assetManager.load("weapons/us_fire.png", Texture.class);

        // CHEFES
        assetManager.load("enemies/aus_boss.png", Texture.class);
        assetManager.load("enemies/rg_boss.png", Texture.class);
        assetManager.load("enemies/wim_boss.png", Texture.class);
        assetManager.load("enemies/us_boss.png", Texture.class);

        // BALAS DOS CHEFES
        assetManager.load("weapons/aus_boss_fire.png", Texture.class);
        assetManager.load("weapons/rg_boss_fire.png", Texture.class);
        assetManager.load("weapons/wim_boss_fire.png", Texture.class);
        assetManager.load("weapons/us_boss_fire.png", Texture.class);

        assetManager.load("backgrounds/us_final.png", Texture.class);
        assetManager.load("backgrounds/rg_final.png", Texture.class);
        assetManager.load("backgrounds/wim_final.png", Texture.class);
        assetManager.load("backgrounds/aus_final.png", Texture.class);

        assetManager.finishLoading();

        //setScreen(new MenuScreen(this));

        CharacterFactory characterFactory = new CharacterFactory(assetManager, new CharacterRepository());
        setScreen(new GameScreen(this, GrandSlam.US_OPEN, characterFactory.create("baseline_machine")));
    }

    @Override
    public void dispose() {
        batch.dispose();
        assetManager.dispose();
    }

    public void setSlamScore(String slamId, int score) {
        slamScores.put(slamId, score);
        // Atualiza totalScore
        totalScore = 0;
        for (int s : slamScores.values()) {
            totalScore += s;
        }
        // Verifica desbloqueio do Agassi (5000 pontos)
        if (totalScore >= 5000) {
            com.grandslamwarriors.game.data.SettingsManager.setAgassiUnlocked(true);
        }
    }

    public int getSlamScore(String slamId) {
        return slamScores.getOrDefault(slamId, 0);
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void clearScores() {
        slamScores.clear();
        totalScore = 0;
    }
}
