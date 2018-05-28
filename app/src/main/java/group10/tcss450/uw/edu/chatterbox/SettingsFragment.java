package group10.tcss450.uw.edu.chatterbox;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    private static final String PREFS_THEME = "theme_pref";
    private static final String PREFS_FONT = "font_pref";

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Settings");
        } catch (NullPointerException e) {
            Log.e("Error", "title isn't working");
        }

        Button saveChanges = v.findViewById(R.id.settingsSaveChanges);

        RadioGroup themeRadioGroup = v.findViewById(R.id.settingsColorGroup);
        RadioGroup fontRadioGroup = v.findViewById(R.id.settingsFontGroup);

        //Load shared preference to set the default radio buttons selected for theme
        SharedPreferences themePrefs = getActivity().getApplicationContext().getSharedPreferences(PREFS_THEME, MODE_PRIVATE);
        int themeChoice = themePrefs.getInt(PREFS_THEME, 0);
        switch(themeChoice) {
            case 1:
                themeRadioGroup.check(R.id.settingsThemeOne);
                break;
            case 2:
                themeRadioGroup.check(R.id.settingsThemeTwo);
                break;
            case 3:
                themeRadioGroup.check(R.id.settingsThemeThree);
                break;
            default:
                themeRadioGroup.check(R.id.settingsThemeOne);
                break;

        }

        //Load shared preference to set the default radio buttons selected for font
        SharedPreferences fontPrefs = getActivity().getApplicationContext().getSharedPreferences(PREFS_FONT, MODE_PRIVATE);
        int fontChoice = fontPrefs.getInt(PREFS_FONT, 0);
        switch(fontChoice) {
            case 1:
                fontRadioGroup.check(R.id.settingsFontOne);
                break;
            case 2:
                fontRadioGroup.check(R.id.settingsFontTwo);
                break;
            case 3:
                fontRadioGroup.check(R.id.settingsFontThree);
                break;
            default:
                fontRadioGroup.check(R.id.settingsFontTwo);
                break;

        }


        //Shared preferences editor for choosing color
        SharedPreferences.Editor themeEditor = this.getActivity().getSharedPreferences(PREFS_THEME, MODE_PRIVATE).edit();
        //Shared preferences editor for choosing font
        SharedPreferences.Editor fontEditor = this.getActivity().getSharedPreferences(PREFS_FONT, MODE_PRIVATE).edit();

        /**
         * Saves settings changes
         */
        saveChanges.setOnClickListener(view -> {
                int themeButtonID = themeRadioGroup.getCheckedRadioButtonId();
                if(themeButtonID != -1) {
                    switch(themeButtonID) {
                        case R.id.settingsThemeOne:
                            themeEditor.putInt(PREFS_THEME, 1);
                            themeEditor.apply();
                            break;
                        case R.id.settingsThemeTwo:
                            themeEditor.putInt(PREFS_THEME, 2);
                            themeEditor.apply();
                            break;
                        case R.id.settingsThemeThree:
                            themeEditor.putInt(PREFS_THEME, 3);
                            themeEditor.apply();
                            break;
                        default:
                            themeEditor.putInt(PREFS_THEME, 2);
                            themeEditor.apply();
                            break;
                    }
                }



                int fontButtonID = fontRadioGroup.getCheckedRadioButtonId();
                if(fontButtonID != -1) {
                    switch(fontButtonID) {
                        case R.id.settingsFontOne:
                            fontEditor.putInt(PREFS_FONT, 1);
                            fontEditor.apply();
                            Log.e("Font stored", "1");
                            break;
                        case R.id.settingsFontTwo:
                            fontEditor.putInt(PREFS_FONT, 2);
                            fontEditor.apply();
                            Log.e("Font stored", "2");
                            break;
                        case R.id.settingsFontThree:
                            fontEditor.putInt(PREFS_FONT, 3);
                            fontEditor.apply();
                            Log.e("Font stored", "3");
                            break;
                    }
                }

                getActivity().recreate();
                Toast.makeText(getActivity(), "Settings applied and saved!", Toast.LENGTH_SHORT).show();
        });
        return v;
    }

}
