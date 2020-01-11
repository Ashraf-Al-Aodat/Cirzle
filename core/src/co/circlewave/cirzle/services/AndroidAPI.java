package co.circlewave.cirzle.services;

import co.circlewave.cirzle.player.Player;

public interface AndroidAPI {

    boolean isConnected();

    void share();

    void initializeAdvertisement(final Player player);

    void showInterstitial();

    void setConsent(final boolean state, final Player player);

}
