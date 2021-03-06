package be.ugent.zeus.hydra.ui.resto;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.resto.Sandwich;
import be.ugent.zeus.hydra.ui.common.recyclerview.adapters.MultiSelectDiffAdapter;
import be.ugent.zeus.hydra.ui.common.recyclerview.viewholders.DataViewHolder;
import net.cachapa.expandablelayout.ExpandableLayout;

/**
 * @author Niko Strijbol
 */
class SandwichHolder extends DataViewHolder<Pair<Sandwich, Boolean>> {

    private TextView name;
    private TextView smallPrice;
    private TextView mediumPrice;
    private ExpandableLayout expandableLayout;
    private TextView ingredients;
    private MultiSelectDiffAdapter<Sandwich> adapter;

    SandwichHolder(View itemView, MultiSelectDiffAdapter<Sandwich> adapter) {
        super(itemView);

        name = itemView.findViewById(R.id.sandwich_name);
        mediumPrice = itemView.findViewById(R.id.sandwich_price_medium);
        smallPrice = itemView.findViewById(R.id.sandwich_price_small);
        expandableLayout = itemView.findViewById(R.id.expandable_layout);
        ingredients = itemView.findViewById(R.id.sandwich_ingredients);
        this.adapter = adapter;
    }

    @Override
    public void populate(Pair<Sandwich, Boolean> data) {
        Context c = itemView.getContext();
        Sandwich sandwich = data.first;
        name.setText(sandwich.getName());
        mediumPrice.setText(String.format(c.getString(R.string.sandwich_price_medium), sandwich.getPriceMedium()));
        smallPrice.setText(String.format(c.getString(R.string.sandwich_price_small), sandwich.getPriceSmall()));
        String ingredientsString = TextUtils.join(", ", sandwich.getIngredients());
        ingredients.setText(String.format(c.getString(R.string.sandwich_ingredients), ingredientsString));

        expandableLayout.setExpanded(data.second, false);
        itemView.setOnClickListener(v -> {
            adapter.setChecked(getAdapterPosition());
            expandableLayout.toggle();
        });
    }
}
