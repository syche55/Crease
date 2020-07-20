package neu.edu.crease.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import neu.edu.crease.R;

public class SliderAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context){
        this.context = context;
    }
    public int[] slides_images ={
            R.drawable.ic_tempura,
            R.drawable.ic_tempura,
            // R.raw.book_read,
            R.drawable.ic_logo
    };

    public String[] slides_headings ={
            "Crease",
            "Crease",
            "Crease"
    };

    public String[] slides_slogans ={
            "Record the days reading books",
            "RECORD \n - Record and share your reading experience",
            "MEMORY \n - Your personal reading app"
    };



    @Override
    public int getCount() {
        return slides_images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (RelativeLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_item, container,false);

        ImageView slideLogo = view.findViewById(R.id.slideLogo);
        TextView slideApp = view.findViewById(R.id.slideApp);
        TextView slideSlogan = view.findViewById(R.id.slideSlogan);
//        switch (position){
//            case 0:
//                slideLogo.setImageResource(R.drawable.ic_tempura);
//                slideApp.setText("Crease");
//                slideSlogan.setText(slides_slogans[position]);
//                break;
//            case 1:
//                slideLogo.setImageResource(R.drawable.ic_badge);
//                slideApp.setText("RECORD");
//                slideSlogan.setText("MEMORY");
//                break;
//            case 2:
//                slideLogo.setImageResource(R.drawable.ic_logo);
//                slideApp.setText("RECORD");
//                slideSlogan.setText("MEMORY");
//                break;
//        }
//
        slideLogo.setImageResource(slides_images[position]);
        slideApp.setText(slides_headings[position]);
        slideSlogan.setText(slides_slogans[position]);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }
}
