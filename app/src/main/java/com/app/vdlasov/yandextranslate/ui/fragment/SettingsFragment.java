package com.app.vdlasov.yandextranslate.ui.fragment;

import com.app.vdlasov.yandextranslate.R;
import com.app.vdlasov.yandextranslate.di.DI;
import com.app.vdlasov.yandextranslate.repository.TranslateRepository;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.View;

import javax.inject.Inject;

/**
 * Created by Denis on 04.04.2017.
 */

public class SettingsFragment extends PreferenceFragmentCompat {

    @Inject
    TranslateRepository translateManager;

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DI.getDI().getComponentManager().getBusinessLogicComponent().inject(this);
    }

    @Override
    public void onCreatePreferences(final Bundle savedInstanceState, final String rootKey) {
        addPreferencesFromResource(R.xml.preferences);

        Preference button = findPreference(getString(R.string.preference_key_clear_history_button));
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(final Preference preference) {
                translateManager.clearTranslateHistory();
                return true;
            }
        });
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
