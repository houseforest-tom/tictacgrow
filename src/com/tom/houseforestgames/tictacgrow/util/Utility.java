/*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*
 * <=========================================================================================>
 * File: 	 Utility.java
 * Created:  08.01.2015
 * Author:   HAUSWALD, Tom.
 * <=========================================================================================>
 *'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*
 * Description:
 * <=========================================================================================>
 * Contains various helper functions, e.g. for loading textures for given aspect ratios and
 * centering text objects.
 * <=========================================================================================>
 *'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*/

package com.tom.houseforestgames.tictacgrow.util;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.andengine.entity.text.Text;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.BaseGameActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.tom.houseforestgames.tictacgrow.TicTacGrowActivity;
import com.tom.houseforestgames.tictacgrow.TicTacGrowActivity.RenderSurfaceRatio;

public class Utility
{
	// ===========================================================
	// Constants
	// ===========================================================

	;;

	// ===========================================================
	// Fields
	// ===========================================================

	;;

	// ===========================================================
	// Constructors
	// ===========================================================

	;;

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	;;

	// ===========================================================
	// Override Methods
	// ===========================================================

	;;

	// ===========================================================
	// Methods
	// ===========================================================

	//load a resolution independet texture asset from /textures/res_all/
	public static ITextureRegion load(BaseGameActivity activity, String assetName, int maxWidth, int maxHeight)
	{
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("textures/res_all/");
		BitmapTextureAtlas texture = new BitmapTextureAtlas(activity.getTextureManager(), maxWidth, maxHeight, TextureOptions.BILINEAR);
		ITextureRegion region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(texture, activity, assetName, 0, 0);
		texture.load();
		return region;
	}
	
	//load the texture asset from the specified aspect ratio's dedicated folder using the specified texture filtering method
	public static ITextureRegion loadSuitRatio(BaseGameActivity activity, RenderSurfaceRatio ratio, String assetName, TextureOptions filter)
	{
		int resW = 1152;
		int resH = 1152;
		if(ratio == RenderSurfaceRatio.FOUR_OVER_THREE) resH = 1536;
		else if(ratio == RenderSurfaceRatio.FIVE_OVER_THREE) resH = 1920;
		
		String resolutionStr = "res_" + resW + "x" + resH;
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("textures/" + resolutionStr + "/");
		BitmapTextureAtlas texture = new BitmapTextureAtlas(activity.getTextureManager(), resW, resH, filter);
		ITextureRegion region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(texture, activity, assetName, 0, 0);
		texture.load();
		return region;
	}
	
	//load the texture asset from the specified aspect ratio's dedicated folder
	public static ITextureRegion loadSuitRatio(BaseGameActivity activity, RenderSurfaceRatio ratio, String assetName)
	{
		return loadSuitRatio(activity, ratio, assetName, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
	}
	
	//center a text object horizontally and vertically
	public static void centerText(Text text, float x, float y)
	{
		float w = text.getLineWidths().get(0);
		float h = text.getFont().getLineHeight();
		
		text.setX(x - w / 2);
		text.setY(y - h / 2);
	}
	
	//center a text object horizontally
	public static void centerTextX(Text text, float x)
	{
		float w = text.getLineWidths().get(0);
		text.setX(x - w / 2);
	}
	
	//center a text object vertically
	public static void centerTextY(Text text, float y)
	{
		float h = text.getFont().getLineHeight();
		text.setY(y - h / 2);
	}

	//hash a string and return fixed length output (192 bit)
	public static String quickHash(String input) 
	{
		String temp = "", result = "";
		int index;
		
		for(int i=0; i<24; i++)
		{
			//append 65 + (input[i % len(input)] * i^2) % 58
			temp += (char)( 65 + (((int)input.charAt(i % input.length())) * (i * i)) % 58 );
		}
		
		//random shuffle
		for(int j=0; j<24; j++)
		{
			index = (int)(Math.random() * temp.length());
			result += temp.charAt(index);
			
			//remove already used characters
			temp = temp.substring(0, index) + temp.substring(index + 1, temp.length());
		}
		
		return result;
	}
	
	//returns the amount of milliseconds passed in the number of years specified
	public static long yearsToMillis(int years)
	{
		return (long)Math.round((float)years * 364.25f * 24.0f * 60.0f * 60.0f * 1000.0f);
	}
	
	//constructs a date object referring to the date of birth specified
	public static Date dateOfBirth(int day, int month, int year) 
	{
		Calendar date = Calendar.getInstance(TimeZone.getDefault());
		
		//check input for validity and return null if invalid
		if(month < 1 || month > 12) 
			return null;
		if(day < 1 || day > 31) 	
			return null;
		if(year < 1900 || year > date.get(Calendar.YEAR)) 
			return null;
		if(month == 2 && day > 29) 	
			return null;
		if(month == 2 && day == 29) 
			day = 28;
		
		date.set(Calendar.YEAR,  		year);
		date.set(Calendar.MONTH, 		month-1);
		date.set(Calendar.DAY_OF_MONTH, day);
		return date.getTime();
	}
	
	//show a dialog box popup
	public static void showNotificationDialog(	final BaseGameActivity ctx, 
												final String title, 
												final String text, 
												final String button_text, 
												final OnClickListener listener)
	{
		ctx.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
				builder.setTitle(title);
				builder.setMessage(text);
				builder.setNeutralButton(button_text, listener != null ? listener : new DialogInterface.OnClickListener()
			    {
	            	   @Override public void onClick(DialogInterface dialog, int which)
					   {
	            		   dialog.dismiss();
					   }
			   });;
			   builder.create().show();				
			}
		});	
	}
	
	//show a dialog box popup asking the user to proceed or abort
	public static void showQuestionDialog(	final BaseGameActivity ctx, 
											final String title, 
											final String msg, 
											final String yes_button_text, 
											final String no_button_text, 
											final OnClickListener yes_listener,
											final OnClickListener no_listener)
	{
		ctx.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
				builder.setTitle(title);
				builder.setMessage(msg);
				
				//positive button
				builder.setPositiveButton(yes_button_text, yes_listener != null ? yes_listener : new DialogInterface.OnClickListener()
			    {
	            	   @Override public void onClick(DialogInterface dialog, int which)
					   {
	            		   dialog.dismiss();
					   }
			   });;
			   
			   //negative button
			   builder.setNegativeButton(no_button_text, no_listener != null ? no_listener : new DialogInterface.OnClickListener()
			    {
	            	   @Override public void onClick(DialogInterface dialog, int which)
					   {
	            		   dialog.dismiss();
					   }
			   });;

			   builder.create().show();				
			}
		});	
	}
	
	//show a custom layout dialog
	public static void showCustomDialog(	
			
			final BaseGameActivity ctx, 
			final String title, 
			final int layoutId, 
			final String yes_button_text, 
			final String no_button_text, 
			final OnClickListener yes_listener,
			final OnClickListener no_listener)
	{
		ctx.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
				builder.setTitle(title);
				builder.setView(ctx.getLayoutInflater().inflate(layoutId, null));
				
				//positive button
				builder.setPositiveButton(yes_button_text, yes_listener != null ? yes_listener : new DialogInterface.OnClickListener()
				{
					@Override public void onClick(DialogInterface dialog, int which)
					{
						dialog.dismiss();
					}
				});;
				
				//negative button
				builder.setNegativeButton(no_button_text, no_listener != null ? no_listener : new DialogInterface.OnClickListener()
				{
					@Override public void onClick(DialogInterface dialog, int which)
					{
						dialog.dismiss();
					}
				});;
				
				builder.create().show();				
			}
		});	
	}
	
	//show a dialog box telling the user that a function he requested isn't yet implemented
	public static void showNotImplDialog(BaseGameActivity a)
	{
		Utility.showNotificationDialog(a, "Oops!", "This is not implemented yet, sorry :(", "Hmkay.", null);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	;;
}
