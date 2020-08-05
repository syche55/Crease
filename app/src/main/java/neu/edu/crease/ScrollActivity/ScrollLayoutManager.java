package neu.edu.crease.ScrollActivity;

import android.content.Context;
import android.graphics.PointF;
import android.util.DisplayMetrics;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

public class ScrollLayoutManager extends LinearLayoutManager {
    private float MILLISECONDS_PER_INCH = 25f;  //speed
    private Context context;


    public ScrollLayoutManager(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        LinearSmoothScroller linearSmoothScroller =
                new LinearSmoothScroller(recyclerView.getContext()) {
                    @Override
                    public PointF computeScrollVectorForPosition(int targetPosition) {
                        return ScrollLayoutManager.this
                                .computeScrollVectorForPosition(targetPosition);
                    }

                    @Override
                    protected float calculateSpeedPerPixel
                            (DisplayMetrics displayMetrics) {
                        return MILLISECONDS_PER_INCH / displayMetrics.density;
                    }

                };
        linearSmoothScroller.setTargetPosition(position);
        startSmoothScroll(linearSmoothScroller);
    }

    public void setSpeedSlow(float x) {
        MILLISECONDS_PER_INCH = context.getResources().getDisplayMetrics().density * 0.3f + (x);
    }
}

