package com.app.vdlasov.yandextranslate.ui.fragment;

import com.app.vdlasov.yandextranslate.Config;
import com.app.vdlasov.yandextranslate.R;
import com.app.vdlasov.yandextranslate.presentation.presenter.TranslatePresenter;
import com.app.vdlasov.yandextranslate.presentation.view.TranslateView;
import com.app.vdlasov.yandextranslate.ui.common.MvpFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import static android.app.Activity.RESULT_OK;
import static com.app.vdlasov.yandextranslate.ui.activity.SelectLanguageActivity.SELECT_LANGUAGE_FROM;
import static com.app.vdlasov.yandextranslate.ui.activity.SelectLanguageActivity.SELECT_LANGUAGE_RESULT;
import static com.app.vdlasov.yandextranslate.ui.activity.SelectLanguageActivity.SELECT_LANGUAGE_TO;
import static com.app.vdlasov.yandextranslate.ui.activity.SelectLanguageActivity.getIntent;

public class TranslateFragment extends MvpFragment implements TranslateView, View.OnClickListener {

    public static final String TAG = "TranslateFragment";

    @InjectPresenter
    TranslatePresenter mTranslatePresenter;

    private EditText edtPhrase;

    private TextView tvResult;

    private Button btnLangFrom;

    private Button btnLangTo;

    private ImageButton btnLangSwitch;

    private String translateFromLang = "None";

    private String translateToLang = "None";

    // code for activity for result
    private static final int REQUEST_CODE_LANGUAGE_FROM = 1;

    private static final int REQUEST_CODE_LANGUAGE_TO = 2;

    public static TranslateFragment newInstance() {
        TranslateFragment fragment = new TranslateFragment();

        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_translate, container, false);
        edtPhrase = ((EditText) view.findViewById(R.id.fragment_translate_edit_text_phrase));
        tvResult = ((TextView) view.findViewById(R.id.fragment_translate_text_view_result));
        ((ImageButton) view.findViewById(R.id.fragment_translate_image_button_translate)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        mTranslatePresenter.requestTranslatePhrase(translateFromLang, translateToLang,
                                edtPhrase.getText().toString());
                    }
                });

        ((ImageButton) view.findViewById(R.id.fragment_translate_image_button_clear)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        clearView();
                    }
                }
        );

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        //setHasOptionsMenu(true);
        //activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnLangFrom = ((Button) view.findViewById(R.id.fragment_translate_button_lang_from));
        btnLangTo = ((Button) view.findViewById(R.id.fragment_translate_button_lang_to));
        btnLangSwitch = ((ImageButton) view.findViewById(R.id.fragment_translate_button_lang_switch));

        btnLangFrom.setOnClickListener(this);
        btnLangTo.setOnClickListener(this);
        btnLangSwitch.setOnClickListener(this);
        if (savedInstanceState == null) {
            translateFromLang = Config.Lang_Names.get(0);
            translateToLang = Config.Lang_Names.get(1);
            updateLanguages();
        }
        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void showTranslatedPhrase(final String result) {
        tvResult.setText(result);
    }

    @Override
    public void showError(final String message) {

    }


    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (resultCode == RESULT_OK) {
            String language = data.getStringExtra(SELECT_LANGUAGE_RESULT);
            switch (requestCode) {
                // switch languages if user select existing
                case REQUEST_CODE_LANGUAGE_FROM: {
                    if (language.equals(translateToLang)) {
                        translateToLang = translateFromLang;
                    }
                    translateFromLang = language;
                    break;
                }
                case REQUEST_CODE_LANGUAGE_TO: {
                    if (language.equals(translateFromLang)) {
                        translateFromLang = translateToLang;
                    }
                    translateToLang = language;
                    break;
                }
            }
            updateLanguages();
        }
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.fragment_translate_button_lang_from: {
                startActivityForResult(getIntent(getActivity(), SELECT_LANGUAGE_FROM), REQUEST_CODE_LANGUAGE_FROM);
                break;
            }
            case R.id.fragment_translate_button_lang_to: {
                startActivityForResult(getIntent(getActivity(), SELECT_LANGUAGE_TO), REQUEST_CODE_LANGUAGE_TO);
                break;
            }
            case R.id.fragment_translate_button_lang_switch: {
                String temp = translateToLang;
                translateToLang = translateFromLang;
                translateFromLang = temp;
                updateLanguages();
                break;
            }
        }
    }

    private void clearView() {
        edtPhrase.setText("");
        tvResult.setText("");
    }

    private void updateLanguages() {
        if (!translateFromLang.isEmpty()) {
            btnLangFrom.setText(translateFromLang);
        }
        if (!translateToLang.isEmpty()) {
            btnLangTo.setText(translateToLang);
        }
    }
}
