package newwave.videomaker.statusmaker;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import newwave.videomaker.statusmaker.utils.Util_StringShared;
import newwave.videomaker.statusmaker.utils.Utils_Shareddata;

import com.facebook.ads.AdSettings;
import com.google.android.exoplayer2.database.ExoDatabaseProvider;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings.Builder;

import java.io.File;

public class MyApplication extends Application {
    public static boolean Click = false;
    public static SimpleCache simpleCache = null;
    public static Long exoPlayerCacheSize = (long) (90 * 1024 * 1024);
    public static LeastRecentlyUsedCacheEvictor leastRecentlyUsedCacheEvictor = null;
    public static ExoDatabaseProvider exoDatabaseProvider = null;
    public Context context;
    public FirebaseRemoteConfig mFirebaseRemoteConfig;

    public void onCreate() {
        super.onCreate();
        Utils_Shareddata.init(this);
        FirebaseApp.initializeApp(context);
        AdSettings.addTestDevice("3daf2c72-15f5-4301-90bd-16e92f5b1692");
        initializeFirebase();
        new Thread(this::FirebaseConfig).start();
        try {
            context = this;
        } catch (Exception e) {
            e.printStackTrace();
        }
        FirebaseApp.initializeApp(this);
    }


    public void FirebaseConfig() {
        try {
            mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(this::setupFirebaseConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void initializeFirebase() {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        mFirebaseRemoteConfig.setConfigSettingsAsync(new Builder().build());
    }

    public void CacheClear() {

        try {
            File dir = getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }


    public void setupFirebaseConfig(Task task) {
        if (task.isSuccessful()) {
            String string = mFirebaseRemoteConfig.getString(getResources().getString(R.string.Kotlins_api));
            String string2 = mFirebaseRemoteConfig.getString(getResources().getString(R.string.Kotlins_key));
            if (TextUtils.isEmpty(Utils_Shareddata.get(Util_StringShared.MYGST_API))) {
                Utils_Shareddata.set(Util_StringShared.MYGST_API, string);
            }
            if (TextUtils.isEmpty(Utils_Shareddata.get(Util_StringShared.MYGST_KEY))) {
                Utils_Shareddata.set(Util_StringShared.MYGST_KEY, string2);
            }
        }


        if (leastRecentlyUsedCacheEvictor == null) {
            leastRecentlyUsedCacheEvictor = new LeastRecentlyUsedCacheEvictor(exoPlayerCacheSize);
        }

        if (exoDatabaseProvider != null) {
            exoDatabaseProvider = new ExoDatabaseProvider(this);
        }

        if (simpleCache == null) {
            simpleCache = new SimpleCache(getCacheDir(), leastRecentlyUsedCacheEvictor, exoDatabaseProvider);
            if (simpleCache.getCacheSpace() >= 400207768) {
                CacheClear();
            }
            Log.i("TAG", "onCreate: " + simpleCache.getCacheSpace());
        }


    }
    public boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
}

