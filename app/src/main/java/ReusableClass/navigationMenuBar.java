package ReusableClass;

import android.content.Intent;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.memoria.ChatList;
import com.example.memoria.Classes;
import com.example.memoria.Home;
import com.example.memoria.R;
import com.example.memoria.createClass;
import com.example.memoria.singleClass;
import com.google.android.material.navigation.NavigationBarView;

public class navigationMenuBar {

    public void menuBar(NavigationBarView navigationMenu, final FragmentActivity activity) {
        navigationMenu.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            Integer id = item.getItemId();

            // Handle navigation item selections
            if (id == R.id.home) {
                // Do nothing since we're already on the Home activity
                Intent homeIntent = new Intent(activity, Home.class);
                activity.startActivity(homeIntent);
                return true; // Prevents unnecessary processing
            } else if (id == R.id.classes) {
                // Handle classes fragment (uncomment if needed)
                 selectedFragment = new Classes();
            } else if (id == R.id.message) {
                selectedFragment = new ChatList();
            } else if (id == R.id.singleClass) {
                selectedFragment = new singleClass();
            } else if (id == R.id.add) {
                selectedFragment = new createClass();
            }

            // Replace the fragment if one was selected
            if (selectedFragment != null) {
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, selectedFragment)
                        .commit();
            }

            return true;
        });
    }

}
