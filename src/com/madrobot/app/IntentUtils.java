package com.madrobot.app;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

/**
 * Intent utilities
 * 
 * @author elton.stephen.kent
 * 
 */
public class IntentUtils {

	/**
	 * Check if the given intent is available/can be resolved.
	 * 
	 * @param context
	 * @param action
	 * @return
	 */
	public static boolean isIntentAvailable(Context context, String action) {
		final PackageManager packageManager = context.getPackageManager();
		final Intent intent = new Intent(action);
		List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(
				intent, PackageManager.MATCH_DEFAULT_ONLY);
		if (resolveInfo.size() > 0) {
			return true;
		}
		return false;
	}

	/**
	 * Open the android market page for the given application.
	 * 
	 * @param context
	 *            application context
	 */
	public static void openMarketPage(Context context) {
		Intent marketIntent = new Intent(Intent.ACTION_VIEW,
				Uri.parse("market://details?id=" + context.getPackageName()));
		context.startActivity(marketIntent);
	}

	/**
	 * Search android market
	 * 
	 * @param query
	 *            to search
	 * @param context
	 *            application context
	 */
	public static void searchMarket(String query, Context context) {
		Intent marketIntent = new Intent(Intent.ACTION_VIEW,
				Uri.parse("market://search?q=" + query));
		context.startActivity(marketIntent);
	}
}
