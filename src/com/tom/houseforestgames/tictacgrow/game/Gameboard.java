/*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*
 * <=========================================================================================>
 * File: 	 Gameboard.java
 * Created:  08.01.2015
 * Author:   HAUSWALD, Tom.
 * <=========================================================================================>
 *'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*
 * Description:
 * <=========================================================================================>
 * Represents the board a game of Tic, Tac, Grow is set on. Contains the internal
 * representation of each block and cell and keep status information about eacn of its
 * integral parts. Provides various condition checks and utility functions that help to
 * determine the current state of the game set on the board.
 * <=========================================================================================>
 *'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*/

package com.tom.houseforestgames.tictacgrow.game;

import java.util.ArrayList;
import java.util.Locale;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.BaseGameActivity;

import android.util.Log;

import com.tom.houseforestgames.tictacgrow.TicTacGrowActivity;
import com.tom.houseforestgames.tictacgrow.TicTacGrowActivity.PlayerIndex;
import com.tom.houseforestgames.tictacgrow.game.CellToken.CellTokenType;
import com.tom.houseforestgames.tictacgrow.scenes.MatchScene.MatchState;
import com.tom.houseforestgames.tictacgrow.util.Utility;

public class Gameboard extends Sprite
{
	// ===========================================================
	// Constants
	// ===========================================================

	//padding of board in pixels (between blocks and around board)
	public static final int PADDING = 8;
	
	//semi-constant size of game board in pixels
	public static int BOARDSZ = 0;

	//semi-constant size of each block in pixels
	public static int BLOCKSZ = 0;

	//semi-constant size of each cell in pixels
	public static int CELLSZ = 0;
	
	//string corresponding to an emtpy stringified game board
	public static String EMPTY_BOARD_STRING = 	
	"---------------------------------------------------------------------------------";

	// ===========================================================
	// Fields
	// ===========================================================

	//activity reference
	private BaseGameActivity mActivity;

	//touched this frame?
	private boolean mTouched;

	//latest touch coordinates transformed into board space
	private CellLocation mTouchCoordinates = null;
	
	//cell tokens
	private CellToken[][][][] mCellTokens = null;

	//full block texture
	private ITextureRegion mFullTexture = null;
	
	//state of each block
	private SquareState[][] mBlockStates = null;
	
	// ===========================================================
	// Constructors
	// ===========================================================

	//instantiate new empty tic-tac-grow board of given pixel size at the specified location
	public Gameboard(BaseGameActivity activity, Scene scene, int size, int x, int y)
	{
		super(x, y, Utility.load(activity, "grid.png", size, size), activity.getVertexBufferObjectManager());
		
		//calculate board, block and cell size
		BOARDSZ = size;
		BLOCKSZ = Math.round((BOARDSZ + 2 * PADDING) / 3.f);
		CELLSZ  = Math.round((BLOCKSZ - 2 * PADDING) / 3.f);

		mActivity = activity;
		mTouchCoordinates = toCellLocation(this, 0, 0);
		mCellTokens = new CellToken[3][3][3][3];
		mTouched = false;

		mFullTexture = Utility.load(mActivity, "full.png", 512, 512);

		//all blocks are open in the beginning
		mBlockStates = new SquareState[3][3];
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				mBlockStates[i][j] = SquareState.FREE;

		//fill all cells with emty symbols upon initialization
		for (int xb = 0; xb < 3; ++xb)
		{
			for (int yb = 0; yb < 3; ++yb)
			{
				for (int xc = 0; xc < 3; ++xc)
				{
					for (int yc = 0; yc < 3; ++yc)
					{
						setCellToken(new CellLocation(xb, yb, xc, yc), CellTokenType.FREE);
						getLastChild().setVisible(false);
					}
				}
			}
		}

		setWidth(TicTacGrowActivity.WIDTH);
		setHeight(TicTacGrowActivity.WIDTH);
		scene.registerTouchArea(this);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	//return the token (object reference) at the given cell location
	public CellToken getCellToken(CellLocation location)
	{
		return mCellTokens[location.xBlock][location.yBlock][location.xCell][location.yCell];
	}

	//return the token type of the given cell location
	public CellTokenType getCellTokenType(CellLocation location)
	{
		return getCellToken(location).getType();
	}

	//set token type at location
	public void setCellToken(CellLocation location, CellTokenType newtype)
	{
		CellToken currentsym = getCellToken(location);

		if (currentsym != null)
		{
			detachChild(currentsym);
			mCellTokens[location.xBlock][location.yBlock][location.xCell][location.yCell] = null;
		}

		//set the new symbol
		CellToken ref = mCellTokens[location.xBlock][location.yBlock][location.xCell][location.yCell] = new CellToken(mActivity, newtype, location);

		if (newtype == CellTokenType.FREE) ref.setVisible(false);

		attachChild(ref);
	}

	//was the game board touched by a player this frame
	public boolean wasTouched()
	{
		return mTouched;
	}

	//return the location of the last touch event
	public CellLocation getTouchLocation()
	{
		return mTouchCoordinates;
	}
	
	// ===========================================================
	// Override Methods
	// ===========================================================

	//reset touch event each frame
	@Override public void onManagedUpdate(float pSecondsElapsed)
	{
		if (mTouched)
		{
			mTouched = false;
		}

		super.onManagedUpdate(pSecondsElapsed);
	}

	//store touch event information internally until next frame
	@Override public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
	{
		if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN)
		{
			mTouched = true;
			mTouchCoordinates = toCellLocation(this, (int) pTouchAreaLocalX, (int) pTouchAreaLocalY);
		}

		return false;
	}

	// ===========================================================
	// Methods
	// ===========================================================

	//transform touch event relative pixels to board coordinates
	private static CellLocation toCellLocation(Gameboard board, int xpx, int ypx)
	{
		//coordinate regions possible
		final int CREG_INVALID = -1, CREG_LEFT_TOP = 0, CREG_MID = 1, CREG_RIGHT_BOTTOM = 2;

		CellLocation l = board.new CellLocation(0, 0, 0, 0);
		l.invalidate();

		int[] coords = new int[] { xpx, ypx };
		int[] coordinateRegions = new int[] { /* x */CREG_INVALID, /* y */CREG_INVALID };
		int[] blockCoordinates = new int[] { -1, -1 };
		int[] cellCoordinates = new int[] { -1, -1 };

		//check for validity of coordinates
		for (int coord = 0; coord < 2; coord++)
		{
			//current coordinate
			int c = coords[coord];

			//inside left / top block
			if (PADDING <= c && c < 2 * PADDING + 3 * CELLSZ)
				coordinateRegions[coord] = CREG_LEFT_TOP;

			//inside middle block
			else if (BLOCKSZ <= c && c < BLOCKSZ + 3 * CELLSZ)
				coordinateRegions[coord] = CREG_MID;

			//inside right or bottom block
			else if (2 * BLOCKSZ - PADDING <= c && c < 2 * BLOCKSZ - PADDING + 3 * CELLSZ)
				coordinateRegions[coord] = CREG_RIGHT_BOTTOM;

			else
			;
			; //coordinate region stays invalid

			//invalid touch location on this axis -> abort
			if (coordinateRegions[coord] == CREG_INVALID)
			{
				//Log.d("app", String.format("Touch location {%d, %d} not inside a block!", coords[0], coords[1]));
				return l;
			}

			//find block & cell coordinates
			switch (coordinateRegions[coord])
			{
			//top or left block
				case CREG_LEFT_TOP:
					blockCoordinates[coord] = 0;
					cellCoordinates[coord] = (c - PADDING) / CELLSZ;
				break;

				//middle block of row / column
				case CREG_MID:
					blockCoordinates[coord] = 1;
					cellCoordinates[coord] = (c - BLOCKSZ) / CELLSZ;
				break;

				//bottom or right block
				case CREG_RIGHT_BOTTOM:
					blockCoordinates[coord] = 2;
					cellCoordinates[coord] = (c - (2 * BLOCKSZ - PADDING)) / CELLSZ;
				break;

				default:
				break;
			}
		}

		l.xBlock = blockCoordinates[0];
		l.yBlock = blockCoordinates[1];
		l.xCell = cellCoordinates[0];
		l.yCell = cellCoordinates[1];

		//Log.d("app", String.format("Returning touch location: " + l.toString()));
		return l;
	}

	//returns whether the given block is closed (won by player / full)
	public boolean isBlockClosed(int x, int y)
	{
		return mBlockStates[x][y] != SquareState.FREE;
	}

	//returns whether the given block is completely filled
	public boolean isBlockFull(int x, int y)
	{
		boolean full = true;

		for (int xc = 0; xc < 3; ++xc)
		{
			for (int yc = 0; yc < 3; ++yc)
			{
				//empty cell found -> abort
				if (getCellTokenType(new CellLocation(x, y, xc, yc)) == CellTokenType.FREE)
				{
					full = false;
					break;
				}
			}
		}

		return full;
	}

	//calculate the current state of the specified block
	public SquareState calculateBlockState(int x, int y)
	{
		CellLocation pos = new CellLocation(x, y, 0, 0);
		CellTokenType type = CellTokenType.FREE;
		boolean success = true;

		//check for filled columns
		for (int column = 0; column < 3; column++)
		{
			pos.xCell = column;
			pos.yCell = 0;
			type = getCellTokenType(pos);

			success = true;

			for (int row = 1; row < 3; row++)
			{
				pos.yCell = row;

				//doesn't match cell above
				if (getCellTokenType(pos) != type)
				{
					success = false;
					break;
				}
			}

			//column is completely filled by one player
			if (success && type != CellTokenType.FREE) return (type == CellTokenType.P1TOKEN ? SquareState.P1 : SquareState.P2);
		}

		//check for filled rows
		for (int row = 0; row < 3; row++)
		{
			pos.yCell = row;
			pos.xCell = 0;
			type = getCellTokenType(pos);

			success = true;

			for (int column = 1; column < 3; column++)
			{
				pos.xCell = column;

				//doesn't match cell above
				if (getCellTokenType(pos) != type)
				{
					success = false;
					break;
				}
			}

			//row is completely filled by one player
			if (success && type != CellTokenType.FREE) return (type == CellTokenType.P1TOKEN ? SquareState.P1 : SquareState.P2);
		}

		//check for filled diagonals
		{
			//downward diagonal
			{
				pos.xCell = pos.yCell = 0;
				type = getCellTokenType(pos);
				success = true;

				for (int i = 1; i < 3; i++)
				{
					pos.xCell = pos.yCell = i;

					//doesn't match prior cell
					if (getCellTokenType(pos) != type)
					{
						success = false;
						break;
					}
				}

				//diagonal is completely filled by one player
				if (success && type != CellTokenType.FREE) return (type == CellTokenType.P1TOKEN ? SquareState.P1 : SquareState.P2);
			}

			//upward diagonal
			{
				pos.xCell = 0;
				pos.yCell = 2;
				type = getCellTokenType(pos);
				success = true;

				for (int i = 1; i < 3; i++)
				{
					pos.xCell = i;
					pos.yCell = 2 - i;

					//doesn't match prior cell
					if (getCellTokenType(pos) != type)
					{
						success = false;
						break;
					}
				}

				//diagonal is completely filled by one player
				if (success && type != CellTokenType.FREE) return (type == CellTokenType.P1TOKEN ? SquareState.P1 : SquareState.P2);
			}
		}

		//block is either free or completely full
		return (isBlockFull(x, y) ? SquareState.TIE : SquareState.FREE);
	}

	//returns whether the board is completely filled (no free cell left)
	public boolean isFull()
	{
		for (int x = 0; x < 3; x++)
			for (int y = 0; y < 3; y++)
				if (!isBlockClosed(x, y)) return false;
		return true;
	}

	//returns a random empty cell
	public CellLocation findRandomEmptyCell()
	{
		CellLocation location = new CellLocation(0, 0, 0, 0);
		boolean valid = false;

		while (!valid)
		{
			location.xBlock = (int) (Math.random() * 3);
			location.yBlock = (int) (Math.random() * 3);
			location.xCell = (int) (Math.random() * 3);
			location.yCell = (int) (Math.random() * 3);
			valid = (mBlockStates[location.xBlock][location.yBlock] == SquareState.FREE && getCellTokenType(location) == CellTokenType.FREE);
		}

		return location;
	}
	
	//searches for all cells corresponding to the closed block [blockX, blockY] and puts them in a list.
	//returns true if the created list contains at least one element, indiciating that there are cells
	//left to fill up before the match state can change back to NORMAL.
	public boolean enqueueOpenCorrespondenceCells(int blockX, int blockY, ArrayList<CellLocation> corrCells)
	{
		for (int bx = 0; bx < 3; bx++)
		{
			for (int by = 0; by < 3; by++)
			{
				//blockX & blockY correspond to the block being full
				CellLocation pos = new CellLocation(bx, by, blockX, blockY);
				if (mBlockStates[bx][by] == SquareState.FREE && getCellTokenType(pos) == CellTokenType.FREE) corrCells.add(pos);
			}
		}

		return corrCells.size() > 0;
	}

	//removes all invalid cells from the list, e.g. if the block containing them was filled during an
	//earlier turn. returns true if the list afterwards still contains at least one element.
	public boolean validateOpenCorrespondenceCells(ArrayList<CellLocation> corrCells)
	{
		ArrayList<CellLocation> invalid = new ArrayList<CellLocation>();

		//find invalid cells
		for (CellLocation c : corrCells)
		{
			if (mBlockStates[c.xBlock][c.yBlock] != SquareState.FREE) invalid.add(c);
		}

		//remove invalid cells from list
		for (CellLocation i : invalid)
		{
			corrCells.remove(i);
		}

		return corrCells.size() > 0;
	}
	
	//update a block's state to change sprite after it was filled or won
	public void updateBlockState(int xb, int yb, SquareState bs)
	{
		mBlockStates[xb][yb] = bs;
		if (mBlockStates[xb][yb] != SquareState.FREE)
		{
			//block was won -> draw big player symbol
			if (mBlockStates[xb][yb] != SquareState.TIE)
			{
				//determine winner's symbol
				CellTokenType winnerSymbolType = CellTokenType.P1TOKEN;
				if (mBlockStates[xb][yb] == SquareState.P2) winnerSymbolType = CellTokenType.P2TOKEN;

				Sprite blockSprite = new Sprite(

				PADDING + xb * (BLOCKSZ - PADDING), PADDING + yb * (BLOCKSZ - PADDING), BLOCKSZ - 2 * PADDING, BLOCKSZ - 2 * PADDING,
						CellToken.getTokenTexture(winnerSymbolType), mActivity.getVertexBufferObjectManager());

				blockSprite.setScaleCenter(BLOCKSZ / 2, BLOCKSZ / 2);
				blockSprite.setScale(CellToken.SIZEFACTOR);
				attachChild(blockSprite);
			}
			//block is full and not won -> draw lock
			else
			{
				Sprite blockSprite = new Sprite(

				PADDING + xb * (BLOCKSZ - PADDING), PADDING + yb * (BLOCKSZ - PADDING), BLOCKSZ - 2 * PADDING, BLOCKSZ - 2 * PADDING, mFullTexture,
						mActivity.getVertexBufferObjectManager());

				blockSprite.setScaleCenter(BLOCKSZ / 2, BLOCKSZ / 2);
				blockSprite.setScale(CellToken.SIZEFACTOR);
				attachChild(blockSprite);
			}

			//a big symbol was drawn, so erase all residing small symbol sprites
			for (int x = 0; x < 3; x++)
			{
				for (int y = 0; y < 3; y++)
				{
					detachChild(mCellTokens[xb][yb][x][y]);
				}
			}
		}
	}
	
	//update the game board's appearance to show the game's result (big sprite)
	public void updateState(SquareState state)
	{
		detachChildren();
		ITextureRegion resultTexture = mFullTexture;
		if (state != SquareState.TIE)
		{
			resultTexture = CellToken.getTokenTexture((state == SquareState.P1 ? CellTokenType.P1TOKEN : CellTokenType.P2TOKEN));
		}

		Sprite resultSprite = new Sprite(0, 0, BOARDSZ, BOARDSZ, resultTexture, mActivity.getVertexBufferObjectManager());
		resultSprite.setScaleCenter(BOARDSZ / 2, BOARDSZ / 2);
		resultSprite.setScale(CellToken.SIZEFACTOR);
		attachChild(resultSprite);
	}

	//make the currently viable choices visible to the player
	public void markPossibleCells(MatchState matchState, int enforcedBlockX, int enforcedBlockY, ArrayList<CellLocation> fillCells)
	{
		CellLocation cl = new CellLocation(0, 0, 0, 0);

		for (int xc = 0; xc < 3; xc++)
		{
			for (int yc = 0; yc < 3; yc++)
			{
				for (int xb = 0; xb < 3; xb++)
				{
					for (int yb = 0; yb < 3; yb++)
					{
						cl.xBlock = xb;
						cl.yBlock = yb;
						cl.xCell = xc;
						cl.yCell = yc;

						//current cell is empty
						if (getCellTokenType(cl) == CellTokenType.FREE)
						{
							//in normal mode highlight enforced block
							if (matchState == MatchState.NORMAL)
							{
								if (xb == enforcedBlockX && yb == enforcedBlockY)
								{
									mCellTokens[xb][yb][xc][yc].setVisible(true);
									mCellTokens[xb][yb][xc][yc].setX(PADDING + xb * (BLOCKSZ - PADDING) + (xc + 0.1f) * CELLSZ);
									mCellTokens[xb][yb][xc][yc].setY(PADDING + yb * (BLOCKSZ - PADDING) + (yc + 0.1f) * CELLSZ);
									mCellTokens[xb][yb][xc][yc].setWidth(0.67f * CELLSZ);
									mCellTokens[xb][yb][xc][yc].setHeight(0.67f * CELLSZ);
									mCellTokens[xb][yb][xc][yc].setAlpha(1);
									mCellTokens[xb][yb][xc][yc].setScaleCenter(CELLSZ / 2, CELLSZ / 2);
									mCellTokens[xb][yb][xc][yc].setScale(0.6f);
								}
								else
								{
									mCellTokens[xb][yb][xc][yc].setVisible(false);
									mCellTokens[xb][yb][xc][yc].setX(PADDING + xb * (BLOCKSZ - PADDING) + xc * CELLSZ);
									mCellTokens[xb][yb][xc][yc].setY(PADDING + yb * (BLOCKSZ - PADDING) + yc * CELLSZ);
									mCellTokens[xb][yb][xc][yc].setWidth(CellToken.SIZEFACTOR * CELLSZ);
									mCellTokens[xb][yb][xc][yc].setHeight(CellToken.SIZEFACTOR * CELLSZ);
								}
							}

							//in fill mode check if current cell is still to be filled
							else if (matchState == MatchState.FILL)
							{
								CellToken t = getCellToken(cl);
								t.setVisible(false);
								mCellTokens[xb][yb][xc][yc].setX(PADDING + xb * (BLOCKSZ - PADDING) + xc * CELLSZ);
								mCellTokens[xb][yb][xc][yc].setY(PADDING + yb * (BLOCKSZ - PADDING) + yc * CELLSZ);
								mCellTokens[xb][yb][xc][yc].setWidth(CellToken.SIZEFACTOR * CELLSZ);
								mCellTokens[xb][yb][xc][yc].setHeight(CellToken.SIZEFACTOR * CELLSZ);

								for (CellLocation cell : fillCells)
								{
									if (cell.equals(cl))
									{
										t.setVisible(true);
										mCellTokens[xb][yb][xc][yc].setX(PADDING + xb * (BLOCKSZ - PADDING) + (xc + 0.1f) * CELLSZ);
										mCellTokens[xb][yb][xc][yc].setY(PADDING + yb * (BLOCKSZ - PADDING) + (yc + 0.1f) * CELLSZ);
										mCellTokens[xb][yb][xc][yc].setWidth(0.67f * CELLSZ);
										mCellTokens[xb][yb][xc][yc].setHeight(0.67f * CELLSZ);
										t.setAlpha(1);
										t.setScaleCenter(CELLSZ / 2, CELLSZ / 2);
										t.setScale(0.6f);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	//clear the board by removing all player tokens
	public void clear()
	{
		detachChildren();
		CellLocation loc = new CellLocation(0, 0, 0, 0);
		for (int x = 0; x < 3; x++)
		{
			loc.xBlock = x;
			for (int y = 0; y < 3; y++)
			{
				loc.yBlock = y;
				for (int cell = 0; cell < 9; cell++)
				{
					loc.xCell = cell % 3;
					loc.yCell = cell / 3;
					setCellToken(loc, CellTokenType.FREE);
				}

				mBlockStates[x][y] = SquareState.FREE;
			}
		}
	}

	//calculate the current board state
	public SquareState calculateBoardState()
	{
		SquareState state = SquareState.FREE;

		//check rows
		for (int row = 0; row < 3; row++)
		{
			if ((state = mBlockStates[0][row]) == mBlockStates[1][row] && mBlockStates[0][row] == mBlockStates[2][row])
			{
				//Check if any player has a full row
				if (state == SquareState.FREE) continue;
				if (state == SquareState.TIE) continue;
				return state;
			}
		}

		//check columns
		for (int col = 0; col < 3; col++)
		{
			if ((state = mBlockStates[col][0]) == mBlockStates[col][1] && mBlockStates[col][0] == mBlockStates[col][2])
			{
				//Check if any player has a full column
				if (state == SquareState.FREE) continue;
				if (state == SquareState.TIE) continue;
				return state;
			}
		}

		//check upward diagonal
		if ((state = mBlockStates[0][2]) == mBlockStates[1][1] && mBlockStates[0][2] == mBlockStates[2][0])
		{
			//Check if any player owns the upward diagonal
			if (state != SquareState.FREE && state != SquareState.TIE) return state;
		}

		//check downward diagonal
		if ((state = mBlockStates[0][0]) == mBlockStates[1][1] && mBlockStates[0][0] == mBlockStates[2][2])
		{
			//Check if any player owns the downward diagonal
			if (state != SquareState.FREE && state != SquareState.TIE) return state;
		}

		//check if the board is full 
		//yes -> tie situation; no -> board still free
		return (isFull() ? SquareState.TIE : SquareState.FREE);
	}
	
	//returns whether the given player is able to win a specified block during the current turn and 
	//returns a list containing all cells that achieve this goal when filled.
	public boolean canWinBlockThisTurn(int x, int y, PlayerIndex player, final ArrayList<CellLocation> allowedCells, ArrayList<CellLocation> outCells)
	{
		//block closed already
		if(mBlockStates[x][y] != SquareState.FREE) return false;
		
		//actual cell fill state before replacing it for the test
		CellTokenType temp;
		outCells.clear();
		
		for(int i=0; i<allowedCells.size(); i++)
		{
			CellLocation location = allowedCells.get(i);
			
			//check for correct block
			if(location.xBlock == x && location.yBlock == y)
			{
				//recognize current cell state
				temp = getCellTokenType(location); 
				
				//perform one possible permutation
				setCellToken(location, player == PlayerIndex.ONE ? CellTokenType.P1TOKEN : CellTokenType.P2TOKEN);
				
				//check new state of changed block
				if(calculateBlockState(x, y) == (player == PlayerIndex.ONE ? SquareState.P1 : SquareState.P2))
				{
					CellLocation cell = new CellLocation(x, y, location.xCell, location.yCell);
					outCells.add(cell);
					Log.d("app", "Player " + (player == PlayerIndex.ONE ? "1" : "2") + " is able to win block (" + x + "," + y + ") this turn\n" +
								 "by placing a token at location: " + cell.toString());
				}
				
				//restore prior cell content
				setCellToken(location, temp);
			}
		}
		
		//cells where found
		return outCells.size() > 0;
	}
	
	//returns whether the given player is able to prevent his opponent from winning a specified block 
	//during the current turn and returns a / the cell neccessary to fill in order to achieve this goal.
	public boolean canPreventBlockThisTurn(int x, int y, PlayerIndex player, final ArrayList<CellLocation> allowedCells, ArrayList<CellLocation> outCells)
	{
		//check whether the opponent has a 2-in-a-row arrangement inside the current block
		ArrayList<CellLocation> freeCellsLeftInBlock = new ArrayList<CellLocation>();
		for(int xc=0; xc<3; xc++)
			for(int yc=0; yc<3; yc++)
				if(mCellTokens[x][y][xc][yc].getType() == CellTokenType.FREE)
					freeCellsLeftInBlock.add(new CellLocation(x, y, xc, yc));
		ArrayList<CellLocation> winPossibilities = new ArrayList<CellLocation>();
		if(canWinBlockThisTurn(x, y, player == PlayerIndex.ONE ? PlayerIndex.TWO : PlayerIndex.ONE, freeCellsLeftInBlock, winPossibilities))
		{
			//check for each win possibility, if it can be prevented by the player
			for(int i=0; i<winPossibilities.size(); i++)
			{
				for(int j=0; j<allowedCells.size(); j++)
				{
					//match between possible enemy win cell and possible player move
					if(winPossibilities.get(i).equals(allowedCells.get(j)))
					{
						CellLocation cell = new CellLocation(x, y, winPossibilities.get(i).xCell, winPossibilities.get(i).yCell);
						outCells.add(cell);
						Log.d("app", "Player " + (player == PlayerIndex.ONE ? "1" : "2") + " is able to prevent possible enemy win of block (" + x + "," + y + ") this turn\n" +
									 "by placing a token at location: " + cell.toString());
					}
				}
			}
		}
		
		return outCells.size() > 0;
	}
	
	//stringify the board's state
	@Override public String toString()
	{
		CellTokenType token;
		String result = "";
		
		for(int bx=0; bx<3; bx++)
		{
			for(int by=0; by<3; by++)
			{
				for(int cx=0; cx<3; cx++)
				{
					for(int cy=0; cy<3; cy++)
					{
						token = mCellTokens[bx][by][cx][cy].getType();
						if(token == CellTokenType.FREE) 	 	result += "-";
						else if(token == CellTokenType.P1TOKEN) result += "x";
						else if(token == CellTokenType.P2TOKEN) result += "o";
					}
				}
			}
		}
				
		return result;
	}
	
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	//coordinates in 4D board space: [block-x][block-y][cell-x][cell-y]
	public final class CellLocation
	{
		public int xBlock = 0;
		public int yBlock = 0;
		public int xCell = 0;
		public int yCell = 0;

		//construct new cell location of given coordinates
		public CellLocation(int xblock, int yblock, int xcell, int ycell)
		{
			xBlock = xblock;
			yBlock = yblock;
			xCell = xcell;
			yCell = ycell;
		}

		//make the cell location invalid
		public void invalidate()
		{
			xBlock = yBlock = xCell = yCell = -1;
		}

		//equals operator
		public boolean equals(CellLocation o)
		{
			return (xBlock == o.xBlock && yBlock == o.yBlock && xCell == o.xCell && yCell == o.yCell);
		}

		//check for validity
		public boolean isValid()
		{
			return (xBlock >= 0 && xBlock < 3 && yBlock >= 0 && yBlock < 3 && xCell >= 0 && xCell < 3 && yCell >= 0 && yCell < 3);
		}
		
		//return readable string
		@Override public String toString()
		{
			return String.format(Locale.getDefault(), "[%d][%d]-[%d][%d]", xBlock, yBlock, xCell, yCell);
		}
	}
	
	//possible states of each block and the complete board
	public enum SquareState
	{
		FREE, 
		P1, 
		P2, 
		TIE
	}
}
