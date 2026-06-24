package com.grandslamwarriors.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.grandslamwarriors.game.GrandSlamWarriorsGame;
import com.grandslamwarriors.game.data.GrandSlam;
import com.grandslamwarriors.game.data.LeaderboardManager;
import com.grandslamwarriors.game.entities.GameCharacter;

public class VictoryScreen implements Screen {

    private final GrandSlamWarriorsGame game;
    private final GrandSlam slam;
    private final GameCharacter character;
    private final int finalScore;

    private Stage stage;
    private Skin skin;
    private Texture backgroundFinal;

    private LeaderboardManager leaderboard;

    public VictoryScreen(GrandSlamWarriorsGame game, GrandSlam slam, GameCharacter character, int finalScore) {
        this.game = game;
        this.slam = slam;
        this.character = character;
        this.finalScore = finalScore;
        this.leaderboard = new LeaderboardManager();

        game.setSlamScore(slam.name(), finalScore);

        String bgPath;
        switch (slam) {
            case AUSTRALIAN_OPEN: bgPath = "backgrounds/aus_final.png"; break;
            case ROLAND_GARROS:   bgPath = "backgrounds/rg_final.png"; break;
            case WIMBLEDON:       bgPath = "backgrounds/wim_final.png"; break;
            case US_OPEN:         bgPath = "backgrounds/us_final.png"; break;
            default:              bgPath = null;
        }
        if (bgPath != null) {
            try {
                backgroundFinal = new Texture(bgPath);
            } catch (Exception e) {
                backgroundFinal = null;
            }
        }
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

        Label.LabelStyle titleStyle = new Label.LabelStyle(font, Color.GOLD);
        Label.LabelStyle textStyle = new Label.LabelStyle(font, Color.WHITE);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.fontColor = Color.WHITE;

        Label title = new Label("VICTORY!", titleStyle);
        Label slamLabel = new Label("Completed: " + slam.nome, textStyle);
        Label scoreLabel = new Label("Score: " + finalScore, textStyle);

        GrandSlam nextSlam = slam.getNext();
        TextButton actionButton;

        if (nextSlam != null) {
            actionButton = new TextButton("Next Slam: " + nextSlam.nome, buttonStyle);
            actionButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                    game.setScreen(new GameScreen(game, nextSlam, character));
                }
            });
        } else {
            // Último slam: adiciona ao ranking e vai para Leaderboard
            Gdx.app.log("Victory", "Adding leaderboard entry: " + slam.nome + ", " + finalScore);
            leaderboard.addEntry(slam.nome, character.getConfig().displayName, character.getConfig().id, finalScore);

            actionButton = new TextButton("VIEW FINAL RESULTS", buttonStyle);
            actionButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                    game.setScreen(new LeaderboardScreen(game));
                }
            });
        }

        TextButton retryButton = new TextButton("Retry Level", buttonStyle);
        retryButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                game.setScreen(new GameScreen(game, slam, character));
            }
        });

        Table root = new Table();
        root.setFillParent(true);
        root.center();

        root.add(title).pad(20).row();
        root.add(slamLabel).pad(8).row();
        root.add(scoreLabel).pad(8).row();
        root.add(actionButton).width(450).height(70).padTop(30).row();
        root.add(retryButton).width(350).height(60).padTop(15);

        stage.addActor(root);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        if (backgroundFinal != null) {
            game.batch.draw(backgroundFinal, 0, 0,
                GrandSlamWarriorsGame.V_WIDTH, GrandSlamWarriorsGame.V_HEIGHT);
        } else {
            Gdx.gl.glClearColor(0.08f, 0.10f, 0.16f, 1f);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        }
        game.batch.end();

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
        if (backgroundFinal != null) backgroundFinal.dispose();
    }
}
