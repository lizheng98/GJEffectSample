package com.gj.effect;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.view.View;

import org.json.JSONObject;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Administrator on 2017/2/17.
 */

public class GifLayer extends Layer {

	private boolean isLoop;

	public boolean isLoop() {
		return isLoop;
	}

	public GifLayer(Context context) {
		super(context);
	}

	@Override
	public void fromJson(JSONObject json) {
		super.fromJson(json);
		isLoop = json.optBoolean("loop", false);
	}

	@Override
	public void startAnimator() {
		valueAnimator.setDuration(this.duration);
		valueAnimator.setStartDelay(this.getStartShowTime());
		valueAnimator.addListener(new AnimatorListenerAdapter() {

			@Override
			public void onAnimationStart(Animator animation) {
				super.onAnimationStart(animation);
				if (GifLayer.this.target instanceof GifImageView) {
					((GifDrawable) ((GifImageView) GifLayer.this.target).getDrawable()).start();
				}
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				super.onAnimationEnd(animation);
				if (GifLayer.this.endIsVisible) {
					if (GifLayer.this.target instanceof GifImageView) {
						((View) GifLayer.this.target).setVisibility(View.INVISIBLE);
						((GifImageView) GifLayer.this.target).destroyDrawingCache();
					}
				}
			}
		});
		valueAnimator.start();
	}
}
