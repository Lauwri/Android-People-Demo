package com.example.lauri.androiddemo;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


/**
 * Created by Lauri Mattila on 9.2.2018.
 */

public class VolleyQueue {

    /**
     * Singleton pattern instance
     * Request queue and application context
     */
    private static VolleyQueue mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    /**
     * private constructor for singleton use
     * @param context application context
     */
    private VolleyQueue(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    /**
     * Singleton pattern for Networking only class
     * @param context application context
     * @return new or already existing instance
     */
    public static synchronized VolleyQueue getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleyQueue(context);
        }
        return mInstance;
    }

    /**
     * request queue setup and handling
     * @return requestqueue
     */
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}