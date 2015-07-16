/*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*
 * <=========================================================================================>
 * File: 	 CellToken.java
 * Created:  08.01.2015
 * Author:   HAUSWALD, Tom.
 * <=========================================================================================>
 *'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*
 * Description:
 * <=========================================================================================>
 * This class describes a single cell token. Upon the instantiation of a Gameboard object, 
 * 81 instances of this class will be created in order to represent the state of each of the
 * cells on the board. This class also provides a method to load all token textures from the
 * dedicated asset folder.
 * <=========================================================================================>
 *'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*/

package com.tom.houseforestgames.tictacgrow.game;

import java.io.IOException;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.BaseGameActivity;

import android.util.Log;

import com.tom.houseforestgames.tictacgrow.TicTacGrowActivity.PlayerIndex;
import com.tom.houseforestgames.tictacgrow.game.Gameboard.CellLocation;
import com.tom.houseforestgames.tictacgrow.util.Utility;

public class CellToken extends Sprite
{
	// ===========================================================
	// Constants
	// ===========================================================

	//factor to multiply symbol size with (portion of cell size)
	public static final float SIZEFACTOR = 0.85f;

	// ===========================================================
	// Fields
	// ===========================================================

	//type of this token
	private CellTokenType mType;

	//precached textures
	private static ITextureRegion[] mTokenTextures = null;
	
	//token textures to use for both players
	private static int mP1TokenId = 1, mP2TokenId = 2;

	// ===========================================================
	// Constructors
	// ===========================================================

	//instantiate new symbol
	public CellToken(BaseGameActivity activity, CellTokenType type, CellLocation location)
	{
		super(Gameboard.PADDING + location.xBlock * (Gameboard.BLOCKSZ - Gameboard.PADDING) + location.xCell * Gameboard.CELLSZ, 
		      Gameboard.PADDING + location.yBlock * (Gameboard.BLOCKSZ - Gameboard.PADDING) + location.yCell * Gameboard.CELLSZ, 
		      Gameboard.CELLSZ, 
		      Gameboard.CELLSZ,
			  getTokenTexture(type), 
			  activity.getVertexBufferObjectManager()
		);

		mType = type;
		
		//scale down
		setScaleCenter(Gameboard.CELLSZ / 2, Gameboard.CELLSZ / 2);
		setScale(SIZEFACTOR);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public static int getPlayerTokenId(PlayerIndex player)
	{
		if(player == PlayerIndex.ONE) return mP1TokenId;
		else return mP2TokenId;
	}

	// ===========================================================
	// Override Methods
	// ===========================================================

	;;

	// ===========================================================
	// Methods
	// ===========================================================

	//load all token textures including free cell texture
	public static void loadTokenTextures(BaseGameActivity activity)
	{
		//find token textures
		String[] tokenTextureFiles;
		
		try
		{
			tokenTextureFiles = activity.getAssets().list("textures/res_all/tokens");
			
			//reserve vram for token textures + free cell texture
			mTokenTextures = new ITextureRegion[tokenTextureFiles.length + 1];
			
			//load free cell texture into token slot 0
			Log.d("app", "Loading token #0 from source: free.png");
			mTokenTextures[0] = Utility.load(activity, "free.png", 512, 512);
			
			//load token textures residing in /tokens/
			for(int i=0; i<tokenTextureFiles.length; i++)
			{
				Log.d("app", "Loading token #" + (i + 1) + " from source: " + tokenTextureFiles[i]);
				mTokenTextures[i + 1] = Utility.load(activity, "tokens/" + tokenTextureFiles[i], 512, 512);
			}
		}
		catch (IOException e)
		{
			Log.d("app", "Error occured while loading token textures!");
			e.printStackTrace();
		}
	}

	//retrieve one of the precached textures, setting tokenId to 0 
	//will result in the free cell texture being returned
	public static ITextureRegion getTokenTexture(CellTokenType type)
	{
		if(type == CellTokenType.FREE) 			return mTokenTextures[0];
		else if(type == CellTokenType.P1TOKEN) 	return mTokenTextures[mP1TokenId];
		else									return mTokenTextures[mP2TokenId];
	}

	//return the type of this token
	public CellTokenType getType()
	{
		return mType;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	//symbol types
	public enum CellTokenType
	{
		FREE, 
		P1TOKEN, 
		P2TOKEN
	}
}
