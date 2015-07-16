/* '*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*
 * <=========================================================================================>
 * File: MenuScene.java
 * Created: 10.01.2015
 * Author: HAUSWALD, Tom.
 * <=========================================================================================>
 * '*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*
 * Description:
 * <=========================================================================================>
 * Presents the user the menu screen containing multiple items to select from.
 * <=========================================================================================>
 * '*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*' */

package com.tom.houseforestgames.tictacgrow.scenes;

import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;

import android.graphics.Color;

import com.tom.houseforestgames.tictacgrow.TicTacGrowActivity;
import com.tom.houseforestgames.tictacgrow.TicTacGrowActivity.RenderSurfaceRatio;
import com.tom.houseforestgames.tictacgrow.TicTacGrowActivity.SFXName;
import com.tom.houseforestgames.tictacgrow.scenes.SoloMatchScene.AILevel;
import com.tom.houseforestgames.tictacgrow.util.UIButton;
import com.tom.houseforestgames.tictacgrow.util.Utility;

public class MenuScene extends GameScene
{
	// ===========================================================
	// Constants
	// ===========================================================

	private static String[] MENU_ENTRIES = new String[]
	{
			"* Instructions *",
			"* Solo AI-Match *",
			"* Local 2-Player Match *",
			"* Settings *",
			"* Online Mode *"
	};

	// ===========================================================
	// Fields
	// ===========================================================

	// GUI objects
	private Font mFont;
	private Sprite[] mMuteSprites;
	private Sprite[] mNavigateSprites;
	private Sprite[] mAISelectArrows;
	private Sprite mBackground;
	private MenuSelection mMenuSelection;
	private Text mMenuText;
	private int mAILevelIndex;
	private Text mAILevelText;
	private UIButton mTestButton;

	// ===========================================================
	// Constructors
	// ===========================================================

	// instantiate new menu scene
	public MenuScene( TicTacGrowActivity activity )
	{
		super( activity );
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// return currently selected item
	public MenuSelection getSelectedItem()
	{
		return mMenuSelection;
	}

	// return selected AI level
	public AILevel getSelectedAILevel()
	{
		return AILevel.values()[mAILevelIndex];
	}

	// ===========================================================
	// Override Methods
	// ===========================================================

	// create GUI elements upon setup
	@Override
	public void onSetup( final RenderSurfaceRatio ratio )
	{
		// define menu entries
		mMenuSelection = MenuSelection.INSTRUCTIONS;

		// GUI parameters to be determined according to screen ratio
		float fontsz = 0.f, menuTextCtrY = 0.f, navArrowX = 0.f, navArrowTopY = 0.f, navArrowBotY = 0.f, navArrowW = 0.f, navArrowH = 0.f, aiArrowY = 0.f, aiArrowLeftX =
				0.f, aiArrowRightX = 0.f, muteSpriteX = 0.f, muteSpriteY = 0.f, muteSpriteSz = 0.f;

		switch( ratio )
		{
		// 5:3 menu layout
			case FIVE_OVER_THREE:
			{
				fontsz = 100;
				menuTextCtrY = 1344;
				navArrowX = 480;
				navArrowTopY = 1168;
				navArrowBotY = 1452;
				navArrowW = 180;
				navArrowH = 80;
				aiArrowY = 1720;
				aiArrowLeftX = 270;
				aiArrowRightX = 760;
				muteSpriteX = 1028;
				muteSpriteY = 16;
				muteSpriteSz = 108;
				break;
			}

			default:
			break;
		}

		// load font
		mFont = FontFactory.createFromAsset( mActivity.getFontManager(),
												mActivity.getTextureManager(),
												512,
												512,
												TextureOptions.BILINEAR,
												mActivity.getAssets(),
												"creaticredad.ttf",
												fontsz,
												true,
												Color.WHITE );
		mFont.load();

		// create overlay text
		mMenuText = new Text( 0, 0, mFont, "aAbBcCdDeEfFgGhHiIjJkKlLmMnNoOpPqQrRsStTuUvVwWxXyYzZ0123456789(!*)", mVBOMgr );
		mMenuText.setText( MENU_ENTRIES[mMenuSelection.ordinal()] );
		Utility.centerText( mMenuText, TicTacGrowActivity.WIDTH / 2, menuTextCtrY );

		// mute / unmute sprites
		mMuteSprites = new Sprite[2];
		mMuteSprites[0 /* unmuted */] =
				new Sprite( muteSpriteX, muteSpriteY, muteSpriteSz, muteSpriteSz, Utility.load( mActivity, "unmuted.png", 512, 512 ), mVBOMgr );
		mMuteSprites[1 /* muted */] =
				new Sprite( muteSpriteX, muteSpriteY, muteSpriteSz, muteSpriteSz, Utility.load( mActivity, "muted.png", 512, 512 ), mVBOMgr );
		mMuteSprites[mActivity.isMuted() ? 1 : 0].setVisible( true ); // show current mute state
		mMuteSprites[mActivity.isMuted() ? 0 : 1].setVisible( false ); // hide other sprite

		// navigation arrows
		mNavigateSprites = new Sprite[2];
		mNavigateSprites[0 /* up */] = new Sprite(
													navArrowX,
													navArrowTopY,
													navArrowW,
													navArrowH,
													Utility.load( mActivity, "navigate.png", 512, 512 ),
													mVBOMgr );
		mNavigateSprites[1 /* down */] = new Sprite(
														navArrowX,
														navArrowBotY,
														navArrowW,
														navArrowH,
														Utility.load( mActivity, "navigate.png", 512, 512 ),
														mVBOMgr );
		// turn around the bottom arrow
		mNavigateSprites[1].setRotation( 180 );

		// ai level selection
		mAILevelIndex = 0;
		mAISelectArrows = new Sprite[2];
		mAISelectArrows[0] =
				new Sprite( aiArrowLeftX, aiArrowY, navArrowW * 0.7f, navArrowH * 0.7f, Utility.load( mActivity, "navigate.png", 512, 512 ), mVBOMgr );
		mAISelectArrows[0].setRotation( 270 );
		mAISelectArrows[0].setVisible( false );
		mAISelectArrows[1] =
				new Sprite( aiArrowRightX, aiArrowY, navArrowW * 0.7f, navArrowH * 0.7f, Utility.load( mActivity, "navigate.png", 512, 512 ), mVBOMgr );
		mAISelectArrows[1].setRotation( 90 );
		mAISelectArrows[1].setVisible( false );
		mAILevelText = new Text( 0, 0, mFont, "ABCDEFGHIJKLMNOP", mVBOMgr );
		mAILevelText.setText( AILevel.values()[mAILevelIndex].name() );
		mAILevelText.setColor( 0, 0, 0 );
		mAILevelText.setVisible( false );
		Utility.centerTextX( mAILevelText, 576 );
		Utility.centerTextY( mAILevelText, ( mAISelectArrows[0].getY() + mAISelectArrows[0].getHeight() / 2 ) );

		// load background sprite
		mBackground = new Sprite(
									0,
									0,
									TicTacGrowActivity.WIDTH,
									TicTacGrowActivity.HEIGHT,
									Utility.loadSuitRatio( mActivity, ratio, "menuback.png" ),
									mVBOMgr );

		// attach elements to scene as children
		attachChild( mBackground );
		attachChild( mMenuText );
		attachChild( mMuteSprites[0] );
		attachChild( mMuteSprites[1] );
		attachChild( mNavigateSprites[0] );
		attachChild( mNavigateSprites[1] );
		attachChild( mAISelectArrows[0] );
		attachChild( mAISelectArrows[1] );
		attachChild( mAILevelText );
	}

	// enable touch upon activation
	@Override
	public void onActivated()
	{
		enableTouch();
	}

	// handle item touch events each frame (e.g. menu navigation)
	@Override
	public void onUpdate( float delta, float total )
	{
		Sprite muteSprite = mMuteSprites[0];

		// enable mute toggling
		if( wasTouched( (int) muteSprite.getX(), (int) muteSprite.getY(), (int) muteSprite.getWidth(), (int) muteSprite.getHeight() ) )
		{
			mActivity.mute( !mActivity.isMuted() );
			mMuteSprites[mActivity.isMuted() ? 1 : 0].setVisible( true ); // show current mute state
			mMuteSprites[mActivity.isMuted() ? 0 : 1].setVisible( false ); // hide other sprite
			mActivity.playSFX( SFXName.DRUMLO ); // only plays when changed to unmuted
		}

		// enable menu navigation (up)
		for( int direction = 0; direction < 2; direction++ )
		{
			if( wasTouched( (int) mNavigateSprites[direction].getX(), (int) mNavigateSprites[direction].getY(), (int) mNavigateSprites[direction].getWidth(),
							(int) mNavigateSprites[direction].getHeight() ) )
			{
				onNavigated( direction == 0 );
			}
		}

		// enable choice of currently shown menu entry
		if( wasTouched( 0, 1300, TicTacGrowActivity.WIDTH, 100 ) )
		{
			onMenuItemClicked();
		}

		// enable AI level choice
		if( mMenuSelection == MenuSelection.SOLO )
		{
			if( wasTouched( (int) mAISelectArrows[1].getX(), (int) mAISelectArrows[1].getY(), (int) mAISelectArrows[1].getWidth(),
							(int) mAISelectArrows[1].getHeight() ) )
			{
				mAILevelIndex++;
				mAILevelIndex = mAILevelIndex % AILevel.values().length;
				mAILevelText.setText( AILevel.values()[mAILevelIndex].name() );
				Utility.centerTextX( mAILevelText, 576 );
				mActivity.playSFX( SFXName.CLICK );
			}
			else if( wasTouched( (int) mAISelectArrows[0].getX(), (int) mAISelectArrows[0].getY(), (int) mAISelectArrows[0].getWidth(),
									(int) mAISelectArrows[0].getHeight() ) )
			{
				mAILevelIndex--;
				while( mAILevelIndex < 0 )
					mAILevelIndex += AILevel.values().length;
				mAILevelText.setText( AILevel.values()[mAILevelIndex].name() );
				Utility.centerTextX( mAILevelText, 576 );
				mActivity.playSFX( SFXName.CLICK );
			}
		}

		// update the menu text
		float textwidth = mMenuText.getLineWidths().get( 0 );
		mMenuText.setX( mActivity.sinval( 0.5f, 0, TicTacGrowActivity.WIDTH - textwidth ) );
	}

	// disable touch upon deactivation
	@Override
	public void onDeactivated()
	{
		disableTouch();
	}

	// unload font upon destruction
	@Override
	public void onDestroyed()
	{
		mFont.unload();
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// handle menu navigation
	private void onNavigated( boolean up )
	{
		mActivity.playSFX( SFXName.CLICK );
		int nextselection = mMenuSelection.ordinal() + ( up ? -1 : 1 );
		if( nextselection < 0 ) nextselection = MENU_ENTRIES.length - 1;
		if( nextselection >= MENU_ENTRIES.length ) nextselection = 0;
		mMenuSelection = MenuSelection.values()[nextselection];
		mMenuText.setText( MENU_ENTRIES[nextselection] );

		// toggle AI selection
		if( mMenuSelection == MenuSelection.SOLO )
		{
			mAISelectArrows[0].setVisible( true );
			mAISelectArrows[1].setVisible( true );
			mAILevelText.setVisible( true );
		}
		else
		{
			mAISelectArrows[0].setVisible( false );
			mAISelectArrows[1].setVisible( false );
			mAILevelText.setVisible( false );
		}
	}

	// handle menu item clicks
	private void onMenuItemClicked()
	{
		if( mMenuSelection != MenuSelection.INSTRUCTIONS && mMenuSelection != MenuSelection.SETTINGS )
		{
			mActivity.playSFX( SFXName.DRUMLO );
			deactivate();
		}

		// not yet implemented
		else
		{
			Utility.showNotImplDialog( mActivity );
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	// possible selections
	public enum MenuSelection
	{
		INSTRUCTIONS,
		SOLO,
		OFFLINE2P,
		SETTINGS,
		ONLINE
	}
}
