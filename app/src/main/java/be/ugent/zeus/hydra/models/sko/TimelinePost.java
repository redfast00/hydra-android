package be.ugent.zeus.hydra.models.sko;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;

import be.ugent.zeus.hydra.models.converters.ZonedThreeTenAdapter;
import be.ugent.zeus.hydra.utils.DateUtils;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZonedDateTime;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * A post on the timeline.
 *
 * @author Niko Strijbol
 */
@SuppressWarnings("unused")
public class TimelinePost implements Serializable {

    //Use string def for the post type.
    public static final String PHOTO = "photo";
    public static final String LINK = "link";
    public static final String VIDEO = "video";

    @StringDef({PHOTO, LINK, VIDEO})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PostType {}

    private String title;
    private String body;
    private String link;
    private String media;
    private String origin;
    @SerializedName("post_type")
    private String postType;
    private String poster;
    @SerializedName("created_at")
    @JsonAdapter(ZonedThreeTenAdapter.class)
    private ZonedDateTime createdAt;

    @Nullable
    public String getTitle() {
        return title;
    }

    @Nullable
    public String getBody() {
        return body;
    }

    @Nullable
    public String getLink() {
        return link;
    }

    @Nullable
    public String getMedia() {
        return media;
    }

    public String getOrigin() {
        return origin;
    }

    @PostType
    public String getPostType() {
        return postType;
    }

    @Nullable
    public String getPoster() {
        return poster;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getLocalCreatedAt() {
        return DateUtils.toLocalDateTime(getCreatedAt());
    }

    /**
     * @return The text of the type to display.
     */
    @NonNull
    public String getDisplayType() {
        switch (getPostType()) {
            case PHOTO:
                return "Foto";
            case LINK:
                return "Link";
            case VIDEO:
                return "Video";
            default:
                return "Andere";
        }
    }
}