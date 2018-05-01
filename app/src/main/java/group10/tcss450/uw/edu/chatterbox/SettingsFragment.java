package group10.tcss450.uw.edu.chatterbox;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

        Button saveChanges = v.findViewById(R.id.settingsSaveChanges);

        RadioGroup themeRadioGroup = v.findViewById(R.id.settingsColorGroup);
        RadioGroup fontRadioGroup = v.findViewById(R.id.settingsFontGroup);

        //Load shared preference to set the default radio buttons selected for theme
        SharedPreferences themePrefs = this.getActivity().getSharedPreferences(PREFS_THEME, MODE_PRIVATE);
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
        SharedPreferences fontPrefs = this.getActivity().getSharedPreferences(PREFS_FONT, MODE_PRIVATE);
        int fontChoice = fontPrefs.getInt(PREFS_FONT, 0);
        switch(fontChoice) {
            case 1:
                fontRadioGroup.check(R.id.settingsFontSmall);
                break;
            case 2:
                fontRadioGroup.check(R.id.settingsFontMedium);
                break;
            case 3:
                fontRadioGroup.check(R.id.settingsFontLarge);
                break;
            default:
                fontRadioGroup.check(R.id.settingsFontMedium);
                break;

        }


        //Shared preferences editor for choosing color
        SharedPreferences.Editor themeEditor = this.getActivity().getSharedPreferences(PREFS_THEME, MODE_PRIVATE).edit();
        //Shared preferences editor for choosing font
        SharedPreferences.Editor fontEditor = this.getActivity().getSharedPreferences(PREFS_FONT, MODE_PRIVATE).edit();

        saveChanges.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

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
                    }
                }

                int fontButtonID = fontRadioGroup.getCheckedRadioButtonId();
                if(fontButtonID != -1) {
                    switch(fontButtonID) {
                        case R.id.settingsFontSmall:
                            fontEditor.putInt(PREFS_FONT, 1);
                            fontEditor.apply();
                            break;
                        case R.id.settingsFontMedium:
                            fontEditor.putInt(PREFS_FONT, 2);
                            fontEditor.apply();
                            break;
                        case R.id.settingsFontLarge:
                            fontEditor.putInt(PREFS_FONT, 3);
                            fontEditor.apply();
                            break;
                    }
                }
                getActivity().recreate();
                Toast.makeText(getActivity(), "Settings applied and saved!", Toast.LENGTH_SHORT).show();
            }
        });
        return v;
    }

}
