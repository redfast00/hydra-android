package be.ugent.zeus.hydra.data.sync;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * Define an implementation of ContentProvider that stubs out all methods. You cannot use this class directly; you
 * must inherit from it. You can then register that class in the manifest.
 *
 * @author Niko Strijbol
 */
public abstract class StubProvider extends ContentProvider {

    /**
     * @return Always true, indicating that the provider loaded correctly.
     */
    @Override
    public boolean onCreate() {
        return true;
    }

    /**
     * @return No type for MIME type.
     */
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    /**
     * @return No results.
     */
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    /**
     * @return Nothing.
     */
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        return null;
    }

    /**
     * @return Nothing is affected (0).
     */
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * @return No rows affected (0).
     */
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}