package org.guide.component;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.binioter.guideview.Component;
import com.dyg.android.reader.R;

import java.lang.ref.WeakReference;

/**
 * Created by binIoter on 16/6/17.
 */
public class SimpleComponent implements Component {

    private WeakReference weakReference;
    private Component.CallBack callBack;
    private String txt ;

    public SimpleComponent(View view, String txt,Component.CallBack callBack) {
        weakReference = new WeakReference(view);
        this.callBack = callBack;
        this.txt = txt;
    }

    @Override
    public View getView(LayoutInflater inflater) {

        View ll = inflater.inflate(R.layout.layer_frends, null);
        TextView textView = ll.findViewById(R.id.txt);
        textView.setText(txt);
        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callBack.callBackShown(view);
            }
        });
        return ll;
    }

    @Override
    public int getAnchor() {
        return Component.ANCHOR_BOTTOM;
    }

    @Override
    public int getFitPosition() {
        return Component.FIT_END;
    }

    @Override
    public int getXOffset() {
        return 0;
    }

    @Override
    public int getYOffset() {
        return 10;
    }
}
