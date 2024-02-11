package projekt.view;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.IntegerStringConverter;

/**
 * A {@link TextField} that only accepts integer values.
 * The integer value is stored in a property.
 */
public class IntegerField extends TextField {
    private final IntegerProperty valueProperty = new SimpleIntegerProperty(0);

    /**
     * Creates a new integer field with an initial value of 0.
     */
    public IntegerField() {
        this(0);
    }

    /**
     * Creates a new integer field with the given initial value.
     *
     * @param initialValue The initial value.
     */
    public IntegerField(final int initialValue) {
        super(Integer.toString(initialValue));
        this.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 0, Utils.positiveIntegerFilter));
        textProperty().subscribe((oldText, newText) -> {
            if (newText.isEmpty()) {
                return;
            }
            try {
                valueProperty.set(Integer.parseInt(newText));
            } catch (final NumberFormatException e) {
                setText(oldText);
            }
        });
    }

    /**
     * Returns the property that holds the integer value.
     *
     * @return The property that holds the integer value.
     */
    public ReadOnlyIntegerProperty valueProperty() {
        return valueProperty;
    }

    /**
     * Sets the value of the integer field.
     *
     * @param value The new value.
     */
    public void setValue(final int value) {
        setText(Integer.toString(value));
    }

    /**
     * Sets the value of the integer field.
     *
     * @param value The new value.
     */
    public void setValue(final Number value) {
        setValue(value.intValue());
    }
}
