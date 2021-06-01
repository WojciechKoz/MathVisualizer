import java.awt.*;
import java.awt.event.KeyEvent;

public class SettingsMenu implements GraphicsInterface {
    private Label settings;
    private Label languages;
    private CheckBoxButton englishLangButton;
    private CheckBoxButton polishLangButton;
    private Label themes;
    private CheckBoxButton darkThemeButton;
    private CheckBoxButton lightThemeButton;
    private ClickableButton backButton;
    private int width, height;
    private Panel panel;


    SettingsMenu(int width, int height, Panel panel) {
        this.width = width;
        this.height = height;
        this.panel = panel;
        update();
    }

    void update() {
        settings = new Label(StringsResources.settings(), width/2, (int)(height*0.1), 70);
        languages = new Label(StringsResources.languages(), width/2, (int)(height*0.2), 45);
        englishLangButton = new CheckBoxButton((int)(width*0.4), (int)(height*0.25), width/5, height/20, StringsResources.english(), 25, StringsResources.inEnglish());
        polishLangButton = new CheckBoxButton((int)(width*0.4), (int)(height*0.325), width/5, height/20, StringsResources.polish(), 25, StringsResources.inPolish());
        themes = new Label(StringsResources.themes(), width/2, height/2, 45);
        darkThemeButton = new CheckBoxButton((int)(width*0.4), (int)(height*0.55), width/5, height/20, StringsResources.dark(), 25, DrawUtils.darkMode);
        lightThemeButton = new CheckBoxButton((int)(width*0.4), (int)(height*0.625), width/5, height/20, StringsResources.light(), 25, !DrawUtils.darkMode);
        backButton = new ClickableButton((int)(width*0.4), (int)(height*0.8), width/5, height/20, StringsResources.back(), 25);
    }

    @Override
    public void draw() {
        settings.draw();
        languages.draw();

        englishLangButton.draw();
        polishLangButton.draw();

        themes.draw();

        darkThemeButton.draw();
        lightThemeButton.draw();

        backButton.draw();
    }

    @Override
    public void onRightClick(double mouseX, double mouseY) {

    }

    @Override
    public boolean onLeftClick(double mouseX, double mouseY) {
        polishLangButton.setHover(mouseX, mouseY, true);
        englishLangButton.setHover(mouseX, mouseY, true);
        darkThemeButton.setHover(mouseX, mouseY, true);
        lightThemeButton.setHover(mouseX, mouseY, true);
        backButton.setHover(mouseX, mouseY, true);
        return false;
    }

    @Override
    public void onLeftMouseButtonReleased(double mouseX, double mouseY) {
        // handle all buttons
        if(englishLangButton.hasInside(mouseX, mouseY)) {
            StringsResources.goEnglish();
            update();
            return;
        }

        if(polishLangButton.hasInside(mouseX, mouseY)) {
            StringsResources.goPolish();
            update();
            return;
        }

        if(darkThemeButton.hasInside(mouseX, mouseY)) {
            DrawUtils.goDarkMode();
            update();
            return;
        }

        if(lightThemeButton.hasInside(mouseX, mouseY)) {
            DrawUtils.goLightMode();
            update();
            return;
        }

        if(backButton.hasInside(mouseX, mouseY)) {
            panel.changeGraphics("", StringsResources.title());
        }
    }

    @Override
    public boolean onMouseDragged(double mouseX, double mouseY, double prevMouseX, double prevMouseY) {
        this.onLeftClick(mouseX, mouseY);
        return false;
    }

    @Override
    public void onMouseMoved(double mouseX, double mouseY, double prevMouseX, double prevMouseY) {
        polishLangButton.setHover(mouseX, mouseY, false);
        englishLangButton.setHover(mouseX, mouseY, false);
        darkThemeButton.setHover(mouseX, mouseY, false);
        lightThemeButton.setHover(mouseX, mouseY, false);
        backButton.setHover(mouseX, mouseY, false);
    }

    @Override
    public void onMouseScrolled(int rotation) {

    }

    @Override
    public boolean onKeyPressed(KeyEvent event) {
        return false;
    }
}
