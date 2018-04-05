package com.example.lauri.androiddemo;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.lauri.androiddemo.Content.PersonModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class APIMethods {

    /**
     * Singleton pattern instance
     * Application context
     */
    private static APIMethods mInstance;
    private static Context mCtx;

    /**
     * private constructor for singleton use
     * @param context application context
     */
    private APIMethods(Context context) {
        mCtx = context;
    }

    /**
     * Singleton pattern for APIMethods only class
     * @param context application context
     * @return new or already existing instance
     */
    public static synchronized APIMethods getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new APIMethods(context);
        }
        return mInstance;
    }


    /**
     * Networking methods for getting duck sightings, species and posting a duck sighting
     */


    /**
     * post person to server
     * @param name selected duck specie
     * @param desc description for duck sighting
     * method expects none of the above are not null or empty
     */
    public void postPerson(final String url, String name, String desc, final GetResultListener<Boolean> listener) {
        String urlPost = url + "/people";

        JSONObject postParams = new JSONObject();

        try {
            //Person added time
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            final String postTime = df.format(c.getTime());

            postParams.put("dateTime", postTime);
            postParams.put("name", name);
            postParams.put("description", desc);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //The request for server and on response or error
        JsonObjectRequest postPersonRequest = new JsonObjectRequest(Request.Method.POST, urlPost, postParams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                listener.getResults(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.getResults(false);
            }
        });
        VolleyQueue.getInstance(mCtx).addToRequestQueue(postPersonRequest);
    }

    /**
     * method for getting people from server
     */

    public void getPeople(final ArrayList<PersonModel> people, String url, final GetResultListener<ArrayList<PersonModel>> listener) {
        //set the url
        String urlGet = url + "/people";

        //Request people from server
        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (Request.Method.GET, urlGet, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray array) {
                            people.clear();
                            parsePeopleFromJson(array, people);
                            listener.getResults(people);
                    }
                }, new Response.ErrorListener() {
                    //on error, show snacbar with connection error text
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.getResults(null);

                    }
                });
        // Access the RequestQueue through VolleyQueue class.
        VolleyQueue.getInstance(mCtx).addToRequestQueue(jsObjRequest);
    }

    /**
     * Parses people from JSON array
     * @param arr JSON array from server
     * @param people target array list
     * @return people list
     */
    private void parsePeopleFromJson(JSONArray arr, ArrayList<PersonModel> people) {
        // Loop through the array elements
        for(int i = 0; i < arr.length(); i++){
            try {
                // Get current json object
                JSONObject person = arr.getJSONObject(i);
                PersonModel model = parsePersonFromJSON(person);
                if(model != null) {
                    people.add(model);
                }
            } catch (JSONException e) {
                //just ignore errors here
            }
        }
    }

    /**
     * parses person from JSON object
     * @param obj object to parse from
     * @return people model
     */
    private PersonModel parsePersonFromJSON(JSONObject obj) {
        PersonModel model;
        try {

            int id = obj.getInt("id");
            String name = obj.getString("name");
            String description = obj.getString("description");
            String dateTime = obj.getString("dateTime");

            //format date from yyyy-MM-ddTHH:mm:ssZ
            DateFormat from = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            Date date;

            try {
                date = from.parse(dateTime);
            } catch (ParseException e) {
                return null;
            }
            model = new PersonModel(id, name, description, date);

            return model;
        } catch (JSONException e) {
            return null;
        }
    }
}
