/*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*
 * <=========================================================================================>
 * File: 	 MultiplayerManager.java
 * Created:  16.01.2015
 * Author:   HAUSWALD, Tom.
 * <=========================================================================================>
 *'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*
 * Description:
 * <=========================================================================================>
 * Handles session management using the App42 API and provides the needed functionality to
 * update online game sessions with modified information. Also contains methods needed to 
 * create new user accounts or delete existing ones.
 * <=========================================================================================>
 *'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*/

package com.tom.houseforestgames.tictacgrow.multiplayer;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;

import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.StorageService;
import com.shephertz.app42.paas.sdk.android.user.User;
import com.shephertz.app42.paas.sdk.android.user.User.Profile;
import com.shephertz.app42.paas.sdk.android.user.User.UserGender;
import com.shephertz.app42.paas.sdk.android.user.UserService;
import com.tom.houseforestgames.tictacgrow.TicTacGrowActivity;
import com.tom.houseforestgames.tictacgrow.util.Utility;

public class MultiplayerManager 
{
	// ===========================================================
	// Constants
	// ===========================================================

	//AppWarp key strings
	private static final String API_KEY 	= "49fc3dee9744de2c939e3d85ef9f3c89a4cc2a12c64fdd9448d2816b07006fcb";
	private static final String SECRET_KEY 	= "b6dba1d417f7c51b32e0f8149594f75109e45397e4b712c6006ea67e2137dee8";
	
	//user account constants
	public static final String     COUNTRY_NOT_SPECIFIED  = "";
	public static final UserGender GENDER_NOT_SPECIFIED	  = null;
	public static final Date   	   BIRTHDAY_NOT_SPECIFIED = null;
	
	//App42 storage service database name for game sessions
	private static final String SESSION_DB_NAME = "sessions";
	
	// ===========================================================
	// Fields
	// ===========================================================

	//App42 user service
	private UserService mUserService;
	
	//App42 session storage service
	private StorageService mStorageService;
		
	//Latest account creation and profile update request
	private MultiplayerRequestResult mRequestResult;

	// ===========================================================
	// Constructors
	// ===========================================================

	//instantiate a new multiplayer manager instance, thereby intializing
	//all networking information required for the usage of App42 services.
	public MultiplayerManager(TicTacGrowActivity activity)
	{
		//initialize App42
		App42API.initialize(activity, API_KEY, SECRET_KEY);
		
		//start user service
		mUserService = App42API.buildUserService();
		
		//start storage service
		mStorageService = App42API.buildStorageService();
		
		//latest async request's result
		mRequestResult = new MultiplayerRequestResult();
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	//return a reference to the user service
	public UserService getUserService()
	{
		return mUserService;
	}
	
	//return a reference to the storage service
	public StorageService getStorageService()
	{
		return mStorageService;
	}
	
	// ===========================================================
	// Override Methods
	// ===========================================================

	;;

	// ===========================================================
	// Methods
	// ===========================================================

	//register a new user
	public void queryCreateAccount( 
			
			final String 	name,
			final String 	mail,
			final String 	pwd,
			final UserGender gender, 
			final Date 		birthday, 
			final String 	country )
	{
		//reset request state
		mRequestResult.reset();
		
		//create App42 user
		mUserService.createUser(name, Utility.quickHash(pwd), mail, new App42CallBack(){

			//succesfully created account
			@Override public void onSuccess(Object response) 
			{
				User user = (User)response;
				Log.d("app", "Successfully created account!" +
							 "\nName:    " + user.getUserName() +
							 "\nEmail:   " + user.getEmail());
				
				//obtain detailed profile information from passed account
				Profile profile = user.new Profile();
				
				//update user age (set date of birth precise to year)
				if(birthday != BIRTHDAY_NOT_SPECIFIED)
				{
					profile.setDateOfBirth(birthday);
				}
				
				//update user gender
				if(gender != GENDER_NOT_SPECIFIED)
				{
					profile.setSex(gender);
				}
				
				//update user country
				if(country != COUNTRY_NOT_SPECIFIED)
				{
					profile.setCountry(country);
				}
				
				user.setProfile(profile);
				
				//update user profile with supplied data
				mUserService.createOrUpdateProfile(user, new App42CallBack()
				{
					//user profile update succeeded
					@Override public void onSuccess(Object response)
					{
						User user = (User)response;
						Profile profile = user.getProfile();
						SimpleDateFormat fmt = new SimpleDateFormat("dd.MM.yyyy");
						Log.d("app", "Succesfully updated user profile!" +
						 "\nBorn   : " + fmt.format(profile.getDateOfBirth()) +
						 "\nGender : " + profile.getSex() +
						 "\nCountry: " + profile.getCountry());
						mRequestResult.notifySuccess();
					}
					
					//user profile update failed
					@Override public void onException(Exception e)
					{
						Log.d("app", "Could not update account profile! Reason: " + e.getMessage());
						mRequestResult.notifyFail(e);
					}
				});
			}
			
			//account creation failed
			@Override public void onException(Exception e) 
			{
				//account already exists
				if(extractErrorCode(e) == 2001)
				{
					Log.d("app", "Account requested to create already exists! (" + name + ")");
				}
				else
				{
					Log.d("app", "Could not create account! Reason: " + e.getMessage());
				}
				
				mRequestResult.notifyFail(e);
			}
		});
	}
	
	//authenticate specified user login information
	public void queryAuthenticateUser(final String user, final String pwd)
	{
		//reset request state
		mRequestResult.reset();
		
		//try to authenticate user
		mUserService.authenticate(user, Utility.quickHash(pwd), new App42CallBack() 
		{	
			@Override public void onSuccess(Object response) 
			{
				mRequestResult.notifySuccess();
				Log.d("app", "User authenticated successfully!");
			}
			
			@Override public void onException(Exception e) 
			{
				Log.d("app", "User authentication failed! Reason: " + e.getMessage());
				mRequestResult.notifyFail(e);
			}
		});
	}
	
	//Creates an online 2-player session between the given users
	public void queryCreateSession(final String srcUser, final String dstUser)
	{
		mRequestResult.reset();
		
		//construct game session established between the two players
		//and upload it to the App42 cloud storage
		
		MultiplayerSession session = new MultiplayerSession(srcUser, dstUser);
		String db = SESSION_DB_NAME;
		String collection = session.getCollectionName();
		
		mStorageService.insertJSONDocument(db, collection, session, new App42CallBack()
		{	
			@Override public void onSuccess(Object response) 
			{
				mRequestResult.notifySuccess();
				Log.d("app", "Successfully created Game Session!");
			}
			
			@Override public void onException(Exception e) 
			{
				mRequestResult.notifyFail(e);
				Log.d("app", "Could not create Game Session!");
			}
		});
	}
	
	//check if the specified user exists in the database
	public void queryUserExists(final String name)
	{
		mRequestResult.reset();
		mUserService.getUser(name, new App42CallBack() 
		{
			@Override public void onSuccess(Object response) 
			{
				mRequestResult.notifySuccess();
			}
			
			@Override public void onException(Exception e) 
			{
				mRequestResult.notifyFail(e);
			}
		});
	}

	//wait for the completion of the latest request
	public final MultiplayerRequestResult waitForRequest()
	{
		while(mRequestResult.pending());
		return mRequestResult;
	}
	
	//check whether request result is still pending
	public boolean requestPending()
	{
		return mRequestResult.pending();
	}
	
	//extract App42 error code from generic exception
	private int extractErrorCode(Exception e)
	{
		return ((App42Exception)e).getAppErrorCode();
	}
	
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	public class MultiplayerRequestResult
	{
		//request states
		private static final int REQUEST_UNKNOWN = -1;
		private static final int REQUEST_FAIL 	 =  0;
		private static final int REQUEST_SUCCESS =  1;
		
		private int mState;
		private int mErrorCode;
		
		public MultiplayerRequestResult()
		{
			mState = REQUEST_UNKNOWN;
			mErrorCode = 0;
		}
		
		public void reset()
		{
			mState = REQUEST_UNKNOWN;
			mErrorCode = 0;
		}
		
		public void notifySuccess()
		{
			mState = REQUEST_SUCCESS;
			mErrorCode = 0;
		}
		
		public void notifyFail(Exception e)
		{
			mState = REQUEST_FAIL;
			mErrorCode = extractErrorCode(e);
		}
		
		public boolean succeeded()
		{
			return mState == REQUEST_SUCCESS;
		}
		
		public boolean pending()
		{
			return mState == REQUEST_UNKNOWN;
		}
		
		public boolean failed()
		{
			return mState == REQUEST_FAIL;
		}
		
		public int getAppErrorCode()
		{
			return mErrorCode;
		}
	}
}
