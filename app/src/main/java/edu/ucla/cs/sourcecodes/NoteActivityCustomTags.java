package edu.ucla.cs.sourcecodes;

/**
 * Created by ptb4866 on 5/20/16.
 */
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import co.hkm.soltag.TagContainerLayout;
import co.hkm.soltag.TagView;
import co.hkm.soltag.ext.LayouMode;

/**
 * Created by hesk on 1/2/16.
 *
 */


public enum NoteActivityCustomTags {





    ROUND_CORNER(R.id.tagcontainerLayout) {
        @Override
        protected TagContainerLayout define(final Activity activity) {

             container.setMode(setupModeCode);

            // Set custom click listener
            container.setOnTagClickListener(new TagView.OnTagClickListener() {
                @Override
                public void onTagClick(int position, String text) {

                   new AsyncTaskParseXML(text, context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    //Toast.makeText(activity, "click-position:" + position + ", text:" + text,
                     //       Toast.LENGTH_SHORT).show();

                    Toast.makeText(activity,  text + " selected: Finding Definition",  Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onTagLongClick(final int position, String text) {
                    AlertDialog dialog = new AlertDialog.Builder(activity)
                            .setTitle("long click")
                            .setMessage("You will delete this tag!")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    container.removeTag(position);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create();
                    dialog.show();
                }
            });

            return container;

        }

    } ;






    private int layoutid;
    public static TagContainerLayout container;
    protected LayouMode setupModeCode;
    private static Context context ;

    NoteActivityCustomTags(@IdRes final int layout) {
        layoutid = layout;
        setupModeCode = LayouMode.DEFAULT;
    }

    public NoteActivityCustomTags render(Activity view) {
        container = (TagContainerLayout) view.findViewById(layoutid);
        return this;
    }

    public NoteActivityCustomTags render(View view) {
        container = (TagContainerLayout) view.findViewById(layoutid);
        return this;
    }

    public NoteActivityCustomTags setMode(final LayouMode modecode) {
        setupModeCode = modecode;
        return this;
    }

    protected abstract TagContainerLayout define(final Activity act);

    public void setTag(String n) {
        container.addTag(n);
    }

    public static void setContext(final Context context) {
        NoteActivityCustomTags.context = context;

    }
    public void setTags(List<String> n) {
        container.setTags(n);
    }

    public void setTags(String... n) {
        container.setTags(n);
    }

}