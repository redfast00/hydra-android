package be.ugent.zeus.hydra.domain.entities.homefeed.cards;

import be.ugent.zeus.hydra.domain.entities.event.Event;
import be.ugent.zeus.hydra.domain.entities.homefeed.HomeCard;
import be.ugent.zeus.hydra.domain.entities.homefeed.PriorityUtils;
import java8.util.Objects;
import org.threeten.bp.Duration;
import org.threeten.bp.ZonedDateTime;

/**
 * Home card for {@link Event}.
 *
 * @author silox
 * @author Niko Strijbol
 */
public class EventCard extends HomeCard {

    private Event event;

    public EventCard(Event event) {
        this.event = event;
    }

    @Override
    public int getPriority() {
        Duration duration = Duration.between(ZonedDateTime.now(), event.getStart());
        //Add some to 24*30 for better ordering
        return PriorityUtils.lerp((int) duration.toHours(), 0, 744);
    }

    @Override
    public int getCardType() {
        return CardType.ACTIVITY;
    }

    public Event getEvent() {
        return event;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventCard eventCard = (EventCard) o;
        return Objects.equals(event, eventCard.event);
    }

    @Override
    public int hashCode() {
        return event.hashCode();
    }
}