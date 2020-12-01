package dyg.activity;

import com.alibaba.sdk.android.man.MANHitBuilders;
import com.alibaba.sdk.android.man.MANService;
import com.alibaba.sdk.android.man.MANServiceProvider;

public class FbUtils {
    public static void track(String flag) {
        try {
            MANHitBuilders.MANCustomHitBuilder hitBuilder = new MANHitBuilders.MANCustomHitBuilder(flag);
// 可使用如下接口设置时长：3分钟
            hitBuilder.setDurationOnEvent(3 * 60 * 1000);
// 设置关联的页面名称：聆听
            hitBuilder.setEventPage("Listen");
// 设置属性：类型摇滚
            hitBuilder.setProperty("type", "rock");
// 设置属性：歌曲标题
            hitBuilder.setProperty("title", "wonderful tonight");
// 发送自定义事件打点
            MANService manService = MANServiceProvider.getService();
            manService.getMANAnalytics().getDefaultTracker().send(hitBuilder.build());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
