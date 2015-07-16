/*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*
 * <=========================================================================================>
 * File: 	 OfflineHumanMatchScene.java
 * Created:  15.01.2015
 * Author:   HAUSWALD, Tom.
 * <=========================================================================================>
 *'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*
 * Description:
 * <=========================================================================================>
 * Implements a match between two human players on the same device.
 * <=========================================================================================>
 *'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*/

package com.tom.houseforestgames.tictacgrow.scenes;

import com.tom.houseforestgames.tictacgrow.TicTacGrowActivity;
import com.tom.houseforestgames.tictacgrow.TicTacGrowActivity.PlayerIndex;
import com.tom.houseforestgames.tictacgrow.TicTacGrowActivity.SFXName;
import com.tom.houseforestgames.tictacgrow.game.CellToken.CellTokenType;
import com.tom.houseforestgames.tictacgrow.game.Gameboard;
import com.tom.houseforestgames.tictacgrow.game.Gameboard.CellLocation;
import com.tom.houseforestgames.tictacgrow.game.Gameboard.SquareState;

public class OfflineHumanMatchScene extends MatchScene
{
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	//instantiate new offline 2-player match scene
	public OfflineHumanMatchScene(TicTacGrowActivity activity)
	{
		super(activity);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Override Methods
	// ===========================================================
	
	//handle player turn
	@Override protected void onPlayerTurn(PlayerIndex player, MatchState matchState)
	{
		if (getBoard().wasTouched() && getBoard().getTouchLocation().isValid())
		{
			//normal state
			if(matchState == MatchState.NORMAL)
			{
				final Gameboard.CellLocation touchLocation = getBoard().getTouchLocation();
				int xb = touchLocation.xBlock;
				int yb = touchLocation.yBlock;
				final CellTokenType currentSymbol = getBoard().getCellTokenType(touchLocation);

				//only allow empty cells to be filled and at fitting locations
				if (currentSymbol == CellTokenType.FREE && xb == getEnforcedBlock()[0] && yb == getEnforcedBlock()[1])
				{
					mActivity.playSFX(SFXName.POP);
					getBoard().setCellToken(touchLocation, player == PlayerIndex.ONE ? CellTokenType.P1TOKEN : CellTokenType.P2TOKEN);
					setEnforcedBlock(touchLocation.xCell, touchLocation.yCell);
					setActivePlayer(player == PlayerIndex.ONE ? PlayerIndex.TWO : PlayerIndex.ONE);
				}
			}
			
			//fill state
			else if(matchState == MatchState.FILL)
			{
				CellLocation pos = getBoard().getTouchLocation();
				for (int i = 0; i < getCellsToFill().size(); i++)
				{
					if (pos.equals(getCellsToFill().get(i)))
					{
						mActivity.playSFX(SFXName.POP);
						getBoard().setCellToken(pos, player == PlayerIndex.ONE ? CellTokenType.P1TOKEN : CellTokenType.P2TOKEN);
						getCellsToFill().remove(i);
						setActivePlayer(player == PlayerIndex.ONE ? PlayerIndex.TWO : PlayerIndex.ONE);
						break;
					}
				}
			}
		}
	}

	//handle match end
	@Override protected void onMatchEnded(SquareState result)
	{
		//player won
		if (result != SquareState.TIE)
		{
			mActivity.playSFX(SFXName.APPLAUSE);
			mHUDText.setText(MatchScene.MESSAGE_WON);
			mHUDText.setX(mWonTextX);
			mP1Token.setVisible(false);
			mP2Token.setVisible(false);
			attachChild(mRestartOverlayBar);
			attachChild(mRestartText);
		}

		//a tie happened
		else
		{
			mActivity.playSFX(SFXName.ERROR);
			mHUDText.setText(MESSAGE_TIE);
			mHUDText.setX(mTieTextX);
			mP1Token.setVisible(false);
			mP2Token.setVisible(false);
			attachChild(mRestartOverlayBar);
			attachChild(mRestartText);
		}
	}
	
	//do nothing upon destruction
	@Override public void onDestroyed(){ ;; }
	
	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
