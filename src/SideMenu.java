import java.awt.*;

public class SideMenu extends Menu {
    private int currentY, yOffset;

    SideMenu(Graphics2D g2, int width, int height) {
        super(g2, width, height, null, "");
        currentY = 0;
        yOffset = 0;
    }

    void addButtons(String[] labels, int heightOfButton) {
        for (String label : labels) {
            buttons.add(new ClickableButton(0, currentY + yOffset, width, heightOfButton, label, 24));
            currentY += heightOfButton;
        }
    }

    void onMouseMoved(double mx, double my, double prev_mx, double prev_my, double coordX, double coordY) {
        super.onMouseMoved(mx, my, prev_mx, prev_my);

        for(Button button: buttons) {
            if(button instanceof SampleLabelButton) {
                ((SampleLabelButton)button).hoverFromSample(coordX, coordY);
            }
        }
    }

    @Override
    public void onMouseScrolled(int rotation) {
        if(currentY <= height) return;
        double factor = -30*rotation;
        Button last = buttons.get(buttons.size()-1);

        if(buttons.get(0).getY()+factor > 0) {
            factor = -buttons.get(0).getY();
        } else if(last.getY()+last.getHeight()+factor < height) {
            factor = last.getY()+last.getHeight() - height;
        }

        for(Button button: buttons) {
            button.setY(button.getY()+factor);
        }

        yOffset = (int)buttons.get(0).getY();
    }

    String onReleased(double mx, double my) {
        // WARNING note that it is not the method inherited from interface and it returns String
        for(Button button: buttons) {
            button.setHover(mx, my,false);
            if(button.hasInside(mx, my)) {
                if(button instanceof ClickableButton) {
                    return button.getLabel();
                } else if(button instanceof Slider) {
                    ((Slider) button).setValue(mx);
                    return "";
                }
            }
        }
        return "";
    }

    public void addSlider(String title, double lowerBound, double upperBound, double height, boolean discrete) {
        buttons.add(new Slider(0, currentY+yOffset, width, (int)height, title, 20, lowerBound, upperBound, discrete));
        currentY += height;
    }

    public void addSampleLabel(Sample sample, double height) {
        buttons.add(new SampleLabelButton(0, currentY+yOffset, width, (int)height, sample, 15));
        currentY += height;
    }

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

    public void addMatrixLabel(Matrix2x2 matrix, double height) {
        buttons.add(new MatrixLabelButton(0, currentY+yOffset, width, (int)height, matrix, 17));
        currentY += height;
    }

    public double readValueFromSlider(String title) {
        for(Button button: buttons) {
            if(button instanceof Slider && button.getLabel().equals(title)) {
                return ((Slider)button).getValue();
            }
        }
        return 0;
    }

    public boolean hasInside(double mx, double my) {
        return mx < width && my < currentY;
    }

    public void addValueLabel(String title, String value, double buttonHeight) {
        buttons.add(new ValueLabelButton(0, currentY+yOffset, width, (int) buttonHeight, title, value, 20));
        currentY += buttonHeight;
    }

    public void updateLabel(String title, String value) {
        for(Button button: buttons) {
            if(button instanceof ValueLabelButton && ((ValueLabelButton) button).getTitle().equals(title)) {
                ((ValueLabelButton) button).updateValue(value);
            }
        }
    }
}
