package edu.ucla.cs.utils;

import android.content.Context;
import android.os.Parcel;


/**
 * Created by Plumillon Forge on 25/09/15.
 */
public class Tag implements Chip {
    private String mName;
    private int mType = 0;

    public Tag(String name, int type) {
        this(name);
        mType = type;
    }

    public Tag(String name) {
        mName = name;
    }

    @Override
    public String getText() {
        return mName;
    }

    public int getType() {
        return mType;
    }
}
