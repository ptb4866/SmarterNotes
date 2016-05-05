package edu.ucla.cs.sourcecodes;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.androidbelieve.drawerwithswipetabs.R;

public class MainActivity extends AppCompatActivity{
    DrawerLayout mDrawerLayout;
    NavigationView mBottomView;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         *Setup the  NavigationView
         */

             mBottomView = (NavigationView) findViewById(R.id.bottomView) ;

        /**
         * Lets inflate the very first fragment
         * Here , we are inflating the TabFragmentScreen1 as the first Fragment
         */

             mFragmentManager = getSupportFragmentManager();
             mFragmentTransaction = mFragmentManager.beginTransaction();
             mFragmentTransaction.replace(R.id.containerView,new TabFragmentScreen1()).commit();
        /**
         * Setup click events on the Navigation/Bottom View Items.
         */

             mBottomView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
             @Override
             public boolean onNavigationItemSelected(MenuItem menuItem) {



             //   mDrawerLayout.closeDrawers();

                 return false;
            }

        });



    }

}