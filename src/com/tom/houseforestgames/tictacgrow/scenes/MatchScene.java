/*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*
 * <=========================================================================================>
 * File: 	 MatchScene.java
 * Created:  15.01.2015
 * Author:   HAUSWALD, Tom.
 * <=========================================================================================>
 *'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*
 * Description:
 * <=========================================================================================>
 * Provides the abstract interface for each kind of Tic, Tac, Grow match.
 * Handles match state transitions and calls the player turn listener accordingly in order to
 * request player or AI actions from the child implementation.
 * <=========================================================================================>
 *'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*/

package com.tom.houseforestgames.tictacgrow.scenes;

import java.util.ArrayList;

import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;

import android.graphics.Color;
import android.util.Log;

import com.tom.houseforestgames.tictacgrow.TicTacGrowActivity;
import com.tom.houseforestgames.tictacgrow.TicTacGrowActivity.PlayerIndex;
import com.tom.houseforestgames.tictacgrow.TicTacGrowActivity.RenderSurfaceRatio;
import com.tom.houseforestgames.tictacgrow.TicTacGrowActivity.SFXName;
import com.tom.houseforestgames.tictacgrow.game.CellToken;
import com.tom.houseforestgames.tictacgrow.game.CellToken.CellTokenType;
import com.tom.houseforestgames.tictacgrow.game.Gameboard;
import com.tom.houseforestgames.tictacgrow.game.Gameboard.CellLocation;
import com.tom.houseforestgames.tictacgrow.game.Gameboard.SquareState;
import com.tom.houseforestgames.tictacgrow.util.RGBBackground;
import com.tom.houseforestgames.tictacgrow.util.Utility;

public abstract class MatchScene extends GameScene
{	
	// ===========================================================
	// Constants
	// ===========================================================

	protected static final String MESSAGE_WON  = "Congratulations!";
	protected static final String MESSAGE_TIE  = "It's a tie!";
	protected static final String MESSAGE_LOST = "Sorry, you lost!";

	// ===========================================================
	// Fields
	// ===========================================================

	//board
	private Gameboard mBoard;

	//HUD (current turn, game result,..)
	protected Font 	 mHUDFont;
	protected Font 	 mOverlayFont;
	protected Text 	 mHUDText;
	protected float	 mHUDTextCtrY;
	protected Sprite mBackgroundSprite;
	protected Sprite mP1Token, mP2Token;
	protected Sprite mRestartOverlayBar;
	protected Text 	 mRestartText;
	protected float	 mWonTextX;
	protected float  mTieTextX;
	protected float  mLostTextX;

	//current game state
	private MatchState mMatchState = MatchState.NORMAL;
		
	//force the current player to place his symbol inside
	//a given block
	private int mEnforcedBlockX;
	private int mEnforcedBlockY;
	private PlayerIndex mActivePlayer;
	
	//latest calculated game result
	private SquareState mGameResult;

	//list of cells left to fill due to a formerly completed block
	private ArrayList<Gameboard.CellLocation> mCellsToFill;

	// ===========================================================
	// Constructors
	// ===========================================================

	//instantiate new match scene
	public MatchScene(TicTacGrowActivity activity)
	{
		super(activity);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	//returns a reference to the game board
	protected Gameboard getBoard()
	{
		return mBoard;
	}
	
	//returns the currently enforced block coordinates
	protected int[] getEnforcedBlock()
	{
		return new int[] { mEnforcedBlockX, mEnforcedBlockY };
	}

	//sets the currently enforced block coordinates
	protected void setEnforcedBlock(int x, int y)
	{
		mEnforcedBlockX = x;
		mEnforcedBlockY = y;
	}
	
	//returns a reference to the list of cells to fill
	protected ArrayList<Gameboard.CellLocation> getCellsToFill()
	{
		return mCellsToFill;
	}
	
	// ===========================================================
	// Override Methods
	// ===========================================================

	//create graphical elements and instantiate game board
	@Override public void onSetup(final RenderSurfaceRatio ratio)
	{
		//set background to white
		setBackground(RGBBackground.getHouseforestGamesColor());
		
		//layout parameters
		float hudFontSz 	= 0.f,
			  overlayFontSz = 0.f,
			  hudTextX		= 0.f,
			  curTokenX	 	= 0.f,
			  curTokenY	 	= 0.f,
			  curTokenSz	= 0.f,
			  boardX		= 0.f,
			  boardY		= 0.f,
			  boardSz		= 0.f;
			  
		switch(ratio)
		{
			case FIVE_OVER_THREE:
			{
				hudFontSz 		= 106;
				overlayFontSz 	= 80;
				hudTextX		= 48;
				mWonTextX		= 48;
				mTieTextX		= 48;
				mLostTextX		= 48;
				mHUDTextCtrY	= 192;
				curTokenX		= 936;
				curTokenY		= 130;
				curTokenSz		= 116;
				boardX			= 0;
				boardY			= 384;
				boardSz 		= 1152;
				break;
			}
			
			default: break;
		}

		//load large font
		mHUDFont = FontFactory.createFromAsset(
				mActivity.getFontManager(),
				mActivity.getTextureManager(),
				1024,
				1024,
				TextureOptions.BILINEAR,
				mActivity.getAssets(),
				"creaticredad.ttf",
				hudFontSz,
				true,
				Color.WHITE
				);
		mHUDFont.load();

		//load small font
		mOverlayFont = FontFactory.createFromAsset(
				mActivity.getFontManager(),
				mActivity.getTextureManager(),
				512,
				512,
				TextureOptions.BILINEAR,
				mActivity.getAssets(),
				"creaticredad.ttf",
				overlayFontSz,
				true,
				Color.WHITE
				);
		mOverlayFont.load();

		//drawables to show after the game ended (restart msg)
		mRestartOverlayBar = new Sprite(
				0,
				TicTacGrowActivity.WIDTH / 2 + (TicTacGrowActivity.HEIGHT - TicTacGrowActivity.WIDTH) / 2 - 75,
				TicTacGrowActivity.WIDTH,
				150,
				Utility.loadSuitRatio(mActivity, ratio, "bar.png"),
				mVBOMgr
				);
		mRestartText = new Text(0, 0, mOverlayFont, "* Touch to return to menu *", mVBOMgr);
		Utility.centerTextX(mRestartText, TicTacGrowActivity.WIDTH / 2);
		mRestartText.setY(mRestartOverlayBar.getY() + 43);

		//game background
		mBackgroundSprite = new Sprite(
				0,
				0,
				TicTacGrowActivity.WIDTH,
				TicTacGrowActivity.HEIGHT,
				Utility.loadSuitRatio(mActivity, ratio, "gameback.png"),
				mVBOMgr
				);
		
		mHUDText = new Text(hudTextX, 0, mHUDFont, "abcdefghijklmopqrstuvwxyz0123456789", mVBOMgr);
		mHUDText.setText("Please place token:");
		Utility.centerTextY(mHUDText, mHUDTextCtrY);
		
		mP1Token = new Sprite(curTokenX, curTokenY, curTokenSz, curTokenSz, CellToken.getTokenTexture(CellTokenType.P1TOKEN), mVBOMgr);
		mP2Token = new Sprite(curTokenX, curTokenY, curTokenSz, curTokenSz, CellToken.getTokenTexture(CellTokenType.P2TOKEN), mVBOMgr);

		//create game board
		mBoard = new Gameboard(mActivity, this, (int)boardSz, (int)boardX, (int)boardY);

		//create buffer for valid cells
		mCellsToFill = new ArrayList<Gameboard.CellLocation>();
	}

	//reset game logic and attach sprites to scene
	@Override public void onActivated()
	{
		mActivity.getAdmobManager().updateInterstitialAd();
		enableTouch();

		//show BG
		attachChild(mBackgroundSprite);
		
		//show Board
		attachChild(mBoard);

		//show HUD
		attachChild(mHUDText);
		attachChild(mP1Token);
		attachChild(mP2Token);
		mP1Token.setVisible(true);
		mP2Token.setVisible(false);
		mGameResult = SquareState.FREE;

		//let random player start
		setActivePlayer(PlayerIndex.values()[(int)(Math.random() * 2)]);
		
		//start at middle block
		mEnforcedBlockX = mEnforcedBlockY = 1;
		mMatchState = MatchState.NORMAL;
		
		Log.d("app", "Match State changed to NORMAL");
	}

	//update game logic
	@Override public void onUpdate(float delta, float total)
	{
		//check for game result
		processBoardConstellation();

		//game not decided yet
		if (mGameResult == SquareState.FREE)
		{
			//game didn't end yet, show possible moves
			mBoard.markPossibleCells(mMatchState, mEnforcedBlockX, mEnforcedBlockY, mCellsToFill);
		}

		switch (mMatchState)
		{
			case NORMAL:
				updateGameNormalState();
			break;

			case FILL:
				updateGameFillState();
			break;

			case FINISHED:
				updateGameFinishedState();
			break;
		}
	}

	//detach all children upon deactivation
	@Override public void onDeactivated()
	{
		disableTouch();
		detachChildren();
	}

	// ===========================================================
	// Methods
	// ===========================================================

	//update the game in case it's currently in normal state
	//players can set their symbols until a block gets filled,
	//in this case the state changes to 'FILL'
	protected void updateGameNormalState()
	{
		//avoid enforcement of closed blocks
		if (mBoard.isBlockClosed(mEnforcedBlockX, mEnforcedBlockY))
		{
			CellLocation rdm = mBoard.findRandomEmptyCell();
			if (rdm.xBlock >= 0)
			{
				mEnforcedBlockX = rdm.xBlock;
				mEnforcedBlockY = rdm.yBlock;
			}
			else
			{
				//no free position left, the game ended with a tie
				mGameResult = SquareState.TIE;
				mMatchState = MatchState.FINISHED;
				Log.d("app", "Match State changed to FINISHED");
				return;
			}
		}

		//the game takes its normal course, so
		//allow players to place their symbols
		onPlayerTurn(mActivePlayer, mMatchState);
	}

	//update cells of each block and then check whether the game has ended
	protected void processBoardConstellation()
	{
		//handle closing of blocks
		for (int xb = 0; xb < 3; xb++)
		{
			for (int yb = 0; yb < 3; yb++)
			{
				processCellConstellation(xb, yb);
			}
		}

		//handle game result
		processBlockConstellation();
	}

	//process the cell constellation of the given block
	//and close it if neccessary
	protected void processCellConstellation(int xb, int yb)
	{
		if (!mBoard.isBlockClosed(xb, yb))
		{
			//check the current block state
			SquareState state = mBoard.calculateBlockState(xb, yb);

			//the block is filled in some way
			if (state != SquareState.FREE)
			{
				//one player conquered the block
				if (state != SquareState.TIE)
				{
					mActivity.playSFX(SFXName.DRUMHI);
					mBoard.updateBlockState(xb, yb, state);

					//player who conquered block gets another turn
					setActivePlayer(state == SquareState.P1 ? PlayerIndex.ONE : PlayerIndex.TWO);

					if (mBoard.enqueueOpenCorrespondenceCells(xb, yb, mCellsToFill))
					{
						Log.d("app", "Match State changed to FILL");
						mMatchState = MatchState.FILL;
					}
				}

				//block is full -> tie situation
				else
				{
					mActivity.playSFX(SFXName.ERROR);
					mBoard.updateBlockState(xb, yb, state);
					if (mBoard.enqueueOpenCorrespondenceCells(xb, yb, mCellsToFill))
					{
						Log.d("app", "Match State changed to FILL");
						mMatchState = MatchState.FILL;
					}
				}
			}
		}
	}

	//changes the active player and updates the turn display
	protected void setActivePlayer(PlayerIndex player)
	{
		mActivePlayer = player;
		mP1Token.setVisible(player == PlayerIndex.ONE);
		mP2Token.setVisible(player == PlayerIndex.TWO);
	}

	//update the game during the 'FILL' state
	//players have to alternatingly fill all cells
	//that are left unset and corresponding to a
	//block that has been filled earlier
	//will return to 'NORMAL' state afterwards
	protected void updateGameFillState()
	{
		//no cells to fill left -> return to normal state
		if (!mBoard.validateOpenCorrespondenceCells(mCellsToFill))
		{
			mMatchState = MatchState.NORMAL;
			Log.d("app", "Match State changed to NORMAL");
			return;
		}

		//fill the next cell
		else
		{
			onPlayerTurn(mActivePlayer, mMatchState);
		}
	}

	//process the current constellation of blocks
	protected void processBlockConstellation()
	{
		//check for game end
		if (mMatchState != MatchState.FINISHED)
		{
			//determine board state
			mGameResult = mBoard.calculateBoardState();

			//someone won the game or a tie happened
			if (mGameResult != SquareState.FREE)
			{
				mBoard.updateState(mGameResult);

				//change to small font
				detachChild(mHUDText);
				mHUDText = new Text(0, 0, mHUDFont, "abcdefghijklmopqrstuvwxyz0123456789", mVBOMgr);
				Utility.centerTextY(mHUDText, mHUDTextCtrY);
				attachChild(mHUDText);
				
				//invoke match end handler
				onMatchEnded(mGameResult);
				
				//change to finish state
				mMatchState = MatchState.FINISHED;
				Log.d("app", "Match State changed to FINISHED");
			}
		}
	}

	//update the game after a player has won or the game
	//ended in a tie
	//may return the player to the splash screen
	protected void updateGameFinishedState()
	{
		mRestartText.setAlpha(mActivity.sinval(0.2f, 0.2f, 1.0f));
		if (wasTouched((int)mRestartOverlayBar.getX(), (int)mRestartOverlayBar.getY(), TicTacGrowActivity.WIDTH, (int)mRestartOverlayBar.getHeight()))
		{
			deactivate();
		}
	}

	//prompt for player action
	protected abstract void onPlayerTurn(PlayerIndex player, MatchState matchState);

	//invoked after game ended
	protected abstract void onMatchEnded(SquareState result);
	
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	//possible game states
	public enum MatchState
	{
		NORMAL, 
		FILL, 
		FINISHED
	}
	

	

	

	

	

	
}
