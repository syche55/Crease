package neu.edu.crease.ui.explore;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import neu.edu.crease.Adapter.ExploreAdapter;
import neu.edu.crease.Model.Post;
import neu.edu.crease.R;

public class ExploreFragment extends Fragment {

    private RecyclerView recyclerView;
    private ExploreAdapter exploreAdapter;
    private List<Post> exploreLists;
    private String signOnUserID;

    public StaggeredGridLayoutManager layoutManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        recyclerView = view.findViewById(R.id.explore_item);

        // grid to 2 col & set auto match [gridlayout can't auto fixed match]
        // GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2);
        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        // GridLayoutManager does not support stack from end. Consider using reverse layout
        // gridLayoutManager.setStackFromEnd(true);

        exploreLists = new ArrayList<>();
        exploreAdapter = new ExploreAdapter(getContext(), exploreLists);
        exploreAdapter.setHasStableIds(true);
        recyclerView.setAdapter(exploreAdapter);

        //refresh layout
        final SwipeRefreshLayout refreshLayout = view.findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // func
                Collections.shuffle(exploreLists);
                // exploreAdapter.notifyDataSetChanged();
                // if adapter, will call two listener, when refresh and save, will shuffle twice
                readPost();
                //IMPORTANT - otherwise infinite refresh
                refreshLayout.setRefreshing(false);
            }
        });

        //load all posts
        readPost();
        return view;
    }

    // read posts from db
    private void readPost(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                exploreLists.clear();
                signOnUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                for(DataSnapshot datasnapshot: snapshot.getChildren()){
                    Post post = datasnapshot.getValue(Post.class);
                    // do not load current sign user's post in explore
                    if(!(post.getPostPublisher().equals(signOnUserID))){
                        exploreLists.add(post);
                    }
                }
                // Random - or replaced after saved implement
                // Collections.shuffle(exploreLists);
                exploreAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}