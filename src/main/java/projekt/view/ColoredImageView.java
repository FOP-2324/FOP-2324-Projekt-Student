package projekt.view;

import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import org.tudalgo.algoutils.student.annotation.DoNotTouch;

/**
 * An ImageView that can be colored with the provided color.
 */
@DoNotTouch
public class ColoredImageView extends ImageView {
    /**
     * Creates a new ColoredImageView with the image at the given path and color.
     *
     * @param imagePath path to the image
     * @param color     color to colorize the image with
     */
    public ColoredImageView(final String imagePath, final Color color) {
        super(imagePath);
        colorize(color);
    }

    /**
     * Creates a new ColoredImageView with the given image and color.
     *
     * @param image image to display
     * @param color color to colorize the image with
     */
    public ColoredImageView(final Image image, final Color color) {
        super(image);
        colorize(color);
    }

    /**
     * Colors the image with the given color.
     *
     * @param color color to colorize the image with
     */
    private void colorize(final Color color) {
        if (color == null) {
            return;
        }

        final Lighting lighting = new Lighting();
        lighting.setDiffuseConstant(1.0);
        lighting.setSpecularConstant(0.0);
        lighting.setSpecularExponent(0.0);
        lighting.setSurfaceScale(0.0);

        // idk why but these values produces accurate colors, azimuth seems to not
        // matter that much
        lighting.setLight(new Light.Distant(0.0, 90.0, color));

        this.setEffect(lighting);
    }
}
