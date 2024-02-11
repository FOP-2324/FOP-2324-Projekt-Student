package projekt.view;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.tudalgo.algoutils.student.annotation.DoNotTouch;

/**
 * A Sprite is used to display a single image from a sprite sheet and can be
 * dynamically changed.
 * The sprite sheet must be a rectangle a display all images in a single column.
 */
@DoNotTouch
public class Sprite extends ColoredImageView {
    private final IntegerProperty imageIndex;

    /**
     * Creates a new sprite with the given sprite sheet path and starting index.
     *
     * @param spriteFilePath The path to the sprite sheet.
     * @param startingIndex  The index of the first image in the sprite sheet.
     */
    public Sprite(final String spriteFilePath, final int startingIndex) {
        this(spriteFilePath, startingIndex, null);
    }

    /**
     * Creates a new sprite with the given sprite sheet path, starting index and
     * color.
     *
     * @param spriteFilePath The path to the sprite sheet.
     * @param startingIndex  The index of the first image in the sprite sheet.
     * @param color          The color of the sprite.
     */
    public Sprite(final String spriteFilePath, final int startingIndex, final Color color) {
        this(new Image(spriteFilePath), startingIndex, color);
    }

    /**
     * Creates a new sprite with the given sprite sheet, starting index and color.
     *
     * @param spriteSheet   The sprite sheet.
     * @param startingIndex The index of the first image in the sprite sheet.
     * @param color         The color of the sprite.
     */
    public Sprite(final Image spriteSheet, final int startingIndex, final Color color) {
        super(spriteSheet, color);
        this.imageIndex = new SimpleIntegerProperty(startingIndex);
        initialize();
    }

    /**
     * Initializes the srpite by setting the viewport and adding a listener to the
     * image index to move the viewport to the correct position.
     */
    private void initialize() {
        final double cellSize = getImage().getWidth();
        setPreserveRatio(true);
        setViewport(cellSize);
        imageIndex.addListener(
            observable -> setViewport(cellSize));
    }

    /**
     * Sets the viewport to the correct size and position for the current image.
     *
     * @param cellSize The size of a single cell in the sprite sheet.
     */
    private void setViewport(final double cellSize) {
        setViewport(new Rectangle2D(0, cellSize * imageIndex.get(), cellSize, cellSize));
    }

    /**
     * Returns the property that holds the index of the current image.
     *
     * @return The property that holds the index of the current image.
     */
    public IntegerProperty imageIndexProperty() {
        return imageIndex;
    }
}
