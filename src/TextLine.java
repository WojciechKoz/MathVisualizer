public class TextLine implements Container {
    private int x, y;
    private final int fontSize;
    private final String text;

    TextLine(String text, int x, int y, int fontSize) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.fontSize = fontSize;
    }

    void draw(int upperBound, int lowerBound) {
        if(y < upperBound || y + getHeight() > lowerBound) return;
        DrawUtils.drawStringWithLeftAlignment(text, x, y + fontSize/2);
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public void setY(int newY) {
        y = newY;
    }

    void setX(int newX) {
        x = newX;
    }

    @Override
    public int getHeight() {
        return fontSize + 5; // line height + 5 pixels of free space
    }

    public void move(int dx, int dy) {
        x += dx;
        y += dy;
    }
}
