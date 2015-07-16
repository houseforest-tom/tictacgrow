/* '*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*
 * <=========================================================================================>
 * File: GameScene.java
 * Created: 15.01.2015
 * Author: HAUSWALD, Tom.
 * <=========================================================================================>
 * '*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*
 * Description:
 * <=========================================================================================>
 * Provides the abstract interface for all types of scenes to appear during the game.
 * Also stores touch events that can be handled inside child classes.
 * <=========================================================================================>
 * '*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*' */

package com.tom.houseforestgames.tictacgrow.scenes;

import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.tom.houseforestgames.tictacgrow.TicTacGrowActivity;
import com.tom.houseforestgames.tictacgrow.TicTacGrowActivity.RenderSurfaceRatio;

public abstract class GameScene extends Scene implements ITouchArea
{
	// ===========================================================
	// Constants
	// ===========================================================

	;;

	// ===========================================================
	// Fields
	// ===========================================================

	// parent activity
	protected TicTacGrowActivity mActivity;

	// vertex buffer object manager
	protected VertexBufferObjectManager mVBOMgr;

	// touch event coordinates
	private float mTouchX, mTouchY;
	private float mReleaseX, mReleaseY;
	private float mDraggedX, mDraggedY;
	private boolean mTouched, mReleased, mDragged;
	private boolean mTouchEnabled;

	// scene active?
	protected boolean mActive;

	// ===========================================================
	// Constructors
	// ===========================================================

	// construct a new game scene
	public GameScene( TicTacGrowActivity activity )
	{
		mActive = false;
		mActivity = activity;
		mTouchEnabled = true;
		mVBOMgr = mActivity.getVertexBufferObjectManager();
		onSetup( activity.getAspectRatio() );
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// is the scene currently active
	public boolean isActive()
	{
		return mActive;
	}

	// was the screen touched during this scene (if touch enabled)
	public boolean wasTouched()
	{
		return mTouchEnabled && mTouched;
	}

	// was the screen released during this scene (if touch enabled)
	public boolean wasTouchReleased()
	{
		return mTouchEnabled && mReleased;
	}

	// was a finger dragged across the screen during this scene (if touch enabled)
	public boolean wasTouchDragged()
	{
		return mTouchEnabled && mDragged;
	}

	// return the VBO manager
	public VertexBufferObjectManager getVBOManager()
	{
		return mVBOMgr;
	}

	// return the aspect ratio
	public RenderSurfaceRatio getAspectRatio()
	{
		return mActivity.getAspectRatio();
	}

	// return the parent activty
	public TicTacGrowActivity getActivity()
	{
		return mActivity;
	}

	// ===========================================================
	// Override Methods
	// ===========================================================

	// scene was touched -> store location
	@Override
	public boolean onAreaTouched( TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY )
	{
		if( mTouchEnabled )
		{
			switch( pSceneTouchEvent.getAction() )
			{
				case TouchEvent.ACTION_DOWN:
					mTouched = true;
					mTouchX = pTouchAreaLocalX;
					mTouchY = pTouchAreaLocalY;
				break;

				case TouchEvent.ACTION_UP:
					mReleased = true;
					mReleaseX = pTouchAreaLocalX;
					mReleaseY = pTouchAreaLocalY;

					// dragged
					if( Math.abs( mTouchX - mReleaseX ) > 10.0 )
					{
						mDragged = true;
						mDraggedX = mReleaseX - mTouchX;
						mDraggedY = mReleaseY - mTouchY;
					}
				break;

				default:
				break;
			}

			return true;
		}

		return false;
	}

	// scenes should always be full-screen so each touch is contained within the area
	@Override
	public boolean contains( float pX, float pY )
	{
		return true;
	}

	// scenes should always be full-screen so no conversion neccessary
	@Override
	public float[] convertSceneToLocalCoordinates( float pX, float pY )
	{
		return new float[] { pX, pY };
	}

	// scenes should always be full-screen so no conversion neccessary
	@Override
	public float[] convertLocalToSceneCoordinates( float pX, float pY )
	{
		return new float[] { pX, pY };
	}

	// handle touch events each frame
	@Override
	public void onManagedUpdate( float pSecondsElapsed )
	{
		super.onManagedUpdate( pSecondsElapsed );

		if( mTouchEnabled )
		{
			mTouched = false;
			mReleased = false;
			mDragged = false;
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// called upon instantiation
	protected abstract void onSetup( final RenderSurfaceRatio ratio );

	// called each frame
	public abstract void onUpdate( float delta, float total );

	// called upon activation
	public abstract void onActivated();

	// called upon deactivation
	public abstract void onDeactivated();

	// called upon destruction / deallocation
	public abstract void onDestroyed();

	// activate the scene -> invoke listener
	public void activate()
	{
		mActive = true;
		onActivated();
	}

	// deactivate the scene -> invoke listener
	public void deactivate()
	{
		mActive = false;
		onDeactivated();
	}

	// destroy the scene -> invoke listener
	public void destroy()
	{
		if( mActive ) deactivate();
		onDestroyed();
		detachChildren();
	}

	// enable listening to touch events
	public void enableTouch()
	{
		// already enabled
		if( getTouchAreas().contains( this ) ) return;

		// setup scene touch area
		mTouched = mReleased = mDragged = false;
		mTouchX = mTouchY = mReleaseX = mReleaseY = 0;
		registerTouchArea( this );
		mTouchEnabled = true;
	}

	// disable listening to touch events
	public void disableTouch()
	{
		if( getTouchAreas().contains( this ) )
		{
			unregisterTouchArea( this );
		}
		mTouchEnabled = false;
	}

	// was rectangle area touched
	public boolean wasTouched( float x, float y, float w, float h )
	{
		return ( mTouchEnabled && mTouched && x <= mTouchX && mTouchX <= x + w && y <= mTouchY && mTouchY <= y + h );
	}

	// was touch in rectangle area released
	public boolean wasTouchReleased( float x, float y, float w, float h )
	{
		return ( mTouchEnabled && mReleased && x <= mReleaseX && mReleaseX <= x + w && y <= mReleaseY && mReleaseY <= y + h );
	}

	// return delta-x of drag movement
	public float getDraggedX()
	{
		return mDraggedX;
	}

	// return delta-y of drag movement
	public float getDraggedY()
	{
		return mDraggedY;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	;;
}
