package projekt.model.buildings;

import org.tudalgo.algoutils.student.annotation.DoNotTouch;
import projekt.model.ResourceType;

/**
 * Holds information on a port.
 * Players can trade {@code ratio} resources of the same type for one resource of any type
 * with the bank when they have access to a port. Specialized ports only accept trades when the player
 * offers them resources with the same type as {@code resourceType}.
 *
 * @param ratio        how many resources a player has to trade at this port
 * @param resourceType limit the resources that can be traded at this port to this resource type.
 *                     {@code null} means unrestricted
 */
@DoNotTouch
public record Port(int ratio, ResourceType resourceType) {

    /**
     * Constructs a new port with n:1 ratio, meaning that n resources of the same
     * type can be traded for 1 resource of any type.
     *
     * @param ratio amount of same-type resources to trade
     */
    public Port(final int ratio) {
        this(ratio, null);
    }
}
