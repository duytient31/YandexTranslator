package com.app.vdlasov.yandextranslate.repository.local;

import com.app.vdlasov.yandextranslate.repository.AppDataStore;
import com.app.vdlasov.yandextranslate.repository.local.models.TranslatePhrase;
import com.app.vdlasov.yandextranslate.repository.local.models.TranslatePhraseSQLiteTypeMapping;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.impl.DefaultStorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.delete.DeleteResult;

import android.content.Context;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by Denis on 04.04.2017.
 */

public class AppLocalDataStore implements AppDataStore {

    private StorIOSQLite mStorIOSQLite;

    @Inject
    public AppLocalDataStore(Context context) {
        mStorIOSQLite = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(new DatabaseHelper(context))
                .addTypeMapping(TranslatePhrase.class,
                        new TranslatePhraseSQLiteTypeMapping())
                .build();
    }

    @Override
    public Observable<List<TranslatePhrase>> getTranslatePhrases() {

        return mStorIOSQLite.get()
                .listOfObjects(TranslatePhrase.class)
                .withQuery(DatabaseContract.TranslatePhraseTableMeta.QUERY_ALL_SORTED)
                .prepare()
                .asRxObservable();
    }

    public Observable<TranslatePhrase> getTranslateFromHistory(int id) {
        return mStorIOSQLite.get()
                .object(TranslatePhrase.class)
                .withQuery(DatabaseContract.TranslatePhraseTableMeta.QUERY_BY_ID(id))
                .prepare()
                .asRxObservable();
    }

    public Observable<TranslatePhrase> getTranslateFromHistory(String lang, String text) {
        return mStorIOSQLite.get()
                .object(TranslatePhrase.class)
                .withQuery(DatabaseContract.TranslatePhraseTableMeta.QUERY_BY_PRIMARY_AND_LANG(lang, text))
                .prepare()
                .asRxObservable();
    }

    public void saveTranslatePhrasesToDatabase(List<TranslatePhrase> history) {
        mStorIOSQLite.put()
                .objects(history)
                .withPutResolver(DatabaseContract.TranslatePhraseTableMeta.PUT_RESOLVER)
                .prepare()
                .executeAsBlocking();
    }

    public void saveTranslatePhraseToDatabase(TranslatePhrase phrase) {
        mStorIOSQLite.put()
                .object(phrase)
                .withPutResolver(DatabaseContract.TranslatePhraseTableMeta.PUT_RESOLVER)
                .prepare()
                .executeAsBlocking();
    }

    public void deleteTranslatePhraseFromDatabase(TranslatePhrase phrase) {
        mStorIOSQLite.delete()
                .object(phrase)
                .prepare()
                .executeAsBlocking();
    }

    public Observable<DeleteResult> deleteTranslatePhraseFromDatabase(int id) {
        return mStorIOSQLite.delete().byQuery(DatabaseContract.TranslatePhraseTableMeta.DELETE_BY_ID(id))
                .prepare()
                .asRxObservable();
    }

    public void deleteTranslatePhrasesFromDatabase(List<TranslatePhrase> history) {
        mStorIOSQLite.delete()
                .objects(history)
                .prepare()
                .executeAsBlocking();
    }

    public void deleteTranslatePhrasesFromDatabase() {
        mStorIOSQLite.delete()
                .byQuery(DatabaseContract.TranslatePhraseTableMeta.DELETE_ALL)
                .prepare()
                .executeAsBlocking();
    }
}
