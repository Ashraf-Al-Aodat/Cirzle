package co.circlewave.cirzle.player;

import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.Json;

import java.io.Serializable;

public class Player implements Serializable {

    private static final long serialVersionUID = 1L;
    public transient static int SOUND = 0;
    public transient static int EFFECT = 1;
    public transient static int MUSIC = 2;
    public transient static int MUTE = 3;
    private int keys;
    private int currentLevel;
    private int adCount;
    private int soundStatue;
    private int askForRatingCounter;
    private boolean policy;
    private boolean askForRating;
    private boolean premium;
    private boolean sign;
    private boolean consent;
    private long timeToUpdateLevels;

    public Player() {
        this.keys = 5;
        this.currentLevel = 1;
        this.soundStatue = SOUND;
        this.adCount = 0;
        this.askForRatingCounter = 0;
        this.policy = false;
        this.premium = false;
        this.sign = false;
        this.askForRating = true;
        this.consent = false;
    }

    public byte[] toByte() {
        final Json json = new Json();
        json.setUsePrototypes(false);
        return Base64Coder.encodeString(json.toJson(this)).getBytes();
    }

    public String toJsonString() {
        final Json json = new Json();
        json.setUsePrototypes(false);
        return Base64Coder.encodeString(json.toJson(this));
    }

    public long getTimeToUpdateLevels() {
        return timeToUpdateLevels;
    }

    public void resetTimeToUpdateLevels() {
        this.timeToUpdateLevels = System.currentTimeMillis();
    }

    public int getKeys() {
        return keys;
    }

    public void setKeys(final int keys) {
        this.keys = keys;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(final int currentLevel, final int maxLevel) {
        if (currentLevel <= maxLevel) this.currentLevel = currentLevel;
    }

    public boolean isConsent() {
        return consent;
    }

    public void setConsent(final boolean consent) {
        this.consent = consent;
    }

    public boolean checkAdCount() {
        if (adCount < 2) {
            adCount++;
            return true;
        }
        return false;
    }

    public void resetAdCount() {
        adCount = 0;
    }

    public void neverAskForRating() {
        askForRating = false;
    }

    public boolean isAskForRating() {
        return askForRating;
    }

    public boolean shouldAskForRating() {
        if (askForRatingCounter == 10) {
            askForRatingCounter = 0;
            return true;
        } else {
            askForRatingCounter++;
            return false;
        }
    }

    public int getSoundStatue() {
        return soundStatue;
    }

    public void setSoundStatue(final int state) {
        this.soundStatue = state;
    }

    public void agreeToPolicy() {
        policy = true;
    }

    public boolean isAgreedToPolicy() {
        return policy;
    }

    public void activatePremium() {
        premium = true;
    }

    public boolean isPremium() {
        return premium;
    }

    public boolean isSign() {
        return sign;
    }

    public void signIn() {
        sign = true;
    }

    public void signOut() {
        sign = false;
    }
}

