package projekt.model.buildings;

import org.tudalgo.algoutils.student.annotation.DoNotTouch;
import projekt.model.Intersection;
import projekt.model.Player;

/**
 * Represents a settlement, i.e. a village or city, built by a player.
 *
 * @param owner        the owner / builder of this settlement
 * @param type         the type of this settlement
 * @param intersection the intersection this settlement is placed on
 */
@DoNotTouch
public record Settlement(Player owner, Type type, Intersection intersection) {

    /**
     * Defines the different types of settlements.
     * The icons are determined by the order of the enum values.
     * first value: village
     * second value: city
     * potential third value: metropolis
     */
    public enum Type {
        VILLAGE(1),
        CITY(2);

        /**
         * The amount of resources that are produced by a settlement of this type.
         * Also used to determine how many victory points a settlement of this type is
         * worth.
         */
        public final int resourceAmount;

        Type(final int resourceAmount) {
            this.resourceAmount = resourceAmount;
        }
    }
}
