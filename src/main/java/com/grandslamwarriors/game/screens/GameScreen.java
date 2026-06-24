package com.grandslamwarriors.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.grandslamwarriors.game.GrandSlamWarriorsGame;
import com.grandslamwarriors.game.data.GrandSlam;
import com.grandslamwarriors.game.data.SettingsManager;
import com.grandslamwarriors.game.entities.GameCharacter;
import com.grandslamwarriors.game.entities.MovingPlatform;
import com.grandslamwarriors.game.entities.Player;
import com.grandslamwarriors.game.entities.enemies.*;
import com.grandslamwarriors.game.entities.enemies.flying.*;
import com.grandslamwarriors.game.entities.projectiles.FireProjectile;
import com.grandslamwarriors.game.entities.projectiles.TennisBallProjectile;
import com.grandslamwarriors.game.entities.weapons.Trap;
import com.grandslamwarriors.game.leveldata.AustralianLevelData;
import com.grandslamwarriors.game.leveldata.RolandGarrosLevelData;
import com.grandslamwarriors.game.leveldata.USOpenLevelData;
import com.grandslamwarriors.game.leveldata.WimbledonLevelData;
import com.grandslamwarriors.game.renderers.LevelRenderer;
import com.grandslamwarriors.game.tiles.TileType;

import java.util.HashMap;
import java.util.Map;

public class GameScreen implements Screen {

    private boolean timerExpired;
    private final GrandSlamWarriorsGame game;
    private final GrandSlam currentSlam;

    private final Player player;

    private float doorCooldown = 0f;

    private final OrthographicCamera camera;
    private final Viewport viewport;

    private final Stage uiStage;
    private final Viewport uiViewport;
    private final Skin skin;

    //private Texture backgroundTexture;
    private final Texture ballTexture;
    private final Texture item1Texture;
    private final Texture item2Texture;
    private final Texture levelTrophyTexture;
    private final Texture lifeTrophyTexture;
    private Texture placeholderTexture;

    private LevelRenderer levelRenderer;

    private Array<Rectangle> tennisBalls;
    private Array<Rectangle> levelItems;
    private Rectangle endLevelTrophy;
    private Rectangle lifeTrophy;

    private Array<Enemy> enemies;
    private Array<com.grandslamwarriors.game.entities.projectiles.BossProjectile> bossProjectiles;
    private Array<TennisBallProjectile> projectiles;
    private Array<FireProjectile> enemyProjectiles;
    private Map<String, Texture> enemyTextures;

    private Array<Trap> traps;
    private Texture trapTexture;
    private TileType[][] currentMap;

    // Novos elementos dinâmicos
    private Array<MovingPlatform> movingPlatforms;
    private java.util.Map<Rectangle, Rectangle> doorPairs;
    private Array<Rectangle> doorEntryRects;
    private Array<Rectangle> doorExitRects;
    private Texture doorTexture;
    private Texture doorKeyTexture;
    private float doorAnimTime = 0f;
    private float levelTimer = 120f; // 120 segundos inicial
    private Label timerLabel;
    private Rectangle doorKeyRect;
    private boolean hasKey = false;
    private com.badlogic.gdx.scenes.scene2d.ui.Image keyIcon;

    // Condição de finalização (coletar todos os itens)
    private int itemsToCollect;
    private boolean allCollected = false;

    // Aviso de itens faltantes
    private Label warningLabel;
    private float warningTimer = 0f;
    private boolean showingWarning = false;

    private float fireRainTimer;

    private boolean leftPressed;
    private boolean rightPressed;
    private boolean downPressed;
    private boolean jumpRequested;
    private boolean attackRequested;
    private boolean shootRequested;

    private Label hpLabel;
    private Label livesLabel;
    private Label ammoLabel;
    private Label scoreLabel;
    private Label itemsLabel;

    // ESTADO
    private boolean paused = false;
    private Table pauseTable;
    private float checkpointX = 100f;
    private float checkpointY = 320f;

    // SONS
    private com.badlogic.gdx.audio.Sound startLevelSound;
    private com.badlogic.gdx.audio.Sound collectBallSound;
    private com.badlogic.gdx.audio.Sound collectHealSound;
    private com.badlogic.gdx.audio.Sound collectLifeSound;
    private com.badlogic.gdx.audio.Sound winSound;
    private com.badlogic.gdx.audio.Sound shootSound;
    private com.badlogic.gdx.audio.Sound hitSound;
    private com.badlogic.gdx.audio.Sound doorSound;
    private com.badlogic.gdx.audio.Sound powerupSound;
    private long powerupSoundId = -1;
    private com.badlogic.gdx.audio.Music levelMusic;

    private static final int HEAL_AMOUNT = 10;
    private static final int TILE_SIZE = 32;
    private static final float POWERUP_DURATION = 6f;

    private String currentMusicPath = "";

    public GameScreen(GrandSlamWarriorsGame game, GrandSlam slam, GameCharacter character) {
        this.game = game;
        this.currentSlam = slam;

        com.grandslamwarriors.game.data.CheckpointManager.reset();
        player = new Player(character, 100, 320);

        camera = new OrthographicCamera();
        viewport = new StretchViewport(
            GrandSlamWarriorsGame.V_WIDTH,
            GrandSlamWarriorsGame.V_HEIGHT,
            camera
        );

        uiViewport = new StretchViewport(
            GrandSlamWarriorsGame.V_WIDTH,
            GrandSlamWarriorsGame.V_HEIGHT,
            new OrthographicCamera()
        );
        uiStage = new Stage(uiViewport, game.batch);

        skin = new Skin();
        Pixmap whitePix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        whitePix.setColor(Color.WHITE);
        whitePix.fill();
        skin.add("white", new Texture(whitePix));
        whitePix.dispose();

        BitmapFont font = new BitmapFont();
        font.getData().setScale(1.5f);
        skin.add("default", font);
        skin.add("default", new Label.LabelStyle(font, Color.WHITE));

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.fontColor = Color.WHITE;
        skin.add("default", textButtonStyle, TextButton.TextButtonStyle.class);

        // Carrega Sons
        if (SettingsManager.isSoundEnabled()) {
            try {
                float sfxVol = SettingsManager.getSfxVolume();
                float musicVol = SettingsManager.getMusicVolume();

                startLevelSound = Gdx.audio.newSound(Gdx.files.internal("sounds/inicio_fase.wav"));
                collectBallSound = Gdx.audio.newSound(Gdx.files.internal("sounds/obtem_bola.mp3"));
                collectHealSound = Gdx.audio.newSound(Gdx.files.internal("sounds/obtem_liquido.mp3"));
                collectLifeSound = Gdx.audio.newSound(Gdx.files.internal("sounds/obtem_vida.mp3"));
                winSound = Gdx.audio.newSound(Gdx.files.internal("sounds/obtem_trofeu_fim_fase.mp3"));
                shootSound = Gdx.audio.newSound(Gdx.files.internal("sounds/bolada.mp3"));
                hitSound = Gdx.audio.newSound(Gdx.files.internal("sounds/raquetada.mp3"));
                powerupSound = Gdx.audio.newSound(Gdx.files.internal("sounds/obtem_liquido.mp3"));

                String prefix = getSlamPrefix();
                try {
                    doorSound = Gdx.audio.newSound(Gdx.files.internal("sounds/" + prefix + "_door.wav"));
                } catch (Exception e) {
                    doorSound = null;
                }

                try {
                    String musicPath = "sounds/" + prefix + "_music.wav";
                    levelMusic = Gdx.audio.newMusic(Gdx.files.internal(musicPath));
                    currentMusicPath = musicPath;
                } catch (Exception e) {
                    levelMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/intro_music.wav"));
                    currentMusicPath = "sounds/intro_music.wav";
                }
                levelMusic.setLooping(true);
                levelMusic.setVolume(musicVol);
                if (SettingsManager.isSoundEnabled()) {
                    levelMusic.play();
                }

                if (startLevelSound != null && SettingsManager.isSoundEnabled()) startLevelSound.play(sfxVol);
            } catch (Exception e) {
                Gdx.app.error("Audio", "Erro ao carregar sons", e);
            }
        }

        //backgroundTexture = new Texture(slam.bgPath);
        ballTexture = loadTransparentTexture("collectables/tennis_ball.png");

        String prefix = getSlamPrefix();

        item1Texture = loadTransparentTexture("collectables/" + prefix + "_item1.png");
        item2Texture = loadTransparentTexture("collectables/" + prefix + "_item2.png");
        levelTrophyTexture = loadTransparentTexture("collectables/" + prefix + "_trophy.png");
        lifeTrophyTexture = loadTransparentTexture("collectables/trophy.png");

        trapTexture = loadTransparentTexture("weapons/" + prefix + "_trap.png");

        doorTexture = loadTransparentTexture("doors/" + prefix + "_door.png");
        if (doorTexture == null) {
            // Tenta sem a pasta doors caso o asset esteja na raiz ou o carregamento falhe
            doorTexture = loadTransparentTexture(prefix + "_door.png");
        }

        if (doorTexture == null || doorTexture == placeholderTexture) {
            Pixmap pm = new Pixmap(32, 48, Pixmap.Format.RGBA8888);
            pm.setColor(new Color(0.2f, 0.8f, 0.2f, 0.8f)); // Verde porta
            pm.fill();
            doorTexture = new Texture(pm);
            pm.dispose();
        }

        doorKeyTexture = loadTransparentTexture("doors/" + prefix + "_key.png");
        if (doorKeyTexture == null) {
            doorKeyTexture = loadTransparentTexture(prefix + "_key.png");
        }

        if (doorKeyTexture == null || doorKeyTexture == placeholderTexture) {
            Pixmap pm = new Pixmap(12, 12, Pixmap.Format.RGBA8888);
            pm.setColor(Color.YELLOW);
            pm.fill();
            doorKeyTexture = new Texture(pm);
            pm.dispose();
        }

        tennisBalls = new Array<>();
        levelItems = new Array<>();
        enemies = new Array<>();
        bossProjectiles = new Array<>();
        projectiles = new Array<>();
        enemyProjectiles = new Array<>();
        enemyTextures = new HashMap<>();
        traps = new Array<>();
        movingPlatforms = new Array<>();
        doorPairs = new java.util.HashMap<>();
        doorEntryRects = new Array<>();
        doorExitRects = new Array<>();

        fireRainTimer = 0f;

        loadTileLevel(slam);
        spawnCollectibles();
        spawnEnemiesByTiles();
        ensureMinimumEnemiesPerSlam();
        float factor = SettingsManager.getDifficultyFactor();

        for (Enemy enemy : enemies) {
            enemy.setPlayer(player);
            enemy.setLevelSolids(levelRenderer.getSolidRects());
            enemy.setDifficultyFactor(factor);
        }
        getPlaceholderTexture();
        spawnTraps();

        // Carrega portas secretas do mapa (exemplo)
        loadDoors();

        // Carrega plataformas móveis (exemplo)
        loadMovingPlatforms();

        createUI();
        Gdx.input.setInputProcessor(uiStage);
    }

    private String getSlamPrefix() {
        if (currentSlam == GrandSlam.AUSTRALIAN_OPEN) return "aus";
        if (currentSlam == GrandSlam.ROLAND_GARROS) return "rg";
        if (currentSlam == GrandSlam.WIMBLEDON) return "wim";
        if (currentSlam == GrandSlam.US_OPEN) return "us";
        return "aus";
    }

    private void createPauseMenu() {
        pauseTable = new Table();
        pauseTable.setFillParent(true);
        pauseTable.setVisible(false);
        pauseTable.setBackground(skin.newDrawable("white", new Color(0, 0, 0, 0.7f)));

        Label pauseLabel = new Label("PAUSE", new Label.LabelStyle(skin.getFont("default"), Color.YELLOW));
        pauseLabel.setFontScale(2f);
        pauseTable.add(pauseLabel).padBottom(30).row();

        com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle sliderStyle = new com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle();
        Pixmap pix = new Pixmap(10, 10, Pixmap.Format.RGBA8888);
        pix.setColor(Color.WHITE);
        pix.fill();
        skin.add("slider_bg", new Texture(pix));
        pix.dispose();

        sliderStyle.background = skin.newDrawable("slider_bg", Color.DARK_GRAY);
        sliderStyle.knob = skin.newDrawable("slider_bg", Color.YELLOW);
        sliderStyle.knob.setMinWidth(20);
        sliderStyle.knob.setMinHeight(20);

        final com.badlogic.gdx.scenes.scene2d.ui.Slider musicSlider = new com.badlogic.gdx.scenes.scene2d.ui.Slider(0, 1, 0.1f, false, sliderStyle);
        musicSlider.setValue(SettingsManager.getMusicVolume());
        musicSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                float vol = musicSlider.getValue();
                SettingsManager.setMusicVolume(vol);
                if (levelMusic != null) {
                    levelMusic.setVolume(vol);
                    if (SettingsManager.isSoundEnabled()) {
                        if (!levelMusic.isPlaying()) levelMusic.play();
                    } else {
                        levelMusic.pause();
                    }
                }
            }
        });

        pauseTable.add(new Label("Music", skin)).padRight(10);
        pauseTable.add(musicSlider).width(200).row();

        final com.badlogic.gdx.scenes.scene2d.ui.Slider sfxSlider = new com.badlogic.gdx.scenes.scene2d.ui.Slider(0, 1, 0.1f, false, sliderStyle);
        sfxSlider.setValue(SettingsManager.getSfxVolume());
        sfxSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                SettingsManager.setSfxVolume(sfxSlider.getValue());
            }
        });

        pauseTable.add(new Label("Effects", skin)).padRight(10);
        pauseTable.add(sfxSlider).width(200).padTop(10).row();

        TextButton resumeBtn = new TextButton("RESUME", skin);
        resumeBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                togglePause();
            }
        });
        pauseTable.add(resumeBtn).width(200).height(60).padTop(30).row();

        TextButton quitBtn = new TextButton("QUIT", skin);
        quitBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                game.setScreen(new MenuScreen(game));
            }
        });
        pauseTable.add(quitBtn).width(200).height(60).padTop(10);

        uiStage.addActor(pauseTable);
    }

    private void togglePause() {
        paused = !paused;
        pauseTable.setVisible(paused);
    }

    private void createUI() {
        BitmapFont font = skin.getFont("default");

        TextButton.TextButtonStyle invisibleStyle = new TextButton.TextButtonStyle();
        invisibleStyle.font = font;
        invisibleStyle.fontColor = new Color(1, 1, 1, 0);

        TextButton.TextButtonStyle actionStyle = new TextButton.TextButtonStyle();
        actionStyle.font = font;
        actionStyle.fontColor = Color.WHITE;

        hpLabel = new Label("HP: 100", new Label.LabelStyle(font, Color.RED));
        livesLabel = new Label("LIVES: 3", new Label.LabelStyle(font, Color.GOLD));
        ammoLabel = new Label("BALLS: 0", new Label.LabelStyle(font, Color.GREEN));
        timerLabel = new Label("TIME: 120", new Label.LabelStyle(font, Color.WHITE));
        scoreLabel = new Label("SCORE: 0", new Label.LabelStyle(font, Color.CYAN));
        itemsLabel = new Label("ITEMS: 0", new Label.LabelStyle(font, Color.ORANGE));

        // --- WARNING LABEL (invisível inicialmente) ---
        warningLabel = new Label("", new Label.LabelStyle(font, Color.RED));
        warningLabel.setVisible(false);
        warningLabel.setFontScale(1.8f);
        warningLabel.setPosition(GrandSlamWarriorsGame.V_WIDTH / 2f - 300, GrandSlamWarriorsGame.V_HEIGHT / 2f + 30);
        uiStage.addActor(warningLabel);

        TextButton btnLeftArea = new TextButton("", invisibleStyle);
        TextButton btnRightArea = new TextButton("", invisibleStyle);

        float btnSize = 110f;
        TextButton btnJump = new TextButton("SERVE", actionStyle);
        TextButton btnDown = new TextButton("SPLIT STEP", actionStyle);
        TextButton btnHit = new TextButton("SLICE", actionStyle);  // ALTERADO
        TextButton btnFire = new TextButton("SMASH", actionStyle); // ALTERADO

        btnLeftArea.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent ev, float x, float y, int p, int b) {
                leftPressed = true;
                return true;
            }
            @Override
            public void touchUp(InputEvent ev, float x, float y, int p, int b) {
                leftPressed = false;
            }
        });

        btnRightArea.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent ev, float x, float y, int p, int b) {
                rightPressed = true;
                return true;
            }
            @Override
            public void touchUp(InputEvent ev, float x, float y, int p, int b) {
                rightPressed = false;
            }
        });

        btnJump.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent ev, float x, float y, int p, int b) {
                jumpRequested = true;
                return true;
            }
        });

        btnHit.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent ev, float x, float y, int p, int b) {
                attackRequested = true;
                return true;
            }
        });

        btnFire.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent ev, float x, float y, int p, int b) {
                shootRequested = true;
                return true;
            }
        });

        btnDown.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent ev, float x, float y, int p, int b) {
                downPressed = true;
                return true;
            }
            @Override
            public void touchUp(InputEvent ev, float x, float y, int p, int b) {
                downPressed = false;
            }
        });

        Table root = new Table();
        root.setFillParent(true);
        uiStage.addActor(root);

        createPauseMenu();

        Table hudTable = new Table();
        hudTable.add(hpLabel).padLeft(10).padRight(20);
        hudTable.add(livesLabel).padRight(20);
        hudTable.add(ammoLabel).padLeft(10).padRight(20);
        hudTable.add(itemsLabel).padRight(20);
        hudTable.add(timerLabel).padRight(20);

        keyIcon = new com.badlogic.gdx.scenes.scene2d.ui.Image(doorKeyTexture);
        keyIcon.setVisible(false);
        hudTable.add(keyIcon).size(16, 16).padRight(20);

        hudTable.add(scoreLabel).expandX().right().padRight(10);
        root.add(hudTable).expandX().fillX().top().padTop(10).row();

        Table controlLayer = new Table();
        controlLayer.add(btnLeftArea).expand().fill().left().width(GrandSlamWarriorsGame.V_WIDTH * 0.25f);

        Table actionPanel = new Table();
        actionPanel.add(btnJump).size(btnSize).pad(5);
        actionPanel.add(btnDown).size(btnSize).pad(5).row();
        actionPanel.add(btnHit).size(btnSize).pad(5);
        actionPanel.add(btnFire).size(btnSize).pad(5);

        controlLayer.add(actionPanel).expand().right().padRight(GrandSlamWarriorsGame.V_WIDTH * 0.15f);
        controlLayer.add(btnRightArea).expand().fill().right().width(GrandSlamWarriorsGame.V_WIDTH * 0.25f);

        root.add(controlLayer).expand().fill();
    }

    private void loadTileLevel(GrandSlam slam) {
        String tilesPath = null;
        TileType[][] levelMap = null;

        if (slam == GrandSlam.AUSTRALIAN_OPEN) {
            tilesPath = "tiles/aus-tiles.png";
            levelMap = AustralianLevelData.MAP;
        } else if (slam == GrandSlam.ROLAND_GARROS) {
            tilesPath = "tiles/rg-tiles.png";
            levelMap = RolandGarrosLevelData.MAP;
        } else if (slam == GrandSlam.WIMBLEDON) {
            tilesPath = "tiles/wim-tiles.png";
            levelMap = WimbledonLevelData.MAP;
        } else if (slam == GrandSlam.US_OPEN) {
            tilesPath = "tiles/us-tiles.png";
            levelMap = USOpenLevelData.MAP;
        }

        if (tilesPath != null && levelMap != null) {
            levelRenderer = new LevelRenderer(slam.bgPath, tilesPath, levelMap, TILE_SIZE);
            currentMap = levelMap;
        }
    }

    private void loadDoors() {
        if (currentMap == null) return;

        for (int y = 0; y < currentMap.length; y++) {
            for (int x = 0; x < currentMap[y].length; x++) {
                if (currentMap[y][x] == TileType.DOOR_ENTRY) {
                    // Porta com 64 de altura e largura proporcional ao seu desenho (32x64)
                    // Colocada no centro do tile
                    Rectangle entry = new Rectangle(x * TILE_SIZE, (currentMap.length - 1 - y) * TILE_SIZE, 32, 64);
                    doorEntryRects.add(entry);
                } else if (currentMap[y][x] == TileType.DOOR_EXIT) {
                    Rectangle exit = new Rectangle(x * TILE_SIZE, (currentMap.length - 1 - y) * TILE_SIZE, 32, 64);
                    doorExitRects.add(exit);
                }
            }
        }

        // Tenta parear as portas (supondo 1 entrada e 1 saída por nível por enquanto)
        if (doorEntryRects.size > 0 && doorExitRects.size > 0) {
            doorPairs.put(doorEntryRects.first(), doorExitRects.first());
        }
    }

    private void loadMovingPlatforms() {
        if (currentSlam == GrandSlam.AUSTRALIAN_OPEN) {
            // Plataforma sobre o primeiro grande buraco (x=15-19 -> aprox 480-608px)
            movingPlatforms.add(new MovingPlatform(450, 200, 650, 200, 64, 16, 80f));

            // Plataforma sobre o segundo grande buraco (x=45-50 -> aprox 1440-1600px)
            movingPlatforms.add(new MovingPlatform(1400, 250, 1650, 250, 80, 16, 100f));

            // Plataforma vertical para ajudar a subir em áreas altas
            movingPlatforms.add(new MovingPlatform(2500, 150, 2500, 350, 64, 16, 60f));
        } else if (currentSlam == GrandSlam.ROLAND_GARROS) {
            // Gap entre 25-29 (aprox 800-928px)
            movingPlatforms.add(new MovingPlatform(750, 220, 950, 220, 64, 16, 90f));
            // Gap entre 70-75
            movingPlatforms.add(new MovingPlatform(2100, 250, 2450, 250, 80, 16, 110f));
        } else if (currentSlam == GrandSlam.WIMBLEDON) {
            // Gap entre 30-35
            movingPlatforms.add(new MovingPlatform(900, 200, 1150, 200, 64, 16, 85f));
            // Gap entre 80-86
            movingPlatforms.add(new MovingPlatform(2500, 280, 2800, 280, 80, 16, 100f));
        } else if (currentSlam == GrandSlam.US_OPEN) {
            // Entre prédios (buracos de x % 25 < 5)
            movingPlatforms.add(new MovingPlatform(600, 240, 850, 240, 64, 16, 120f));
            movingPlatforms.add(new MovingPlatform(2400, 300, 2700, 300, 80, 16, 130f));
        }
    }

    private Rectangle getLastSolidRect() {
        if (levelRenderer == null || levelRenderer.getSolidRects().size == 0) return null;
        Rectangle best = null;
        float bestRight = -1f;
        for (Rectangle rect : levelRenderer.getSolidRects()) {
            float right = rect.x + rect.width;
            if (right > bestRight) {
                bestRight = right;
                best = rect;
            }
        }
        return best;
    }

    private float getLevelRightEdge() {
        Rectangle last = getLastSolidRect();
        if (last == null) return GrandSlamWarriorsGame.V_WIDTH;
        return last.x + last.width;
    }

    private float findGroundTopAtX(float worldX) {
        if (levelRenderer == null) return -1f;
        float bestY = -1f;
        for (Rectangle rect : levelRenderer.getSolidRects()) {
            if (worldX >= rect.x && worldX <= rect.x + rect.width) {
                float top = rect.y + rect.height;
                if (top > bestY) bestY = top;
            }
        }
        return bestY;
    }

    private float findGroundTopNearX(float worldX, float halfSearchWidth) {
        if (levelRenderer == null) return -1f;
        float bestY = -1f;
        float searchLeft = worldX - halfSearchWidth;
        float searchRight = worldX + halfSearchWidth;
        for (Rectangle rect : levelRenderer.getSolidRects()) {
            float left = rect.x;
            float right = rect.x + rect.width;
            if (right >= searchLeft && left <= searchRight) {
                float top = rect.y + rect.height;
                if (top > bestY) bestY = top;
            }
        }
        return bestY;
    }

    private boolean hasGroundNear(float centerX) {
        return findGroundTopNearX(centerX, 48f) >= 0f;
    }

    private float safeGroundY(float centerX, float fallbackY) {
        float y = findGroundTopNearX(centerX, 48f);
        if (y >= 0f) return y;
        y = findGroundTopAtX(centerX);
        if (y >= 0f) return y;
        return fallbackY;
    }

    private void spawnEndLevelTrophy() {
        Rectangle last = getLastSolidRect();
        if (last == null) {
            endLevelTrophy = new Rectangle(2000f, 220f, 38f, 38f);
            return;
        }
        float trophyWidth = 38f;
        float trophyHeight = 38f;
        float trophyX = last.x + last.width - trophyWidth - 24f;
        float trophyY = last.y + last.height + 8f;
        endLevelTrophy = new Rectangle(trophyX, trophyY, trophyWidth, trophyHeight);
    }

    private void spawnCollectibles() {
        float levelRight = getLevelRightEdge();
        for (int i = 0; i < 20; i++) {
            float x = 500 + i * 450;
            if (x + 10f > levelRight - 64f) break;
            if (!hasGroundNear(x + 5f)) continue;
            float y = safeGroundY(x + 5f, 180f) + 20f;
            tennisBalls.add(new Rectangle(x, y, 10f, 10f));
        }
        for (int i = 0; i < 12; i++) {
            float x = 800 + i * 800;
            if (x + 32f > levelRight - 96f) break;
            if (!hasGroundNear(x + 16f)) continue;
            float y = safeGroundY(x + 16f, 220f) + 24f;
            levelItems.add(new Rectangle(x, y, 32f, 32f));
        }
        spawnEndLevelTrophy();
        Rectangle targetBlock = null;
        switch (currentSlam) {
            case AUSTRALIAN_OPEN: targetBlock = findSolidBlockAt(60f * 32f, 192f); break;
            case ROLAND_GARROS:   targetBlock = findSolidBlockAt(90f * 32f, 128f); break;
            case WIMBLEDON:       targetBlock = findSolidBlockAt(60f * 32f, 160f); break;
            case US_OPEN:         targetBlock = findSolidBlockAt(40f * 32f, 192f); break;
        }
        if (targetBlock != null) {
            float trophyX = targetBlock.x + (targetBlock.width - 48f) / 2f;
            float trophyY = targetBlock.y + targetBlock.height + 8f;
            lifeTrophy = new Rectangle(trophyX, trophyY, 48f, 48f);
        } else {
            for (Rectangle rect : levelRenderer.getSolidRects()) {
                if (rect.x > 500 && rect.x < getLevelRightEdge() - 500) {
                    lifeTrophy = new Rectangle(rect.x + 8f, rect.y + rect.height + 8f, 48f, 48f);
                    break;
                }
            }
        }
        if (lifeTrophy == null) lifeTrophy = new Rectangle(1500f, 280f, 48f, 48f);

        // Spawn da chave da porta secreta: Procura um lugar sólido com espaço em cima
        for (int i = levelRenderer.getSolidRects().size - 1; i >= 0; i--) {
            Rectangle rect = levelRenderer.getSolidRects().get(i);
            // Verifica se está na faixa de distância e se NÃO tem teto logo acima (espaço de pelo menos 2 blocos)
            if (rect.x > 1000 && rect.x < getLevelRightEdge() - 1200) {
                boolean hasCeiling = false;
                for (Rectangle other : levelRenderer.getSolidRects()) {
                    if (Math.abs(other.x - rect.x) < 5f && other.y > rect.y && other.y < rect.y + 100f) {
                        hasCeiling = true;
                        break;
                    }
                }
                if (!hasCeiling) {
                    doorKeyRect = new Rectangle(rect.x + (rect.width - 12f)/2f, rect.y + rect.height + 15f, 24f, 24f);
                    break;
                }
            }
        }
        if (doorKeyRect == null) doorKeyRect = new Rectangle(1000f, 400f, 12f, 12f);

        // Conta apenas itens de VIDA (troféus e itens especiais) para obrigatoriedade
        itemsToCollect = levelItems.size + (lifeTrophy != null ? 1 : 0);
    }

    private Rectangle findSolidBlockAt(float worldX, float worldY) {
        for (Rectangle rect : levelRenderer.getSolidRects()) {
            if (Math.abs(rect.x - worldX) < 2f && Math.abs(rect.y - worldY) < 2f) return rect;
        }
        return null;
    }

    private void spawnTraps() {
        traps.clear();
        if (currentMap == null || levelRenderer == null) return;
        int rows = currentMap.length;
        int cols = currentMap[0].length;
        int targetCount;
        switch (currentSlam) {
            case AUSTRALIAN_OPEN: targetCount = 5; break;
            case ROLAND_GARROS:   targetCount = 9; break;
            case WIMBLEDON:       targetCount = 14; break;
            case US_OPEN:         targetCount = 20; break;
            default:              targetCount = 5;
        }
        if (SettingsManager.getDifficulty() == 1) {
            targetCount = (int)(targetCount * 1.5f);
        }
        Array<Rectangle> solidPositions = new Array<>();
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                TileType type = currentMap[y][x];
                if (type != null && type.solid) {
                    float worldX = x * TILE_SIZE;
                    float worldY = (rows - 1 - y) * TILE_SIZE;
                    solidPositions.add(new Rectangle(worldX, worldY, TILE_SIZE, TILE_SIZE));
                }
            }
        }
        solidPositions.shuffle();
        int placed = 0;
        for (Rectangle rect : solidPositions) {
            if (placed >= targetCount) break;
            if (rect.x < 200 || rect.x > getLevelRightEdge() - 200) continue;
            traps.add(new Trap(rect.x, rect.y, TILE_SIZE, TILE_SIZE, trapTexture));
            placed++;
        }
    }

    private void spawnEnemiesByTiles() {
        if (levelRenderer == null) return;
        TileType[][] currentMap = null;
        switch (currentSlam) {
            case AUSTRALIAN_OPEN: currentMap = AustralianLevelData.MAP; break;
            case ROLAND_GARROS: currentMap = RolandGarrosLevelData.MAP; break;
            case WIMBLEDON: currentMap = WimbledonLevelData.MAP; break;
            case US_OPEN: currentMap = USOpenLevelData.MAP; break;
        }
        if (currentMap == null) return;
        for (Rectangle rect : levelRenderer.getSpawnRects()) {
            int tx = (int) (rect.x / TILE_SIZE);
            int ty = (int) (rect.y / TILE_SIZE);
            int rows = currentMap.length;
            int mapY = rows - 1 - ty;
            if (mapY < 0 || mapY >= rows || tx < 0 || tx >= currentMap[0].length) continue;
            TileType type = currentMap[mapY][tx];
            if (type == null) continue;
            float spawnX = rect.x;
            float spawnCenterX = spawnX + TILE_SIZE / 2f;
            if (!hasGroundNear(spawnCenterX) && type != TileType.SPAWN_RAT) continue;
            float spawnY = safeGroundY(spawnCenterX, rect.y);
            switch (type) {
                case SPAWN_FLYING:
                    Texture flyingTex = getEnemyTexture("flying");
                    Texture fireTex = getEnemyTexture("fire");
                    switch (currentSlam) {
                        case AUSTRALIAN_OPEN: enemies.add(new AUS_Flying(spawnX, spawnY + 120f, flyingTex, fireTex)); break;
                        case ROLAND_GARROS:   enemies.add(new RG_Flying(spawnX, spawnY + 120f, flyingTex, fireTex)); break;
                        case WIMBLEDON:       enemies.add(new WIM_Flying(spawnX, spawnY + 120f, flyingTex, fireTex)); break;
                        case US_OPEN:         enemies.add(new US_Flying(spawnX, spawnY + 120f, flyingTex, fireTex)); break;
                    }
                    break;
                case SPAWN_KANGAROO:   enemies.add(new AUS_Enemy1(spawnX, spawnY, getEnemyTexture("kangaroo"))); break;
                case SPAWN_CROCODILE:  enemies.add(new AUS_Enemy2(spawnX, spawnY, getEnemyTexture("crocodile"))); break;
                case SPAWN_GOBLIN:     enemies.add(new RG_Enemy1(spawnX, spawnY, getEnemyTexture("goblin"))); break;
                case SPAWN_CLAY_MONSTER: enemies.add(new RG_Enemy2(spawnX, spawnY, getEnemyTexture("clay_monster"))); break;
                case SPAWN_GRASS_CUTTER: enemies.add(new WIM_Enemy2(spawnX, spawnY, getEnemyTexture("grass_cutter"))); break;
                case SPAWN_QUEEN_GUARD:  enemies.add(new WIM_Enemy1(spawnX, spawnY, getEnemyTexture("queen_guard"))); break;
                case SPAWN_RAT:        enemies.add(new US_Enemy1(spawnX, spawnY, getEnemyTexture("sewer_gas"))); break;
                case SPAWN_SKYSCRAPER:  enemies.add(new US_Enemy2(spawnX, spawnY, getEnemyTexture("skyscraper"))); break;
                case SPAWN_BOSS:
                    String pfx = getSlamPrefix();
                    Texture bossTex = getEnemyTexture(pfx + "_boss");
                    Texture prjTex = game.assetManager.get("weapons/" + pfx + "_boss_fire.png", Texture.class);
                    Boss bossObj = null;
                    switch(currentSlam) {
                        case AUSTRALIAN_OPEN: bossObj = new com.grandslamwarriors.game.entities.enemies.AUS_Boss(spawnX, spawnY, bossTex, prjTex, bossProjectiles); break;
                        case ROLAND_GARROS: bossObj = new com.grandslamwarriors.game.entities.enemies.RG_Boss(spawnX, spawnY, bossTex, prjTex, bossProjectiles); break;
                        case WIMBLEDON: bossObj = new com.grandslamwarriors.game.entities.enemies.WIM_Boss(spawnX, spawnY, bossTex, prjTex, bossProjectiles); break;
                        case US_OPEN: bossObj = new com.grandslamwarriors.game.entities.enemies.US_Boss(spawnX, spawnY, bossTex, prjTex, bossProjectiles); break;
                    }
                    if (bossObj != null) {
                        bossObj.setPlayer(player);
                        bossObj.setLevelSolids(levelRenderer.getSolidRects());
                        bossObj.setDifficultyFactor(com.grandslamwarriors.game.data.SettingsManager.getDifficultyFactor());
                        enemies.add(bossObj);
                    }
                    break;
            }
        }
    }

    private void ensureMinimumEnemiesPerSlam() {
        float levelRight = getLevelRightEdge();
        switch (currentSlam) {
            case AUSTRALIAN_OPEN:
                tryAddEnemy(new AUS_Enemy1(900, safeGroundY(932, 220), getEnemyTexture("kangaroo")), 932, levelRight);
                tryAddEnemy(new AUS_Enemy2(2600, safeGroundY(2632, 220), getEnemyTexture("crocodile")), 2632, levelRight);
                break;
            case ROLAND_GARROS:
                tryAddEnemy(new RG_Enemy1(1000, safeGroundY(1032, 220), getEnemyTexture("goblin")), 1032, levelRight);
                tryAddEnemy(new RG_Enemy2(2800, safeGroundY(2832, 220), getEnemyTexture("clay_monster")), 2832, levelRight);
                break;
            case WIMBLEDON:
                tryAddEnemy(new WIM_Enemy1(1100, safeGroundY(1132, 220), getEnemyTexture("queen_guard")), 1132, levelRight);
                tryAddEnemy(new WIM_Enemy2(3000, safeGroundY(3032, 220), getEnemyTexture("grass_cutter")), 3032, levelRight);
                break;
            case US_OPEN:
                tryAddEnemy(new US_Enemy1(1200, safeGroundY(1232, 220), getEnemyTexture("sewer_gas")), 1232, levelRight);
                tryAddEnemy(new US_Enemy2(2800, safeGroundY(2832, 220), getEnemyTexture("skyscraper")), 2832, levelRight);
                break;
        }

        // Modo difícil: inimigos extras
        if (SettingsManager.getDifficulty() == 1) {
            switch (currentSlam) {
                case AUSTRALIAN_OPEN:
                    tryAddEnemy(new AUS_Enemy1(1500, safeGroundY(1532, 220), getEnemyTexture("kangaroo")), 1532, levelRight);
                    tryAddEnemy(new AUS_Enemy2(3800, safeGroundY(3832, 220), getEnemyTexture("crocodile")), 3832, levelRight);
                    tryAddEnemy(new AUS_Enemy1(4300, safeGroundY(4332, 220), getEnemyTexture("kangaroo")), 4332, levelRight);
                    break;
                case ROLAND_GARROS:
                    tryAddEnemy(new RG_Enemy1(2000, safeGroundY(2032, 220), getEnemyTexture("goblin")), 2032, levelRight);
                    tryAddEnemy(new RG_Enemy2(4000, safeGroundY(4032, 220), getEnemyTexture("clay_monster")), 4032, levelRight);
                    tryAddEnemy(new RG_Enemy1(4600, safeGroundY(4632, 220), getEnemyTexture("goblin")), 4632, levelRight);
                    break;
                case WIMBLEDON:
                    tryAddEnemy(new WIM_Enemy1(2000, safeGroundY(2032, 220), getEnemyTexture("queen_guard")), 2032, levelRight);
                    tryAddEnemy(new WIM_Enemy2(3900, safeGroundY(3932, 220), getEnemyTexture("grass_cutter")), 3932, levelRight);
                    tryAddEnemy(new WIM_Enemy1(4900, safeGroundY(4932, 220), getEnemyTexture("queen_guard")), 4932, levelRight);
                    break;
                case US_OPEN:
                    tryAddEnemy(new US_Enemy1(1900, safeGroundY(1932, 220), getEnemyTexture("sewer_gas")), 1932, levelRight);
                    tryAddEnemy(new US_Enemy2(3700, safeGroundY(3732, 220), getEnemyTexture("skyscraper")), 3732, levelRight);
                    tryAddEnemy(new US_Enemy1(4900, safeGroundY(4932, 220), getEnemyTexture("sewer_gas")), 4932, levelRight);
                    break;
            }
        }
    }

    private void tryAddEnemy(Enemy enemy, float centerX, float levelRight) {
        if (centerX < levelRight - 96f && hasGroundNear(centerX)) {
            enemy.setPlayer(player);
            enemy.setLevelSolids(levelRenderer.getSolidRects());
            enemies.add(enemy);
        }
    }

    private Texture getEnemyTexture(String key) {
        if (enemyTextures.containsKey(key)) return enemyTextures.get(key);
        String path = null;
        switch (currentSlam) {
            case AUSTRALIAN_OPEN:
                if (key.equals("kangaroo")) path = "enemies/aus_enemy1.png";
                else if (key.equals("crocodile")) path = "enemies/aus_enemy2.png";
                else if (key.equals("flying")) path = "enemies/aus_enemy3.png";
                else if (key.equals("fire")) path = "weapons/aus_fire.png";
                else if (key.equals("aus_boss")) path = "enemies/aus_boss.png";
                break;
            case ROLAND_GARROS:
                if (key.equals("goblin")) path = "enemies/rg_enemy1.png";
                else if (key.equals("clay_monster")) path = "enemies/rg_enemy2.png";
                else if (key.equals("flying")) path = "enemies/rg_enemy3.png";
                else if (key.equals("fire")) path = "weapons/rg_fire.png";
                else if (key.equals("rg_boss")) path = "enemies/rg_boss.png";
                break;
            case WIMBLEDON:
                if (key.equals("grass_cutter")) path = "enemies/wim_enemy2.png";
                else if (key.equals("queen_guard")) path = "enemies/wim_enemy1.png";
                else if (key.equals("flying")) path = "enemies/wim_enemy3.png";
                else if (key.equals("fire")) path = "weapons/wim_fire.png";
                else if (key.equals("wim_boss")) path = "enemies/wim_boss.png";
                break;
            case US_OPEN:
                if (key.equals("sewer_gas")) path = "enemies/us_enemy1.png";
                else if (key.equals("skyscraper")) path = "enemies/us_enemy2.png";
                else if (key.equals("flying")) path = "enemies/us_enemy3.png";
                else if (key.equals("fire")) path = "weapons/us_fire.png";
                else if (key.equals("us_boss")) path = "enemies/us_boss.png";
                break;
        }
        Texture tex;
        try {
            if (path != null) {
                Pixmap raw = new Pixmap(Gdx.files.internal(path));
                Pixmap processed = makeTransparent(raw);
                tex = new Texture(processed);
                raw.dispose();
                processed.dispose();
            } else {
                tex = getPlaceholderTexture();
            }
        } catch (Exception e) {
            tex = getPlaceholderTexture();
        }
        enemyTextures.put(key, tex);
        return tex;
    }

    private Texture loadTransparentTexture(String path) {
        try {
            if (!Gdx.files.internal(path).exists()) return null;
            Pixmap raw = new Pixmap(Gdx.files.internal(path));
            Pixmap processed = makeTransparent(raw);
            Texture tex = new Texture(processed);
            raw.dispose();
            processed.dispose();
            return tex;
        } catch (Exception e) {
            return null;
        }
    }

    private Pixmap makeTransparent(Pixmap source) {
        Pixmap result = new Pixmap(source.getWidth(), source.getHeight(), Pixmap.Format.RGBA8888);
        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                int color = source.getPixel(x, y);
                int r = (color >>> 24) & 0xff;
                int g = (color >>> 16) & 0xff;
                int b = (color >>> 8) & 0xff;
                if (r > 230 && g > 230 && b > 230) result.drawPixel(x, y, 0x00000000);
                else result.drawPixel(x, y, color);
            }
        }
        return result;
    }

    private Texture getPlaceholderTexture() {
        if (placeholderTexture == null) {
            Pixmap pm = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
            pm.setColor(Color.MAGENTA);
            pm.fill();
            placeholderTexture = new Texture(pm);
            pm.dispose();
        }
        return placeholderTexture;
    }

    private void handleInput() {
        if (paused) return;

        // Sprint (Shift)
        boolean sprint = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);
        player.setSprint(sprint);

        // Aim (Ctrl)
        boolean aim = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT);
        player.setAim(aim);

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A) || leftPressed) {
            player.moveLeft();
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D) || rightPressed) {
            player.moveRight();
        } else {
            player.stop();
        }
        player.crouch(Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S) || downPressed);
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || jumpRequested) {
            player.jump();
            jumpRequested = false;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.Z) || attackRequested) {
            if (hitSound != null && SettingsManager.isSoundEnabled()) hitSound.play(SettingsManager.getSfxVolume());
            player.meleeAttack();
            attackRequested = false;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.X) || shootRequested) {
            if (player.tennisBallsCollected > 0) {
                if (shootSound != null && SettingsManager.isSoundEnabled()) shootSound.play(SettingsManager.getSfxVolume());
                float dir = player.facingRight ? 1f : -1f;
                float spawnX = player.facingRight ? player.pos.x + 40f : player.pos.x - 8f;
                float spawnY = player.pos.y + 26f;
                projectiles.add(new TennisBallProjectile(ballTexture, spawnX, spawnY, dir));
                player.tennisBallsCollected--;
            }
            shootRequested = false;
        }
    }

    private void updatePlayerAndCollisions(float delta) {
        if (paused) return;
        float oldX = player.pos.x;
        float oldY = player.pos.y;
        player.update(delta);
        player.grounded = false;

        // --- Colisão com Plataformas Móveis ---
        for (MovingPlatform mp : movingPlatforms) {
            if (player.bounds.overlaps(mp.getBounds()) && player.vel.y <= 0) {
                if (oldY >= mp.getBounds().y + mp.getBounds().height - 5f) {
                    player.pos.y = mp.getBounds().y + mp.getBounds().height;
                    player.vel.y = 0f;
                    player.grounded = true;
                    // Move o jogador junto com a plataforma
                    player.pos.x += mp.getVelocityX() * delta;
                    player.pos.y += mp.getVelocityY() * delta;
                }
            }
        }

        // Verifica portas secretas
        for (int i = 0; i < doorEntryRects.size; i++) {
            Rectangle entry = doorEntryRects.get(i);

            // Área de detecção um pouco maior para facilitar a entrada, mesmo perto de paredes
            Rectangle detectRect = new Rectangle(entry.x - 12f, entry.y, entry.width + 24f, entry.height);

            if (doorCooldown <= 0f &&
                player.getBounds().overlaps(detectRect)) {

                // Exige que o jogador tente "entrar" na porta (Jump/UP ou Crouch/DOWN)
                boolean interactionPressed = Gdx.input.isKeyJustPressed(Input.Keys.UP) ||
                                           Gdx.input.isKeyJustPressed(Input.Keys.W) ||
                                           Gdx.input.isKeyJustPressed(Input.Keys.SPACE) ||
                                           jumpRequested ||
                                           Gdx.input.isKeyJustPressed(Input.Keys.DOWN) ||
                                           Gdx.input.isKeyJustPressed(Input.Keys.S) ||
                                           downPressed;

                if (interactionPressed) {
                    if (hasKey) {
                        Rectangle exit = doorPairs.get(entry);

                        if (exit != null) {
                            if (doorSound != null && SettingsManager.isSoundEnabled()) {
                                doorSound.play(SettingsManager.getSfxVolume());
                            }

                            player.pos.x = exit.x + exit.width / 2f - player.bounds.width / 2f - 12f;
                            player.pos.y = exit.y;

                            player.vel.set(0, 0);
                            player.grounded = false;
                            player.updateBounds();

                            // Atualiza oldX e oldY para evitar empurrões errados na colisão logo abaixo
                            oldX = player.pos.x;
                            oldY = player.pos.y;

                            doorCooldown = 1.5f;
                            jumpRequested = false; // Consome o pulo para não pular ao sair da porta
                        }
                    } else {
                        if (warningTimer <= 0f) {
                            showWarning("COLLECT THE KEY FIRST!");
                        }
                    }
                }

                break;
            }
        }

        for (Rectangle solid : levelRenderer.getSolidRects()) {
            if (!player.bounds.overlaps(solid)) continue;
            Rectangle overlap = new Rectangle();
            IntersectorUtil.computeOverlap(player.bounds, solid, overlap);
            if (overlap.width > 4f && overlap.height < 12f) {
                if (oldY >= solid.y + solid.height - 5f) {
                    player.pos.y = solid.y + solid.height;
                    player.vel.y = 0f;
                    player.grounded = true;
                } else if (oldY + player.bounds.height <= solid.y + 5f) {
                    player.pos.y = solid.y - player.bounds.height;
                    player.vel.y = 0f;
                }
            } else if (overlap.height > 4f) {
                if (oldX + player.bounds.width <= solid.x + 5f) player.pos.x = solid.x - player.bounds.width - 12f;
                else if (oldX >= solid.x + solid.width - 17f) player.pos.x = solid.x + solid.width - 12f;
                player.vel.x = 0f;
            }
            player.getBounds().setPosition(player.pos.x + 12f, player.pos.y);
        }
        if (player.pos.y < -150f) player.takeDamage(999);


    }

    private void updateEnemies(float delta) {
        if (paused) return;
        for (Enemy enemy : enemies) {
            enemy.update(delta);
            if (enemy instanceof FlyingEnemy) {
                FlyingEnemy fe = (FlyingEnemy) enemy;
                if (fe.checkAndResetFire()) enemyProjectiles.add(new FireProjectile(fe.getFireTexture(), fe.getPosition().x, fe.getPosition().y));
            }
        }
        for (int i = enemies.size - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            if (enemy.isDead()) { player.score += 100; enemies.removeIndex(i); continue; }
            if (enemy.getBounds().overlaps(player.getBounds()) && !player.isPowerupActive && player.invincibilityTimer <= 0f) {
                player.takeDamage(20, enemy.getPosition().x, 220f, 220f);
            }
            if (player.meleeActive) {
                Rectangle meleeRect = player.facingRight ? new Rectangle(player.pos.x + 34f, player.pos.y + 8f, 36f, 40f) : new Rectangle(player.pos.x - 18f, player.pos.y + 8f, 36f, 40f);
                if (meleeRect.overlaps(enemy.getBounds())) enemy.damage(1);
            }
        }
        for (int i = enemyProjectiles.size - 1; i >= 0; i--) {
            FireProjectile fp = enemyProjectiles.get(i);
            fp.update(delta);
            if (!fp.active) { enemyProjectiles.removeIndex(i); continue; }
            if (fp.bounds.overlaps(player.getBounds()) && !player.isPowerupActive && player.invincibilityTimer <= 0f) {
                player.takeDamage(15, fp.pos.x, 150f, 150f);
                fp.active = false;
                enemyProjectiles.removeIndex(i);
            }
        }
    }

    private void updateBossProjectiles(float delta) {
        if (paused) return;
        for (int i = bossProjectiles.size - 1; i >= 0; i--) {
            com.grandslamwarriors.game.entities.projectiles.BossProjectile bp = bossProjectiles.get(i);
            bp.update(delta);
            if (bp.bounds.overlaps(player.getBounds())) {
                player.takeDamage(15);
                bp.active = false;
            }
            if (!bp.active) bossProjectiles.removeIndex(i);
        }
    }

    private void updateProjectiles(float delta) {
        if (paused) return;
        for (int i = projectiles.size - 1; i >= 0; i--) {
            TennisBallProjectile p = projectiles.get(i);
            p.update(delta);
            boolean remove = false;
            for (Rectangle solid : levelRenderer.getSolidRects()) { if (p.getBounds().overlaps(solid)) { remove = true; break; } }
            if (!remove) {
                for (Enemy enemy : enemies) { if (!enemy.isDead() && p.getBounds().overlaps(enemy.getBounds())) { enemy.damage(1); remove = true; break; } }
            }
            if (remove || p.getBounds().x > getLevelRightEdge() + 200f || p.getBounds().x < -200f) projectiles.removeIndex(i);
        }
    }

    private void updateCollectibles() {
        if (paused) return;
        float sfxVol = SettingsManager.getSfxVolume();
        boolean soundEnabled = SettingsManager.isSoundEnabled();

        for (int i = tennisBalls.size - 1; i >= 0; i--) {
            if (player.getBounds().overlaps(tennisBalls.get(i))) {
                if (collectBallSound != null && soundEnabled) collectBallSound.play(sfxVol);
                player.collectTennisBall();
                tennisBalls.removeIndex(i);
                // Não desconta mais de itemsToCollect (munição é opcional)
            }
        }
        for (int i = levelItems.size - 1; i >= 0; i--) {
            if (player.getBounds().overlaps(levelItems.get(i))) {
                if (collectHealSound != null && soundEnabled) collectHealSound.play(sfxVol);
                player.heal(HEAL_AMOUNT);
                levelItems.removeIndex(i);
                itemsToCollect--;
                if (itemsToCollect <= 0) allCollected = true;
            }
        }
        if (lifeTrophy != null && player.getBounds().overlaps(lifeTrophy)) {
            if (collectLifeSound != null && soundEnabled) collectLifeSound.play(sfxVol);
            player.lives++;
            player.activatePowerup(POWERUP_DURATION);
            com.grandslamwarriors.game.data.CheckpointManager.setCheckpoint(lifeTrophy.x, lifeTrophy.y);
            lifeTrophy = null;
            itemsToCollect--;
            if (itemsToCollect <= 0) allCollected = true;
        }

        if (doorKeyRect != null && player.getBounds().overlaps(doorKeyRect)) {
            if (collectBallSound != null && soundEnabled) collectBallSound.play(sfxVol);
            hasKey = true;
            doorKeyRect = null;
            showWarning("KEY COLLECTED!");
        }
        if (allCollected && endLevelTrophy == null) {
            boolean bossAlive = false;
            for (Enemy e : enemies) {
                if (e instanceof Boss && !e.isDead()) {
                    bossAlive = true;
                    break;
                }
            }
            if (!bossAlive) spawnEndLevelTrophy();
        }

        if (endLevelTrophy != null && player.getBounds().overlaps(endLevelTrophy)) {
            if (allCollected) {
                if (winSound != null && soundEnabled) winSound.play(sfxVol);
                game.setScreen(new VictoryScreen(game, currentSlam, player.character, player.score));
            } else {
                showWarning("You need to collect all items first!");
            }
        }
    }

    private void showWarning(String message) {
        warningLabel.setText(message);
        warningLabel.setVisible(true);
        warningTimer = 2.5f;
        showingWarning = true;
    }

    private void updateWarning(float delta) {
        if (showingWarning) {
            warningTimer -= delta;
            if (warningTimer <= 0) {
                warningLabel.setVisible(false);
                showingWarning = false;
            }
        }
    }

    private void updateTraps(float delta) {
        if (paused) return;
        for (Trap trap : traps) {
            trap.update(delta);
            if (trap.canDamage(player.getBounds()) && !player.isPowerupActive) player.takeDamage(20, trap.getX(), 50f, 80f);
        }
    }

    private void updateMovingPlatforms(float delta) {
        if (paused) return;
        for (MovingPlatform mp : movingPlatforms) {
            mp.update(delta);
        }
    }

    private void updateCamera() {
        float levelRight = getLevelRightEdge();
        float halfViewport = viewport.getWorldWidth() * 0.5f;
        float camX = MathUtils.clamp(player.pos.x + 20f, halfViewport, Math.max(halfViewport, levelRight - halfViewport));
        float camY = viewport.getWorldHeight() * 0.5f;
        camera.position.set(camX, camY, 0f);
        camera.update();
    }

    private void updateHud() {
        hpLabel.setText("HP: " + player.health);
        livesLabel.setText("LIVES: " + player.lives);
        ammoLabel.setText("AMMO: " + player.tennisBallsCollected);
        timerLabel.setText("TIME: " + (int)levelTimer);
        scoreLabel.setText("SCORE: " + player.score);

        // Verifica se há chefes vivos
        boolean bossAlive = false;
        Boss currentBoss = null;
        for (Enemy e : enemies) {
            if (e instanceof Boss && !e.isDead()) {
                bossAlive = true;
                currentBoss = (Boss) e;
                break;
            }
        }

        // --- LÓGICA DE MÚSICA DO CHEFE ---
        if (bossAlive && currentBoss != null) {
            float dist = Math.abs(currentBoss.getPosition().x - player.pos.x);
            if (dist < 600f) {
                if (levelMusic != null && !currentMusicPath.contains("intro_music.wav")) {
                    levelMusic.stop();
                    try {
                        currentMusicPath = "sounds/intro_music.wav";
                        levelMusic = Gdx.audio.newMusic(Gdx.files.internal(currentMusicPath));
                        levelMusic.setLooping(true);
                        levelMusic.setVolume(SettingsManager.getMusicVolume());
                        levelMusic.play();
                    } catch (Exception ignored) {}
                }
            }
        }

        // --- LÓGICA DE ÁUDIO DO POWER-UP ---
        if (player.isPowerupActive) {
            if (powerupSoundId == -1 && powerupSound != null && SettingsManager.isSoundEnabled()) {
                powerupSoundId = powerupSound.loop(SettingsManager.getSfxVolume() * 0.5f);
                if (levelMusic != null) levelMusic.setVolume(Math.min(1.0f, SettingsManager.getMusicVolume() * 1.5f));
            }
        } else {
            if (powerupSoundId != -1) {
                if (powerupSound != null) powerupSound.stop(powerupSoundId);
                powerupSoundId = -1;
                if (levelMusic != null) levelMusic.setVolume(SettingsManager.getMusicVolume());
            }
        }

        if (!allCollected) {
            itemsLabel.setText("ITEMS: " + itemsToCollect);
            itemsLabel.setColor(Color.ORANGE);
        } else if (bossAlive) {
            itemsLabel.setText("DEFEAT THE BOSS!");
            itemsLabel.setColor(Color.RED);
        } else {
            itemsLabel.setText("TROPHY READY!");
            itemsLabel.setColor(Color.YELLOW);
        }

        if (keyIcon != null) keyIcon.setVisible(hasKey);
    }

    @Override
    public void render(float delta) {
        doorAnimTime += delta;

        if (doorCooldown > 0f) {
            doorCooldown -= delta;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) togglePause();
        if (!paused) {
            if (player.lives <= 0) {
                game.setScreen(new GameOverScreen(game, currentSlam.nome, player.character.getConfig().displayName, player.character.getConfig().id, player.score));
                return;
            }
            handleInput();
            updatePlayerAndCollisions(delta);
            updateEnemies(delta);
            updateBossProjectiles(delta);
            updateProjectiles(delta);
            updateCollectibles();
            updateTraps(delta);
            updateMovingPlatforms(delta);
            updateCamera();
            updateHud();
            updateWarning(delta);
            doorAnimTime += delta;

            levelTimer -= delta;
            if (!timerExpired && levelTimer <= 0) {
                timerExpired = true;
                game.setScreen(new GameOverScreen(game, currentSlam.nome, player.character.getConfig().displayName, player.character.getConfig().id, player.score));
                return;
            }
        }
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        viewport.apply();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        if (levelRenderer != null) levelRenderer.render(game.batch, camera);

        // --- DESENHA PORTAS SECRETAS ---
        if (doorTexture != null) {
            game.batch.setColor(Color.WHITE); // Remove qualquer efeito de cor ou transparência
            for (Rectangle entry : doorEntryRects) {
                game.batch.draw(doorTexture, entry.x, entry.y, entry.width, entry.height);
            }
            for (Rectangle exit : doorExitRects) {
                game.batch.draw(doorTexture, exit.x, exit.y, exit.width, exit.height);
            }
        }

        // --- DESENHA CHAVE ---
        if (doorKeyRect != null && doorKeyTexture != null) {
            game.batch.draw(doorKeyTexture, doorKeyRect.x, doorKeyRect.y, doorKeyRect.width, doorKeyRect.height);
        }

        for (Rectangle ball : tennisBalls) game.batch.draw(ballTexture, ball.x, ball.y, ball.width, ball.height);
        for (Rectangle item : levelItems) game.batch.draw(item1Texture, item.x, item.y, item.width, item.height);

        for (com.grandslamwarriors.game.entities.projectiles.BossProjectile bp : bossProjectiles) {
            bp.draw(game.batch);
        }

        if (lifeTrophy != null) game.batch.draw(lifeTrophyTexture, lifeTrophy.x, lifeTrophy.y, lifeTrophy.width, lifeTrophy.height);
        if (endLevelTrophy != null) game.batch.draw(levelTrophyTexture, endLevelTrophy.x, endLevelTrophy.y, endLevelTrophy.width, endLevelTrophy.height);

        // --- DESENHA PLATAFORMAS MÓVEIS ---
        if (levelRenderer != null) {
            TextureRegion platformRegion = levelRenderer.getPlatformTexture();
            if (platformRegion != null) {
                for (MovingPlatform mp : movingPlatforms) {
                    game.batch.draw(platformRegion, mp.getBounds().x, mp.getBounds().y, mp.getBounds().width, mp.getBounds().height);
                }
            }
        }

        for (Trap trap : traps) trap.draw(game.batch);
        for (Enemy enemy : enemies) enemy.draw(game.batch);
        for (FireProjectile fp : enemyProjectiles) fp.draw(game.batch);
        for (TennisBallProjectile projectile : projectiles) projectile.draw(game.batch);
        drawPlayer();
        game.batch.end();
        uiViewport.apply();
        uiStage.act(delta);
        uiStage.draw();
    }

    private void drawPlayer() {
        if (player.invincibilityTimer > 0 && (int)(player.invincibilityTimer * 10) % 2 == 0) return;
        TextureRegion currentFrame = player.character.getAnimFrame(player.animTime);
        float ratio = (float) currentFrame.getRegionWidth() / currentFrame.getRegionHeight();
        float drawHeight = player.crouching ? 84f : 120f;
        float drawWidth = ratio * drawHeight;
        float drawX = player.pos.x - (drawWidth - player.bounds.width) / 2f;
        float drawY = player.pos.y - (player.crouching ? 35f : 50f);

        if (player.isPowerupActive && placeholderTexture != null) {
            float pulse = (float) Math.sin(doorAnimTime * 6f) * 6f;

            // Aura externa (bem suave)
            game.batch.setColor(1f, 0.9f, 0f, 0.15f);
            game.batch.draw(placeholderTexture, drawX - 30f - pulse, drawY - 20f - pulse, drawWidth + 60f + pulse * 2, drawHeight + 40f + pulse * 2);

            // Aura interna (mais brilhante)
            game.batch.setColor(1f, 1f, 0.3f, 0.35f);
            game.batch.draw(placeholderTexture, drawX - 15f + pulse/2f, drawY - 10f + pulse/2f, drawWidth + 30f - pulse, drawHeight + 20f - pulse);

            // Brilho dourado no próprio personagem
            game.batch.setColor(1f, 1f, 0.6f, 1f);
        }

        player.character.draw(game.batch, drawX, drawY, drawWidth, drawHeight, !player.facingRight, player.animTime);
        game.batch.setColor(Color.WHITE);
    }

    @Override public void resize(int width, int height) { viewport.update(width, height); uiViewport.update(width, height, true); }
    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() { if (levelMusic != null) { levelMusic.stop(); levelMusic.dispose(); levelMusic = null; } }

    @Override public void dispose() {
        if (uiStage != null) uiStage.dispose();
        if (skin != null)skin.dispose();
        //if (backgroundTexture != null) backgroundTexture.dispose();
        if (item1Texture != null) item1Texture.dispose();
        if (item2Texture != null) item2Texture.dispose();
        if (levelTrophyTexture != null) levelTrophyTexture.dispose();
        if (lifeTrophyTexture != null) lifeTrophyTexture.dispose();
        if (placeholderTexture != null) placeholderTexture.dispose();
        if (trapTexture != null) trapTexture.dispose();
        if (doorTexture != null) doorTexture.dispose();
        if (doorKeyTexture != null) doorKeyTexture.dispose();
        for (Texture tex : enemyTextures.values()) {
            if (tex != null && tex != placeholderTexture) tex.dispose();
        }
        if (startLevelSound != null) startLevelSound.dispose();
        if (collectBallSound != null) collectBallSound.dispose();
        if (collectHealSound != null) collectHealSound.dispose();
        if (collectLifeSound != null) collectLifeSound.dispose();
        if (winSound != null) winSound.dispose();
        if (shootSound != null) shootSound.dispose();
        if (hitSound != null) hitSound.dispose();
        if (doorSound != null) doorSound.dispose();
        if (powerupSound != null) powerupSound.dispose();
        if (levelMusic != null) levelMusic.dispose();
    }

    private static class IntersectorUtil {
        static void computeOverlap(Rectangle a, Rectangle b, Rectangle out) {
            float x = Math.max(a.x, b.x); float y = Math.max(a.y, b.y);
            float w = Math.min(a.x + a.width, b.x + b.width) - x; float h = Math.min(a.y + a.height, b.y + b.height) - y;
            out.set(x, y, Math.max(0f, w), Math.max(0f, h));
        }
    }
}
