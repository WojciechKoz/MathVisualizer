
public class Main {
    public static void main(String [] args) {
        new Window();
    }
}

/* FIXME
    Known bugs:
    -   mouse position while key pressing is not relative to the program window but to the screen
        (doesn't work when there is no fullscreen)
    -   Sometimes side menu doesn't stop scrolling down and instead it scrolls a little bit up.
        (usually works so its hard to find the cause)
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
    - KNN Algorithm
    - tutorial
    - scrollbar and show/hide sidemenu
    ...
    version 0.5
    - photos in message boxes
    - Theory of linear algebra chapter 1
    - sounds
    - Settings
    - polish language
    - Improve PCA (add 2d transformation)
    - ID-tree
    ...
    version 0.6
    - curve lines

 */
