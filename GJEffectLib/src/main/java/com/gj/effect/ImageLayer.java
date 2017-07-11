package com.gj.effect;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/2/15.
 */

public class ImageLayer extends Layer {

	public static String ACTION_TYPE_TRANS = "trans";
	public static String ACTION_TYPE_APHA = "alpha";
	public static String ACTION_TYPE_SCALE = "scale";
	public static String ACTION_TYPE_ROTATION = "rotation";

	private int imageMaxSize;

	private ArrayList<Animator> animations = new ArrayList<>();

	public int getImageMaxSize() {
		return imageMaxSize;
	}

	public ImageLayer(Context context) {
		super(context);
	}

	@Override
	public void fromJson(JSONObject json) {
		super.fromJson(json);
		try {
			int tempSize = width > heigth ? width : heigth;
			imageMaxSize = EffectComposition.effectPx2Px(context, json.optInt("imageMaxSize"));
			imageMaxSize = imageMaxSize > tempSize ? imageMaxSize : tempSize;
			JSONArray actions = json.optJSONArray("actions");
			if (actions != null) {
				for (int i = 0; i < actions.length(); i++) {
					JSONObject action = actions.getJSONObject(i);
					if (ACTION_TYPE_TRANS.equals(action.getString("type"))) {

						PropertyValuesHolder[] propertyValuesHolders = new PropertyValuesHolder[2];
						JSONArray transXKeyframeArray = action.getJSONArray("keyframesX");
						if (transXKeyframeArray.length() > 0) {
							Keyframe[] keyframes = new Keyframe[transXKeyframeArray.length()];
							for (int k = 0; k < transXKeyframeArray.length(); k++) {
								JSONObject tranKeyframe = transXKeyframeArray.getJSONObject(k);
								Keyframe frame = Keyframe.ofFloat((float) tranKeyframe.getDouble("fraction"), EffectComposition.effectPx2Px(context, tranKeyframe.getInt("value")));
								keyframes[k] = frame;
							}
							PropertyValuesHolder p = PropertyValuesHolder.ofKeyframe("translationX", keyframes);
							propertyValuesHolders[0] = p;
						}

						JSONArray transYKeyframeArray = action.getJSONArray("keyframesY");
						if (transYKeyframeArray.length() > 0) {
							Keyframe[] keyframes = new Keyframe[transYKeyframeArray.length()];
							for (int k = 0; k < transYKeyframeArray.length(); k++) {
								JSONObject tranKeyframe = transYKeyframeArray.getJSONObject(k);
								Keyframe frame = Keyframe.ofFloat((float) tranKeyframe.getDouble("fraction"), EffectComposition.effectPx2Px(context, tranKeyframe.getInt("value")));
								keyframes[k] = frame;
							}
							PropertyValuesHolder p = PropertyValuesHolder.ofKeyframe("translationY", keyframes);
							propertyValuesHolders[1] = p;
						}

						ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(target, propertyValuesHolders);
						animator.setStartDelay(action.getInt("startTime"));
						animator.setRepeatCount(action.optInt("repeatCount", 0));
						animator.setDuration(action.getInt("duration"));
						animations.add(animator);
					} else if (ACTION_TYPE_SCALE.equals(action.getString("type"))) {
						JSONArray keyframeArray = action.getJSONArray("keyframes");
						Keyframe[] keyframes = new Keyframe[keyframeArray.length()];
						for (int k = 0; k < keyframeArray.length(); k++) {
							JSONObject tranKeyframe = keyframeArray.getJSONObject(k);
							Keyframe frame = Keyframe.ofFloat((float) tranKeyframe.getDouble("fraction"), (float) tranKeyframe.getDouble("value"));
							keyframes[k] = frame;
						}

						ArrayList<PropertyValuesHolder> arrayList = new ArrayList<>();
						PropertyValuesHolder scaleX = PropertyValuesHolder.ofKeyframe("scaleX", keyframes);
						PropertyValuesHolder scaleY = PropertyValuesHolder.ofKeyframe("scaleY", keyframes);

						arrayList.add(scaleX);
						arrayList.add(scaleY);

						float pivotXValue = (float) action.optDouble("pivotX", 0);
						if (pivotXValue != 0) {
							PropertyValuesHolder pivotX = PropertyValuesHolder.ofFloat("pivotX", pivotXValue);
							arrayList.add(pivotX);
						}
						float pivotYValue = (float) action.optDouble("pivotY", 0);
						if (pivotYValue != 0) {
							PropertyValuesHolder pivotY = PropertyValuesHolder.ofFloat("pivotY", pivotYValue);
							arrayList.add(pivotY);
						}

						PropertyValuesHolder[] valuesHolders = new PropertyValuesHolder[arrayList.size()];
						for (int position = 0; position < arrayList.size(); position++) {
							valuesHolders[position] = arrayList.get(position);
						}

						ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(target, valuesHolders);
						animator.setStartDelay(action.getInt("startTime"));
						animator.setRepeatCount(action.optInt("repeatCount", 0));
						animator.setDuration(action.getInt("duration"));
						animations.add(animator);
					} else if (ACTION_TYPE_APHA.equals(action.getString("type"))) {
						JSONArray keyframeArray = action.getJSONArray("keyframes");
						Keyframe[] keyframes = new Keyframe[keyframeArray.length()];
						for (int k = 0; k < keyframeArray.length(); k++) {
							JSONObject tranKeyframe = keyframeArray.getJSONObject(k);
							Keyframe frame = Keyframe.ofFloat((float) tranKeyframe.getDouble("fraction"), (float) tranKeyframe.getDouble("value"));
							keyframes[k] = frame;
						}

						PropertyValuesHolder alpha = PropertyValuesHolder.ofKeyframe("alpha", keyframes);
						ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(target, alpha);
						animator.setStartDelay(action.getInt("startTime"));
						animator.setRepeatCount(action.optInt("repeatCount", 0));
						animator.setDuration(action.getInt("duration"));
						animations.add(animator);
					} else if (ACTION_TYPE_ROTATION.equals(action.getString("type"))) {
						JSONArray keyframeArray = action.getJSONArray("keyframes");
						Keyframe[] keyframes = new Keyframe[keyframeArray.length()];
						for (int k = 0; k < keyframeArray.length(); k++) {
							JSONObject tranKeyframe = keyframeArray.getJSONObject(k);
							Keyframe frame = Keyframe.ofFloat((float) tranKeyframe.getDouble("fraction"), tranKeyframe.getInt("value"));
							keyframes[k] = frame;
						}

						ArrayList<PropertyValuesHolder> arrayList = new ArrayList<>();
						PropertyValuesHolder rotation = PropertyValuesHolder.ofKeyframe("rotation", keyframes);
						arrayList.add(rotation);

						float pivotXValue = (float) action.optDouble("pivotX", 0);
						if (pivotXValue != 0) {
							PropertyValuesHolder pivotX = PropertyValuesHolder.ofFloat("pivotX", pivotXValue);
							arrayList.add(pivotX);
						}
						float pivotYValue = (float) action.optDouble("pivotY", 0);
						if (pivotYValue != 0) {
							PropertyValuesHolder pivotY = PropertyValuesHolder.ofFloat("pivotY", pivotYValue);
							arrayList.add(pivotY);
						}

						PropertyValuesHolder[] valuesHolders = new PropertyValuesHolder[arrayList.size()];
						for (int position = 0; position < arrayList.size(); position++) {
							valuesHolders[position] = arrayList.get(position);
						}


						ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(target, valuesHolders);
						animator.setStartDelay(action.getInt("startTime"));
						animator.setRepeatCount(action.optInt("repeatCount", 0));
						animator.setDuration(action.getInt("duration"));
						animations.add(animator);
					}
				}
			}
		} catch (JSONException e) {
			throw new IllegalArgumentException("Unable to parse ImageLayer json.", e);
		}
	}

	public void startAnimator() {
		valueAnimator.setDuration(this.duration);
		valueAnimator.setStartDelay(this.getStartShowTime());
		valueAnimator.addListener(new AnimatorListenerAdapter() {

			@Override
			public void onAnimationStart(Animator animation) {
				super.onAnimationStart(animation);
				if (ImageLayer.this.target instanceof ImageView) {
					((ImageView) ImageLayer.this.target).setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				super.onAnimationEnd(animation);
				if (ImageLayer.this.endIsVisible) {
					if (ImageLayer.this.target instanceof ImageView) {
						((ImageView) ImageLayer.this.target).setVisibility(View.INVISIBLE);
					}
				}
			}
		});
		valueAnimator.start();
		for (Animator animator : this.animations) {
			animator.setTarget(this.target);
			animator.start();
		}
	}
}
