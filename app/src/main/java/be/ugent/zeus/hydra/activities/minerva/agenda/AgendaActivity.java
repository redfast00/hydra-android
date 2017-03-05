package be.ugent.zeus.hydra.activities.minerva.agenda;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.common.HydraActivity;
import be.ugent.zeus.hydra.loaders.LoaderPlugin;
import be.ugent.zeus.hydra.loaders.LoaderProvider;
import be.ugent.zeus.hydra.loaders.LoaderResult;
import be.ugent.zeus.hydra.minerva.agenda.AgendaDao;
import be.ugent.zeus.hydra.models.minerva.AgendaItem;
import be.ugent.zeus.hydra.plugins.ProgressBarPlugin;
import be.ugent.zeus.hydra.plugins.common.Plugin;
import be.ugent.zeus.hydra.utils.html.Utils;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * @author Niko Strijbol
 */
public class AgendaActivity extends HydraActivity implements LoaderProvider<AgendaItem> {

    private static final String ARG_AGENDA_ITEM = "argAgendaItem";
    private static final DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private LoaderPlugin<AgendaItem> plugin = new LoaderPlugin<>(this);
    private ProgressBarPlugin barPlugin = new ProgressBarPlugin();

    private int agendaItemId;
    private AgendaItem item;

    private View errorView;
    private View normalView;

    public static void start(Context context, int agendaId) {
        Intent intent = new Intent(context, AgendaActivity.class);
        intent.putExtra(ARG_AGENDA_ITEM, agendaId);
        context.startActivity(intent);
    }

    @Override
    protected void onAddPlugins(List<Plugin> plugins) {
        super.onAddPlugins(plugins);
        plugin.setDataCallback(this::onResult);
        plugin.addErrorListener(this::onError);
        plugin.addResetListener(this::onReset);
        plugin.addResultListener(barPlugin.getFinishedCallback());
        plugins.add(plugin);
        plugins.add(barPlugin);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minerva_agenda);

        Intent intent = getIntent();
        agendaItemId = intent.getIntExtra(ARG_AGENDA_ITEM, -1);

        errorView = $(R.id.error_view);
        normalView = $(R.id.normal_view);

        plugin.startLoader();
    }

    private void onResult(AgendaItem result) {

        errorView.setVisibility(GONE);
        normalView.setVisibility(VISIBLE);
        invalidateOptionsMenu();
        item = result;

        TextView title = $(R.id.title);
        title.setText(item.getTitle());
        getToolbar().setTitle(item.getTitle());

        //Description
        if (!TextUtils.isEmpty(item.getContent())) {
            TextView description = $(R.id.agenda_description);
            description.setText(Utils.fromHtml(item.getContent()));
        }

        if (TextUtils.isEmpty(item.getLocation())) {
            $(R.id.agenda_location_row).setVisibility(GONE);
            $(R.id.divider_below_location).setVisibility(GONE);
        } else {
            //TODO: onclick?
            TextView location = $(R.id.agenda_location);
            location.setText(item.getLocation());
        }

        TextView startTime = $(R.id.agenda_time_start);
        TextView endTime = $(R.id.agenda_time_end);

        startTime.setText(item.getStartDate().format(format));
        endTime.setText(item.getEndDate().format(format));

        TextView course = $(R.id.agenda_course);
        if (TextUtils.isEmpty(item.getCourse().getTitle())) {
            course.setText(item.getCourse().getCode());
        } else {
            course.setText(item.getCourse().getTitle());
        }

        TextView edit = $(R.id.agenda_organiser);
        edit.setText(item.getLastEditUser());
    }

    private void onError(Throwable error) {
        invalidateOptionsMenu();
        errorView.setVisibility(VISIBLE);
        normalView.setVisibility(GONE);
    }

    private void onReset(Loader<LoaderResult<AgendaItem>> loader) {
        this.item = null;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.minerva_agenda_add).setEnabled(item != null);
        menu.findItem(R.id.minerva_agenda_add).setVisible(item != null);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.minerva_agenda_add:
                addToCalendar();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_minerva_agenda, menu);
        tintToolbarIcons(menu, R.id.minerva_agenda_add);
        return super.onCreateOptionsMenu(menu);
    }

    private void addToCalendar() {
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, item.getStartDate().toInstant().toEpochMilli())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, item.getEndDate().toInstant().toEpochMilli())
                .putExtra(CalendarContract.Events.TITLE, item.getTitle())
                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);

        if (!TextUtils.isEmpty(item.getContent())) {
            intent.putExtra(CalendarContract.Events.DESCRIPTION, item.getContent());
        }

        if (!TextUtils.isEmpty(item.getLocation())) {
            intent.putExtra(CalendarContract.Events.EVENT_LOCATION, item.getLocation());
        }
        startActivity(intent);
    }

    @Override
    public Loader<LoaderResult<AgendaItem>> getLoader(Context context) {
        return new AgendaItemLoader(context, new AgendaDao(context), agendaItemId);
    }
}