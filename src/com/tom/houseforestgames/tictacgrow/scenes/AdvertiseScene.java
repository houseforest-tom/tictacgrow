/*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*
 * <=========================================================================================>
 * File: 	 AdvertiseScene.java
 * Created:  12.01.2015
 * Author:   HAUSWALD, Tom.
 * <=========================================================================================>
 *'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*
 * Description:
 * <=========================================================================================>
 * This scene displays an interstitial ad if appropiate content could be loaded in the time 
 * before. By canceling the interstitial, the user gets back to the main menu.
 * <=========================================================================================>
 *'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*/
 
package com.tom.houseforestgames.tictacgrow.scenes;

import com.tom.houseforestgames.tictacgrow.TicTacGrowActivity;
import com.tom.houseforestgames.tictacgrow.TicTacGrowActivity.RenderSurfaceRatio;
import com.tom.houseforestgames.tictacgrow.util.RGBBackground;

public class AdvertiseScene extends GameScene
{
	// ===========================================================
	// Constants
	// ===========================================================

	;;
	
	// ===========================================================
	// Fields
	// ===========================================================

	;;
	
	// ===========================================================
	// Constructors
	// ===========================================================

	//contruct the scene
	public AdvertiseScene(TicTacGrowActivity activity)
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

	//on setup set background to black
	@Override public void onSetup(RenderSurfaceRatio ratio)
	{
		setBackground(new RGBBackground(0, 0, 0));
	}
	
	//upon activatio,n enable touch and show the interstitial if possible
	@Override public void onActivated()
	{
		enableTouch();
		mActivity.getAdmobManager().showInterstitialAd();
	}
	
	//check for interstitial state
	@Override public void onUpdate(float delta, float total)
	{
		if (mActivity.getAdmobManager().wasInterstitialAdClosed())
		{
			deactivate();
		}
	}
	
	//upon deactivation, disable touch
	@Override public void onDeactivated()
	{
		disableTouch();
	}
	
	//do nothing upon destruction
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
