import java.awt.*;
import java.awt.event.KeyEvent;


/**
 * Small menu at the left hand side of the screen, inside the cartesian plane simulations.
 */
public class SideMenu extends Menu {
    // currentY is an offset of the last added button. next button is added in that place
    // yOffset is related with scrolling the menu. (its a y coordinate of first button) it is 0 or negative.
    private int currentY, yOffset;
    private final int smallFont, normalFont, bigFont;

    SideMenu(Graphics2D g2, int width, int height) {
        super(g2, width, height, null, "");
        currentY = 0;
        yOffset = 0;

        smallFont = (int)(width/10.0);
        normalFont = (int)(width/9.0);
        bigFont = (int)(width/8.0);
    }

    /**
     * adds list of clickable buttons. Their labels are given and all of them has the same height (also given value)
     * after each button currentY is updating.
     * @param labels - list of labels that will be on the buttons
     * @param heightOfButton - height of each added button
     */
    void addButtons(String[] labels, int heightOfButton) {
        for (String label : labels) {
            buttons.add(new ClickableButton(0, currentY + yOffset, width, heightOfButton, label, bigFont));
            currentY += heightOfButton;
        }
    }

    /**
     * adds list of checkbox buttons. Their labels are given and all of them has the same height (also given value)
     * after each button currentY is updating. All values of these buttons are given.
     * @param labels - list of labels that will be on the buttons
     * @param values - list of logical values of buttons
     * @param heightOfButton - height of each added button
     */
    void addCheckBoxButtons(String[] labels, Boolean[] values, int heightOfButton) {
        for (int i = 0; i < labels.length; i++) {
            buttons.add(new CheckBoxButton(0, currentY + yOffset, width, heightOfButton, labels[i], bigFont, values[i]));
            currentY += heightOfButton;
        }
    }

    /**
     * Performs Menu.onMouseMoved.
     * For each button checks if it is a button related with a sample
     * If so then checks if the sample is under the mouse. If so then changes color of the button.
     * @param mx - mouse X position
     * @param my - mouse Y position
     * @param prev_mx - mouse X position in the previous frame.
     * @param prev_my - mouse Y position in the previous frame.
     * @param simulatedX - mouse X position in the cartesian plane simulation
     * @param simulatedY - mouse Y position in the cartesian plane simulation
     */
    void onMouseMoved(double mx, double my, double prev_mx, double prev_my, double simulatedX, double simulatedY) {
        super.onMouseMoved(mx, my, prev_mx, prev_my);

        for(Button button: buttons) {
            if(button instanceof SampleLabelButton) {
                ((SampleLabelButton)button).hoverFromSample(simulatedX, simulatedY);
            }
        }
    }

    /**
     * moves each button in the direction of scrolling.
     * updates yOffset
     * @param rotation direction of scrolling. Can be either 1 [down] or -1 [up]
     */
    @Override
    public void onMouseScrolled(int rotation) {
        // there is no need to scrolling
        if(currentY <= height) return;

        // scrolls 30 pixel per event
        double factor = -30*rotation;
        Button last = buttons.get(buttons.size()-1);

        // checks if top of first button is under the screen top
        // or if bottom of last button is above the screen bottom
        // if so then aligns all button to that bounds.
        if(buttons.get(0).getY()+factor > 0) {
            factor = -buttons.get(0).getY();
        } else if(last.getY()+last.getHeight()+factor < height) {
            factor = last.getY()+last.getHeight() - height;
        }

        // moves each button by that factor
        for(Button button: buttons) {
            button.setY(button.getY()+factor);
        }

        // update the offset
        yOffset = (int)buttons.get(0).getY();
    }

    @Override
    public boolean onKeyPressed(KeyEvent event) {
        for(Button b: buttons) {
            if(b instanceof SampleLabelButton) {
                if(((SampleLabelButton) b).onKeyPressed(event)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * WARNING note that it is not the method inherited from interface and it returns String
     * Sets color of each button.
     * Checks if some button is under the mouse and if it is a Clickable button returns its label.
     * If it is a slider - updates its value and return "" means that no "action" button was pressed
     * @param mouseX - x coordinate of the mouse (in pixels)
     * @param mouseY - y coordinate of the mouse (in pixels)
     * @return label of pressed button or empty string if any button was pressed or pressed button has not any action
     */
    String onReleased(double mouseX, double mouseY) {
        for(Button button: buttons) {
            button.setHover(mouseX, mouseY,false);
            if(button.hasInside(mouseX, mouseY)) {
                if(button instanceof ClickableButton) {
                    return button.getLabel();
                } else if(button instanceof Slider) {
                    ((Slider) button).setValue(mouseX);
                    return "";
                }
            }
        }
        return "";
    }

    /**
     * Adds the slider to the menu. Updates the currentY variable.
     * @param title - title of the slider
     * @param lowerBound - minimal value of the slider ( when it is on the left side )
     * @param upperBound - maximal value of the slider ( when it is on the right side )
     * @param height - height of the button
     * @param discrete - if true then only integers can be returned from this slider else any real number.
     */
    public void addSlider(String title, double lowerBound, double upperBound, double height, boolean discrete) {
        buttons.add(new Slider(0, currentY+yOffset, width, (int)height, title, normalFont, 
                    lowerBound, upperBound, discrete));
        currentY += height;
    }

    /**
     * Adds label that describes some sample. Updates the currentY variable
     * @param sample - sample that will be described
     * @param height - height of the button
     */
    public void addSampleLabel(Sample sample, double height, boolean available) {
        buttons.add(new SampleLabelButton(0, currentY+yOffset, width, (int)height, sample, smallFont, available));
        currentY += height;
    }

    /**
     * removes a button that describes given sample. Y positions of buttons below him should be changed by its height.
     * @param sample - sample that describes the button that will be removed.
     */
    public void removeSampleLabel(Sample sample) {
        boolean buttonsAfter = false;
        int toRemove = -1;
        double heightOfRemovedButton = 0;

        for(int i = 0; i < buttons.size(); i++) {
            if(buttons.get(i) instanceof SampleLabelButton && ((SampleLabelButton)buttons.get(i)).getSample() == sample) {
                heightOfRemovedButton = buttons.get(i).getHeight();
                toRemove = i;
                buttonsAfter = true;
                continue;
            }
            if(buttonsAfter) {
                buttons.get(i).setY(buttons.get(i).getY() - heightOfRemovedButton);
            }
        }
        buttons.remove(toRemove);
        currentY -= heightOfRemovedButton;
    }

    /**
     * Adds label that describes a matrix
     * @param matrix - matrix that will be described by that button
     * @param height - height of that button
     */
    public void addMatrixLabel(Matrix2x2 matrix, double height) {
        buttons.add(new MatrixLabelButton(0, currentY+yOffset, width, (int)height, matrix, smallFont));
        currentY += height;
    }

    /**
     * Reads value of the slider that has a given title.
     * @param title - title of the slider
     * @return - value of the slider (should be between its lower bound and upper bound) and if its discrete then round
     *           to an integer). 0 if there are no such a slider.
     */
    public double readValueFromSlider(String title) {
        for(Button button: buttons) {
            if(button instanceof Slider && button.getLabel().equals(title)) {
                return ((Slider)button).getValue();
            }
        }
        return 0;
    }

    /**
     * Checks whether mouse is inside the menu.
     * @param mouseX - x coordinate of the mouse (in pixels)
     * @param mouseY - y coordinate of the mouse (in pixels)
     * @return - true if mouse is inside the menu otherwise false
     */
    public boolean hasInside(double mouseX, double mouseY) {
        return mouseX < width && mouseY < currentY;
    }

    /**
     * Adds a button that will hold some value. CurrentY is updated.
     * @param title - title of that button.
     * @param value - initial value
     * @param buttonHeight - height of the button
     */
    public void addValueLabel(String title, String value, double buttonHeight) {
        buttons.add(new ValueLabelButton(0, currentY+yOffset, width, (int) buttonHeight, title, value, normalFont));
        currentY += buttonHeight;
    }

    /**
     * updates a value of the button with given title.
     * @param title - title of the button that will be updated
     * @param value - new value
     */
    public void updateLabel(String title, String value) {
        for(Button button: buttons) {
            if(button instanceof ValueLabelButton && ((ValueLabelButton) button).getTitle().equals(title)) {
                ((ValueLabelButton) button).updateValue(value);
            }
        }
    }

    public boolean focusingInputs(double mouseX, double mouseY) {
        for(Button b: buttons) {
            if(b instanceof SampleLabelButton) {
                ((SampleLabelButton) b).onLeftClick(mouseX, mouseY);
            }
        }
        return true;
    }
}
