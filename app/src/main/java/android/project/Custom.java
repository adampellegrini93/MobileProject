package android.project;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ListView;

class Custom extends ListView {

    public Custom(Context context){
        super(context);
    }

    public Custom(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
    }


    public Custom(Context context, AttributeSet attributeSet, int attr){
        super(context,attributeSet,attr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Custom(Context context, AttributeSet attributeSet, int attr, int res){
        super(context, attributeSet, attr, res);
    }

    @Override
    protected void onMeasure(int width, int height){
        int expansion = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(width, expansion);
    }
}
