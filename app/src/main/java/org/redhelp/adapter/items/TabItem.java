package org.redhelp.adapter.items;


import android.support.v4.app.Fragment;

import java.io.Serializable;

/**
 * Created by harshis on 6/3/14.
 */
public class TabItem implements Serializable {
    public String name;
    //public int position;
    public Fragment fragment;
    public int size;

    public TabItem(String name, int position, Fragment fragment, int size) {
        this.name = name;
       // this.position = position;
        this.fragment = fragment;
        this.size = size;
    }
}
