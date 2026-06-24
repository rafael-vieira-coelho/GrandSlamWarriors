package com.grandslamwarriors.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.grandslamwarriors.game.data.GrandSlam;
import com.grandslamwarriors.game.entities.GameCharacter;
import com.grandslamwarriors.game.GrandSlamWarriorsGame;

public class TrophyScreen implements Screen {
    private final GrandSlamWarriorsGame game;
    private final GrandSlam completedSlam;
    private final int finalScore;
    private final GameCharacter playerCharacter;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Texture trophyImage;
    private BitmapFont font;
    private float timer;

    public TrophyScreen(GrandSlamWarriorsGame game, GrandSlam slam, int score, GameCharacter character) {
        this.game = game;
        this.completedSlam = slam;
        this.finalScore = score;
        this.playerCharacter = character;
        camera = new OrthographicCamera();
        viewport = new StretchViewport(GrandSlamWarriorsGame.V_WIDTH, GrandSlamWarriorsGame.V_HEIGHT, camera);
        trophyImage = new Texture("collectables/trophy.png");
        font = new BitmapFont(); // fonte padrão
        font.getData().setScale(1.5f);
    }

    @Override
    public void render(float delta) {
        timer += delta;
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        // Desenha o troféu
        game.batch.draw(trophyImage,
            GrandSlamWarriorsGame.V_WIDTH / 2f - 50,
            GrandSlamWarriorsGame.V_HEIGHT / 2f,
            100, 100);
        // Exibe texto
        String text = "You won " + completedSlam.nome + "!\nScore: " + finalScore;
        font.draw(game.batch, text, 50, GrandSlamWarriorsGame.V_HEIGHT - 50);
        game.batch.end();

        if (timer > 2.5f) {
            GrandSlam nextSlam = getNextSlam();
            if (nextSlam != null) {
                // Avança para a próxima fase com o mesmo personagem
                game.setScreen(new GameScreen(game, nextSlam, playerCharacter));
            } else {
                // Todos os slams concluídos – volta ao menu principal
                game.setScreen(new MenuScreen(game));
            }
        }
    }

    private GrandSlam getNextSlam() {
        switch (completedSlam) {
            case AUSTRALIAN_OPEN: return GrandSlam.ROLAND_GARROS;
            case ROLAND_GARROS:   return GrandSlam.WIMBLEDON;
            case WIMBLEDON:       return GrandSlam.US_OPEN;
            default:              return null;
        }
    }

    @Override
    public void show() {}
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}

    @Override
    public void dispose() {
        trophyImage.dispose();
        font.dispose();
    }
}
