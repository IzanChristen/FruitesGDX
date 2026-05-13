package com.fruitesdeltemps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class GameScreen implements Screen {
    private MainGame game;
    private Texture background;
    private Texture bombaTexture;
    private Array<Texture> fruitTextures;
    private Array<GameObject> fruits;
    private Array<GameObject> bombs;
    private long lastSpawnTime;
    private int score;
    private int lives;
    private boolean gameOver;
    private float gameOverTimer;

    private static final int FRUIT_SIZE = 320;
    private static final int BOMB_SIZE = 320;
    private static final long LIFE_LOSS_COOLDOWN = 500;
    private long lastLifeLossTime = 0;
    private Sound slashSound;
    private Sound bombSound;
    private Sound lifeSound;
    private Sound gameoverSound;

    private class GameObject {
        Texture texture;
        Rectangle bounds;
        float x, y;
        float width, height;
        float velocityX, velocityY;


        GameObject(Texture texture, float x, float y, float vx, float vy, float width, float height) {
            this.texture = texture;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.velocityX = vx;
            this.velocityY = vy;
            this.bounds = new Rectangle(x, y, width, height);
        }

        void update() {
            x += velocityX;
            y += velocityY;
            velocityY -= 0.25f; // Gravedad ligera
            bounds.setPosition(x, y);
        }

        boolean isOutOfScreen() {
            // La fruta desaparece si sale por ARRIBA o por los lados
            return y > Gdx.graphics.getHeight() + 100 ||  // Sale por arriba
                x + width < 0 ||                       // Sale por izquierda
                x > Gdx.graphics.getWidth();           // Sale por derecha
        }

        boolean isBelowScreen() {
            // Si cae por debajo de la pantalla sin ser atrapada
            return y + height < 0;
        }
    }

    public GameScreen(MainGame game) {
        this.game = game;
        background = new Texture("images/background.jpg");
        bombaTexture = new Texture("images/bomba.png");

        fruitTextures = new Array<>();
        fruitTextures.add(new Texture("images/fruita1.png"));
        fruitTextures.add(new Texture("images/fruita2.png"));
        fruitTextures.add(new Texture("images/fruita3.png"));

        fruits = new Array<>();
        bombs = new Array<>();

        // ==========================================
        // ¡AQUÍ VAN LAS LÍNEAS DE LOS SONIDOS!
        // ==========================================
        slashSound = game.assetManager.get("sounds/slash.mp3", Sound.class);
        bombSound = game.assetManager.get("sounds/bomb.mp3", Sound.class);
        lifeSound = game.assetManager.get("sounds/life.mp3", Sound.class);
        gameoverSound = game.assetManager.get("sounds/gameover.mp3", Sound.class);
        // ==========================================

        score = 0;
        lives = 3;
        gameOver = false;
        lastSpawnTime = TimeUtils.millis();
    }

    private void spawnFruit() {
        Texture fruitTex = fruitTextures.random();
        int screenWidth = Gdx.graphics.getWidth();
        int screenHeight = Gdx.graphics.getHeight();

        // Aparece en la PARTE INFERIOR de la pantalla
        float x = MathUtils.random(0, screenWidth - FRUIT_SIZE);
        float y = 0; // Desde el borde inferior

        // Velocidad: SUBE RÁPIDO (positiva) pero con más impulso
        float vx = MathUtils.random(-3, 3);     // Movimiento lateral
        float vy = MathUtils.random(15, 20);    // Impulso fuerte hacia arriba

        fruits.add(new GameObject(fruitTex, x, y, vx, vy, FRUIT_SIZE, FRUIT_SIZE));
    }

    private void spawnBomb() {
        int screenWidth = Gdx.graphics.getWidth();

        float x = MathUtils.random(0, screenWidth - BOMB_SIZE);
        float y = 0;
        float vx = MathUtils.random(-3, 3);
        float vy = MathUtils.random(15, 20);

        bombs.add(new GameObject(bombaTexture, x, y, vx, vy, BOMB_SIZE, BOMB_SIZE));
    }

    private void loseLife() {
        long currentTime = TimeUtils.millis();
        if ((currentTime - lastLifeLossTime) >= LIFE_LOSS_COOLDOWN) {
            lives--;
            lastLifeLossTime = currentTime;

            if (lives <= 0) {
                gameOver = true;
                gameoverSound.play();  // ← AÑADE ESTA LÍNEA
                gameOverTimer = 3;
            }
        }
    }

    private void checkCollisions() {
        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

            // Comprobar frutas
            for (int i = fruits.size - 1; i >= 0; i--) {
                GameObject fruit = fruits.get(i);
                if (fruit.bounds.contains(touchX, touchY)) {
                    fruits.removeIndex(i);
                    score += 10;
                    slashSound.play();  // ← AÑADE ESTA LÍNEA
                    // Ganar vida cada 100 puntos
                    if (score % 100 == 0 && score > 0) {
                        lives = Math.min(lives + 1, 5);
                        lifeSound.play();  // ← AÑADE ESTA LÍNEA
                    }
                    break;
                }
            }

            // Comprobar bombas
            for (int i = bombs.size - 1; i >= 0; i--) {
                GameObject bomb = bombs.get(i);
                if (bomb.bounds.contains(touchX, touchY)) {
                    bombs.removeIndex(i);
                    bombSound.play();  // ← AÑADE ESTA LÍNEA
                    loseLife();
                    break;
                }
            }
        }
    }

    @Override
    public void render(float delta) {
        if (gameOver) {
            gameOverTimer -= delta;
            if (gameOverTimer <= 0) {
                game.setScreen(new MenuScreen(game));
            }
            // Pantalla de game over
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            game.batch.begin();
            game.batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            game.titleFont.draw(game.batch, "GAME OVER", Gdx.graphics.getWidth()/2 - 120, Gdx.graphics.getHeight()/2);
            game.scoreFont.draw(game.batch, "Puntuación: " + score, Gdx.graphics.getWidth()/2 - 80, Gdx.graphics.getHeight()/2 - 50);
            game.batch.end();
            return;
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Spawnear objetos cada 0.8 segundos
        if (TimeUtils.timeSinceMillis(lastSpawnTime) > 1500) {
            spawnFruit();
            // 25% de probabilidad de bomba
            if (MathUtils.random() < 0.25f) {
                spawnBomb();
            }
            lastSpawnTime = TimeUtils.millis();
        }

        // Actualizar frutas y comprobar si caen por debajo
        for (int i = fruits.size - 1; i >= 0; i--) {
            GameObject fruit = fruits.get(i);
            fruit.update();

            // Perder vida si la fruta cae por debajo de la pantalla sin ser atrapada
            if (fruit.isBelowScreen()) {
                fruits.removeIndex(i);
                loseLife();
            }
            // También eliminar si sale por arriba (sin perder vida, simplemente desaparece)
            else if (fruit.isOutOfScreen() && fruit.y > Gdx.graphics.getHeight()) {
                fruits.removeIndex(i);
            }
        }

        // Actualizar bombas
        for (int i = bombs.size - 1; i >= 0; i--) {
            GameObject bomb = bombs.get(i);
            bomb.update();
            if (bomb.isOutOfScreen() || bomb.isBelowScreen()) {
                bombs.removeIndex(i);
            }
        }

        checkCollisions();

        // Dibujar
        game.batch.begin();
        game.batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        for (GameObject fruit : fruits) {
            game.batch.draw(fruit.texture, fruit.x, fruit.y, fruit.width, fruit.height);
        }

        for (GameObject bomb : bombs) {
            game.batch.draw(bomb.texture, bomb.x, bomb.y, bomb.width, bomb.height);
        }
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

// Puntuación centrada arriba
        float puntuacionX = screenWidth / 2 - 80;
        game.scoreFont.draw(game.batch, "Puntuación " + score, puntuacionX, screenHeight - 35);

// Vidas centrada debajo de la puntuación
        float vidasX = screenWidth / 2 - 60;
        game.scoreFont.draw(game.batch, "Vidas " + lives, vidasX, screenHeight - 75);
        game.batch.end();
    }

    @Override
    public void dispose() {
        background.dispose();
        bombaTexture.dispose();
        for (Texture tex : fruitTextures) {
            tex.dispose();
        }
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
