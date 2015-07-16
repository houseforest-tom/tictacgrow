/* '*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*
 * <=========================================================================================>
 * File: AdmobManager.java
 * Created: 12.01.2015
 * Author: HAUSWALD, Tom.
 * <=========================================================================================>
 * '*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*
 * Description:
 * <=========================================================================================>
 * This class handles everything related to in-app advertisement. It provides the needed
 * functionality to the user to show / hide the banner and interstitial ads and to update
 * the content of both ads. By toggling the ADVERTISEMENT_ENABLED switch, all ads can be
 * turned off, including that no content requests are sent.
 * <=========================================================================================>
 * '*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*' */

package com.tom.houseforestgames.tictacgrow.util;

import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.startapp.android.publish.StartAppSDK;
import com.tom.houseforestgames.tictacgrow.TicTacGrowActivity;

public class AdmobManager
{
	// ===========================================================
	// Constants
	// ===========================================================

	// for distributed test versions disable advertisements in all cases!
	private static final boolean ADVERTISEMENT_ENABLED = false;

	// admob and mediation partner related identifiers
	private static final String
			BANNER_UNIT_ID = "ca-app-pub-5692831875719243/7271134610",
			INTERSTITIAL_UNIT_ID = "ca-app-pub-5692831875719243/4520987813",
			TEST_DEVICE_ID = "D282F310CA4DF191849C136FDDAC1C63", 		// Hauswald HTC One XL
			STARTAPP_DEVELOPER_ID = "101313428",
			STARTAPP_APP_ID = "201077808";

	// ===========================================================
	// Fields
	// ===========================================================

	// parent activity
	private TicTacGrowActivity mActivity;

	// banner
	private AdView mAdBannerView;

	// interstitial
	private InterstitialAd mInterstitialAd;
	private boolean mInterstitialAdClosed;

	// ===========================================================
	// Constructors
	// ===========================================================

	// construct a new admob manager, initialize the startapp SDK and
	// create and load the banner and interstitial ads.
	public AdmobManager( TicTacGrowActivity activity )
	{
		mActivity = activity;
		mInterstitialAdClosed = false;

		StartAppSDK.init( activity, STARTAPP_DEVELOPER_ID, STARTAPP_APP_ID, false );

		// load ad and add banner view to screen layout (at bottom)
		mAdBannerView = new AdView( activity );
		mAdBannerView.setAdSize( AdSize.SMART_BANNER );
		mAdBannerView.setAdUnitId( AdmobManager.BANNER_UNIT_ID );
		mAdBannerView.refreshDrawableState();
		mAdBannerView.setVisibility( AdView.INVISIBLE );

		// load interstitial ad and add view to screen layout (whole screen)
		mInterstitialAd = new InterstitialAd( activity );
		mInterstitialAd.setAdUnitId( AdmobManager.INTERSTITIAL_UNIT_ID );
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// return a reference to the banner ad view
	public final AdView getBannerAd()
	{
		return mAdBannerView;
	}

	// return a reference to the interstitial ad
	public final InterstitialAd getInterstitialAd()
	{
		return mInterstitialAd;
	}

	// ===========================================================
	// Override Methods
	// ===========================================================

	;;

	// ===========================================================
	// Methods
	// ===========================================================

	// retrieve content for the banner ad from admob mediation partners
	public void updateBannerAd()
	{
		// skip id ads are disabled
		if( !ADVERTISEMENT_ENABLED ) return;

		mActivity.runOnUiThread( new Runnable()
		{

			@Override
			public void run()
			{
				AdRequest.Builder adReqBuilder = new AdRequest.Builder();
				adReqBuilder.addTestDevice( TEST_DEVICE_ID );
				adReqBuilder.addTestDevice( AdRequest.DEVICE_ID_EMULATOR );
				AdRequest req = adReqBuilder.build();
				mAdBannerView.loadAd( req );
				mAdBannerView.setAdListener( new AdListener()
				{

					@Override
					public void onAdClosed()
					{
						Log.d( "app", "Banner ad was closed" );
						super.onAdClosed();
					}

					@Override
					public void onAdFailedToLoad( int errorCode )
					{
						String errstr = "";

						switch( errorCode )
						{
							case AdRequest.ERROR_CODE_NETWORK_ERROR:
								errstr = "network error";
							break;
							case AdRequest.ERROR_CODE_INTERNAL_ERROR:
								errstr = "internal error";
							break;
							case AdRequest.ERROR_CODE_INVALID_REQUEST:
								errstr = "invalid request error";
							break;
							case AdRequest.ERROR_CODE_NO_FILL:
								errstr = "no-fill error";
							break;
						}

						Log.d( "app", "Banner ad failed to load, reason: " + errstr );
						super.onAdFailedToLoad( errorCode );
					}

					@Override
					public void onAdLeftApplication()
					{
						Log.d( "app", "User left application due to banner ad" );
						super.onAdLeftApplication();
					}

					@Override
					public void onAdLoaded()
					{
						Log.d( "app", "Banner ad loaded successfully" );
						super.onAdLoaded();
					}

					@Override
					public void onAdOpened()
					{
						Log.d( "app", "User clicked banner ad" );
						super.onAdOpened();
					}

				} );
			}
		} );
	}

	// make the banner ad (in-)visible
	public void setBannerAdVisibility( final boolean show )
	{
		// skip if ads are disabled
		if( !ADVERTISEMENT_ENABLED ) return;

		mActivity.runOnUiThread( new Runnable()
		{

			@Override
			public void run()
			{
				mAdBannerView.setVisibility( show ? AdView.VISIBLE : AdView.INVISIBLE );
			}
		} );
	}

	// retrieve content for the interstitial ad from admob mediation partners
	public void updateInterstitialAd()
	{
		// skip update if ads are disabled
		if( !ADVERTISEMENT_ENABLED ) return;

		mActivity.runOnUiThread( new Runnable()
		{

			@Override
			public void run()
			{
				AdRequest.Builder adReqBuilder = new AdRequest.Builder();
				adReqBuilder.addTestDevice( TEST_DEVICE_ID );
				adReqBuilder.addTestDevice( AdRequest.DEVICE_ID_EMULATOR );
				AdRequest req = adReqBuilder.build();
				mInterstitialAd.loadAd( req );
				mInterstitialAd.setAdListener( new AdListener()
				{

					@Override
					public void onAdClosed()
					{
						mInterstitialAdClosed = true;
						Log.d( "app", "Interstitial ad was closed" );
						super.onAdClosed();
					}

					@Override
					public void onAdFailedToLoad( int errorCode )
					{
						String errstr = "";

						switch( errorCode )
						{
							case AdRequest.ERROR_CODE_NETWORK_ERROR:
								errstr = "network error";
							break;
							case AdRequest.ERROR_CODE_INTERNAL_ERROR:
								errstr = "internal error";
							break;
							case AdRequest.ERROR_CODE_INVALID_REQUEST:
								errstr = "invalid request error";
							break;
							case AdRequest.ERROR_CODE_NO_FILL:
								errstr = "no-fill error";
							break;
						}

						Log.d( "app", "Interstitial ad failed to load, reason: " + errstr );
						super.onAdFailedToLoad( errorCode );
					}

					@Override
					public void onAdLeftApplication()
					{
						Log.d( "app", "User left application due to interstitial ad" );
						super.onAdLeftApplication();
					}

					@Override
					public void onAdLoaded()
					{
						Log.d( "app", "Interstitial ad loaded successfully" );
						super.onAdLoaded();
					}

					@Override
					public void onAdOpened()
					{
						Log.d( "app", "User clicked interstitial ad" );
						super.onAdOpened();
					}
				} );
			}
		} );
	}

	// make interstitial ad visible
	public void showInterstitialAd()
	{
		// skip showing the interstital if ads are disabled
		if( !ADVERTISEMENT_ENABLED ) return;

		mActivity.runOnUiThread( new Runnable()
		{

			@Override
			public void run()
			{
				// show ad only if loaded
				if( mInterstitialAd.isLoaded() )
				{
					mInterstitialAd.show();
					mInterstitialAdClosed = false;
				}
				else mInterstitialAdClosed = true;
			}
		} );
	}

	// was the interstitial ad closed?
	public boolean wasInterstitialAdClosed()
	{
		// skip check if ads are disabled
		if( !ADVERTISEMENT_ENABLED ) return true;

		// if the ad was closed, reset the flag and return true
		if( mInterstitialAdClosed )
		{
			mInterstitialAdClosed = false;
			return true;
		}
		return false;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	;;
}
