/*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*
 * <=========================================================================================>
 * File: 	 TicTacGrowActivity.java
 * Created:  07.01.2015
 * Author:   HAUSWALD, Tom.
 * <=========================================================================================>
 *'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*
 * Description:
 * <=========================================================================================>
 * Main activity of the Tic, Tac, Grow app. Handles resolution measurement, layout and top-
 * level scene initialization. Also responsible for transitioning between game states, playing
 * sound effects and handling system-side events.
 * <=========================================================================================>
 *'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*/

package com.tom.houseforestgames.tictacgrow;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.Engine;
import org.andengine.engine.LimitedFPSEngine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RelativeResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.view.RenderSurfaceView;
import org.andengine.ui.activity.BaseGameActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdSize;
import com.shephertz.app42.paas.sdk.android.user.User.UserGender;
import com.tom.houseforestgames.tictacgrow.game.CellToken;
import com.tom.houseforestgames.tictacgrow.multiplayer.MultiplayerManager;
import com.tom.houseforestgames.tictacgrow.multiplayer.MultiplayerManager.MultiplayerRequestResult;
import com.tom.houseforestgames.tictacgrow.scenes.AdvertiseScene;
import com.tom.houseforestgames.tictacgrow.scenes.GameScene;
import com.tom.houseforestgames.tictacgrow.scenes.MenuScene;
import com.tom.houseforestgames.tictacgrow.scenes.OfflineHumanMatchScene;
import com.tom.houseforestgames.tictacgrow.scenes.OnlineMenuScene;
import com.tom.houseforestgames.tictacgrow.scenes.SoloMatchScene;
import com.tom.houseforestgames.tictacgrow.scenes.SplashScene;
import com.tom.houseforestgames.tictacgrow.scenes.MenuScene.MenuSelection;
import com.tom.houseforestgames.tictacgrow.scenes.SoloMatchScene.AILevel;
import com.tom.houseforestgames.tictacgrow.util.AdmobManager;
import com.tom.houseforestgames.tictacgrow.util.RGBBackground;
import com.tom.houseforestgames.tictacgrow.util.Utility;

public class TicTacGrowActivity extends BaseGameActivity
{
	// ===========================================================
	// Constants
	// ===========================================================

	//semi-constant andengine render surface view dimensions
	public static int WIDTH, HEIGHT;

	//sound files to load
	private static final String[] mSFXSources = new String[] {
			
			"pop.ogg", 
			"click.wav",
			"drumhi.ogg",
			"error.wav", 
			"applause.wav", 
			"fail.wav", 
			"drumlo.ogg",
			"trumpet.wav"
	};
	
	// ===========================================================
	// Fields
	// ===========================================================

	//surface aspect ratio
	private RenderSurfaceRatio mAspectRatio;
	
	//total elapsed time
	private float mTotalTime;

	//top level scene
	private Scene mTopScene;

	//current game scene
	private GameScene mScene;

	//current game state
	private GameState mGameState = GameState.SPLASH;
	
	//sound muted?
	private boolean mMuted;

	//sound effect buffer
	private Sound[] mSFX;

	//advertisement
	private AdmobManager mAdmobMgr;
	
	//online capabilities
	private MultiplayerManager mMultiplayerMgr;

	// ===========================================================
	// Constructors
	// ===========================================================

	;;

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	//return whether game is muted
	public boolean isMuted()
	{
		return mMuted;
	}

	//change mute state
	public void mute(boolean mute)
	{
		mMuted = mute;
	}
	
	//return admob manager
	public AdmobManager getAdmobManager()
	{
		return mAdmobMgr;
	}
	
	//return multiplayer manager
	public MultiplayerManager getMultiplayerManager()
	{
		return mMultiplayerMgr;
	}

	// ===========================================================
	// Override Methods
	// ===========================================================

	//measure device dimensions and find corresponding aspect ratio.
	//also setup application layout and update content view to show
	//render surface view and ad banner below.
	@Override protected void onCreate(Bundle pSavedInstanceState)
	{
		//calcualte aspect ratio
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		float w = (float) dm.widthPixels;
		float h = dm.heightPixels - AdSize.SMART_BANNER.getHeightInPixels(this);
		float rtvAspectRatio = h / w;
		String ratioText = "";
		Log.d("app", "Found device screen ratio to be: " + rtvAspectRatio);

		//use fixed width
		WIDTH = 1152;

		//height / width < 1 or closer to 1 than to 4:3
		if (rtvAspectRatio < (1 + (4.f / 3)) / 2)
		{
			rtvAspectRatio = 1.f;
			mAspectRatio = RenderSurfaceRatio.ONE;
			ratioText = "1:1 (sqr)";
		}

		//height / width closer to 4:3 than to 5:3
		else if (rtvAspectRatio < ((4.f / 3) + (5.f / 3)) / 2)
		{
			rtvAspectRatio = 4 / 3.f;
			mAspectRatio = RenderSurfaceRatio.FOUR_OVER_THREE;
			ratioText = "4:3";
		}

		//height / width closest to 5:3
		else
		{
			rtvAspectRatio = 5 / 3.f;
			mAspectRatio = RenderSurfaceRatio.FIVE_OVER_THREE;
			ratioText = "5:3";
		}
		Log.d("app", "Choosing closest available aspect ratio: " + ratioText);
		
		/////////////////////////////////////////////////////////////////////////////
		////  DEBUGGING!  ///////////////////////////////////////////////////////////
		Log.d("app", "Overriding choice with 5:3 due to missing implementation!");
		mAspectRatio = RenderSurfaceRatio.FIVE_OVER_THREE;
		rtvAspectRatio = 5 / 3.f;
		/////////////////////////////////////////////////////////////////////////////
		
		//calculate height to use from chosen aspect ratio
		HEIGHT = (int) (WIDTH * rtvAspectRatio);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(pSavedInstanceState);

		//declare screen layout
		FrameLayout screenLayout = new FrameLayout(this);
		screenLayout.setPadding(0, 0, 0, 0);
		FrameLayout.LayoutParams screenLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
																				   FrameLayout.LayoutParams.MATCH_PARENT,
																				   Gravity.BOTTOM);
		screenLayoutParams.setMargins(0, 0, 0, 0);
		screenLayout.setBackgroundColor(Color.BLACK);

		//declare ad banner view layout
		FrameLayout.LayoutParams adBannerLayout = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
																			   FrameLayout.LayoutParams.WRAP_CONTENT,
																			   Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);

		//add render surface view to screen layout (at top)
		mRenderSurfaceView = new RenderSurfaceView(this);
		mRenderSurfaceView.setRenderer(this.mEngine,
										this);

		final android.widget.RelativeLayout.LayoutParams surfaceViewLayoutParams = new RelativeLayout.LayoutParams(BaseGameActivity.createSurfaceViewLayoutParams());
		surfaceViewLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		screenLayout.addView(this.mRenderSurfaceView, surfaceViewLayoutParams);

		//initialize admob mediation & create banner and interstial ads
		mAdmobMgr = new AdmobManager(this);
		screenLayout.addView(mAdmobMgr.getBannerAd(), adBannerLayout);
		setContentView(screenLayout, screenLayoutParams);
		mAdmobMgr.updateBannerAd();
		
		//initialize multiplayer services
		mMultiplayerMgr = new MultiplayerManager(this);
		
		//check whether user exists
		mMultiplayerMgr.queryUserExists("houseforestgames");
		if(mMultiplayerMgr.waitForRequest().failed())
		{
			//if not, newly create
			mMultiplayerMgr.queryCreateAccount(	"houseforestgames", 
												"mail.houseforestgames@gmail.com", 
												"pwd", 
												UserGender.MALE, 
												Utility.dateOfBirth(7, 3, 1995), 
												"Germany"
			);
			
			if(mMultiplayerMgr.waitForRequest().failed())
			{
				Utility.showNotificationDialog(this, "Error", "Could not create user!", "OK", null);
			}
		}
		
		//create game session between testuser1 and testuser2
		mMultiplayerMgr.queryCreateSession("testuser1", "testuser2");
		if(mMultiplayerMgr.waitForRequest().failed())
		{
			Utility.showNotificationDialog(this, "Error", "Could not create game session!", "OK", null);
		}
	}

	//setup engine parameters and initialize resolution policy
	@Override public EngineOptions onCreateEngineOptions()
	{
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		float hRatio = 1.f - AdSize.SMART_BANNER.getHeightInPixels(this) / (float)dm.heightPixels;
		EngineOptions eopt = new EngineOptions(true,
											   ScreenOrientation.PORTRAIT_FIXED,
											   new RelativeResolutionPolicy(1.0f, hRatio),
											   new Camera(0, 0, WIDTH, HEIGHT));
		eopt.getAudioOptions().setNeedsMusic(true);
		eopt.getAudioOptions().setNeedsSound(true);
		return eopt;
	}

	//create engine instance
	@Override public Engine onCreateEngine(EngineOptions pEngineOptions)
	{
		//Limit game logic to 60 fps
		return new LimitedFPSEngine(pEngineOptions, 60);
	}

	//load textures, sounds,..
	@Override public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws Exception
	{
		//load textures
		CellToken.loadTokenTextures(this);

		//load fonts
		FontFactory.setAssetBasePath("fonts/");

		//load sounds
		SoundFactory.setAssetBasePath("sfx/");
		mSFX = new Sound[SFXName.values().length];
		for (int i = 0; i < mSFX.length; i++)
		{
			mSFX[i] = SoundFactory.createSoundFromAsset(getSoundManager(),
														this,
														mSFXSources[i]);
		}

		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	//create top-level scene graph node
	@Override public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws Exception
	{
		//start with unmuted mode
		mMuted = false;

		//create parent scene
		mTopScene = new Scene();
		mTopScene.setBackground(RGBBackground.getHouseforestGamesColor());
		pOnCreateSceneCallback.onCreateSceneFinished(mTopScene);
	}

	//instantiate splash screen
	@Override public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception
	{
		//create splash scene
		mTopScene.setChildScene(mScene = new SplashScene(this));

		//activate splash scene
		mScene.activate();
		mGameState = GameState.SPLASH;
		Log.d("app", "Game State changed to SPLASH");

		//show banner ad
		mAdmobMgr.setBannerAdVisibility(true);
		
		//update current scene regularly
		mTopScene.registerUpdateHandler(new IUpdateHandler()
		{
			@Override
			public void onUpdate(float pSecondsElapsed)
			{
				mTotalTime += pSecondsElapsed;
				mScene.onUpdate(pSecondsElapsed, mTotalTime);
				updateGameState(pSecondsElapsed);
			}

			@Override
			public void reset()
			{
				;
				;
			}
		});

		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}

	//behave like home button
	@Override public void onBackPressed()
	{
		moveTaskToBack(true);
	}
	
	// ===========================================================
	// Methods
	// ===========================================================

	//play sound if not muted
	public void playSFX(SFXName sfx)
	{
		if (!mMuted)
		{
			mSFX[sfx.ordinal()].stop();
			mSFX[sfx.ordinal()].play();
		}
	}

	//return value of positive sine function with given frequency
	public float sinval(float freq, float min, float max)
	{
		float amp = max - min;
		return min + (float) (amp + amp * Math.sin(6.283f * mTotalTime * freq)) / 2.f;
	}

	//update the game's status
	private void updateGameState(float dt)
	{
		switch (mGameState)
		{
			case SPLASH:
			{
				//splash scene ended
				if (!mScene.isActive())
				{
					setGameState(GameState.MENU_MAIN);
					changeToScene(new MenuScene(this));
				}
				break;
			}

			case MENU_MAIN:
			{
				//menu scene ended
				if (!mScene.isActive())
				{
					MenuSelection item = ((MenuScene)mScene).getSelectedItem();
					AILevel ai = ((MenuScene)mScene).getSelectedAILevel();
					
					//player started solo game
					if(item == MenuSelection.SOLO)
					{
						setGameState(GameState.OFFLINE_1P);
						changeToScene(new SoloMatchScene(this, ai));
					}
					
					//player started local 2p game
					else if(item == MenuSelection.OFFLINE2P)
					{
						setGameState(GameState.OFFLINE_2P);
						changeToScene(new OfflineHumanMatchScene(this));
					}
					
					//player started online mode
					else if(item == MenuSelection.ONLINE)
					{
						setGameState(GameState.MENU_ONLINE);
						changeToScene(new OnlineMenuScene(this));
					}
				}
				break;
			}

			case OFFLINE_2P:
			{
				//match scene ended
				if (!mScene.isActive())
				{
					setGameState(GameState.ADVERTISE);
					changeToScene(new AdvertiseScene(this));
				}
				break;
			}
			
			case OFFLINE_1P:
			{
				//match scene ended
				if (!mScene.isActive())
				{
					setGameState(GameState.ADVERTISE);
					changeToScene(new AdvertiseScene(this));
				}
				break;
			}
			
			/*
			case MENU_ONLINE:
			{
				//online menu scene ended
				if(!mScene.isActive())
				{
					setGameState(GameState.MENU_MAIN);
					changeToScene(new MenuScene(this));
				}
				break;
			}*/

			case ADVERTISE:
			{
				//advertise scene ended
				if (!mScene.isActive())
				{
					//change to menu scene
					setGameState(GameState.MENU_MAIN);
					changeToScene(new MenuScene(this));
				}
				break;
			}

			default:
			{
				break;
			}
		}
	}
	
	//change game state
	private void setGameState(GameState state)
	{
		mGameState = state;
		Log.d("app", "Game state changed to " + state.name());
	}
	
	//change currently active scene
	private void changeToScene(GameScene scene)
	{
		mScene.destroy();
		mTopScene.setChildScene(mScene = scene);
		mScene.activate();
	}

	//return device aspect ratio
	public final RenderSurfaceRatio getAspectRatio()
	{
		return mAspectRatio;
	}

	//convert dp to pixels
	public int topx(float dp)
	{
		float density = getApplicationContext().getResources().getDisplayMetrics().density;
		return Math.round(dp * density);
	}

	//convert pixels to dp
	public float todp(int px)
	{
		float density = getApplicationContext().getResources().getDisplayMetrics().density;
		return px / density;
	}
	
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	//player indices (for type safety)
	public enum PlayerIndex
	{
		ONE,
		TWO
	}
	
	//screen dimensions
	public enum RenderSurfaceRatio
	{
		ONE,
		FOUR_OVER_THREE,
		FIVE_OVER_THREE
	}
		
	//sound effect aliases
	public enum SFXName
	{
		POP,
		CLICK,
		DRUMHI,
		ERROR,
		APPLAUSE,
		FAIL,
		DRUMLO,
		TRUMPET
	}
	
	//game states
	public enum GameState
	{
		SPLASH,
		MENU_MAIN,
		INSTRUCTIONS,
		OFFLINE_1P,
		OFFLINE_2P,
		MENU_ONLINE,
		ONLINE_2P,
		ADVERTISE
	}
}
