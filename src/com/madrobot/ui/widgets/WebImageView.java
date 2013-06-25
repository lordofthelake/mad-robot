package com.madrobot.ui.widgets;

import java.net.URI;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.ViewSwitcher;

import com.madrobot.R;
import com.madrobot.tasks.DataResponse;
import com.madrobot.tasks.DownloadBitmapTask;
import com.madrobot.tasks.TaskNotifier;

/**
 * An image view that fetches its image off the web using the supplied URL. While the image is being downloaded, a
 * progress indicator will be shown.
 * <p>
 * <b>Styled Attributes</b><br/>
 * <ul>
 * <li><b>imageUrl</b>(string): URL for the image to be downloaded</li>
 * <li><b>autoLoad</b>(boolean): Flag to indicate if the image should be downloaded automatically or should be user
 * initiated {@link #loadImage()}</li>
 * <li><b>progressDrawable</b>(drawable): Drawable to be used while the image is loaded. if the ProgressBar is not desired.</li>
 * <li><b>errorDrawable</b>(drawable):Drawable to be used if the image could not be loaded.
 * </ul>
 * </p>
 */
public class WebImageView extends ViewSwitcher implements TaskNotifier {

	private boolean autoload;

	private String imageUrl;

	private ImageView imageView;

	private boolean isLoaded;

	private ProgressBar loadingSpinner;

	private Drawable progressDrawable, errorDrawable;

	private ScaleType scaleType = ScaleType.CENTER_CROP;

	public WebImageView(Context context, AttributeSet attributes) {
		super(context, attributes);
		TypedArray styledAttrs = context.obtainStyledAttributes(attributes, R.styleable.WebImageView);

		int progressDrawableId = styledAttrs.getInt(R.styleable.WebImageView_progressDrawable, 0);

		int errorDrawableId = styledAttrs.getInt(R.styleable.WebImageView_errorDrawable, 0);

		Drawable progressDrawable = null;
		if (progressDrawableId > 0) {
			progressDrawable = context.getResources().getDrawable(progressDrawableId);
		}
		Drawable errorDrawable = null;
		if (errorDrawableId > 0) {
			errorDrawable = context.getResources().getDrawable(errorDrawableId);
		}
		initialize(context, styledAttrs.getString(R.styleable.WebImageView_imageUrl), progressDrawable, errorDrawable,
				styledAttrs.getBoolean(R.styleable.WebImageView_autoLoad, true));
		// styles.recycle();
	}

	/**
	 * @param context
	 *            the view's current context
	 * @param imageUrl
	 *            the URL of the image to download and show
	 * @param autoLoad
	 *            Whether the download should start immediately after creating the view. If set to false, use
	 *            {@link #loadImage()} to manually trigger the image download.
	 */
	public WebImageView(Context context, String imageUrl, boolean autoLoad) {
		super(context);
		initialize(context, imageUrl, null, null, autoLoad);
	}

	/**
	 * @param context
	 *            the view's current context
	 * @param imageUrl
	 *            the URL of the image to download and show
	 * @param progressDrawable
	 *            the drawable to be used for the {@link ProgressBar} which is displayed while the image is loading
	 * @param autoLoad
	 *            Whether the download should start immediately after creating the view. If set to false, use
	 *            {@link #loadImage()} to manually trigger the image download.
	 */
	public WebImageView(Context context, String imageUrl, Drawable progressDrawable, boolean autoLoad) {
		super(context);
		initialize(context, imageUrl, progressDrawable, null, autoLoad);
	}

	/**
	 * @param context
	 *            the view's current context
	 * @param imageUrl
	 *            the URL of the image to download and show
	 * @param progressDrawable
	 *            the drawable to be used for the {@link ProgressBar} which is displayed while the image is loading
	 * @param errorDrawable
	 *            the drawable to be used if a download error occurs
	 * @param autoLoad
	 *            Whether the download should start immediately after creating the view. If set to false, use
	 *            {@link #loadImage()} to manually trigger the image download.
	 */
	public WebImageView(
			Context context,
			String imageUrl,
			Drawable progressDrawable,
			Drawable errorDrawable,
			boolean autoLoad) {
		super(context);
		initialize(context, imageUrl, progressDrawable, errorDrawable, autoLoad);
	}

	private void addImageView(Context context) {
		imageView = new ImageView(context);
		imageView.setScaleType(scaleType);
		LayoutParams lp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		lp.gravity = Gravity.CENTER;
		addView(imageView, 1, lp);
	}

	private void addLoadingSpinnerView(Context context) {
		loadingSpinner = new ProgressBar(context);
		loadingSpinner.setIndeterminate(true);
		if (this.progressDrawable == null) {
			this.progressDrawable = loadingSpinner.getIndeterminateDrawable();
		} else {
			loadingSpinner.setIndeterminateDrawable(progressDrawable);
			if (progressDrawable instanceof AnimationDrawable) {
				((AnimationDrawable) progressDrawable).start();
			}
		}
		LayoutParams lp = new LayoutParams(progressDrawable.getIntrinsicWidth(), progressDrawable.getIntrinsicHeight());
		lp.gravity = Gravity.CENTER;
		addView(loadingSpinner, 0, lp);
	}

	/**
	 * Returns the URL of the image to show
	 * 
	 * @return
	 */
	public String getImageUrl() {
		return imageUrl;
	}

	private void initialize(Context context, String imageUrl, Drawable progressDrawable, Drawable errorDrawable, boolean autoLoad) {
		this.imageUrl = imageUrl;
		this.progressDrawable = progressDrawable;
		this.errorDrawable = errorDrawable;
		this.autoload = autoLoad;
		addLoadingSpinnerView(context);
		addImageView(context);

		if (autoLoad && imageUrl != null) {
			loadImage();
		}
	}

	public boolean isLoaded() {
		return isLoaded;
	}

	/**
	 * Use this method to trigger the image download if you had previously set autoLoad to false.
	 */
	public void loadImage() {
		if (imageUrl == null) {
			throw new IllegalStateException("image URL is null; did you forget to set it for this view?");
		}
		DownloadBitmapTask task = new DownloadBitmapTask(getContext(), this);
		Object[] param = new Object[2];
		param[0] = new URI[] { URI.create(imageUrl) };
		param[1] = new BitmapFactory.Options();
		task.execute(param);
	}

	@Override
	public void onCarrierChanged(int bearerStatus) {

	}

	@Override
	public void onError(Throwable t) {

	}

	@Override
	public void onSuccess(DataResponse response) {

		if (response.getResponseStatus() > 0) {
			Object data = response.getData();
			if (data != null) {
				imageView.setImageBitmap((Bitmap) data);
			}
		} else {
			// error occured
			imageView.setImageDrawable(errorDrawable);
		}
		isLoaded = true;
		setDisplayedChild(1);
	}

	@Override
	public void onTaskCompleted() {

	}

	@Override
	public void onTaskStarted() {

	}

	/**
	 * Reload the imageview with a new url or use the existing one.
	 * 
	 * @param url
	 *            if null the currently set url is used.
	 */
	public void reloadImage(String url) {
		if (url != null)
			this.imageUrl = url;
		loadImage();
	}

	@Override
	public void reset() {
		super.reset();

		this.setDisplayedChild(0);
	}

	/**
	 * Set the imageUrl
	 * 
	 * @param imageUrl
	 */
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
		if (autoload)
			loadImage();
	}

	/**
	 * Often you have resources which usually have an image, but some don't. For these cases, use this method to supply
	 * a placeholder drawable which will be loaded instead of a web image.
	 * 
	 * @param imageResourceId
	 *            the resource of the placeholder image drawable
	 */
	public void setNoImageDrawable(int imageResourceId) {
		imageView.setImageDrawable(getContext().getResources().getDrawable(imageResourceId));
		setDisplayedChild(1);
	}
}
