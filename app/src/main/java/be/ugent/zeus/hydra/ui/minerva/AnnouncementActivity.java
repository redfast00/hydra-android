package be.ugent.zeus.hydra.ui.minerva;

import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.database.minerva.AnnouncementDao;
import be.ugent.zeus.hydra.data.models.minerva.Announcement;
import be.ugent.zeus.hydra.ui.common.BaseActivity;
import be.ugent.zeus.hydra.ui.common.html.PicassoImageGetter;
import be.ugent.zeus.hydra.ui.common.html.Utils;
import be.ugent.zeus.hydra.ui.minerva.overview.CourseActivity;
import be.ugent.zeus.hydra.ui.preferences.MinervaFragment;
import be.ugent.zeus.hydra.utils.DateUtils;
import be.ugent.zeus.hydra.utils.NetworkUtils;
import org.threeten.bp.ZonedDateTime;

/**
 * Show a Minerva announcement.
 *
 * If the announcement was marked as read, the activity will return a result. The resulting intent will a boolean named
 * {@link #RESULT_ANNOUNCEMENT_READ}. True indicates the announcement was marked as read, false indicates the
 * announcement has not been changed. In this second case the activity currently does not return a result, but this
 * might change in the future, so relying on the presence of a result alone is not sufficient.
 *
 * @author Niko Strijbol
 */
public class AnnouncementActivity extends BaseActivity {

    public static final String ARG_ANNOUNCEMENT = "announcement_view";

    public static final String RESULT_ANNOUNCEMENT_READ = "be.ugent.zeus.hydra.result.minerva.announcement.read";

    private static final String STATE_MARKED = "state_marked";

    private static final String ONLINE_URL_MOBILE = "https://minerva.ugent.be/mobile/courses/%s/announcement";
    private static final String ONLINE_URL_DESKTOP = "http://minerva.ugent.be/main/announcements/announcements.php?cidReq=%s";

    private Announcement announcement;
    private AnnouncementDao dao;
    private boolean marked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minerva_announcement);

        Intent intent = getIntent();
        announcement = intent.getParcelableExtra(ARG_ANNOUNCEMENT);

        dao = new AnnouncementDao(getApplicationContext());

        TextView title = findViewById(R.id.title);
        TextView date = findViewById(R.id.date);
        TextView text = findViewById(R.id.text);
        TextView author = findViewById(R.id.author);
        TextView course = findViewById(R.id.course);

        course.setText(announcement.getCourse().getTitle());

        if (announcement.getLecturer() != null) {
            author.setText(announcement.getLecturer());
        }

        if (announcement.getDate() != null) {
            date.setText(DateUtils.relativeDateTimeString(announcement.getDate(), this));
        }

        if (announcement.getContent() != null) {
            text.setText(Utils.fromHtml(announcement.getContent(), new PicassoImageGetter(text, getResources(), this)));
            text.setMovementMethod(LinkMovementMethod.getInstance());
        }

        if (announcement.getTitle() != null) {
            title.setText(announcement.getTitle());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.minerva_announcement_link:
                NetworkUtils.maybeLaunchBrowser(this, getOnlineUrl());
                return true;
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                upIntent.putExtra(CourseActivity.ARG_COURSE, (Parcelable) announcement.getCourse());
                if (NavUtils.shouldUpRecreateTask(this, upIntent) || isTaskRoot()) {
                    // This activity is NOT part of this app's task, so create a new task
                    // when navigating up, with a synthesized back stack.
                    TaskStackBuilder.create(this)
                            // Add all of this activity's parents to the back stack
                            .addNextIntentWithParentStack(upIntent)
                            // Navigate up to the closest parent
                            .startActivities();
                } else {
                    // To make sure we go to the correct activity, we add the flag.
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_minerva_announcement, menu);
        tintToolbarIcons(menu, R.id.minerva_announcement_link);
        return super.onCreateOptionsMenu(menu);
    }

    private String getOnlineUrl() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean(MinervaFragment.PREF_USE_MOBILE_URL, false)) {
            return String.format(ONLINE_URL_MOBILE, announcement.getCourse().getId());
        } else {
            return String.format(ONLINE_URL_DESKTOP, announcement.getCourse().getId());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Set the read date if needed
        if (!announcement.isRead()) {
            announcement.setRead(ZonedDateTime.now());
            setResult();
            AsyncTask.execute(() -> dao.update(announcement));
        }
    }

    private void setResult() {
        Intent intent = new Intent();
        intent.putExtra(RESULT_ANNOUNCEMENT_READ, true);
        setResult(RESULT_OK, intent);
        marked = true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_MARKED, marked);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState.getBoolean(STATE_MARKED, false)) {
            setResult();
        }
    }
}