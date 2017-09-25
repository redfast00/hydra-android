package be.ugent.zeus.hydra.domain.usecases.homefeed;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.util.Log;

import be.ugent.zeus.hydra.domain.entities.homefeed.HomeCard;
import be.ugent.zeus.hydra.domain.usecases.Executor;
import be.ugent.zeus.hydra.domain.usecases.UseCase;
import be.ugent.zeus.hydra.domain.requests.Result;
import be.ugent.zeus.hydra.ui.main.homefeed.FeedException;
import java8.util.stream.Collectors;
import java8.util.stream.RefStreams;
import java8.util.stream.Stream;
import java8.util.stream.StreamSupport;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * Get the home card.
 *
 * @author Niko Strijbol
 */
public class GetHomeFeed implements UseCase<Void, FeedLiveData>, FeedLiveData.OnRefreshListener {

    private final HomeFeedOptions options;
    private final Executor executor;
    private final FeedSourceProvider sourceProvider;

    private final transient Object feedLock = new Object();

    private Wrapper<Result<List<HomeCard>>> currentData;

    /**
     * The current data. Is null if there is no data yet.
     */
    @Nullable
    private FeedLiveData data;

    @Inject
    public GetHomeFeed(HomeFeedOptions options, @Named(Executor.BACKGROUND) Executor executor, FeedSourceProvider provider) {
        this.options = options;
        this.executor = executor;
        this.sourceProvider = provider;
    }

    @Override
    public FeedLiveData execute(Void ignored) {
        invalidate(Bundle.EMPTY);
        return data;
    }

    @MainThread
    private void invalidate(Bundle args) {
        if (data == null) {
            constructNewData();
        }

        Log.i("TEMP-FEED", "execute: Is this the main thread: " + (Looper.getMainLooper().getThread() == Thread.currentThread()));

        Set<Integer> errors = Collections.newSetFromMap(new ConcurrentHashMap<>());
        CountDownLatch latch = new CountDownLatch(sourceProvider.getCount());

        FeedSource.Args arguments = new FeedSource.Args(options, args);

        Log.i("TEMP-FEED-EXECUTE", "execute2: Is this the main thread: " + (Looper.getMainLooper().getThread() == Thread.currentThread()));
        for (FeedSource source : getSources()) {
            data.addSource(source.getCardType(), source.execute(arguments), new FeedObserver(currentData, data, source.getCardType(), errors, latch, executor, feedLock));
        }
    }

    @MainThread
    private void constructNewData() {
        data = new FeedLiveData(this);
        Result<List<HomeCard>> begin = new Result.Builder<List<HomeCard>>()
                .withData(Collections.emptyList())
                .buildPartial();
        currentData = new Wrapper<>();
        currentData.object = begin;
        data.setValue(begin);
    }

    @Override
    public void requestRefresh(Bundle args) {
        invalidate(args);
    }

    private static class FeedObserver implements Observer<Result<List<HomeCard>>> {

        private final Wrapper<Result<List<HomeCard>>> existing;
        @HomeCard.CardType
        private final int cardType;
        private final Set<Integer> errors;
        private final CountDownLatch latch;
        private final Executor executor;
        private final Object lock;
        private final MutableLiveData<Result<List<HomeCard>>> publisher;

        private FeedObserver(Wrapper<Result<List<HomeCard>>> existing, MutableLiveData<Result<List<HomeCard>>> publisher, @HomeCard.CardType int cardType, Set<Integer> errors, CountDownLatch latch, Executor executor, Object lock) {
            this.existing = existing;
            this.cardType = cardType;
            this.errors = errors;
            this.latch = latch;
            this.executor = executor;
            this.lock = lock;
            this.publisher = publisher;
        }

        @Override
        public void onChanged(@Nullable Result<List<HomeCard>> listResult) {
            executor.execute(() -> doInBackground(listResult));
        }

        private void doInBackground(Result<List<HomeCard>> listResult) {
            Log.i("TEMP-FEED", "Observer: Is this the main thread: " + (Looper.myLooper() == Looper.getMainLooper()));

            // Only one thread can access the feed
            synchronized (lock) {
                // Get the existing data
                Result<List<HomeCard>> data = existing.object;
                // This should not be null.
                assert data != null;
                assert listResult != null;

                Result.Builder<List<HomeCard>> builder = new Result.Builder<>();

                if (data.hasException() || listResult.hasException()) {
                    if (listResult.hasException()) {
                        errors.add(cardType);
                    }
                    builder.withError(new FeedException(errors));
                }

                // There should always be data in the existing result, even if the data is an empty list.
                List<HomeCard> existingData = data.orElse(new ArrayList<>());
                // Remove existing cards for this type.
                Stream<HomeCard> temp = StreamSupport.stream(existingData)
                        .filter(c -> c.getCardType() != cardType);

                Stream<HomeCard> newData = StreamSupport.stream(listResult.orElse(Collections.emptyList()));

                builder.withData(RefStreams.concat(temp, newData)
                        .sorted()
                        .collect(Collectors.toList()));


                // If this is the last source to complete, set it to final.
                if (latch.getCount() == 1) {
                    existing.object = builder.build();
                } else {
                    existing.object = builder.buildPartial();
                }

                publisher.postValue(existing.object);
            }

            // Indicate this source has completed.
            latch.countDown();
        }
    }

    private List<FeedSource> getSources() {
        return sourceProvider.getAll();
    }

    private static class Wrapper<E> {
        public transient E object;
    }
}