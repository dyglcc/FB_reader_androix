package dyg.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import android.util.Log;
import android.view.ViewGroup;

import com.dyg.android.reader.R;
import com.sspsdk.RYSDK;
import com.sspsdk.error.ADError;
import com.sspsdk.listener.RYSplashADListener;

import org.geometerplus.android.fbreader.FBReader;
import org.geometerplus.zlibrary.core.options.Config;

public class SplashActivity extends FragmentActivity {

    private static final String first_open = "first_open";
    private static final String TAG = "SplashActivity" ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_splash);
        // 第一次打开，直接显示prewindow
        // 第一次打开直接到本地书库
        boolean first = Config.getInstance().getSpecialBooleanValue(first_open, true);
        if (first) {
            FbDefaultActivity.startActivity(this);
            finish();
            Config.getInstance().setSpecialBooleanValue(first_open, false);
        } else {
            loadAd();
//            next();
        }
    }

    private void loadAd() {

        RYSDK.loadSplashAd(this, (ViewGroup) findViewById(R.id.layout_splash),
                "40a0dc6e", new RYSplashADListener() {
                    @Override
                    public void adDismissed() {
                        Log.e("RYAPP", "关闭回调");
                        next();
                    }


                    @Override
                    public void adError(ADError adError) {
                        Log.e("RYAPP", "错误回调" + adError.getErrorMessage());
                        next();
                    }


                    @Override
                    public void adLoadSuccess() {
                        Log.e("RYAPP", "请求成功回调");
                    }


                    @Override
                    public void adClicked() {
                        Log.e("RYAPP", "点击回调");
                    }


                    @Override
                    public void adExposure() {
                        Log.e("RYAPP", "曝光回调");
                    }


                    @Override
                    public void adTick(long var1) {
                        Log.e("RYAPP", "心跳回调");
                    }
                }, 5000);

    }

    private void next() {
        startActivity(new Intent(SplashActivity.this, FBReader.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        Log.e(TAG, "onBackPressed: cannot back");
    }
}
