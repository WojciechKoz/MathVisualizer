public class AutoMotion {
    private final CoordinateSystem plane;
    private double cameraDX, cameraDY, scaleDifference;
    private int cameraIteration, scaleIteration;

    AutoMotion(CoordinateSystem plane) {
        this.plane = plane;
        cameraDX = 0;
        cameraDY = 0;
        scaleDifference = 0;
        cameraIteration = 0;
        scaleIteration = 0;
    }

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

    void setTargetPoint(Point2D target, int howManyIterations) {
        cameraDX = (target.x - plane.camera.x)/howManyIterations;
        cameraDY = (target.y - plane.camera.y)/howManyIterations;
        cameraIteration = howManyIterations;
    }

    void setTargetScale(double target, int howManyIterations) {
        scaleDifference = (target - plane.scale)/howManyIterations;
        scaleIteration = howManyIterations;
    }
}
