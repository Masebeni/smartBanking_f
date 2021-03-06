package android.support.v4.net;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.NotificationCompat.WearableExtender;
import android.support.v4.media.TransportMediator;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import com.shane.smartbanking.C0160R;
import org.apache.http.protocol.HTTP;

class ConnectivityManagerCompatHoneycombMR2 {
    ConnectivityManagerCompatHoneycombMR2() {
    }

    public static boolean isActiveNetworkMetered(ConnectivityManager cm) {
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) {
            return true;
        }
        switch (info.getType()) {
            case SwipeRefreshLayout.LARGE /*0*/:
            case DrawerLayout.STATE_SETTLING /*2*/:
            case WearableExtender.SIZE_MEDIUM /*3*/:
            case TransportMediator.FLAG_KEY_MEDIA_PLAY /*4*/:
            case WearableExtender.SIZE_FULL_SCREEN /*5*/:
            case FragmentManagerImpl.ANIM_STYLE_FADE_EXIT /*6*/:
                return true;
            case SwipeRefreshLayout.DEFAULT /*1*/:
            case C0160R.styleable.Toolbar_contentInsetLeft /*7*/:
            case HTTP.HT /*9*/:
                return false;
            default:
                return true;
        }
    }
}
