package com.fruitesdeltemps;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Gdx;

public class MainGame extends Game {
    public SpriteBatch batch;
    public BitmapFont titleFont;
    public BitmapFont scoreFont;
    public AssetManager assetManager;

    @Override
    public void create() {
        batch = new SpriteBatch();
        assetManager = new AssetManager();

        // Fuente personalizada si existe title.ttf
        try {
            com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator generator =
                new com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator(Gdx.files.internal("ui/title.ttf"));
            com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter parameter =
                new com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = 72;
            titleFont = generator.generateFont(parameter);
            parameter.size = 48;
            scoreFont = generator.generateFont(parameter);
            generator.dispose();
        } catch (Exception e) {
            // Fallback a fuentes por defecto
            titleFont = new BitmapFont();
            scoreFont = new BitmapFont();
            titleFont.getData().setScale(3);
            scoreFont.getData().setScale(2);
        }

        // Cargar sonidos
        try {
            assetManager.load("sounds/slash.mp3", Sound.class);
            assetManager.load("sounds/bomb.mp3", Sound.class);
            assetManager.load("sounds/life.mp3", Sound.class);
            assetManager.load("sounds/gameover.mp3", Sound.class);
            assetManager.finishLoading();
        } catch (Exception e) {
            System.out.println("No se pudieron cargar los sonidos");
        }

        setScreen(new SplashScreen(this));
    }

    @Override
    public void dispose() {
        batch.dispose();
        if (titleFont != null) titleFont.dispose();
        if (scoreFont != null) scoreFont.dispose();
        assetManager.dispose();
    }
}
