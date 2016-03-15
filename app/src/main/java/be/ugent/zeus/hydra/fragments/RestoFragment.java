package be.ugent.zeus.hydra.fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.octo.android.robospice.GsonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.adapters.RestoCardAdapter;
import be.ugent.zeus.hydra.models.Resto.RestoMenuList;
import be.ugent.zeus.hydra.requests.RestoMenuOverviewRequest;

/**
 * Created by mivdnber on 2016-03-03.
 */

public class RestoFragment extends Fragment {
    protected SpiceManager spiceManager = new SpiceManager(GsonSpringAndroidSpiceService.class);
    private RecyclerView recyclerView;
    private RestoCardAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private View layout;

    @Override
    public void onStart() {
        super.onStart();
        spiceManager.start(getContext());
    }

    @Override
    public void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_resto, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.resto_cards_view);
        adapter = new RestoCardAdapter();
        recyclerView.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new StickyRecyclerHeadersDecoration((StickyRecyclerHeadersAdapter) adapter));
        performMenuRequest();

        return layout;
    }

    private void performMenuRequest() {
        final RestoMenuOverviewRequest r = new RestoMenuOverviewRequest();
        spiceManager.execute(r, r.getCacheKey(), r.getCacheDuration(), new RequestListener<RestoMenuList>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                showFailureSnackbar();
            }

            @Override
            public void onRequestSuccess(final RestoMenuList menuList) {
                adapter.setMenuList(menuList);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void showFailureSnackbar() {
        Snackbar
            .make(layout, "Oeps! Kon restomenu niet ophalen.", Snackbar.LENGTH_LONG)
            .setAction("Opnieuw proberen", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    performMenuRequest();
                }
            })
            .show();
    }
}