package be.ugent.zeus.hydra.data.network.requests.sko;

import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.data.models.sko.TimelinePost;
import be.ugent.zeus.hydra.data.network.Endpoints;
import be.ugent.zeus.hydra.data.network.JsonSpringRequest;
import be.ugent.zeus.hydra.repository.Cache;
import be.ugent.zeus.hydra.repository.requests.CacheableRequest;

/**
 * Request for posts.
 *
 * @author Niko Strijbol
 */
public class TimelineRequest extends JsonSpringRequest<TimelinePost[]> implements CacheableRequest<TimelinePost[]> {

    private static final String FILE_NAME = "timeline.json";

    public TimelineRequest() {
        super(TimelinePost[].class);
    }

    @NonNull
    @Override
    public String getCacheKey() {
        return FILE_NAME;
    }

    @Override
    public long getCacheDuration() {
        return Cache.ONE_MINUTE * 15;
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return Endpoints.LIVE_SKO_URL + FILE_NAME;
    }
}