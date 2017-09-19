package be.ugent.zeus.hydra.ui.main.schamper;

import android.app.Application;
import be.ugent.zeus.hydra.domain.entities.SchamperArticle;
import be.ugent.zeus.hydra.domain.requests.Request;
import be.ugent.zeus.hydra.repository.requests.Requests;
import be.ugent.zeus.hydra.data.network.requests.SchamperArticlesRequest;
import be.ugent.zeus.hydra.ui.common.RequestViewModel;

import java.util.Arrays;
import java.util.List;

/**
 * @author Niko Strijbol
 */
public class SchamperViewModel extends RequestViewModel<List<SchamperArticle>> {

    public SchamperViewModel(Application application) {
        super(application);
    }

    @Override
    protected Request<List<SchamperArticle>> getRequest() {
        return Requests.map(Requests.cache(getApplication(), new SchamperArticlesRequest()), Arrays::asList);
    }
}