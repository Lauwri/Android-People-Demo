package com.example.lauri.androiddemo;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PeopleFragment.PanelStateListener {


    private List<MainCustomListener> listeners = new ArrayList<MainCustomListener>();
    private boolean panelOn;
    private Fragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            //Restore the fragment's instance
            mainFragment = getSupportFragmentManager().getFragment(savedInstanceState, "MainFragment");
        } else {
            mainFragment = PeopleFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, mainFragment).commit();
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Variable to check if child fragment has panel open, starts with false
        panelOn = false;

    }


    @Override
    public void onBackPressed() {
        if(!panelOn) {
            super.onBackPressed();
        }
        fireBackPressedListeners();
    }

    /**
     * Save url for use
     * For example, orientation change could empty newly added url
     * @param outState
     */
    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, "MainFragment", mainFragment);
    }

    /**
     Fire main class listeners
     */
    private void fireBackPressedListeners() {
        for(MainCustomListener l : listeners) {
            l.backPressedListener();
        }
    }
    /**
     add observer pattern listeners
     */
    public void setMainListener(MainCustomListener listener) {
        this.listeners.add(listener);
    }
    /**
     Listen to panel state
     */
    @Override
    public void panelStateListener(boolean stateShowing) {
        panelOn = stateShowing;
    }
}
