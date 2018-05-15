package com.example.lauri.androiddemo;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.lauri.androiddemo.Content.PeopleAdapter;
import com.example.lauri.androiddemo.Content.PersonModel;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lauri Mattila on 11.2.2018.
 */

/**
 * A placeholder fragment containing a simple view.
 */
public class PeopleFragment extends Fragment {
    /**
     * fragment root view
     * ref to main activity
     * server url
     * array list to hold PersonModel objects
     * adapter for displaying people in recyclerview
     * umano slidingUpPanel for adding new people
     * swipe to refresh layout for refreshing people
     * boolean variable to describe if sliding panel is showing or collapsed
     *
     * array list to hold custom listeners
     */
    private View rootView;
    private MainActivity activity;
    private String url = "http://0.0.0.0:3000";
    private static final String URL_ADDRESS = "url_address";
    private ArrayList<PersonModel> people;
    private PeopleAdapter adapter;
    private SlidingUpPanelLayout addPeoplePanel;
    private SwipeRefreshLayout refreshPeople;
    boolean panelOn;

    private List<PanelStateListener> fragmentListener = new ArrayList<PanelStateListener>();

    //Required empty constructor
    public PeopleFragment() {

    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PeopleFragment newInstance() {
        return new PeopleFragment();
    }

    /**
     * View creation for fragment. In fragment use mainly this instead of onCreate
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return inflated and set up view for displaying
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            String savedText = savedInstanceState.getString(URL_ADDRESS);
            url = savedText;
        }
        //set root view and enable toolbar options menu
        rootView = inflater.inflate(R.layout.people_fragment, container, false);
        setHasOptionsMenu(true);
        //ref to activity
        activity = (MainActivity) getActivity();

        //setting up the panel for adding more people!
        addPeoplePanel = (SlidingUpPanelLayout) rootView.findViewById(R.id.sliding_layout);
        addPeoplePanel.setScrollableView(rootView.findViewById(R.id.add_person_panel));
        setUpPersonPanel(addPeoplePanel);
        //set up swipe refresh
        refreshPeople = rootView.findViewById(R.id.refresh_people_list);
        refreshPeople.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPeople();
            }
        });

        //Recycler View setup
        final RecyclerView peopleList = rootView.findViewById(R.id.people_list);
        peopleList.setLayoutManager(new LinearLayoutManager(getContext()));
        //First server request and set the custom adapter for recycler view
        people = new ArrayList<PersonModel>();
        //people list, context and listener for listening delete button clicks
        adapter = new PeopleAdapter(people, getContext(), new GetResultListener<Integer>() {
            @Override
            public void getResults(Integer result) {
                deletePerson(result);
            }
        });

        peopleList.setAdapter(adapter);

        /* Listen when recycler view has finished drawing layout
         * After layout is ready, we can call first server request, otherwise
         * we get NullPointerException caused by adapter.adapterDataSetChanged();
         * Finally we remove the listener.
        */
        peopleList.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getPeople();
                peopleList.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });


        //add activity to listen for panel state
        try {
            fragmentListener.add((PanelStateListener) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement CustomFragmentCallback ");
        }

        /*
            Add listener to main class
            this is implemented by using observer pattern
         */

        activity.setMainListener(new MainCustomListener() {
            @Override
            public void backPressedListener() {
                addPeoplePanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });
        return rootView;
    }

    @Override
    public void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(URL_ADDRESS, url);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.server_url) {
            showServerUrlDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Shows dialog to user for inputting custom server address
     */
    private void showServerUrlDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Input test server address:");
        //Set up the input
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        //Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                url = input.getText().toString();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void getPeople() {
        refreshPeople.setRefreshing(true);
        APIMethods.getInstance(getContext()).getPeople(url, people, new GetResultListener<ArrayList<PersonModel>>() {
            @Override
            public void getResults(ArrayList<PersonModel> result) {
                refreshPeople.setRefreshing(false);
                if(result != null) {
                    //notify adapter of list data change
                    adapter.adapterDataSetChanged();
                } else {
                    showSnackbar(rootView, "Connection Error");
                }
            }
        });
    }

    private void postPerson(String name, String desc) {
        APIMethods.getInstance(getContext()).postPerson(url, name, desc, new GetResultListener<Boolean>() {
            @Override
            public void getResults(Boolean result) {
                if(result) {
                    clearPersonPanel();
                    hideSoftKeyboard();
                    showSnackbar(rootView, "Added a new person!");
                    getPeople();
                } else {
                    showSnackbar(rootView, "Adding a new person failed!");
                }
            }
        });
    }

    private void deletePerson(int id) {
        APIMethods.getInstance(getContext()).deletePerson(url, id, new GetResultListener<String>() {
            @Override
            public void getResults(String result) {
                if(result != null) {
                    getPeople();
                    showSnackbar(rootView, "Removed " + result + "!");
                } else {
                    showSnackbar(rootView, "Removing failed!");
                }
            }
        });
    }

    /**
     * Sliding up panel setup
     * @param panel reference to slidingUpPanel
     */
    private void setUpPersonPanel(final SlidingUpPanelLayout panel) {
        final EditText editName = panel.findViewById(R.id.add_person_name);
        final EditText editDesc = panel.findViewById(R.id.add_description);
        final TextView editDescCharCount = panel.findViewById(R.id.add_description_char_count);
        Button postPersonButton = panel.findViewById(R.id.add_person_button);


        // listen for panel state. Panel can be on screen (true) or off screen (false)
        panelOn = false;
        panel.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                /* set panelOn boolean when status changes
                 * Could do panelOn = !panelOn;
                */

                hideSoftKeyboard();
                if(newState == SlidingUpPanelLayout.PanelState.COLLAPSED || newState == SlidingUpPanelLayout.PanelState.HIDDEN) {
                    panelOn = false;
                } else {
                    panelOn = true;
                }
                fireListeners(panelOn);
            }
        });

        /*
            Restricted character limit on description to 255, here we display how many characters
            are used/255
         */
        editDesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                editDescCharCount.setText(editDesc.length() + "/255");
            }
        });
        /*
            Button on click.
            we send post to server on click!
         */
        postPersonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nameString = editName.getText().toString();
                String descString = editDesc.getText().toString();

                //Check user inputs
                if(!nameString.equals("")) {
                    if(!descString.equals("")) {
                        postPerson(nameString, descString);
                        addPeoplePanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    } else {
                        showSnackbar(rootView, "Description can't be empty!");
                    }
                } else {
                    showSnackbar(rootView, "Name can't be empty!");
                }
            }
        });
    }


    /**
     * clears slidingUpPanel
     */
    private void clearPersonPanel() {
        final EditText editName = addPeoplePanel.findViewById(R.id.add_person_name);
        final EditText editDesc = addPeoplePanel.findViewById(R.id.add_description);
        final TextView editDescCharCount = addPeoplePanel.findViewById(R.id.add_description_char_count);

        editName.setText("");
        editDesc.setText("");
        editDescCharCount.setText("0/255");

        editName.clearFocus();
        editDesc.clearFocus();
    }

    /*
     *  Hides soft keyboard, this method is required because of Umano panel's bug
     */
    private void hideSoftKeyboard() {
        InputMethodManager inputManager = (InputMethodManager)
                activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void showSnackbar(View v, String message) {
        Snackbar.make(v, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    /*
        fire fragment listeners
     */
    private void fireListeners(boolean panel) {
        for (PanelStateListener l : fragmentListener) {
            l.panelStateListener(panel);
        }
    }




    /*
        Callback listener
     */
    interface PanelStateListener {
        void panelStateListener(boolean stateShowing);
    }
}