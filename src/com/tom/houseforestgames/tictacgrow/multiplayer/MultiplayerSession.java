/* '*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*
 * <=========================================================================================>
 * File: MultiplayerSession.java
 * Created: 23.01.2015
 * Author: HAUSWALD, Tom.
 * <=========================================================================================>
 * '*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*
 * Description:
 * <=========================================================================================>
 * Stores all information about a Tic, Tac, Grow game session between two online connected
 * users.
 * <=========================================================================================>
 * '*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*' */

package com.tom.houseforestgames.tictacgrow.multiplayer;

import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.tom.houseforestgames.tictacgrow.TicTacGrowActivity.PlayerIndex;
import com.tom.houseforestgames.tictacgrow.game.Gameboard;

public class MultiplayerSession extends JSONObject
{
	// ===========================================================
	// Constants
	// ===========================================================

	// json document attribute keys
	private static final String
			JSON_KEY_ID = "uuid",
			JSON_KEY_USERS = "users",
			JSON_KEY_CURRENT_PLAYER_ID = "current_turn",
			JSON_KEY_BOARD_STATE = "board_state";

	// database collection name prefix for stored game sessions
	private static final String COLLECTION_NAME_PREFIX = "game_session_";

	// ===========================================================
	// Fields
	// ===========================================================

	// storage service collection name

	// ===========================================================
	// Constructors
	// ===========================================================

	// create new multiplayer session object for the given users
	public MultiplayerSession( String user1, String user2 )
	{
		try
		{
			this.put( JSON_KEY_ID, UUID.randomUUID().toString() );
			this.put( JSON_KEY_USERS, user1.trim() + ";" + user2.trim() );
			this.put( JSON_KEY_CURRENT_PLAYER_ID, PlayerIndex.ONE.name() );
			this.put( JSON_KEY_BOARD_STATE, Gameboard.EMPTY_BOARD_STRING );
		}
		catch( JSONException e )
		{
			Log.e( "app", "Error constructing Game Session JSON Document!", e );
		}
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// return the full json collection name
	public final String getCollectionName()
	{
		return COLLECTION_NAME_PREFIX + getSessionId();
	}

	// return the session id
	public final String getSessionId()
	{
		try
		{
			return getString( JSON_KEY_ID );
		}
		catch( JSONException e )
		{
			Log.e( "app", "Error extracting Value from JSON Document!", e );
			return null;
		}
	}

	// return the names of the users participating in the game session
	public final String getUsername( PlayerIndex player )
	{
		try
		{
			return getString( JSON_KEY_USERS ).split( ";" )[player.ordinal()];
		}
		catch( JSONException e )
		{
			Log.e( "app", "Error extracting Value from JSON Document!", e );
			return null;
		}
	}

	// return which player's turn it currently is
	public final PlayerIndex getCurrentPlayerId()
	{
		try
		{
			return PlayerIndex.valueOf( PlayerIndex.class, getString( JSON_KEY_CURRENT_PLAYER_ID ) );
		}
		catch( JSONException e )
		{
			Log.e( "app", "Error extracting Value from JSON Document!", e );
			return null;
		}
	}

	// return the current board state
	public final String getBoardStateStr()
	{
		try
		{
			return getString( JSON_KEY_BOARD_STATE );
		}
		catch( JSONException e )
		{
			Log.e( "app", "Error extracting Value from JSON Document!", e );
			return null;
		}
	}

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
