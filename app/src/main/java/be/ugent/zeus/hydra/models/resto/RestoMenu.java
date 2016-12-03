package be.ugent.zeus.hydra.models.resto;

import android.os.Parcel;
import android.os.Parcelable;
import be.ugent.zeus.hydra.models.converters.DateThreeTenAdapter;
import be.ugent.zeus.hydra.utils.Objects;
import com.google.gson.annotations.JsonAdapter;
import org.threeten.bp.LocalDate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Represents a menu for a single day.
 *
 * @author feliciaan
 * @author Niko Strijbol
 */
public class RestoMenu implements Parcelable, Serializable {

    private boolean open;
    @JsonAdapter(DateThreeTenAdapter.class)
    private LocalDate date;
    private List<RestoMeal> meals;
    private List<RestoMeal> sideDishes;
    private List<RestoMeal> mainDishes;
    private List<String> vegetables;

    /**
     * Sort the meals available in the menu.
     */
    private void fillCategories() {
        sideDishes = new ArrayList<>();
        mainDishes = new ArrayList<>();

        for(RestoMeal meal : meals) {
            if (meal.getType() == RestoMeal.MealType.SIDE) {
                sideDishes.add(meal);
            } else if (meal.getType() == RestoMeal.MealType.MAIN) {
                mainDishes.add(meal);
            }
        }
    }

    /**
     * @return If the resto is open.
     */
    public boolean isOpen() {
        return open;
    }

    /**
     * @see #isOpen()
     */
    public void setOpen(boolean open) {
        this.open = open;
    }

    /**
     * @return The meals available on this menu.
     */
    public List<RestoMeal> getMeals() {
        return meals;
    }

    public void setMeals(List<RestoMeal> meals) {
        this.meals = meals;
        fillCategories();
    }

    public List<String> getVegetables() {
        return vegetables;
    }

    public void setVegetables(List<String> vegetables) {
        this.vegetables = vegetables;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<RestoMeal> getSideDishes() {
        if (sideDishes == null) {
            fillCategories();
        }
        return sideDishes;
    }

    public List<RestoMeal> getMainDishes() {
        if (mainDishes == null) {
            fillCategories();
        }
        return mainDishes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (this.open ? 1 : 0));
        dest.writeLong(this.date != null ? this.date.toEpochDay() : -1);
        dest.writeList(this.meals);
        dest.writeStringList(this.vegetables);
    }

    protected RestoMenu(Parcel in) {
        this.open = in.readByte() != 0;
        long tmpDate = in.readLong();
        this.date = tmpDate == -1 ? null : LocalDate.ofEpochDay(tmpDate);
        this.meals = new ArrayList<>();
        in.readList(this.meals,RestoMeal.class.getClassLoader());
        this.vegetables = in.createStringArrayList();
        fillCategories();
    }

    public static final Parcelable.Creator<RestoMenu> CREATOR = new Parcelable.Creator<RestoMenu>() {
        @Override
        public RestoMenu createFromParcel(Parcel source) {
            return new RestoMenu(source);
        }

        @Override
        public RestoMenu[] newArray(int size) {
            return new RestoMenu[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestoMenu restoMenu = (RestoMenu) o;
        return open == restoMenu.open &&
                Objects.equals(date, restoMenu.date) &&
                Objects.equals(meals, restoMenu.meals);
    }

    @Override
    public int hashCode() {
        return Objects.hash(open, date, meals);
    }
}