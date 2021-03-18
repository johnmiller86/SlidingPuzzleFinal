package com.johnmillercoding.slidingpuzzle.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.johnmillercoding.slidingpuzzle.R;

import java.util.Objects;


@SuppressWarnings("EmptyMethod")
@SuppressLint("CommitTransaction")
public class MainMenuFragment extends Fragment {

    // UI components
    private View view;
    private FragmentTransaction fragmentTransaction;

    public MainMenuFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_main_menu, container, false);
        initialize();
        return view;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void initialize(){

        // Buttons
        Button campaignPlayButton = view.findViewById(R.id.button_campaign);
        Button freePlayButton = view.findViewById(R.id.button_freeplay);
        Button settingsButton = view.findViewById(R.id.button_settings);
        Button leaderboardsButton = view.findViewById(R.id.button_leaderboards);

        // Listeners
        campaignPlayButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                campaignPlay();
            }
        });
        freePlayButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                freePlay();
            }
        });
        settingsButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                settings();
            }
        });
        leaderboardsButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                leaderboards();
            }
        });

        // Ad stuff
        NativeExpressAdView adView = view.findViewById(R.id.adView2);
        AdRequest request = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // Emulators
                .addTestDevice("91D6373C67AB407D90746EAF75E82B1A")  // S7 Edge
                .build();
        adView.loadAd(request);
    }

    /**
     * Free Play button listener.
     */
    private void campaignPlay() {
        MainActivity.fragment = new CampaignFragment();
        fragmentTransaction = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(R.id.fragment_container, MainActivity.fragment);
        fragmentTransaction.commit();
    }

    /**
     * Free Play button listener.
     */
    private void freePlay() {
        Intent intent = new Intent(getActivity(), PuzzleActivity.class);
        startActivity(intent);
        Objects.requireNonNull(getActivity()).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * Settings button listener.
     */
    private void settings() {
        MainActivity.fragment = new SettingsFragment();
        fragmentTransaction = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(R.id.fragment_container, MainActivity.fragment);
        fragmentTransaction.commit();
    }

    /**
     * Leaderboards button listener.
     */
    private void leaderboards() {
        MainActivity.fragment = new LeaderboardFragment();
        fragmentTransaction = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(R.id.fragment_container, MainActivity.fragment);
        fragmentTransaction.commit();
    }
}
