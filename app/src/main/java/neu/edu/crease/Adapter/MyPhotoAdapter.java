package neu.edu.crease.Adapter;

import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;

import com.bumptech.glide.Glide;

import java.util.List;

import neu.edu.crease.Model.Post;
import neu.edu.crease.R;
import neu.edu.crease.ui.postDetail.PostDetailFragment;

public class MyPhotoAdapter extends RecyclerView.Adapter<MyPhotoAdapter.ViewHolder>{

    private Context context;
    private List<Post> mPosts;

    public MyPhotoAdapter(Context context, List<Post> mPosts) {
        this.context = context;
        this.mPosts = mPosts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.photos_item, parent, false);
        return new MyPhotoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Post post = mPosts.get(position);

        Glide.with(context).load(post.getPostImage()).into(holder.post_image);

        holder.post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("", "you clicked the post image in myphoto adapter!");
                SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("postID", post.getPostID());
                editor.apply();

                // when click a user, go to his / her profile

                // create a frame layout
                FrameLayout fragmentLayout = new FrameLayout(context);

                // set the layout params to fill the activity
                fragmentLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                // set an id to the layout
                fragmentLayout.setId(R.id.fragmentLayout); // some positive integer
                // set the layout as Activity content
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                activity.setContentView(fragmentLayout);
                // Finally , add the fragment
                PostDetailFragment newFragment = new PostDetailFragment();
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentLayout, newFragment).commit();  // 1000 - is the id set for the container layout
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView post_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            post_image = itemView.findViewById(R.id.post_image);
        }
    }
}
