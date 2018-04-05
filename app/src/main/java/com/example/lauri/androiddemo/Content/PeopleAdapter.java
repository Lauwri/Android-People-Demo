package com.example.lauri.androiddemo.Content;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.lauri.androiddemo.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by doitn on 9.2.2018.
 */

public class PeopleAdapter extends  RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /**
     *  Using two types of holders, mark them 0 and 1
     *  Array list for items to display
     *  reference to header spinner
     */
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private ArrayList<PersonModel> objects;
    private Spinner headerSpinner;

    /**
     * Adapter constructor
     * @param arrayList list items
     */
    public PeopleAdapter(ArrayList<PersonModel> arrayList) {
        objects = arrayList;
        sortListDateDescending();
    }

    /**
     * Create view holders for corresponding types.
     * @param parent
     * @param viewType
     * @return view holder
     * throws new runtime exception if type doesn't match
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_header, parent, false);
            return new HeaderHolder(view);
        } else if(viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
            return new PeopleHolder(view);
        }
        throw new RuntimeException("List type error. Current type: " + viewType);
    }

    /**
     * When binding view holder to item, setup holder properties
     * For header, only spinner and its listener, when spinner itemSelected, sort the list
     * in appropriate manner and notify adapter of data set change
     *
     * For item view holder (PeopleHolder) set properties from list of duck objects
     *
     * @param holder holder to bind
     * @param position position of list item
     */

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderHolder) {
            HeaderHolder headerHolder = (HeaderHolder)holder;
            headerSpinner = headerHolder.spinner;
            headerHolder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int pos, long id) {
                    if(pos == 0) {
                        sortListDateDescending();
                        notifyDataSetChanged();
                    } else if(pos == 1) {
                        sortListDateAscending();
                        notifyDataSetChanged();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // TODO Auto-generated method stub
                }
            });

        } else if (holder instanceof PeopleHolder) {
            PeopleHolder peopleHolder = (PeopleHolder)holder;
            PersonModel model = getItem(position);

            DateFormat target = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");

            peopleHolder.date.setText(target.format(model.getDateTimeAdded()));
            peopleHolder.description.setText(model.getDescription());
            peopleHolder.name.setText(model.getName());
        }
    }

    /**
     * Return duck object corresponding position.
     * if position == 0, it's a header and we return null
     * @param position of list item
     * @return object in list position position.
     */
    private PersonModel getItem(int position) {

        if(position > 0) {
            return objects.get(position-1);
        } else {
            return null;
        }
    }

    /**
     * Custom adapter data set change notification.
     * Sorts list items in selected manner
     */
    public void adapterDataSetChanged() {
        if(objects.size() > 0) {
            int position = headerSpinner.getSelectedItemPosition();

            if(position == 0) {
                sortListDateDescending();
            } else if(position == 1) {
                sortListDateAscending();
            }
        }
        super.notifyDataSetChanged();
    }

    /**
     * Item's id = its position
     * @param position item position
     * @return item id
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Count of list items. This list ALWAYS has a header, so add 1 to
     * list objects size.
     * @return lists size
     */
    @Override
    public int getItemCount() {
        return objects.size()+1;
    }

    /**
     * View type to use, based on position
     * @param position position of list item
     * @return view type to use for this position
     *
     * If position is first in list, we return header type, else
     * return item type
     */
    @Override
    public int getItemViewType(int position) {
        if (position == TYPE_HEADER)
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    private void sortListDateAscending() {
        Collections.sort(objects, new Comparator<PersonModel>() {
            @Override
            public int compare(PersonModel o1, PersonModel o2) {
                return o1.getDateTimeAdded().compareTo(o2.getDateTimeAdded());
            }
        });
    }

    private void sortListDateDescending() {
        Collections.sort(objects, new Comparator<PersonModel>() {
            @Override
            public int compare(PersonModel o1, PersonModel o2) {
                return o2.getDateTimeAdded().compareTo(o1.getDateTimeAdded());
            }
        });
    }


    /**
     *  View holder class to host duck objects
     */
    public class PeopleHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView description;
        TextView name;

        public PeopleHolder(View itemView) {
            super(itemView);
            date = (TextView)itemView.findViewById(R.id.date);
            description = (TextView)itemView.findViewById(R.id.description);
            name = (TextView)itemView.findViewById(R.id.name);
        }
    }

    /**
     * View holder class to host header
     */
    public class HeaderHolder extends  RecyclerView.ViewHolder {
        TextView spinnerText;
        Spinner spinner;

        public HeaderHolder(View itemView) {
            super(itemView);
            spinnerText = (TextView)itemView.findViewById(R.id.header_arrange);
            spinner = (Spinner)itemView.findViewById(R.id.header_spinner);
        }
    }
}
