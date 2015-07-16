/**
 * <=========================================================================================>
 * File: SceneObject.java
 * Created: 16.07.2015
 * Author: HAUSWALD, Tom.
 * <=========================================================================================>
 */

package com.tom.houseforestgames.tictacgrow.scenes;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.Entity;

import com.tom.houseforestgames.tictacgrow.TicTacGrowActivity;

/**
 * Base class for all scene objects.
 */
public class SceneObject extends Entity implements IUpdateHandler
{

	// ===========================================================
	// Constants
	// ===========================================================

	;;

	// ===========================================================
	// Fields
	// ===========================================================

	// Parent scene
	private GameScene mScene;

	// ===========================================================
	// Constructors
	// ===========================================================

	public SceneObject( GameScene scene )
	{
		mScene = scene;
	}

	public GameScene getScene()
	{
		return mScene;
	}

	public TicTacGrowActivity getActivity()
	{
		return mScene.getActivity();
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	;;

	// ===========================================================
	// Override Methods
	// ===========================================================

	;;

	// ===========================================================
	// Methods
	// ===========================================================

	;;

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	;;
}
