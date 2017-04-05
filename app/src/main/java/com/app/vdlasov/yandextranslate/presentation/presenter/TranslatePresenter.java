package com.app.vdlasov.yandextranslate.presentation.presenter;


import com.app.vdlasov.yandextranslate.Config;
import com.app.vdlasov.yandextranslate.di.DI;
import com.app.vdlasov.yandextranslate.model.YandexTranslateResponse;
import com.app.vdlasov.yandextranslate.presentation.view.TranslateView;
import com.app.vdlasov.yandextranslate.repository.TranslateRepository;
import com.app.vdlasov.yandextranslate.repository.local.models.TranslatePhrase;
import com.app.vdlasov.yandextranslate.utils.Language;
import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import javax.inject.Inject;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

@InjectViewState
public class TranslatePresenter extends MvpPresenter<TranslateView> {

    @Inject
    TranslateRepository translateManager;

    public TranslatePresenter() {
        DI.getDI().getComponentManager().getBusinessLogicComponent().inject(this);
    }

    public void requestTranslatePhrase(final String langFrom, final String langTo, final String text) {
        String abbreviationLangFrom = Config.Lang_Abbreviation.get(Config.Lang_Names.indexOf(langFrom));
        String abbreviationLangTo = Config.Lang_Abbreviation.get(Config.Lang_Names.indexOf(langTo));
        translateManager.translate(abbreviationLangFrom + "-" + abbreviationLangTo, text)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<YandexTranslateResponse>() {
                    @Override
                    public void call(final YandexTranslateResponse yandexTranslateResponse) {
                        getViewState()
                                .showTranslatedPhrase(text, yandexTranslateResponse.text.get(0), langFrom, langTo);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(final Throwable throwable) {
                        if (throwable instanceof HttpException) {
                            // http, internet error
                            HttpException exception = (HttpException) throwable;
                            getViewState().showError(exception.code() + " " + exception.message());
                        } else {
                            // database, device error
                            getViewState().showError(throwable.getMessage());
                        }
                    }
                });
        //getViewState().showTranslatedPhrase(text.concat(" translated"));
    }

    public void loadFromDatabaseTranslatePhrase(int id) {
        translateManager.getTranslateFromHistory(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<TranslatePhrase>() {
                    @Override
                    public void call(final TranslatePhrase translatePhrase) {
                        getViewState().showTranslatedPhrase(
                                translatePhrase.getPrimary(),
                                translatePhrase.getTranslated(),
                                Config.Lang_Names.get(Config.Lang_Abbreviation
                                        .indexOf(Language.parseLangFrom(translatePhrase.getLang()))),
                                Config.Lang_Names.get(Config.Lang_Abbreviation
                                        .indexOf(Language.parseLangTo(translatePhrase.getLang()))));
                    }
                });
    }
}
