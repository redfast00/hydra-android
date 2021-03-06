package be.ugent.zeus.hydra.ui.resto.menu;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import be.ugent.zeus.hydra.data.models.resto.RestoMenu;
import be.ugent.zeus.hydra.utils.DateUtils;
import org.threeten.bp.LocalDate;

import java.util.Collections;
import java.util.List;

/**
 * This class provides the tabs in the resto activity.
 *
 * @author Niko Strijbol
 */
public class MenuPagerAdapter extends FragmentStatePagerAdapter {

    private List<RestoMenu> data = Collections.emptyList();

    public MenuPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setData(List<RestoMenu> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        return MenuFragment.newInstance(data.get(position));
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    public LocalDate getTabDate(int position) {
        return data.get(position).getDate();
    }

    /**
     * The position of a tab is the number of days from today the menu is for.
     * This gets the date from the activity, because at this point, it is not guaranteed
     * the fragments have a date already. The activity does already have the dates however,
     * or no fragments will be made.
     *
     * @param position Which position the tab is in.
     *
     * @return The title.
     */
    @Override
    public CharSequence getPageTitle(int position) {
        return DateUtils.getFriendlyDate(data.get(position).getDate());
    }
}