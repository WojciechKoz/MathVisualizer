import java.util.Arrays;

/**
 * Class that handles changing of menus when buttons are clicked
 */
class MenuScenarios {
    static Panel panel;

    // list of all buttons of all menus
    private static String[] mainMenuButtons() {
        return new String[] {
                StringsResources.firstSteps(),
                StringsResources.visualizations(),
                StringsResources.theory(),
                StringsResources.settings(),
                StringsResources.exit()
        };
    }


    private static String[] visualizationButtons() {
        return new String[]{
                StringsResources.matrixSimulation(),
                StringsResources.linearRegression(),
                StringsResources.logisticRegression(),
                StringsResources.knn(),
                StringsResources.PCAAlgorithm(),
                StringsResources.back()
        };
    }

    private static String[] theoryButtons() {
        return new String[] {
            StringsResources.linearAlgebra(),
            StringsResources.calculus(),
            StringsResources.setsAndGraphsTheory(),
            StringsResources.probabilisticAndStatistic(),
            StringsResources.machineLearning(),
            StringsResources.back()
        };
    }

    private static String[] linearAlgebraButtons() {
        return new String[] {
            StringsResources.vectors(),
            StringsResources.matrixAsASystemOfEquation(),
            StringsResources.matricesGeometrically(),
            StringsResources.inverseOfMatrix(),
            StringsResources.fourSubspacesOfMatrix(),
            StringsResources.determinant(),
            StringsResources.eigenVectorsAndEigenValues(),
            StringsResources.singularValueDecomposition(),
            StringsResources.back()
        };
    }

    /**
     * Creates the menu with given title and buttons labels and returns it
     * @param title - label that stands at the top of menu
     * @param buttons - list of labels on the buttons
     * @return new menu
     */
    private static GraphicsInterface createMenu(String title, String [] buttons) {
        Menu graphics = new Menu(panel.getWidth(), panel.getHeight(), panel, title);

        graphics.addButtons(buttons);

        return graphics;
    }

    /**
     * Checks which button was clicked in the main menu and performs corresponding action
     * @param buttonLabel - label that is on the pressed button
     * @return new menu or simulation that should appears after that button pressing
     */
    static GraphicsInterface mainMenuOptions(String buttonLabel) {
        final String[] options = mainMenuButtons();

        if(buttonLabel.equals(options[0])) {
            return new LRTutorialCoordinateSystem(panel.getWidth(), panel.getHeight(), panel);
        }
        if(buttonLabel.equals(options[1])) {
            return createMenu(StringsResources.visualizations(), visualizationButtons());
        }
        if(buttonLabel.equals(options[2])) {
            return createMenu(StringsResources.theory(), theoryButtons());
        }
        if(buttonLabel.equals(options[3])) {
            return new SettingsMenu(panel.getWidth(), panel.getHeight(), panel);
        }
        if(buttonLabel.equals(options[4])) {
            panel.getWindow().dispose(); System.exit(0);
        }
        return panel.getCurrentGraphics();
    }

    /**
     * Checks which button was clicked in the visualizations menu and performs corresponding action
     * @param buttonLabel - label that is on the pressed button
     * @return new menu or simulation that should appears after that button pressing
     */
    static GraphicsInterface visualizationsMenuOptions(String buttonLabel) {
        String[] options = visualizationButtons();

        if(buttonLabel.equals(options[0])) {
            return new MatrixCoordinateSystem(panel.getWidth(), panel.getHeight(), panel);
        }
        if(buttonLabel.equals(options[1])) {
            return new LRCoordinateSystem(panel.getWidth(), panel.getHeight(), panel);
        }
        if(buttonLabel.equals(options[2])) {
            return new LogCoordinateSystem(panel.getWidth(), panel.getHeight(), panel);
        }
        if(buttonLabel.equals(options[3])) {
            return new KNNCoordinateSystem(panel.getWidth(), panel.getHeight(), panel);
        }
        if(buttonLabel.equals(options[4])) {
            return new PCACoordinateSystem(panel.getWidth(), panel.getHeight(), panel);
        }
        return createMenu(StringsResources.title(), mainMenuButtons());
    }

    /**
     * Checks which button was clicked in the theory menu and performs corresponding action
     * @param buttonLabel - label that is on the pressed button
     * @return new menu or simulation that should appears after that button pressing
     */
    static GraphicsInterface theoryMenuOptions(String buttonLabel) {
        if(buttonLabel.equals(StringsResources.linearAlgebra())) {
            return createMenu(StringsResources.linearAlgebra(), linearAlgebraButtons());
        }
        if(buttonLabel.equals(StringsResources.back())) {
            return createMenu(StringsResources.title(), mainMenuButtons());
        }
        return panel.getCurrentGraphics();
    }

    /**
     * Checks which button was clicked in the linear algebra menu (theory of LA)
     * and performs corresponding action
     * @param buttonLabel - label that is on the pressed button
     * @return new menu or simulation that should appears after that button pressing
     */
    static GraphicsInterface LinearAlgebraMenuOptions(String buttonLabel) {
        if(buttonLabel.equals(StringsResources.back())) {
            return createMenu(StringsResources.theory(), theoryButtons());
        }
        return panel.getCurrentGraphics();
    }

    /**
     * Checks the message that was send from simulation or Panel constructor and creates corresponding menu
     * @param buttonLabel - label that is on the pressed button
     * @return new menu or simulation that should appears after that button pressing
     */
    static GraphicsInterface blankOptions(String buttonLabel) {
        if ("Visualizations".equals(buttonLabel)) {
            return createMenu(StringsResources.visualizations(), visualizationButtons());
        }
        if("Main Menu".equals(buttonLabel)) {
            return createMenu(StringsResources.title(), mainMenuButtons());
        }
        if(buttonLabel.equals("tutorial-part-2")) {
            return new LogTutorialCoordinateSystem(panel.getWidth(), panel.getHeight(), panel);
        }
        return createMenu(StringsResources.title(), mainMenuButtons());
    }


    static void setPanel(Panel givenPanel) {
        panel = givenPanel;
    }
}
