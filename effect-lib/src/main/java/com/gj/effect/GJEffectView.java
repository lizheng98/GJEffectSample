package com.gj.effect;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieImageAsset;
import com.airbnb.lottie.OnCompositionLoadedListener;
import com.gj.effect.util.BitmapUtility;
import com.plattysoft.leonids.ParticleSystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Administrator on 2017/2/13.
 * 果酱直播大额礼物动效View
 */

public class GJEffectView extends RelativeLayout {
	private EffectComposition mEffectComposition;
	private Context mContext;

	private final ValueAnimator mAnimator = ValueAnimator.ofFloat(0f, 1f);

	public GJEffectView(Context context) {
		super(context);
		init(context, null);
	}

	public GJEffectView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public GJEffectView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}

	private void init(Context context, @Nullable AttributeSet attrs) {
		this.mContext = context.getApplicationContext();
//		TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.LottieAnimationView);
//		String fileName = ta.getString(R.styleable.LottieAnimationView_lottie_fileName);
//		if (!isInEditMode() && fileName != null) {
//			setAnimation(fileName)	;
//		}
//		if (ta.getBoolean(R.styleable.LottieAnimationView_lottie_autoPlay, false)) {
//			lottieDrawable.playAnimation();
//		}
//		lottieDrawable.loop(ta.getBoolean(R.styleable.LottieAnimationView_lottie_loop, false));
//		ta.recycle();
//		setLayerType(LAYER_TYPE_SOFTWARE, null);
//		setAnimator();

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	/**
	 * 解析动效参数并显示
	 *
	 * @param composition
	 */
	private void buildLayersForComposition(EffectComposition composition) {
		LayoutParams layoutParams = (LayoutParams) this.getLayoutParams();
		layoutParams.width = composition.getWidth();
		layoutParams.height = composition.getHeigth();
		layoutParams.topMargin = composition.getmMarginTop();
		this.setLayoutParams(layoutParams);
		//开始显示时，初始化动画参数
		this.setAlpha(1);
		this.setTranslationX(0);
		this.setTranslationY(0);
		this.setScaleX(1);
		this.setScaleY(1);
		this.setRotation(0);

		for (int i = 0; i < composition.getmLayers().size(); i++) {
			Layer layer = composition.getmLayers().get(i);
			if (layer instanceof ImageLayer) {
				addEffectImageView((ImageLayer) layer);
			} else if (layer instanceof ParticleLayer) {
				addEffectPartile(composition.getmLayers(), (ParticleLayer) layer);
			} else if (layer instanceof GifLayer) {
				addEffectGif((GifLayer) layer);
			} else if (layer instanceof LottieLayer) {
				addEffectLottie((LottieLayer) layer);
			}
		}
	}

	private void addEffectImageView(ImageLayer layer) {
		ImageView imageView = new ImageView(mContext);
		LayoutParams params = new LayoutParams(layer.getWidth(), layer.getHeigth());
		params.leftMargin = layer.getStartPosition()[0];
		params.topMargin = layer.getStartPosition()[1];

		String imagePath = mEffectComposition.getmEffectFilePath() + File.separator + layer.getValue();
		imageView.setImageBitmap(BitmapUtility.LoadImageFromUrl(imagePath, layer.getImageMaxSize()));
		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		imageView.setVisibility(View.INVISIBLE);
		//file:///mnt/sdcard/image.png
		//显示时，设置动画对象
		layer.setTarget(imageView);
		addView(imageView, params);
	}

	private void addEffectPartile(ArrayList<Layer> layers, ParticleLayer layer) {
		FrameLayout layout = new FrameLayout(mContext);
		LayoutParams params = new LayoutParams(layer.getWidth(), layer.getHeigth());
		params.leftMargin = layer.getStartPosition()[0];
		params.topMargin = layer.getStartPosition()[1];
		addView(layout, params);

		int size = layer.getParticleBtimapValues().size();
		Bitmap[] bitmaps = new Bitmap[size];
		for (int i = 0; i < size; i++) {
			String imagePath = mEffectComposition.getmEffectFilePath() + File.separator + layer.getParticleBtimapValues().get(i);
			bitmaps[i] = BitmapUtility.LoadImageFromUrl(imagePath, layer.getWidth());
		}

		ParticleSystem ps = new ParticleSystem(layout, layer.getMaxParticles(), bitmaps, layer.getTimeToLive());
		if (layer.getSpeedMin() == -1 || layer.getSpeedMax() == -1) {
			ps.setSpeedByComponentsRange(layer.getSpeedMinX(), layer.getSpeedMaxX(), layer.getSpeedMinY(), layer.getSpeedMaxY());
		} else {
			ps.setSpeedModuleAndAngleRange(layer.getSpeedMin(), layer.getSpeedMax(), layer.getMinAngle(), layer.getMaxAngle());
		}
		ps.setRotationSpeedRange(layer.getMinRotationSpeed(), layer.getMaxRotationSpeed());
		ps.setInitialRotationRange(layer.getMinRotationAngle(), layer.getMaxRotationAngle());
		//由于Partile系统化图片是根据Bitmap大小画的，而加载在sd卡的图片吧Bitmap大小都会保持不变，所以通过改变scale来适配不同的机型
		final float scale = mContext.getResources().getDisplayMetrics().density;
		ps.setScaleRange((layer.getMinScale() / 3 * scale), (layer.getMaxScale() / 3 * scale));
		ps.setAccelerationModuleAndAndAngleRange(layer.getMinAcceleration(), layer.getMaxAcceleration(), layer.getMinAccelerateAngle(), layer.getMaxAccelerateAngle());
		ps.setFadeOut(layer.getFadeOutTime());
		layer.setTarget(ps);

		if (layer.getRefId() != ParticleLayer.REF_LAYER_ID_NULL) {
			layer.setRefView((View) layers.get(layer.getRefId()).getTarget());
		}

	}

	private void addEffectGif(GifLayer layer) {
		GifImageView gifImageView = new GifImageView(mContext);
		LayoutParams params = new LayoutParams(layer.getWidth(), layer.getHeigth());
		params.leftMargin = layer.getStartPosition()[0];

		params.topMargin = layer.getStartPosition()[1];
		try {
			GifDrawable gifDrawable = new GifDrawable(mEffectComposition.getmEffectFilePath() + File.separator + layer.getValue());
			gifDrawable.stop();
			gifDrawable.setLoopCount(layer.isLoop() ? Character.MAX_VALUE : 1);
			gifImageView.setImageDrawable(gifDrawable);
			gifImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			gifImageView.setVisibility(View.INVISIBLE);
			//显示时，设置动画对象
			layer.setTarget(gifImageView);
			addView(gifImageView, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addEffectLottie(final LottieLayer layer) {
		final LottieAnimationView animationView = new LottieAnimationView(mContext);
		LayoutParams params = new LayoutParams(layer.getWidth(), layer.getHeigth());
		params.leftMargin = layer.getStartPosition()[0];
		params.topMargin = layer.getStartPosition()[1];
		try {
			String dataFilePath = mEffectComposition.getmEffectFilePath() + File.separator + layer.getFolder() + File.separator + layer.getValue();
			File data = new File(dataFilePath);
			InputStream inputStream = new FileInputStream(data);
			LottieComposition.Factory.fromInputStream(mContext, inputStream, new OnCompositionLoadedListener() {
				@Override
				public void onCompositionLoaded(LottieComposition composition) {
					animationView.setComposition(composition);
				}
			});

			animationView.setVisibility(View.INVISIBLE);
			animationView.loop(layer.isLoop());
			setLottieAnimationScaleType(animationView, layer.getScaleType());
			animationView.setImageAssetDelegate(new ImageAssetDelegate() {
				@Override
				public Bitmap fetchBitmap(LottieImageAsset asset) {
					String imagePath = mEffectComposition.getmEffectFilePath() + File.separator + layer.getFolder() + File.separator + LottieLayer.LOTTIE_IMAGE + File.separator + asset.getFileName();
					BitmapFactory.Options options = new BitmapFactory.Options();
					// 不用加载图片返回图片
					options.inJustDecodeBounds = false;
					options.inPreferredConfig = Bitmap.Config.RGB_565;
					options.inDither = true;
					return BitmapFactory.decodeFile(imagePath, options);
				}
			});
			layer.setTarget(animationView);
			addView(animationView, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置scaleType
	 *
	 * @param scaleType {@link android.widget.ImageView.ScaleType}
	 */
	private void setLottieAnimationScaleType(LottieAnimationView view, int scaleType) {
		if (scaleType == -1) {
			return;
		}
		switch (scaleType) {
			case 0:
				view.setScaleType(ImageView.ScaleType.MATRIX);
				break;
			case 1:
				view.setScaleType(ImageView.ScaleType.FIT_XY);
				break;
			case 2:
				view.setScaleType(ImageView.ScaleType.FIT_START);
				break;
			case 3:
				view.setScaleType(ImageView.ScaleType.FIT_CENTER);
				break;
			case 4:
				view.setScaleType(ImageView.ScaleType.FIT_END);
				break;
			case 5:
				view.setScaleType(ImageView.ScaleType.CENTER);
				break;
			case 6:
				view.setScaleType(ImageView.ScaleType.CENTER_CROP);
				break;
			case 7:
				view.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
				break;
		}
	}

	public void setComposition(EffectComposition composition) {
		if (composition == null)
			throw new NullPointerException("EffectComposition can not be null ");
		mEffectComposition = composition;
		mAnimator.setDuration(composition.getDuration());
		buildLayersForComposition(composition);
	}

	public void startAnimation(final AnimatorListenerAdapter animatorListenerAdapter) {
		if (mEffectComposition == null)
			return;
		mAnimator.start();
		mAnimator.addListener(animatorListenerAdapter);

		for (Animator animator : mEffectComposition.getAnimations()) {
			animator.setTarget(GJEffectView.this);
			animator.start();
		}

		for (int i = 0; i < mEffectComposition.getmLayers().size(); i++) {
			Layer layer = mEffectComposition.getmLayers().get(i);
			layer.startAnimator();
		}
	}


	public void removeAllListeners() {
		mAnimator.removeAllListeners();
	}

}
