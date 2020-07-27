package neu.edu.crease;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import neu.edu.crease.Adapter.SplashAdapter;
import neu.edu.crease.ScrollActivity.ScrollLayoutManager;


public class MainActivity extends AppCompatActivity {

//    private static int SPLASH_SCREEN = 2500;
//
//    Animation topAnim, bottomAnim;
//    ImageView image;
//    TextView logo, slogan;

//    private ViewPager mSlideViewPager;
//    private LinearLayout mDotsLayout;
//    private SliderAdapter sliderAdapter;
//    private TextView[] mDots;
    private RecyclerView mRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recycleView);
        mRecyclerView.setAdapter(new SplashAdapter(MainActivity.this));
        mRecyclerView.setLayoutManager(new ScrollLayoutManager(MainActivity.this));

        mRecyclerView.smoothScrollToPosition(Integer.MAX_VALUE / 2);

//        mSlideViewPager = (ViewPager) findViewById(R.id.slideViewPager);
//        mDotsLayout = (LinearLayout) findViewById(R.id.slideDotsLayout);
//        Button register = findViewById(R.id.slideRegister);
//        Button login = findViewById(R.id.slideLogin);
//
//        sliderAdapter = new SliderAdapter(this);
//        mSlideViewPager.setAdapter(sliderAdapter);
//
//        addDotsIndicator(0);
//
//        mSlideViewPager.addOnPageChangeListener(viewListener);

    }

//    public void addDotsIndicator(int position){
//        mDots = new TextView[3];
//        mDotsLayout.removeAllViews();
//        for(int i =0; i< mDots.length; i++){
//            mDots[i] = new TextView(this);
//            mDots[i].setText(Html.fromHtml("&#8226;"));
//            mDots[i].setTextSize(35);
//            mDots[i].setTextColor(getResources().getColor(R.color.colorTransparentWhite));
//
//            mDotsLayout.addView(mDots[i]);
//        }
//
//        if(mDots.length > 0){
//            mDots[position].setTextColor(getResources().getColor(R.color.colorWhite));
//        }
//    }
//
//    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
//        @Override
//        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//        }
//        @Override
//        public void onPageSelected(int position) {
//            addDotsIndicator(position);
//        }
//
//        @Override
//        public void onPageScrollStateChanged(int state) {
//
//        }
//    };

    public void register(View view){
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    public void login(View view){
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

}