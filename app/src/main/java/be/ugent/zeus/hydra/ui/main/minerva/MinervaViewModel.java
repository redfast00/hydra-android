package be.ugent.zeus.hydra.ui.main.minerva;

import android.app.Application;
import android.util.Pair;

import be.ugent.zeus.hydra.data.models.minerva.Course;
import be.ugent.zeus.hydra.repository.data.BaseLiveData;
import be.ugent.zeus.hydra.repository.requests.Result;
import be.ugent.zeus.hydra.ui.common.RefreshViewModel;

import java.util.List;

/**
 * @author Niko Strijbol
 */
public class MinervaViewModel extends RefreshViewModel<List<Pair<Course, Integer>>> {

    public MinervaViewModel(Application application) {
        super(application);
    }

    @Override
    protected BaseLiveData<Result<List<Pair<Course, Integer>>>> constructDataInstance() {
        return new CourseLiveData(getApplication());
    }

    public void destroyInstance() {
        onCleared();
    }
}