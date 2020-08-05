package neu.edu.crease.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import neu.edu.crease.Adapter.PostAdapter;
import neu.edu.crease.Model.Post;

import neu.edu.crease.R;
import neu.edu.crease.SearchUserActivity;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postLists;
    private ImageView search_user_btn;

    private List<String> followingList;
    public String signOnUserID;

    public ProgressBar progressBar;

    public View onCreateView(@NonNull final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Log.e("", "you entered into home fragment!");

        recyclerView = view.findViewById(R.id.recycle_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        // fresh from top
        recyclerView.setLayoutManager(linearLayoutManager);
        postLists = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postLists);
        postAdapter.setHasStableIds(true);
        recyclerView.setAdapter(postAdapter);

        // refresh layout
        final SwipeRefreshLayout refreshLayout = view.findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // func - by added time
                checkFollowing();
                postAdapter.notifyDataSetChanged();
                //IMPORTANT - otherwise infinite refresh
                refreshLayout.setRefreshing(false);
            }
        });

        // load before the post loaded
        progressBar = view.findViewById(R.id.progress_circular);

        search_user_btn = view.findViewById(R.id.search_user_btn);

        search_user_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchUserActivity.class);
                startActivity(intent);
            }
        });


        checkFollowing();
        return view;
    }

    // check current user's following, load followings and self posts
    private void checkFollowing(){
        followingList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followingList.clear();
                for(DataSnapshot datasnapshot: snapshot.getChildren()){
                    followingList.add(datasnapshot.getKey());
                }
                readPost();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // read posts from db
    private  void readPost(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postLists.clear();
                signOnUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                for(DataSnapshot datasnapshot: snapshot.getChildren()){
                    Post post = datasnapshot.getValue(Post.class);
                    // check is curUser
                    if(signOnUserID.equals(post.getPostPublisher())) {
                        postLists.add(post);
                    } else {
                        for(String id: followingList){
                            if(post.getPostPublisher().equals(id)){
                                postLists.add(post);
                            }
                        }
                    }
                }
                postAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
