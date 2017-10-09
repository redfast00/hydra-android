package be.ugent.zeus.hydra.data.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import be.ugent.zeus.hydra.data.auth.AuthenticatorActionException;
import be.ugent.zeus.hydra.data.network.exceptions.IOFailureException;
import be.ugent.zeus.hydra.data.sync.minerva.SyncBroadcast;
import be.ugent.zeus.hydra.repository.requests.RequestException;
import org.springframework.http.converter.HttpMessageNotReadableException;

/**
 * Abstract adapter that supports broadcasts and syncing the courses.
 *
 * To prevent implementation from having to handle errors every time, this class defines a new synchronisation method.
 * This class will call that method and catch and process errors.
 *
 * @author Niko Strijbol
 */
public abstract class AbstractAdapter extends AbstractThreadedSyncAdapter {

    private static final String TAG = "AbstractAdapter";

    /**
     * This is a boolean flag; and is false by default.
     *
     * Indicate that this is the first synchronisation for an account. This will prompt a removal of any present data,
     * since Android sometimes deletes accounts without removing data.
     *
     * It will also suppress notifications about newly synchronised items, regardless of the user settings. When syncing
     * for the first time, the user does not want to be bombarded with notifications about new announcements.
     */
    public static final String EXTRA_FIRST_SYNC = "firstSync";

    /**
     * Indicates that the sync has been cancelled. This value should be checked regularly during synchronisation, and
     * implementations must stop syncing if the value is true.
     */
    protected boolean isCancelled;

    protected final SyncBroadcast broadcast;

    public AbstractAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        broadcast = new SyncBroadcast(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        // The sync is no longer cancelled.
        isCancelled = false;
        Log.i(TAG, "Starting Minerva synchronisation...");

        if (isCancelled) {
            broadcast.publishIntent(SyncBroadcast.SYNC_CANCELLED);
            return;
        }

        final boolean isFirstSync = extras.getBoolean(EXTRA_FIRST_SYNC, false);

        try {
            broadcast.publishIntent(SyncBroadcast.SYNC_START);

            onPerformCheckedSync(account, extras, authority, provider, syncResult, isFirstSync);

            if (isCancelled) {
                broadcast.publishIntent(SyncBroadcast.SYNC_CANCELLED);
                return;
            }

            broadcast.publishIntent(SyncBroadcast.SYNC_DONE);

        } catch (IOFailureException e) {
            Log.i(TAG, "IO error while syncing.", e);
            syncResult.stats.numIoExceptions++;
            broadcast.publishIntent(SyncBroadcast.SYNC_ERROR);
        } catch (AuthenticatorActionException e) {
            Log.w(TAG, "Auth exception while syncing.", e);
            syncResult.stats.numAuthExceptions++;
            broadcast.publishIntent(SyncBroadcast.SYNC_ERROR);
        } catch (RequestException e) {
            Log.w(TAG, "Exception during sync:", e);
            // TODO: this needs attention.
            syncResult.stats.numParseExceptions++;
            broadcast.publishIntent(SyncBroadcast.SYNC_ERROR);
        } catch (SQLException e) {
            Log.e(TAG, "Exception during sync:", e);
            syncResult.databaseError = true;
            broadcast.publishIntent(SyncBroadcast.SYNC_ERROR);
        } catch (HttpMessageNotReadableException e) {
            Log.e(TAG, "Exception during sync:", e);
            syncResult.stats.numParseExceptions++;
            broadcast.publishIntent(SyncBroadcast.SYNC_ERROR);
        }
    }

    @Override
    public void onSyncCanceled() {
        super.onSyncCanceled();
        isCancelled = true;
    }

    /**
     * Same as {@link #onPerformSync(Account, Bundle, String, ContentProviderClient, SyncResult)}, except various
     * exceptions are already catched and handled.
     *
     * @see #onPerformSync(Account, Bundle, String, ContentProviderClient, SyncResult)
     */
    protected abstract void onPerformCheckedSync(Account account,
                                                 Bundle extras,
                                                 String authority,
                                                 ContentProviderClient provider,
                                                 SyncResult results,
                                                 boolean isFirstSync) throws RequestException;
}