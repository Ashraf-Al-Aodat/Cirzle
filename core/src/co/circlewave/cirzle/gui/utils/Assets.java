package co.circlewave.cirzle.gui.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Assets {

    public static final String BOLD = "font/comfortaa_bold.ttf";
    public static final String REGULAR = "font/comfortaa_regular.ttf";
    public static final String LIGHT = "font/comfortaa_light.ttf";

    private final AssetManager manager;

    private final String atlas = "sprites/texture.atlas";
    private final String logo = "sprites/logo.png";
    private final String skin = "gui/skin.json";
    private final String music = "row/erik_satie_gymnopedie_3.mp3";
    private final String clickSfx = "row/click.mp3";
    private final String restartSfx = "row/restart.mp3";
    private final String transitionSfx = "row/transition.mp3";
    private final String pieceSelectionSfx = "row/piece_selection.mp3";
    private final String winSfx = "row/win.mp3";
    private final String cameraSfx = "row/camera.mp3";
    private float volume;

    public Assets() {
        manager = new AssetManager(new InternalFileHandleResolver());
        volume = 0.3f;
    }

    public void load() {
        final FileHandleResolver resolver = new InternalFileHandleResolver();
        manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        manager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));

        final FreeTypeFontLoaderParameter light = new FreeTypeFontLoaderParameter();
        light.fontFileName = LIGHT;
        light.fontParameters.size = Gdx.graphics.getWidth() / 9;
        manager.load(LIGHT, BitmapFont.class, light);

        final FreeTypeFontLoaderParameter regular = new FreeTypeFontLoaderParameter();
        regular.fontFileName = REGULAR;
        regular.fontParameters.size = Gdx.graphics.getWidth() / 26;
        manager.load(REGULAR, BitmapFont.class, regular);

        final TextureLoader.TextureParameter parameter = new TextureLoader.TextureParameter();
        parameter.minFilter = Texture.TextureFilter.MipMapLinearLinear;
        parameter.genMipMaps = true;
        manager.load(logo, Texture.class, parameter);

        manager.finishLoading();

        manager.load(atlas, TextureAtlas.class);

        manager.load(music, Music.class);

        manager.load(clickSfx, Sound.class);
        manager.load(restartSfx, Sound.class);
        manager.load(transitionSfx, Sound.class);
        manager.load(pieceSelectionSfx, Sound.class);
        manager.load(winSfx, Sound.class);
        manager.load(cameraSfx, Sound.class);

        manager.load(skin, Skin.class, new SkinLoader.SkinParameter(atlas));

        final FreeTypeFontLoaderParameter bold = new FreeTypeFontLoaderParameter();
        bold.fontFileName = BOLD;
        bold.fontParameters.size = Gdx.graphics.getWidth() / 22;
        manager.load(BOLD, BitmapFont.class, bold);
    }

    public TextureAtlas getAtlas() {
        return manager.get(atlas);
    }

    public Texture getLogo() {
        return manager.get(logo);
    }

    public Skin getSkin() {
        return manager.get(skin);
    }

    public BitmapFont getFont(final String name) {
        return manager.get(name);
    }

    public void changeFontSize(final String name, final float size) {
        manager.unload(name);
        FreeTypeFontLoaderParameter parameter = new FreeTypeFontLoaderParameter();
        parameter.fontFileName = name;
        parameter.fontParameters.size = (int) size;
        manager.load(name, BitmapFont.class, parameter);
        manager.finishLoading();
    }

    public void playMusic() {
        final Music music = manager.get(this.music);
        music.play();
        music.setLooping(true);
        music.setVolume(0.5f);
    }

    public void pauseMusic() {
        ((Music) manager.get(music)).pause();
    }

    public void playClickSoundEffect() {
        ((Sound) manager.get(clickSfx)).play(volume);
    }

    public void playTransitionSoundEffect() {
        ((Sound) manager.get(transitionSfx)).play(volume);
    }

    public void playRestartSoundEffect() {
        ((Sound) manager.get(restartSfx)).play(volume);
    }

    public void playPieceSelectionSoundEffect() {
        ((Sound) manager.get(pieceSelectionSfx)).play(volume);
    }

    public void playPieceCameraSoundEffect() {
        ((Sound) manager.get(cameraSfx)).play(volume);
    }

    public void playWinSoundEffect() {
        ((Sound) manager.get(winSfx)).play(volume);
    }

    public void playSoundEffect() {
        volume = 0.3f;
    }

    public void pauseSoundEffect() {
        volume = 0;
    }

    public void update() {
        manager.update();
    }

    public Boolean finishLoading() {
        return manager.update() && manager.isLoaded(atlas) &&
                manager.isLoaded(skin) && manager.isLoaded(BOLD) &&
                manager.isLoaded(REGULAR) && manager.isLoaded(music);
    }

    public void dispose() {
        manager.dispose();
    }
}
