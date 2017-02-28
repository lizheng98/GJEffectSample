package com.gj.effect.sample;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.gj.effect.CompositionLoader;
import com.gj.effect.EffectComposition;
import com.gj.effect.GJEffectView;

import effect.gj.com.gjeffectsample.R;

public class MainActivity extends AppCompatActivity {
	private String TAG = MainActivity.class.getSimpleName();
	private GJEffectView mGJEffect;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mGJEffect = (GJEffectView) findViewById(R.id.live_gift_effect);
		startAnimation();
	}

	private void startAnimation() {
		final AnimatorListenerAdapter animatorListenerAdapter = new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				super.onAnimationEnd(animation);
				mGJEffect.removeAllListeners();
				mGJEffect.removeAllViews();
				mGJEffect.setVisibility(View.GONE);
			}
		};
		//加载网络动画数据
//		EffectGiftLoader.getInstance(this).loadDataForComposition("http://",
//				new EffectComposition.OnCompositionLoadedListener() {
//					@Override
//					public void onCompositionLoaded(EffectComposition composition) {
//						Log.e(TAG, "showGifEffect...loading EffectComposition：" + composition);
//						//开始显示动画
//						if (composition != null) {
//							//显示大额礼物动效
//							mGJEffect.setComposition(composition);
//							mGJEffect.setVisibility(View.VISIBLE);
//							mGJEffect.startAnimation(animatorListenerAdapter);
//						}
//					}
//				});


		//加载sd卡动画数据
		CompositionLoader compositionLoader = new CompositionLoader(this, new EffectComposition.OnCompositionLoadedListener() {
			@Override
			public void onCompositionLoaded(EffectComposition composition) {
				Log.e(TAG, "showGifEffect...loading EffectComposition：" + composition);
				//开始显示动画
				if (composition != null) {
					//显示大额礼物动效
					mGJEffect.setComposition(composition);
					mGJEffect.setVisibility(View.VISIBLE);
					mGJEffect.startAnimation(animatorListenerAdapter);
				}
			}
		});
		compositionLoader.execute("/storage/emulated/0/Android/data/com.gj.effectsample/effect/yacht.zip");
	}
}
