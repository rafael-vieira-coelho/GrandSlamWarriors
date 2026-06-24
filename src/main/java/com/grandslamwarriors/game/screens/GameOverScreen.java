package com.grandslamwarriors.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.grandslamwarriors.game.GrandSlamWarriorsGame;

public class GameOverScreen implements Screen {

    private final GrandSlamWarriorsGame game;
    private Stage stage;
    private Skin skin;
    private final String lastSlamName;
    private final int lastScore;
    private final String characterName;
    private final String characterId;

    public GameOverScreen(GrandSlamWarriorsGame game, String slamName, String charName, String charId, int score) {
        this.game = game;
        this.lastSlamName = slamName;
        this.characterName = charName;
        this.characterId = charId;
        this.lastScore = score;

        // Som de Game Over
        try {
            com.badlogic.gdx.audio.Sound gameOverSound = Gdx.audio.newSound(Gdx.files.internal("sounds/game_over.mp3"));
            if (gameOverSound != null && com.grandslamwarriors.game.data.SettingsManager.isSoundEnabled()) {
                gameOverSound.play();
            }
        } catch (Exception ignored) {}

        // Salva no ranking ao morrer
        new com.grandslamwarriors.game.data.LeaderboardManager()
            .addEntry(slamName, charName, charId, score);
    }

    @Override
    public void show() {
        stage = new Stage(
            new StretchViewport(
                GrandSlamWarriorsGame.V_WIDTH,
                GrandSlamWarriorsGame.V_HEIGHT,
                new OrthographicCamera()
            )
        );

        skin = new Skin();

        BitmapFont font = new BitmapFont();
        font.getData().setScale(2f);
        skin.add("default", font);

        TextButton.TextButtonStyle defaultBtnStyle = new TextButton.TextButtonStyle();
        defaultBtnStyle.font = font;
        defaultBtnStyle.fontColor = Color.WHITE;
        skin.add("default", defaultBtnStyle, TextButton.TextButtonStyle.class);

        Label.LabelStyle titleStyle = new Label.LabelStyle(font, Color.RED);
        Label.LabelStyle textStyle = new Label.LabelStyle(font, Color.WHITE);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.fontColor = Color.WHITE;

        Label title = new Label("GAME OVER", titleStyle);
        Label subtitle = new Label("You lost all your lives.", textStyle);

        TextButton menuButton = new TextButton("Menu", buttonStyle);
        menuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                game.setScreen(new CharacterSelectScreen(game));
            }
        });

        // Button to view ranking
        TextButton rankingButton = new TextButton("Ranking", buttonStyle);
        rankingButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                game.setScreen(new LeaderboardScreen(game));
            }
        });

        Table root = new Table();
        root.setFillParent(true);
        root.center();

        root.add(title).pad(20).row();
        root.add(subtitle).pad(12).row();

        Table buttonTable = new Table();
        buttonTable.add(menuButton).width(200).height(70).pad(10);
        buttonTable.add(rankingButton).width(200).height(70).pad(10);
        root.add(buttonTable).padTop(20);

        stage.addActor(root);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.12f, 0.05f, 0.05f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
