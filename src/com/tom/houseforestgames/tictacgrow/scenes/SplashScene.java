/*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*
 * <=========================================================================================>
 * File: 	 file.java
 * Created:  10.01.2015
 * Author:   HAUSWALD, Tom.
 * <=========================================================================================>
 *'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*
 * Description:
 * <=========================================================================================>
 * Displays the Houseforest Games logo and vanishes after the player touched the screen.
 * <=========================================================================================>
 *'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*/

package com.tom.houseforestgames.tictacgrow.scenes;

import org.andengine.entity.sprite.Sprite;

import com.tom.houseforestgames.tictacgrow.TicTacGrowActivity;
import com.tom.houseforestgames.tictacgrow.TicTacGrowActivity.RenderSurfaceRatio;
import com.tom.houseforestgames.tictacgrow.util.Utility;

public class SplashScene extends GameScene
{
	// ===========================================================
	// Constants
	// ===========================================================

	;;

	// ===========================================================
	// Fields
	// ===========================================================

	//splash texture
	private Sprite mBackground;

	// ===========================================================
	// Constructors
	// ===========================================================

	//instantiate new splash scene
	public SplashScene(TicTacGrowActivity activity)
	{
		super(activity);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	;;

	// ===========================================================
	// Override Methods
	// ===========================================================

	//load splash texture and show it upon setup
	@Override public void onSetup(final RenderSurfaceRatio ratio)
	{
		mBackground = new Sprite(
			0,
			0,
			TicTacGrowActivity.WIDTH,
			TicTacGrowActivity.HEIGHT,
			Utility.loadSuitRatio(mActivity, ratio, "splash.png"),
			mVBOMgr
		);

		attachChild(mBackground);
	}

	//enable touch upon activation
	@Override public void onActivated()
	{
		enableTouch();
		setVisible(true);
	}

	//vanish upon touch
	@Override public void onUpdate(float delta, float total)
	{
		if (wasTouched())
		{
			deactivate();
		}
	}

	//disable touch upon deactivation
	@Override public void onDeactivated()
	{
		disableTouch();
		setVisible(false);
	}

	//do nothin upon destruction
	@Override public void onDestroyed(){ ;; }

	// ===========================================================
	// Methods
	// ===========================================================

	;;

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	;;
}
