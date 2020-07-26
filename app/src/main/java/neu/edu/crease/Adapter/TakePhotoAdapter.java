package neu.edu.crease.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import neu.edu.crease.R;

public class TakePhotoAdapter extends RecyclerView.Adapter<TakePhotoAdapter.ViewHolder>{
    private int imgWidth;
    public Context context;
    public LayoutInflater layoutInflater;

    public TakePhotoAdapter(Context context) {
        imgWidth = getScreenWidth(context);
    }


    @NonNull
    @Override
    public TakePhotoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.take_photo_item, parent, false);
        return new TakePhotoAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView backgroundImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            backgroundImage = itemView.findViewById(R.id.main_bg_item);
        }

    }

    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        return width;
    }

}