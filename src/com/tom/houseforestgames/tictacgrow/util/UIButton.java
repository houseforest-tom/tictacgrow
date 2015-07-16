/**
 * <=========================================================================================>
 * File: UIButton.java
 * Created: 16.07.2015
 * Author: HAUSWALD, Tom.
 * <=========================================================================================>
 */

package com.tom.houseforestgames.tictacgrow.util;

import org.andengine.entity.sprite.Sprite;

import com.tom.houseforestgames.tictacgrow.scenes.GameScene;
import com.tom.houseforestgames.tictacgrow.scenes.SceneObject;

/**
 * Implements a simple button to incorporate within user interfaces.
 */
public class UIButton extends SceneObject
{

	// ===========================================================
	// Constants
	// ===========================================================

	// Duration of release animation
	public static final float RELEASE_TIME = 0.4f;

	// ===========================================================
	// Fields
	// ===========================================================

	// Button state
	private UIButtonState mState, mPrevState;

	// Time since last touch was released
	private float mTimeSinceRelease;

	// Button sprites (1 per state)
	private Sprite[] mSprites;

	// ===========================================================
	// Constructors
	// ===========================================================

	public UIButton( GameScene scene, float x, float y, float w, float h, String[] textureNames )
	{
		super( scene );
		mState = UIButtonState.IDLE;
		mPrevState = UIButtonState.IDLE;
		mTimeSinceRelease = 0.0f;

		setX( x );
		setY( y );
		setRotationCenterX( w / 2 );
		setRotationCenterY( h / 2 );

		// Load textures
		mSprites = new Sprite[UIButtonState.values().length];
		for( int sprite = 0; sprite < mSprites.length && sprite < textureNames.length; ++sprite )
		{
			mSprites[sprite] = new Sprite(
											0, 0, w, h,
											Utility.load( scene.getActivity(), textureNames[sprite], 512, 512 ), scene.getVBOManager() );
			mSprites[sprite].setVisible( sprite == 0 );
			attachChild( mSprites[sprite] );
		}
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// Was just touched
	public boolean touched()
	{
		return pressed() && mPrevState != UIButtonState.TOUCH;
	}

	// Was just released
	public boolean released()
	{
		return mState == UIButtonState.RELEASE && mTimeSinceRelease == 0.0f;
	}

	// Currently pressed
	public boolean pressed()
	{
		return mState == UIButtonState.TOUCH;
	}

	public float getWidth()
	{
		return mSprites[0].getWidth();
	}

	public float getHeight()
	{
		return mSprites[0].getHeight();
	}

	public void setWidth( float w )
	{
		for( int sprite = 0; sprite < mSprites.length; ++sprite )
		{
			mSprites[sprite].setWidth( w );
		}
	}

	public void setHeight( float h )
	{
		for( int sprite = 0; sprite < mSprites.length; ++sprite )
		{
			mSprites[sprite].setHeight( h );
		}
	}

	// ===========================================================
	// Override Methods
	// ===========================================================

	;;

	// ===========================================================
	// Methods
	// ===========================================================

	@Override
	public void onManagedUpdate( float dt )
	{
		// Touched this frame
		boolean touched = getScene().wasTouched( getX(), getY(), getWidth(), getHeight() );

		// Released this frame
		boolean released = getScene().wasTouchReleased();

		switch( mState )
		{
		// Currently idling
			case IDLE:
				if( touched )
				{
					// Touched -> go to touch state
					mState = UIButtonState.TOUCH;
				}
			break;

			// Currently touched
			case TOUCH:
				if( released )
				{
					// Touch was released -> go to release state
					mState = UIButtonState.RELEASE;
					mTimeSinceRelease = 0.0f;
				}
			break;

			// Released since a short amount of time
			case RELEASE:
				if( touched )
				{
					// Release interrupted -> go to touch state
					mState = UIButtonState.TOUCH;
					mTimeSinceRelease = 0.0f;
				}
				else
				{
					// Increase released time
					mTimeSinceRelease += dt;
					if( mTimeSinceRelease >= RELEASE_TIME )
					{
						// Released long enouh -> go to idle state
						mState = UIButtonState.IDLE;
						mTimeSinceRelease = 0.0f;
					}
				}
			break;
		}

		// Set active sprite according to state
		for( int sprite = 0; sprite < mSprites.length; ++sprite )
		{
			mSprites[sprite].setVisible( sprite == mState.ordinal() );
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	// Enumeration of possible button states
	public enum UIButtonState
	{
		IDLE,	// Not in use
		TOUCH,	// Currently touched
		RELEASE	// Shortly released
	}
}
