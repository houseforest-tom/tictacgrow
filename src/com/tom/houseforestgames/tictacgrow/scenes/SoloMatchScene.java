/*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*
 * <=========================================================================================>
 * File: 	 SoloMatchScene.java
 * Created:  15.01.2015
 * Author:   HAUSWALD, Tom.
 * <=========================================================================================>
 *'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*
 * Description:
 * <=========================================================================================>
 * Implements a Tic, Tac, Grow match between a human player and the AI.
 * <=========================================================================================>
 *'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*/

package com.tom.houseforestgames.tictacgrow.scenes;

import java.util.ArrayList;

import com.tom.houseforestgames.tictacgrow.TicTacGrowActivity;
import com.tom.houseforestgames.tictacgrow.TicTacGrowActivity.PlayerIndex;
import com.tom.houseforestgames.tictacgrow.TicTacGrowActivity.SFXName;
import com.tom.houseforestgames.tictacgrow.game.CellToken;
import com.tom.houseforestgames.tictacgrow.game.Gameboard;
import com.tom.houseforestgames.tictacgrow.game.CellToken.CellTokenType;
import com.tom.houseforestgames.tictacgrow.game.Gameboard.CellLocation;
import com.tom.houseforestgames.tictacgrow.game.Gameboard.SquareState;

public class SoloMatchScene extends MatchScene
{
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	//level of enemy AI
	private AILevel mAILevel;
	
	// ===========================================================
	// Constructors
	// ===========================================================

	//instantiate a new match scene between human and AI
	public SoloMatchScene(TicTacGrowActivity activity, AILevel ai)
	{
		super(activity);
		mAILevel = ai;
	}
	
	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Override Methods
	// ===========================================================
	
	//let human player choose his move and simulate AI behaviour on its turn
	@Override protected void onPlayerTurn(PlayerIndex player, MatchState matchState)
	{
		//validate open cells if in FILL state
		if(matchState == MatchState.FILL && !getBoard().validateOpenCorrespondenceCells(getCellsToFill()))
			return;
		
		//AI's turn
		if(player == PlayerIndex.TWO) this.onAITurn(matchState);
		
		//player turn
		else
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
						getBoard().setCellToken(touchLocation, CellTokenType.P1TOKEN);
						setEnforcedBlock(touchLocation.xCell, touchLocation.yCell);
						setActivePlayer(PlayerIndex.TWO);
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
							getBoard().setCellToken(pos, CellTokenType.P1TOKEN);
							setActivePlayer(PlayerIndex.TWO);
							getCellsToFill().remove(i);
							break;
						}
					}
				}
			}
		}
	}
	
	//handle match end
	@Override protected void onMatchEnded(SquareState result)
	{
		//player won
		if (result == SquareState.P1)
		{
			mActivity.playSFX(SFXName.APPLAUSE);
			mHUDText.setText(MatchScene.MESSAGE_WON);
			mHUDText.setX(mWonTextX);
			mP1Token.setVisible(false);
			mP2Token.setVisible(false);
			attachChild(mRestartOverlayBar);
			attachChild(mRestartText);
		}
		
		//AI won
		else if(result == SquareState.P2)
		{
			mActivity.playSFX(SFXName.FAIL);
			mHUDText.setText(MatchScene.MESSAGE_LOST);
			mHUDText.setX(mLostTextX);
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
	
	//do nothing upon scene destruction
	@Override public void onDestroyed(){ ;; }
	
	// ===========================================================
	// Methods
	// ===========================================================

	//handle AI actions
	private void onAITurn(MatchState matchState)
	{
		CellLocation chosenCell = null;
		ArrayList<CellLocation> possibleCells 	= (matchState == MatchState.FILL ? getCellsToFill() : new ArrayList<CellLocation>()),
								winCells 		= new ArrayList<CellLocation>(), 
								preventCells 	= new ArrayList<CellLocation>();
		
		if(matchState != MatchState.FILL)
		{
			for(int x=0; x<3; x++)
			{
				for(int y=0; y<3; y++)
				{
					CellLocation loc = getBoard().new CellLocation(getEnforcedBlock()[0], getEnforcedBlock()[1], x, y);
					if(getBoard().getCellTokenType(loc) == CellTokenType.FREE)
						possibleCells.add(loc);
				}
			}
		}

		switch(mAILevel)
		{
			//TRIVIAL AI
			case EASY:
			{
				chosenCell = possibleCells.get((int)(Math.random() * possibleCells.size()));				
				break;
			}

			//EASY AI
			case AMATEUR:
			{
				boolean canWinBlock = false;
				
				//check for possible blocks to close this turn
				for(int x=0; x<3; x++)
				{
					for(int y=0; y<3; y++)
					{
						//can win block this turn
						if(getBoard().canWinBlockThisTurn(x, y, PlayerIndex.TWO, possibleCells, winCells))
						{
							chosenCell = winCells.get((int)(Math.random() * winCells.size()));
							canWinBlock = true;
							break;
						}
					}

					if(canWinBlock) break;
				}

				//fallback: choose random cell
				if(!canWinBlock) 
					chosenCell = possibleCells.get((int)(Math.random() * possibleCells.size()));
				
				break;
			}

			//MEDIUM AI
			case MEDIUM:
			{
				boolean canWinBlock = false;
				boolean canPreventBlock = false;
				
				//check for possible blocks to close this turn
				for(int x=0; x<3; x++)
				{
					for(int y=0; y<3; y++)
					{
						//can win block this turn
						if(getBoard().canWinBlockThisTurn(x, y, PlayerIndex.TWO, possibleCells, winCells))
						{
							chosenCell = winCells.get((int)(Math.random() * winCells.size()));
							canWinBlock = true;
							break;
						}
					}

					if(canWinBlock) break;
				}

				//if the AI can't close a block, check for possibly preventable block wins by the player
				if(!canWinBlock) 
				{
					//check for possible blocks to prevent
					for(int x=0; x<3; x++)
					{
						for(int y=0; y<3; y++)
						{
							if(getBoard().canPreventBlockThisTurn(x, y, PlayerIndex.TWO, possibleCells, preventCells))
							{
								chosenCell = preventCells.get((int)(Math.random() * preventCells.size()));
								canPreventBlock = true;
								break;
							}
						}

						if(canPreventBlock) break;	
					}

					//fallback: choose random cell
					if(!canPreventBlock)
					{
						chosenCell = possibleCells.get((int)(Math.random() * possibleCells.size()));
					}
				}

				break;
			}
			
			//unimplemented
			default: chosenCell = possibleCells.get((int)(Math.random() * possibleCells.size())); break;
		}

		mActivity.playSFX(SFXName.POP);
		getBoard().setCellToken(chosenCell, CellTokenType.P2TOKEN);
		if(matchState == MatchState.NORMAL) setEnforcedBlock(chosenCell.xCell, chosenCell.yCell);
		else possibleCells.remove(chosenCell);
		setActivePlayer(PlayerIndex.ONE);
	}
		
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	
	//implemented ai levels
	public enum AILevel
	{
		//at EASY level the AI chooses a random cell to populate 
		//on every turn.
		EASY,
		
		//at AMATEUR level the AI will check if it can close a block
		//on its current turn and choose to do so, if not it will
		//fall back to choosing a random cell.
		AMATEUR,
		
		//if the AI can't close a block on the current turn, on 
		//MEDIUM level it will then check if it can disturb any
		//of your potential rows and will choose to do so if 
		//possible. It will else fall back to choosing a random
		//cell.
		MEDIUM,
		
		//on PRO level the AI will in addition never send you 
		//to a block that you could potentially close on your 
		//next turn.
		PRO
	}
}
