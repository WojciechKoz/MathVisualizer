/**
 * Class used in tutorials to move the camera and change the scale of simulation.
 * It doesn't move the camera instantly but creates an animation when camera goes
 * toward target point. The same thing with scale.
 */
public class AutoMotion {
    private final CoordinateSystem plane;
    // changes of camera position and scale value per frame
    private double cameraDX, cameraDY, scaleDifference;
    // how many time camera has to be moved or scale changed. goes to zero
    // and when iteration == 0 nothing moves
    private int cameraIteration, scaleIteration;

    AutoMotion(CoordinateSystem plane) {
        this.plane = plane;
        cameraDX = 0;
        cameraDY = 0;
        scaleDifference = 0;
        cameraIteration = 0;
        scaleIteration = 0;
    }

    /**
     * performs camera move and scale change if iterations are greater that 0.
     */
    void proceed() {
        if(cameraIteration > 0) {
            plane.camera.move(cameraDX, cameraDY);
            cameraIteration -= 1;
        }
        if(scaleIteration > 0) {
            plane.scale += scaleDifference;
            scaleIteration -= 1;
        }
    }

    /**
     * Sets target place of the camera. The camera will move toward this point.
     * howManyIteration means how many frame should be took by this animation.
     * If howManyIteration is a small number the animation will be faster.
     * @param target - destination point of the camera
     * @param howManyIterations - how many frames should take this animation
     */
    void setTargetPoint(Point2D target, int howManyIterations) {
        cameraDX = (target.x - plane.camera.x)/howManyIterations;
        cameraDY = (target.y - plane.camera.y)/howManyIterations;
        cameraIteration = howManyIterations;
    }

    /**
     * set target value of the scale of the simulation. Scale will slightly
     * change to reach this value.
     * howManyIteration means how many frame should be took by this animation.
     * If howManyIteration is a small number the animation will be faster.
     * @param target - target value of the scale
     * @param howManyIterations - how many frames should take this animation
     */
    void setTargetScale(double target, int howManyIterations) {
        scaleDifference = (target - plane.scale)/howManyIterations;
        scaleIteration = howManyIterations;
    }
}
