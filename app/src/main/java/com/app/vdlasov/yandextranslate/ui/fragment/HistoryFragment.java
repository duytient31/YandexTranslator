package com.app.vdlasov.yandextranslate.ui.fragment;

import com.app.vdlasov.yandextranslate.R;
import com.app.vdlasov.yandextranslate.model.HistoryUiItem;
import com.app.vdlasov.yandextranslate.presentation.presenter.HistoryPresenter;
import com.app.vdlasov.yandextranslate.presentation.view.HistoryView;
import com.app.vdlasov.yandextranslate.ui.adapter.HistoryAdapter;
import com.app.vdlasov.yandextranslate.ui.common.MvpFragment;
import com.app.vdlasov.yandextranslate.ui.common.OnItemClickListener;
import com.app.vdlasov.yandextranslate.ui.common.RecyclerViewEmptySupport;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.jakewharton.rxbinding.widget.RxTextView;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.functions.Action1;

public class HistoryFragment extends MvpFragment implements HistoryView {

    public static final String TAG = "HistoryFragment";

    private RecyclerViewEmptySupport recyclerView;

    private HistoryAdapter adapter;

    private OnFragmentInteractionListener mListener;

    @InjectPresenter
    HistoryPresenter mHistoryPresenter;

    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();

        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        initRecyclerView(view);
        return view;
    }

    @Override
    public void showTranslateHistory(final List<HistoryUiItem> history) {
        adapter.add(history);
    }

    @Override
    public void showError(final String message) {
        View view = getView();
        if (view != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showError(@StringRes final int messageResID) {
        showError(getString(messageResID));
    }

    @Override
    public void showSuccess(final String message) {
        View view = getView();
        if (view != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showSuccess(@StringRes final int messageResID) {
        showSuccess(getString(messageResID));
    }

    private void initRecyclerView(final View view) {
        recyclerView = (RecyclerViewEmptySupport) view.findViewById(R.id.fragment_history_recycler_history);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setEmptyView(view.findViewById(R.id.fragment_history_text_view_list_empty));
        adapter = new HistoryAdapter(createDataset(), new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ///Toast.makeText(getContext(), "position = " + position, Toast.LENGTH_SHORT).show();
                mListener.onFragmentTranslateFromHistory(adapter.getItem(position).getId());
            }
        });
        recyclerView.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                    RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition(); //get position which is swipe

                if (direction == ItemTouchHelper.LEFT) {    //if swipe left

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()); //alert for confirm to delete
                    builder.setMessage(R.string.dialog_delete_history_title);    //set message

                    builder.setPositiveButton(R.string.dialog_delete_history_remove_button,
                            new DialogInterface.OnClickListener() {
                                //when click on DELETE
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mHistoryPresenter
                                            .deleteTranslatePhrasesFromDatabase(adapter.getItem(position).getId());
                                    adapter.remove(position);

                                    return;
                                }
                            }).setNegativeButton(R.string.dialog_delete_history_cancel_button,
                            new DialogInterface.OnClickListener() {
                                //not removing items if cancel is done
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    adapter.notifyItemRemoved(position + 1);
                                    //notifies the RecyclerView Adapter that data in adapter has been removed at a particular position.
                                    adapter.notifyItemRangeChanged(position, adapter.getItemCount());
                                    //notifies the RecyclerView Adapter that positions of element in adapter has been changed from position(removed element index to end of list), please update it.
                                    return;
                                }
                            }).show();
                    //show alert dialog
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView); //set swipe to recylcerview

        EditText edtFilter = (EditText) view.findViewById(R.id.fragment_history_edit_text_filter);

        unsubscribeOnDestroy(RxTextView
                .textChanges(edtFilter)
                .debounce(800, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<CharSequence>() {
                    @Override
                    public void call(final CharSequence charSequence) {
                        adapter.getFilter().filter(charSequence.toString());
                    }
                }));

        mHistoryPresenter.getTranslateHistory();
    }

    private List<HistoryUiItem> createDataset() {
        List<HistoryUiItem> array = new ArrayList<HistoryUiItem>();
//        array.add(new HistoryUiItem("Apple", "Яблоко", "en-ru"));
//        array.add(new HistoryUiItem("Яблоко", "Apple", "ru-en"));
//        array.add(new HistoryUiItem("Tree", "Дерево", "en-ru"));
//        array.add(new HistoryUiItem("Дерево", "Tree", "ru-en"));
        return array;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {

        void onFragmentTranslateFromHistory(Integer databaseId);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}
