package projekt.model;

import org.jetbrains.annotations.NotNull;
import org.tudalgo.algoutils.student.annotation.DoNotTouch;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A position in the grid using the axial coordinate system.
 *
 * @param q the q-coordinate
 * @param r the r-coordinate
 */
@DoNotTouch
public record TilePosition(int q, int r) implements Comparable<TilePosition> {

    /**
     * Calculates the s coordinate of this position.
     *
     * @return the s coordinate
     */
    public int s() {
        return -this.q - this.r;
    }

    /**
     * Scales up the given position by the given amount.
     *
     * @param position the position to scale
     * @param scale    the amount to scale by
     * @return a new scaled position
     */
    public static TilePosition scale(final TilePosition position, final int scale) {
        return new TilePosition(position.q * scale, position.r * scale);
    }

    /**
     * Adds two positions together.
     *
     * @param position1 the first position
     * @param position2 the second position
     * @return the newly calculated position
     */
    public static TilePosition add(final TilePosition position1, final TilePosition position2) {
        return new TilePosition(position1.q + position2.q, position1.r + position2.r);
    }

    /**
     * Subtracts two positions from each other.
     *
     * @param position1 the first position
     * @param position2 the second position
     * @return the newly calculated position
     */
    public static TilePosition subtract(final TilePosition position1, final TilePosition position2) {
        return new TilePosition(position1.q - position2.q, position1.r - position2.r);
    }

    /**
     * Returns the position of the neighbour in the given direction.
     *
     * @param position  the position to start from
     * @param direction the direction to go in
     * @return the position of the neighbour in the given direction
     */
    public static TilePosition neighbour(final TilePosition position, final EdgeDirection direction) {
        return TilePosition.add(position, direction.position);
    }

    /**
     * Returns all neighbours of the given position.
     *
     * @param position the position to get the neighbours of
     * @return all neighbours of the given position
     */
    public static Set<TilePosition> neighbours(final TilePosition position) {
        return Arrays.stream(EdgeDirection.values()).map(direction -> neighbour(position, direction))
            .collect(Collectors.toSet());
    }

    /**
     * Executes the given function on each {@link TilePosition} on a ring with the given
     * radius around the given center.
     *
     * @param center   the center of the ring
     * @param radius   the radius of the ring
     * @param function the function to execute, gets the current position and an
     *                 array with the radius, side index and tile index
     */
    public static void forEachRing(
        final TilePosition center, final int radius,
        final BiConsumer<TilePosition, Integer[]> function
    ) {
        if (radius == 0) {
            function.accept(center, new Integer[]{radius, 0, 0});
            return;
        }
        TilePosition current = TilePosition.add(center, TilePosition.scale(EdgeDirection.values()[4].position, radius));
        for (int side = 0; side < 6; side++) {
            for (int tile = 0; tile < radius; tile++) {
                function.accept(current, new Integer[]{radius, side, tile});
                current = TilePosition.neighbour(current, EdgeDirection.values()[side]);
            }
        }
    }

    /**
     * Executes the given function on each TilePosition on a spiral with the given
     * radius around the given center.
     *
     * @param center   the center of the spiral
     * @param radius   the radius of the spiral including the center
     * @param function the function to execute
     */
    public static void forEachSpiral(
        final TilePosition center, final int radius,
        final BiConsumer<TilePosition, Integer[]> function
    ) {
        for (int i = 0; i < radius; i++) {
            forEachRing(center, i, function);
        }
    }

    /**
     * The possible directions around a tile position other tiles may be placed.
     * The order of the directions is counterclockwise.
     */
    public enum EdgeDirection {
        EAST(new TilePosition(1, 0)),
        NORTH_EAST(new TilePosition(1, -1)),
        NORTH_WEST(new TilePosition(0, -1)),
        WEST(new TilePosition(-1, 0)),
        SOUTH_WEST(new TilePosition(-1, 1)),
        SOUTH_EAST(new TilePosition(0, 1));

        /**
         * The relative position this direction is pointing to; with q, r, s in [-1, 1].
         */
        public final TilePosition position;

        /**
         * The direction of the left intersection, as seen from the original tile position.
         *
         * @return the direction of the intersection
         */
        public IntersectionDirection getLeftIntersection() {
            return switch (this) {
                case NORTH_EAST -> IntersectionDirection.NORTH;
                case EAST -> IntersectionDirection.NORTH_EAST;
                case SOUTH_EAST -> IntersectionDirection.SOUTH_EAST;
                case SOUTH_WEST -> IntersectionDirection.SOUTH;
                case WEST -> IntersectionDirection.SOUTH_WEST;
                case NORTH_WEST -> IntersectionDirection.NORTH_WEST;
            };
        }

        /**
         * The direction of the right intersection, as seen from the original tile position.
         *
         * @return the direction of the intersection
         */
        public IntersectionDirection getRightIntersection() {
            return switch (this) {
                case NORTH_EAST -> IntersectionDirection.NORTH_EAST;
                case EAST -> IntersectionDirection.SOUTH_EAST;
                case SOUTH_EAST -> IntersectionDirection.SOUTH;
                case SOUTH_WEST -> IntersectionDirection.SOUTH_WEST;
                case WEST -> IntersectionDirection.NORTH_WEST;
                case NORTH_WEST -> IntersectionDirection.NORTH;
            };
        }

        /**
         * Calculates the edge direction from the given tile position relative to position (0, 0, 0).
         * The given tile position must satisfy "q, r, s in [-1, 1]".
         *
         * @param position the tile position to get the edge direction for
         * @return the direction of the edge
         */
        public static EdgeDirection fromRelativePosition(final TilePosition position) {
            return Arrays.stream(EdgeDirection.values())
                .filter(direction -> direction.position.equals(position))
                .findFirst()
                .orElseThrow();
        }

        /**
         * The direction of the third tile (left, as seen from the original tile position)
         * needed to derive an intersection's position.
         *
         * @return the direction of the third tile
         */
        public EdgeDirection left() {
            return getLeftIntersection().leftDirection;
        }

        /**
         * The direction of the third tile (right, as seen from the original tile position)
         * needed to derive an intersection's position.
         *
         * @return the direction of the third tile
         */
        public EdgeDirection right() {
            return getRightIntersection().rightDirection;
        }

        /**
         * Returns a stream of all possible edge directions.
         *
         * @return a stream of all possible edge directions
         */
        public static Stream<EdgeDirection> stream() {
            return Arrays.stream(EdgeDirection.values());
        }

        EdgeDirection(final TilePosition position) {
            this.position = position;
        }
    }

    /**
     * The possible directions around a tile position where its intersections are located.
     * The order of the directions is clockwise.
     */
    public enum IntersectionDirection {
        NORTH(EdgeDirection.NORTH_WEST, EdgeDirection.NORTH_EAST),
        NORTH_EAST(EdgeDirection.NORTH_EAST, EdgeDirection.EAST),
        SOUTH_EAST(EdgeDirection.EAST, EdgeDirection.SOUTH_EAST),
        SOUTH(EdgeDirection.SOUTH_EAST, EdgeDirection.SOUTH_WEST),
        SOUTH_WEST(EdgeDirection.SOUTH_WEST, EdgeDirection.WEST),
        NORTH_WEST(EdgeDirection.WEST, EdgeDirection.NORTH_WEST);

        /**
         * Direction of the third tile needed to derive the left intersection's position.
         */
        public final EdgeDirection leftDirection;

        /**
         * Direction of the third tile needed to derive the right intersection's position.
         */
        public final EdgeDirection rightDirection;

        IntersectionDirection(final EdgeDirection leftPosition, final EdgeDirection rightPosition) {
            this.leftDirection = leftPosition;
            this.rightDirection = rightPosition;
        }
    }

    /**
     * Compares a given tile position with this one.
     * Positions are first compared based on their q-coordinate, then based on their r-coordinate.
     * The general contract of {@link Comparable#compareTo(Object)} holds.
     *
     * @param otherPosition the tile position to compare
     */
    @Override
    public int compareTo(@NotNull final TilePosition otherPosition) {
        // top to bottom
        // left to right
        return Comparator.comparingInt(TilePosition::q)
            .thenComparingInt(TilePosition::r)
            .compare(this, otherPosition);
    }

    /**
     * Returns the string representation of this position.
     * The returned string has the format "(±q, ±r, ±s)".
     */
    @Override
    public String toString() {
        return String.format(
            "(%+d, %+d, %+d)",
            this.q,
            this.r,
            this.s()
        );
    }
}
