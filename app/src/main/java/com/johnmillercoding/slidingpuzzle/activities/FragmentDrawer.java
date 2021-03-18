package com.johnmillercoding.slidingpuzzle.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import java.util.Objects;

public class FragmentDrawer extends Fragment {

    private SessionManager sessionManager;
    private DrawerLayout mDrawerLayout;
    private View containerView;
    private static String[] titles = null;
    private FragmentDrawerListener drawerListener;
    private ImageView imageView;
    private int longClicks;

    public FragmentDrawer() {
        // Required default constructor
    }

    /**
     * The listener for the NavigationDrawer.
     * @param listener an instance of FragmentDrawerListener.
     */
    public void setDrawerListener(FragmentDrawerListener listener) {
        this.drawerListener = listener;
    }

    /**
     * The list of NavigationDrawer items.
     * @return the list.
     */
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
        titles = Objects.requireNonNull(getActivity()).getResources().getStringArray(R.array.nav_drawer_labels);
        sessionManager = new SessionManager(getActivity().getApplicationContext());
        longClicks = 0;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflating view layout
        View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        RecyclerView recyclerView = layout.findViewById(R.id.drawerList);

        // Configure the NavigationDrawerAdapter
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

        // Configure the ImageView easter egg
        imageView = layout.findViewById(R.id.profile);
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                switch (longClicks) {
                    case 0: {
                        RotateAnimation rotateAnimation = new RotateAnimation(0.0f, 360, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
                        rotateAnimation.setDuration(1200);
                        rotateAnimation.setFillAfter(true);
                        imageView.startAnimation(rotateAnimation);
                        longClicks++;
                        break;
                    }
                    case 1: {
                        RotateAnimation rotateAnimation = new RotateAnimation(360, 0.0f, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
                        rotateAnimation.setDuration(1200);
                        rotateAnimation.setFillAfter(true);
                        imageView.startAnimation(rotateAnimation);
                        longClicks++;
                        break;
                    }
                    default:
                        MainActivity.enterCheat(getActivity());
                        break;
                }
                return false;
            }
        });

        // Configure the TextView
        TextView textView = layout.findViewById(R.id.emailTextView);
        textView.setText(sessionManager.getEmail());
        textView.setSingleLine(true);
        textView.setMarqueeRepeatLimit(-1);
        textView.setSelected(true);
        return layout;
    }

    /**
     * Sets up the NavigationDrawer.
     * @param fragmentId the containing fragment's id.
     * @param drawerLayout the NavigationDrawer layout.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        containerView = Objects.requireNonNull(getActivity()).findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
    }

    @SuppressWarnings({"EmptyMethod", "UnusedParameters"})
    interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    /**
     * Listener for the NavigationDrawer's RecyclerViews.
     */
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
        public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
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
    @SuppressWarnings("UnusedParameters")
    public interface FragmentDrawerListener {
        void onDrawerItemSelected(View view, int position);
    }

    public void setProfilePicture(){
        Glide.with(this.getContext()).load(sessionManager.getFacebookImageUrl()).into(imageView);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(layoutParams);
    }
}