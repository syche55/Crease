package neu.edu.crease.Adapter;

import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import neu.edu.crease.Model.Post;
import neu.edu.crease.R;
import neu.edu.crease.ui.postDetail.PostDetailFragment;

public class MySaveAdapter extends RecyclerView.Adapter<MySaveAdapter.ViewHolder>{

    private Context context;
    private List<Post> mPosts;

    public MySaveAdapter(Context context, List<Post> mPosts) {
        this.context = context;
        this.mPosts = mPosts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.saves_item, parent, false);
        return new MySaveAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Post post = mPosts.get(position);

        Glide.with(context).load(post.getPostImage()).into(holder.post_image);
        holder.bookName.setText(post.getPostTitle());

        // if user click the post image in the profile, then direct to the detail of the post
        // here we click the textview, because it covers the picture
        holder.save_background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("postID", post.getPostID());
                editor.apply();

                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.container,
                        new PostDetailFragment()).addToBackStack(null).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView bookName;
        public ImageView post_image;
        public TextView save_background;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bookName = itemView.findViewById(R.id.save_book_title);
            post_image = itemView.findViewById(R.id.save_post_img);
            save_background = itemView.findViewById(R.id.save_background);
        }
    }
}
