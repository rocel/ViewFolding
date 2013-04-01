package com.example.viewfolding;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * An animation that rotates the view on the Y axis between two specified
 * angles. This animation also adds a translation on the Z axis (depth) to
 * improve the effect.
 */
public class CustomRotate3dAnimation extends Animation {
	public final static String TAG_END = "ANIMATION_END";
	
	public final static int DIRECTION_LEFT = 0;
	public final static int DIRECTION_RIGHT = 1;
	
	public static int GAP_TRICK = 2;
	
    private final float mFromDegrees;
    private final float mToDegrees;
    private final float mCenterX;
    private final float mCenterY;
    private final float mDepthZ;
    private final int mPosition;
    private final int mDirection;
	private final int mNbFolds;
	private final int mImageWidth;
	private final int mWidth;
    
    private Camera mCamera;

    /**
     * Creates a new 3D rotation on the Y axis. The rotation is defined by its
     * start angle and its end angle. Both angles are in degrees. The rotation
     * is performed around a center point on the 2D space, definied by a pair of
     * X and Y coordinates, called centerX and centerY. When the animation
     * starts, a translation on the Z axis (depth) is performed. The length of
     * the translation can be specified, as well as whether the translation
     * should be reversed in time.
     * 
     * @param fromDegrees
     *            the start angle of the 3D rotation
     * @param toDegrees
     *            the end angle of the 3D rotation
     * @param centerX
     *            the X center of the 3D rotation
     * @param centerY
     *            the Y center of the 3D rotation
     * @param reverse
     *            true if the translation should be reversed, false otherwise
     */
    public CustomRotate3dAnimation(float fromDegrees, float toDegrees, float centerX,
            float centerY, float depthZ, int position, int direction, int nbFolds,int imageWidth ) {
        mFromDegrees = fromDegrees;
        mToDegrees = toDegrees;
        mCenterX = centerX;
        mCenterY = centerY;
        mDepthZ = depthZ;
        mPosition = position;
        mDirection = direction;
        mNbFolds = nbFolds;
        mImageWidth = imageWidth;
        mWidth = mImageWidth / mNbFolds;
        GAP_TRICK = (int) ((Math.cos(toDegrees * Math.PI / 180) * mWidth)); 
        System.out.println("===================GAP " + GAP_TRICK);
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        mCamera = new Camera();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        final float fromDegrees = mFromDegrees;
        float degrees = fromDegrees + ((mToDegrees - fromDegrees) * interpolatedTime);

        final float centerX = mCenterX;
        final float centerY = mCenterY;
        final Camera camera = mCamera;

        final Matrix matrix = t.getMatrix();
    	
        camera.save();

        camera.rotateY(degrees);
        camera.getMatrix(matrix);
        camera.restore();
        
		int decal = (int) (
				(mWidth - (Math.cos(degrees * Math.PI / 180) * mWidth)) * mPosition
		) ;
		
        matrix.preTranslate(-centerX, -centerY);
        camera.getMatrix(matrix);
        //TODO: DIRECTION CHECK IS WRONG, pos = 0 SHOULD GO TO RIGHT
        if(mPosition>1 && mDirection == DIRECTION_LEFT){
        	matrix.postTranslate(centerX-decal - GAP_TRICK, centerY);
        } else if(mPosition>1 && mDirection == DIRECTION_RIGHT){
        	matrix.postTranslate(centerX + decal + GAP_TRICK, centerY);
        } else {
        	matrix.postTranslate(centerX, centerY);
        }
        
		if (mPosition == 2) {
            Log.d(TAG_END,"interpolatedTime =" + interpolatedTime + " | "+  mImageWidth/mNbFolds + "  | decal = " + decal);
        }
    }
}