package be.ugent.zeus.hydra.minerva.announcement;

import android.app.NotificationManager;
import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.minerva.Announcement;
import be.ugent.zeus.hydra.models.minerva.Course;

import java.util.Collection;
import java.util.Iterator;

import static android.support.v4.app.NotificationCompat.CATEGORY_EMAIL;

/**
 * @author Niko Strijbol
 */
public class AnnouncementNotificationBuilder {

    private static final String TAG = "AnnounceNotiBuilder";
    private static final int NOTIFICATION_ID = 1;

    private final Context context;

    @DrawableRes
    private int smallIcon;
    private Collection<Announcement> announcements;
    private Course course;

    public AnnouncementNotificationBuilder(Context context) {
        this.context = context;

        //Set default values
        smallIcon = R.drawable.ic_notification_announcement;
    }
    
    public AnnouncementNotificationBuilder setSmallIcon(@DrawableRes int icon) {
        this.smallIcon = icon;
        return this;
    }

    public AnnouncementNotificationBuilder setAnnouncements(Collection<Announcement> announcements) {
        this.announcements = announcements;
        return this;
    }

    public AnnouncementNotificationBuilder setCourse(Course course) {
        this.course = course;
        return this;
    }

    public void publish() {

        if(announcements == null) {
            throw new IllegalStateException("Announcements must be set.");
        }

        if (course == null) {
            //TODO: should we do this or not?
            if(!announcements.isEmpty() && announcements.iterator().next().getCourse() != null) {
                this.course = announcements.iterator().next().getCourse();
            } else {
                throw new IllegalStateException("The course must be set.");
            }
        }

        Log.d(TAG, "Publishing notification");


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(smallIcon)
                .setCategory(CATEGORY_EMAIL);

        //For one message
        if(announcements.size() == 1) {
            publishOne(builder);
        } else {
            publishMultiple(builder);
        }

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(course.getId(), NOTIFICATION_ID, builder.build());
    }

    private NotificationCompat.Builder publishOne(NotificationCompat.Builder builder) {
        Announcement announcement = announcements.iterator().next();

        setTitle(builder);

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle(announcement.getTitle());
        bigTextStyle.bigText(announcement.getContent());
        bigTextStyle.setSummaryText(announcement.getLecturer());

        if(TextUtils.isEmpty(announcement.getTitle())) {
            builder.setContentText("Nieuwe aankondiging.");
        } else {
            builder.setContentText(announcement.getTitle());
        }

        builder.setStyle(bigTextStyle);
        return builder;
    }

    private NotificationCompat.Builder publishMultiple(NotificationCompat.Builder builder) {

        setTitle(builder);

        builder.setContentText(announcements.size() + " aankondigingen");

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        inboxStyle.setBigContentTitle(course.getTitle());
        Iterator<Announcement> iterator = announcements.iterator();
        for(int i = 0; i < 5 && iterator.hasNext(); i++) {
            inboxStyle.addLine(iterator.next().getTitle());
        }

        if(announcements.size() > 4) {
            inboxStyle.setSummaryText("nog " + (announcements.size() - 4) + " aankondigingen...");
        }

        builder.setStyle(inboxStyle);
        return builder;
    }

    private void setTitle(NotificationCompat.Builder builder) {
        if(TextUtils.isEmpty(course.getTitle())) {
            builder.setContentTitle("Aankondiging in " + course.getCode());
        } else {
            builder.setContentTitle(course.getTitle());
        }
    }
}