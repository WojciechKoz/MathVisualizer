
public class Main {
    public static final String VERSION = "V0.5";
    public static void main(String [] args) {
        new Window();
    }
}

/* FIXME
    Known bugs:
    -   mouse position while key pressing is not relative to the program window but to the screen
        (doesn't work when there is no fullscreen) [ fixed - now it uses previous values stored in panel ]
    -   Sometimes side menu doesn't stop scrolling down and instead it scrolls a little bit up.
        (usually works so its hard to find the cause) [ fixed - scrolling formula was changed ]
    -   PCA when all samples are on the same line (vertical or horizontal) then there is a NaN bug
 */

/* Classes waiting for refactoring
    - Shapes -> Arrow
    - MathUtils ( split into small classes )
    - MessageWindow merge two constructors and introduce some constant and comments
 */

/* TODO
    version 0.0
    - show plane origin in the center of the screen [checked]
    - draw only visible axes [checked]
    - project points onto x-axis (PCA) [checked]
    - scale [checked]
    - different color palette [checked]
    - some options via keyboard [checked]
    - make CartesianPlane the parent class of PCACartesianPlane etc [checked]
    ...
    version 0.1
    - only one panel [checked]
    - menu [checked]
    - buttons in menu and cartesian plane [checked]
    - switch on/off some visualization (e.g. covariance matrix) [checked]
    - linear regression [checked]
    - exit button in plane and menu panels [checked]
    - visualization of errors in linear regression [checked]
    ...
    version 0.2
    - Matrix2x2 class [checked]
    - improve efficiency of grid drawing [checked]
    - Matrix visualization [checked]
    - Matrix projection [checked]
    - determinant, inverse, transpose, eigenline visualizations [checked]
    - submenus [checked]
    - selectable samples and matrices [checked]
    ...
    version 0.3
    - logistic regression [checked]
    - hyperparameter value bars [checked]
    - information about samples [checked]
    - printing some value like error in linear regression or value of determinant [checked]
    - info about matrix and projected samples [checked]
    - scroll in sideMenu [checked]
    - scroll in menu (with transparency) [checked]
    ...
    version 0.4
    - refactor code [checked]
    - message box inside CartesianPlane [checked]
    - input numbers from keyboard [checked]
    - checkboxes buttons [checked]
    - KNN Algorithm [checked]
    - tutorial [checked]
    - show/hide side menu [checked]
    ...
    version 0.5
    - scrollbar [checked]
    - dialog window when exiting the simulation [checked]
    - themes [checked]
    - Settings [checked]
    - polish language [checked]
    - Improve PCA (add 2d transformation), matrix (info about inverse in sidemenu), LogReg (background of areas)
    - ID-tree
    ...
    version 0.6
    - curve lines
    - sounds
    - latex support in message boxes
    - Simulation of RB-Tree
    - Theory of linear algebra chapter 1
 */
