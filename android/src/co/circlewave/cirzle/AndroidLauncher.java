package co.circlewave.cirzle;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.content.FileProvider;
import co.circlewave.cirzle.player.Player;
import co.circlewave.cirzle.services.AndroidAPI;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.pay.android.googlebilling.PurchaseManagerGoogleBilling;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.integration.IntegrationHelper;
import de.golfgl.gdxgamesvcs.GpgsClient;

import java.io.File;
import java.util.Objects;


public class AndroidLauncher extends AndroidApplication implements AndroidAPI {

    private GpgsClient gpgsClient;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gpgsClient = new GpgsClient().initialize(this, true);
        initialize(new Game(this, gpgsClient, new PurchaseManagerGoogleBilling(this)), getConfiguration());
    }

    private AndroidApplicationConfiguration getConfiguration() {
        final AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.numSamples = 2;
        config.useImmersiveMode = true;
        config.useCompass = false;
        config.useRotationVectorSensor = false;
        config.useGyroscope = false;
        config.depth = 15;
        return config;
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (gpgsClient != null) {
            gpgsClient.onGpgsActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void initializeAdvertisement(final Player player) {
        IronSource.init(this, getString(R.string.iron_source_app_id), IronSource.AD_UNIT.INTERSTITIAL);
        IntegrationHelper.validateIntegration(this);
        IronSource.setAdaptersDebug(true);
        IronSource.setConsent(player.isConsent());
        showInterstitial();
    }

    @Override
    public void setConsent(final boolean state, final Player player) {
        player.setConsent(state);
        IronSource.setConsent(state);
        Gdx.app.log("Consent information code", "" + state);
    }

    @Override
    public boolean isConnected() {
        return ((ConnectivityManager) Objects.requireNonNull(getSystemService(Context.CONNECTIVITY_SERVICE))).getActiveNetworkInfo() != null;
    }

    @Override
    public void share() {
        final Uri image = FileProvider.getUriForFile(getContext(), "co.circlewave.cirzle.provider",
                new File(getFilesDir(), "screenshot.png"));
        if (image != null) {
            final Intent sharing = new Intent(Intent.ACTION_SEND);
            sharing.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            sharing.setDataAndType(image, getContentResolver().getType(image));
            sharing.putExtra(Intent.EXTRA_STREAM, image);
            startActivity(Intent.createChooser(sharing, "Share via..."));
        }
    }

    @Override
    public void showInterstitial() {
        if (IronSource.isInterstitialReady()) {
            IronSource.showInterstitial("DefaultInterstitial");
        } else {
            IronSource.loadInterstitial();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        IronSource.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IronSource.onResume(this);
    }
}
