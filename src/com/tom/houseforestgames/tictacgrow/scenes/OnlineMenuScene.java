/* '*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*
 * <=========================================================================================>
 * File: OnlineMenuScene.java
 * Created: 27.01.2015
 * Author: HAUSWALD, Tom.
 * <=========================================================================================>
 * '*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*
 * Description:
 * <=========================================================================================>
 * Shows the online menu to the user wherein he is able to create a new user account, log in
 * and out, update his profile settings and challenge his friends.
 * <=========================================================================================>
 * '*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*' */

package com.tom.houseforestgames.tictacgrow.scenes;

import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.region.ITextureRegion;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.widget.EditText;

import com.tom.houseforestgames.tictacgrow.R;
import com.tom.houseforestgames.tictacgrow.TicTacGrowActivity;
import com.tom.houseforestgames.tictacgrow.TicTacGrowActivity.RenderSurfaceRatio;
import com.tom.houseforestgames.tictacgrow.TicTacGrowActivity.SFXName;
import com.tom.houseforestgames.tictacgrow.util.Utility;

public class OnlineMenuScene extends GameScene
{
	// ===========================================================
	// Constants
	// ===========================================================

	// menu options to show to non-authenticated users
	private static String[] LOGGED_OUT_MENU_ENTRIES = new String[]
	{
			"LOGIN",
			"CREATE ACCOUNT",
			"BACK TO MAIN MENU"
	};

	// menu options to show to logged in users
	private static String[] LOGGED_IN_MENU_ENTRIES = new String[]
	{
			"* Challenge Friend *",
			"* Edit Profile *",
			"* Logout *",
			"* Delete Account *"
	};

	// ===========================================================
	// Fields
	// ===========================================================

	// Logged in as a valid user?
	private boolean mLoggedIn;

	// GUI objects
	private Font mFont;
	private Sprite mBackground;
	private Sprite[] mMenuButtons;
	private Text[] mMenuTexts;
	private int mMenuSelection;

	// ===========================================================
	// Constructors
	// ===========================================================

	public OnlineMenuScene( TicTacGrowActivity activity )
	{
		super( activity );
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	;;

	// ===========================================================
	// Override Methods
	// ===========================================================

	@Override
	protected void onSetup( RenderSurfaceRatio ratio )
	{
		int w = TicTacGrowActivity.WIDTH, h = TicTacGrowActivity.HEIGHT;

		// log out by default
		mLoggedIn = false;

		// load font
		mFont = FontFactory.createFromAsset( mActivity.getFontManager(),
												mActivity.getTextureManager(),
												256,
												256,
												TextureOptions.BILINEAR,
												mActivity.getAssets(),
												"creaticredad.ttf",
												64,
												true,
												Color.WHITE );
		mFont.load();

		// load background
		mBackground = new Sprite( 0, 0, w, h,
									Utility.loadSuitRatio( mActivity, ratio, "menubg.png" ),
									mVBOMgr );

		// GUI parameters
		float buttonWidth = 0.f, buttonHeight = 0.f, buttonStartY = 0.f, buttonEndY = 0.f;

		switch( ratio )
		{
			case FIVE_OVER_THREE:
			{
				buttonWidth = 800;
				buttonHeight = 240;
				buttonStartY = 912;
				buttonEndY = 1760;
				break;
			}

			default:
			break;
		}

		// provide one button per menu option
		mMenuButtons = new Sprite[LOGGED_OUT_MENU_ENTRIES.length];
		mMenuTexts = new Text[mMenuButtons.length];
		for( int btn = 0; btn < mMenuButtons.length; btn++ )
		{
			ITextureRegion buttonTxr = Utility.load( mActivity, "button.png", 300, 100 );
			mMenuButtons[btn] = new Sprite(

											( w - buttonWidth ) / 2,
											buttonStartY + btn / (float) mMenuButtons.length * ( buttonEndY - buttonStartY ),
											buttonWidth,
											buttonHeight,
											buttonTxr,
											mVBOMgr
					);

			mMenuTexts[btn] = new Text( 0, 0, mFont, LOGGED_OUT_MENU_ENTRIES[btn], mVBOMgr );
			Utility.centerText( mMenuTexts[btn],
								mMenuButtons[btn].getX() + buttonWidth / 2,
								mMenuButtons[btn].getY() + buttonHeight / 2 );
		}
	}

	@Override
	public void onUpdate( float delta, float total )
	{
		for( int btn = 0; btn < mMenuButtons.length; btn++ )
		{
			Sprite buttonSpr = mMenuButtons[btn];
			if( wasTouched( (int) buttonSpr.getX(), (int) buttonSpr.getY(), (int) buttonSpr.getWidth(), (int) buttonSpr.getHeight() ) )
			{
				onButtonClicked( btn );
				return;
			}
		}
	}

	@Override
	public void onActivated()
	{
		enableTouch();
		attachChild( mBackground );
		for( int btn = 0; btn < mMenuButtons.length; btn++ )
		{
			attachChild( mMenuButtons[btn] );
			attachChild( mMenuTexts[btn] );
		}
	}

	@Override
	public void onDeactivated()
	{
		disableTouch();
		detachChildren();
	}

	// do nothing when destroyed
	@Override
	public void onDestroyed()
	{
		;;
	}

	// ===========================================================
	// Methods
	// ===========================================================

	private void onButtonClicked( int selection )
	{
		mMenuSelection = selection;
		mActivity.playSFX( SFXName.CLICK );

		// not authenticated yet
		if( !mLoggedIn )
		{
			switch( selection )
			{
			// login clicked
				case 0:
					Utility.showCustomDialog(

												mActivity,
												"Enter Login Information",
												R.layout.login_dialog,
												"Confirm",
												"Cancel",

												new OnClickListener()
												{
													@Override
													public void onClick( DialogInterface dialog, int which )
													{
														mActivity.getMultiplayerManager()
																	.queryAuthenticateUser(
																							( (EditText) ( mActivity.findViewById( R.id.username ) ) ).getText()
																																						.toString(),
																							( (EditText) ( mActivity.findViewById( R.id.password ) ) ).getText()
																																						.toString()
																	);

														// login successful
														if( mActivity.getMultiplayerManager().waitForRequest().succeeded() )
														{
															deactivate();
														}

														// login failed
														else
														{
															dialog.dismiss();
														}
													}
												}, null );
				break;

				// back to main menu clicked
				case 2:
					deactivate();
				break;
			}
		}

		// user is logged in
		else
		{

		}
		// return to main menu clicked
		if( !mLoggedIn && selection == 2 )
		{

		}

		else Utility.showNotImplDialog( mActivity );
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	;;
}
