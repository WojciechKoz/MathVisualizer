import java.awt.event.KeyEvent;


/**
 * Small menu at the left hand side of the screen, inside the cartesian plane simulations.
 */
public class SideMenu extends Menu {
    private final int smallFont, normalFont, bigFont;

    SideMenu(int width, int height) {
        super(width, height, null, "");

        smallFont = 19;
        normalFont = 21;
        bigFont = 23;

    }

    @Override
    protected void initScrollbar() {
        scrollbar = new Scrollbar(0, height, 0, width, 0, 20, height);
    }

    /**
     * adds list of clickable buttons. Their labels are given and all of them has the same height (also given value)
     * after each button currentY is updating.
     * @param labels - list of labels that will be on the buttons
     * @param heightOfButton - height of each added button
     */
    void addButtons(String[] labels, int heightOfButton) {
        for (String label : labels) {
            buttons.add(new ClickableButton(0, scrollbar.getTotalHeight() + scrollbar.getCurrentShift(),
                    width, heightOfButton, label, bigFont));
            scrollbar.incrementTotalHeight(heightOfButton);
        }
    }

    @Override
    protected void setTransparency() { }

    @Override
    public void draw() {
        if(visible) {
            super.draw();
        } else {
            buttons.get(0).draw();
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
            buttons.add(new CheckBoxButton(0, scrollbar.getTotalHeight() + scrollbar.getCurrentShift(),
                    width, heightOfButton, labels[i], bigFont, values[i]));
            scrollbar.incrementTotalHeight(heightOfButton);
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
     * Runs when some key is pressed. For now only sample label button has input fields
     * so only these buttons are handling this event.
     * @param event - all information about pressed button
     * @return - true if some input was active, meaning that coordiante system simulation has to be refreshed.
     */
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
        scrollbar.setSelected(false);
        for(Button button: buttons) {
            button.setHover(mouseX, mouseY, false);
            if(button.hasInside(mouseX, mouseY)) {
                if(button.getLabel().equals(StringsResources.hide())) {
                    visible = !visible;
                }
                return button.onClicked(mouseX, mouseY);
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
        buttons.add(new Slider(0, scrollbar.getTotalHeight() + scrollbar.getCurrentShift(), width, (int)height, title, normalFont,
                    lowerBound, upperBound, discrete));
        scrollbar.incrementTotalHeight((int)height);
    }

    /**
     * Adds label that describes some sample. Updates the currentY variable
     * @param sample - sample that will be described
     * @param height - height of the button
     */
    public void addSampleLabel(Sample sample, double height, boolean available) {
        buttons.add(new SampleLabelButton(0, scrollbar.getTotalHeight() + scrollbar.getCurrentShift(), width, (int)height, sample, smallFont, available));
        scrollbar.incrementTotalHeight((int)height);
    }

    /**
     * removes a button that describes given sample. Y positions of buttons below him should be changed by its height.
     * @param sample - sample that describes the button that will be removed.
     */
    public void removeSampleLabel(Sample sample) {
        boolean buttonsAfter = false;
        int toRemove = -1;
        int heightOfRemovedButton = 0;

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
        scrollbar.decrementTotalHeight(heightOfRemovedButton, buttons);
    }

    /**
     * Adds label that describes a matrix
     * @param matrix - matrix that will be described by that button
     * @param height - height of that button
     */
    public void addMatrixLabel(Matrix2x2 matrix, double height) {
        buttons.add(new MatrixLabelButton(0, scrollbar.getTotalHeight() + scrollbar.getCurrentShift(), width, (int)height, matrix, smallFont));
        scrollbar.incrementTotalHeight((int)height);
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
        if(visible) {
            return mouseX < width + scrollbar.getWidth() && mouseY < scrollbar.getTotalHeight();
        }
        return mouseX < width && mouseY < buttons.get(0).getHeight();
    }

    /**
     * Adds a button that will hold some value. CurrentY is updated.
     * @param title - title of that button.
     * @param value - initial value
     * @param buttonHeight - height of the button
     */
    public void addValueLabel(String title, String value, double buttonHeight) {
        buttons.add(new ValueLabelButton(0, scrollbar.getTotalHeight() + scrollbar.getCurrentShift(), width, (int) buttonHeight, title, value, normalFont));
        scrollbar.incrementTotalHeight((int)buttonHeight);
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

    /**
     * When mouse is pressed it updates the availability of
     * each input fields that are inside the "sample label buttons"
     * @param mouseX - current x position of the mouse (in pixels)
     * @param mouseY - current y position of the mouse (in pixels)
     */
    public void focusingInputs(double mouseX, double mouseY) {
        for(Button b: buttons) {
            if(b instanceof SampleLabelButton) {
                ((SampleLabelButton) b).onLeftClick(mouseX, mouseY);
            }
        }
    }

    public void toggleCheckBoxButton(String label) {
        for(Button b: buttons) {
            if(b instanceof CheckBoxButton && b.getLabel().equals(label)) {
                ((CheckBoxButton)b).toggleValue();

                if(label.equals("Visible")) {
                    visible = !visible;
                }
            }
        }
    }

    public Scrollbar getScrollbar() {
        return scrollbar;
    }

    public void disableScrollbar() {
        scrollbar.setSelected(false);
    }
}
