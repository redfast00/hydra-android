package be.ugent.zeus.hydra.ui.main.homefeed.content.schamper;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.domain.entities.SchamperArticle;
import be.ugent.zeus.hydra.domain.entities.homefeed.cards.SchamperCard;
import be.ugent.zeus.hydra.ui.SchamperArticleActivity;
import be.ugent.zeus.hydra.ui.main.homefeed.HomeFeedAdapter;
import be.ugent.zeus.hydra.ui.main.homefeed.content.FeedUtils;
import be.ugent.zeus.hydra.ui.main.homefeed.content.FeedViewHolder;
import be.ugent.zeus.hydra.domain.entities.homefeed.HomeCard;
import be.ugent.zeus.hydra.utils.DateUtils;

/**
 * Home feed view holder for Schamper articles.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
public class SchamperViewHolder extends FeedViewHolder {

    private static final String TAG = "SchamperViewHolder";

    private final TextView title;
    private final TextView date;
    private final TextView author;
    private final ImageView image;

    public SchamperViewHolder(View v, HomeFeedAdapter adapter) {
        super(v, adapter);
        title = v.findViewById(R.id.title);
        date = v.findViewById(R.id.date);
        author = v.findViewById(R.id.author);
        image = v.findViewById(R.id.image);
    }

    @Override
    public void populate(HomeCard card) {
        super.populate(card);

        SchamperArticle article = card.<SchamperCard>checkCard(HomeCard.CardType.SCHAMPER).getArticle();

        title.setText(article.getTitle());
        date.setText(DateUtils.relativeDateTimeString(article.getPubDate(), itemView.getContext()));
        author.setText(article.getAuthor());

        FeedUtils.loadThumbnail(itemView.getContext(), article.getImage(), image);

        this.itemView.setOnClickListener(v -> SchamperArticleActivity.viewArticle(v.getContext(), article, adapter.getCompanion().getHelper(), image));
    }
}