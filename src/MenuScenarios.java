/**
 * Class that handles changing of menus when buttons are clicked
 */
class MenuScenarios {
    static Panel panel;

    // list of all buttons of all menus
    private static final String[] mainMenuButtons = new String[] {
            "First Steps",
            "Visualizations",
            "Theory",
            "Settings",
            "Exit"
    };

    private static final String[] visualizationButtons = new String[] {
            "Matrix Simulation",
            "Linear Regression",
            "Logistic Regression",
            "PCA Algorithm",
            "Back"
    };

    private static final String[] theoryButtons = new String[] {
            "Linear Algebra",
            "Calculus",
            "Sets & Graphs Theory",
            "Probabilistic & Statistic",
            "Machine Learning",
            "Back"
    };

    private static final String[] linearAlgebraButtons = new String[] {
            "Vectors",
            "Matrix as a system of equations",
            "Matrices geometrically",
            "Inverse of Matrix",
            "Four Subspaces of Matrix",
            "Determinant",
            "Eigenvectors & Eigenvalues",
            "Singular Value Decomposition",
            "Back"
    };

    /**
     * Creates the menu with given title and buttons labels and returns it
     * @param title - label that stands at the top of menu
     * @param buttons - list of labels on the buttons
     * @return new menu
     */
    private static GraphicsInterface createMenu(String title, String [] buttons) {
        Menu graphics = new Menu(panel.getG2(), panel.getWidth(), panel.getHeight(), panel, title);

        graphics.addButtons(buttons);

        return graphics;
    }

    /**
     * Checks which button was clicked in the main menu and performs corresponding action
     * @param buttonLabel - label that is on the pressed button
     * @return new menu or simulation that should appears after that button pressing
     */
    static GraphicsInterface mainMenuOptions(String buttonLabel) {
        switch(buttonLabel) {
            case "First Steps":
                return new CartesianPlane(panel.getG2(), panel.getWidth(), panel.getHeight(), panel);

            case "Visualizations":
                return createMenu("Visualizations", visualizationButtons);

            case "Theory":
                return createMenu("Theory", theoryButtons);

            case "Exit":
                panel.getWindow().dispose(); System.exit(0);

            default:
                return panel.getCurrentGraphics();
        }
    }

    /**
     * Checks which button was clicked in the visualizations menu and performs corresponding action
     * @param buttonLabel - label that is on the pressed button
     * @return new menu or simulation that should appears after that button pressing
     */
    static GraphicsInterface visualizationsMenuOptions(String buttonLabel) {
        switch(buttonLabel) {
            case "Matrix Simulation":
                return new MatrixCartesianPlane(panel.getG2(), panel.getWidth(), panel.getHeight(), panel);

            case "Linear Regression":
                return new LRCartesianPlane(panel.getG2(), panel.getWidth(), panel.getHeight(), panel);

            case "Logistic Regression":
                return new LogCartesianPlane(panel.getG2(), panel.getWidth(), panel.getHeight(), panel);

            case "PCA Algorithm":
                return new PCACartesianPlane(panel.getG2(), panel.getWidth(), panel.getHeight(), panel);

            default: // "Back"
                return createMenu(panel.getAppName(), mainMenuButtons);
        }
    }

    /**
     * Checks which button was clicked in the theory menu and performs corresponding action
     * @param buttonLabel - label that is on the pressed button
     * @return new menu or simulation that should appears after that button pressing
     */
    static GraphicsInterface theoryMenuOptions(String buttonLabel) {
        switch(buttonLabel) {
            case "Linear Algebra":
                return createMenu("Linear Algebra", linearAlgebraButtons);

            case "Back":
                return createMenu(panel.getAppName(), mainMenuButtons);

            default:
                return panel.getCurrentGraphics();
        }
    }

    /**
     * Checks which button was clicked in the linear algebra menu (theory of LA)
     * and performs corresponding action
     * @param buttonLabel - label that is on the pressed button
     * @return new menu or simulation that should appears after that button pressing
     */
    static GraphicsInterface LinearAlgebraMenuOptions(String buttonLabel) {
        if(buttonLabel.equals("Back")) {
            return createMenu("Theory", theoryButtons);
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
            return createMenu("Visualizations", visualizationButtons);
        }
        return createMenu(panel.getAppName(), mainMenuButtons);
    }


    static void setPanel(Panel givenPanel) {
        panel = givenPanel;
    }
}
