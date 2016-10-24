package com.johnmillercoding.slidingpuzzle.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.johnmillercoding.slidingpuzzle.R;
import com.johnmillercoding.slidingpuzzle.models.NavDrawerItem;
import com.johnmillercoding.slidingpuzzle.utilities.NavigationDrawerAdapter;
import com.johnmillercoding.slidingpuzzle.utilities.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class FragmentDrawer extends Fragment {

    private SessionManager sessionManager;
    private DrawerLayout mDrawerLayout;
    private View containerView;
    private static String[] titles = null;
    private FragmentDrawerListener drawerListener;
    private ImageView imageView;

    public FragmentDrawer() {
        // Required default constructor
    }

    public void setDrawerListener(FragmentDrawerListener listener) {
        this.drawerListener = listener;
    }

    private static List<NavDrawerItem> getData() {
        List<NavDrawerItem> data = new ArrayList<>();


        // preparing navigation drawer items
        for (String title : titles) {
            NavDrawerItem navItem = new NavDrawerItem();
            navItem.setTitle(title);
            data.add(navItem);
        }
        return data;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // drawer labels
        titles = getActivity().getResources().getStringArray(R.array.nav_drawer_labels);
        sessionManager = new SessionManager(getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflating view layout
        View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        RecyclerView recyclerView = (RecyclerView) layout.findViewById(R.id.drawerList);

        NavigationDrawerAdapter adapter = new NavigationDrawerAdapter(getActivity(), getData());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                drawerListener.onDrawerItemSelected(view, position);
                mDrawerLayout.closeDrawer(containerView);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        imageView = (ImageView) layout.findViewById(R.id.profile);
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                RotateAnimation rotateAnimation = new RotateAnimation(0.0f, 360, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
                rotateAnimation.setDuration(1200);
                rotateAnimation.setFillAfter(true);
                imageView.startAnimation(rotateAnimation);
                return false;
            }
        });

        TextView textView = (TextView) layout.findViewById(R.id.emailTextView);
        textView.setText(sessionManager.getEmail());
        return layout;
    }


    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
    }

    interface ClickListener {
        void onClick(View view, int position);

        @SuppressWarnings("EmptyMethod")
        void onLongClick(@SuppressWarnings("UnusedParameters") View view, @SuppressWarnings("UnusedParameters") int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private final GestureDetector gestureDetector;
        private final ClickListener clickListener;

        RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            // Required overridden method
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            // Required overridden method
        }
    }

    /**
     * Interface for drawer listener.
     */
    public interface FragmentDrawerListener {
        void onDrawerItemSelected(@SuppressWarnings("UnusedParameters") View view, int position);
    }

//    public void setProfilePicture(Bitmap picture){
//        imageView.setImageBitmap(picture);
//    }
    public void setProfilePicture(){
        Glide.with(FragmentDrawer.this).load(sessionManager.getFacebookImageUrl()).into(imageView);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(layoutParams);
    }
}