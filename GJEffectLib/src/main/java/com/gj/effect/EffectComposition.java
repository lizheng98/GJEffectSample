package com.gj.effect;

import android.animation.Animator;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;

import com.gj.effect.util.ZipUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


/**
 * Created by Administrator on 2017/2/14.
 */

public class EffectComposition {

	private static final String EFFECT_CONFIG_FILE = "config.txt";
	private int mWidth;
	private int mHeigth;
	private int mMarginTop;
	private int mDuration;
	private String mEffectFilePath;
	private ArrayList<Layer> mLayers = new ArrayList<>();
	private ArrayList<Animator> animations = new ArrayList<>();

	public ArrayList<Layer> getmLayers() {
		return mLayers;
	}

	public int getWidth() {
		return mWidth;
	}

	public int getHeigth() {
		return mHeigth;
	}

	public int getmMarginTop() {
		return mMarginTop;
	}

	public int getDuration() {
		return mDuration;
	}

	public String getmEffectFilePath() {
		return mEffectFilePath;
	}

	public ArrayList<Animator> getAnimations() {
		return animations;
	}

	/**
	 * 根据文件路径解析，生成EffectComposition
	 *
	 * @param zipFilePath 压缩包的绝对路径
	 * @return
	 */
	public static EffectComposition fromFileSync(Context context, String zipFilePath) {
		try {
			String unZipRootFile = getUnZipRootFile(zipFilePath);

			ZipUtil.UnZipFolder(zipFilePath, unZipRootFile);
//			InputStream configFile = ZipUtil.UpZip(path, EFFECT_CONFIG_FILE);
			File configFile = new File(unZipRootFile, EFFECT_CONFIG_FILE);
			InputStream inputStream = new FileInputStream(configFile);
			return fromInputStream(context, unZipRootFile, inputStream);
		} catch (Exception e) {
			throw new IllegalStateException("Unable to find file.", e);
		}
	}

	private static EffectComposition fromInputStream(Context context, String unZipRootFile, InputStream file) {
		try {
			int size = file.available();
			byte[] buffer = new byte[size];
			//noinspection ResultOfMethodCallIgnored
			file.read(buffer);
			file.close();
			String json = new String(buffer, "UTF-8");
			JSONObject jsonObject = new JSONObject(json);
			return fromJsonSync(context, unZipRootFile, jsonObject);
		} catch (IOException e) {
			throw new IllegalStateException("Unable to find file.", e);
		} catch (JSONException e) {
			throw new IllegalStateException("Unable to load JSON.", e);
		}
	}

	static EffectComposition fromJsonSync(Context context, String unZipRootFile, JSONObject json) {
		EffectComposition composition = new EffectComposition();
		try {
			composition.mEffectFilePath = unZipRootFile;
			if (json.getInt("w") >= 0) {
				composition.mWidth = EffectComposition.effectPx2Px(context, json.getInt("w"));
			} else {
				composition.mWidth = json.getInt("w");
			}
			if (json.getInt("h") >= 0) {
				composition.mHeigth = EffectComposition.effectPx2Px(context, json.getInt("h"));
			} else {
				composition.mHeigth = json.getInt("h");
			}

			composition.mMarginTop = effectPx2Px(context, json.getInt("marginTop"));
			composition.mDuration = json.getInt("duration");

			JSONArray actions = json.optJSONArray("actions");
			if (actions != null) {
				for (int i = 0; i < actions.length(); i++) {
					JSONObject action = actions.getJSONObject(i);
					if (ImageLayer.ACTION_TYPE_TRANS.equals(action.getString("type"))) {

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

						ObjectAnimator animator = new ObjectAnimator();
						animator.setValues(propertyValuesHolders);
						animator.setStartDelay(action.getInt("startTime"));
						animator.setRepeatCount(action.optInt("repeatCount", 0));
						animator.setDuration(action.getInt("duration"));
						composition.animations.add(animator);
					} else if (ImageLayer.ACTION_TYPE_SCALE.equals(action.getString("type"))) {
						JSONArray keyframeArray = action.getJSONArray("keyframes");
						Keyframe[] keyframes = new Keyframe[keyframeArray.length()];
						for (int k = 0; k < keyframeArray.length(); k++) {
							JSONObject tranKeyframe = keyframeArray.getJSONObject(k);
							Keyframe frame = Keyframe.ofFloat((float) tranKeyframe.getDouble("fraction"), (float) tranKeyframe.getDouble("value"));
							keyframes[k] = frame;
						}
						PropertyValuesHolder scaleX = PropertyValuesHolder.ofKeyframe("scaleX", keyframes);
						PropertyValuesHolder scaleY = PropertyValuesHolder.ofKeyframe("scaleY", keyframes);

						ObjectAnimator animator = new ObjectAnimator();
						animator.setValues(scaleX, scaleY);
						animator.setStartDelay(action.getInt("startTime"));
						animator.setRepeatCount(action.optInt("repeatCount", 0));
						animator.setDuration(action.getInt("duration"));
						composition.animations.add(animator);
					} else if (ImageLayer.ACTION_TYPE_APHA.equals(action.getString("type"))) {
						JSONArray keyframeArray = action.getJSONArray("keyframes");
						Keyframe[] keyframes = new Keyframe[keyframeArray.length()];
						for (int k = 0; k < keyframeArray.length(); k++) {
							JSONObject tranKeyframe = keyframeArray.getJSONObject(k);
							Keyframe frame = Keyframe.ofFloat((float) tranKeyframe.getDouble("fraction"), (float) tranKeyframe.getDouble("value"));
							keyframes[k] = frame;
						}

						PropertyValuesHolder alpha = PropertyValuesHolder.ofKeyframe("alpha", keyframes);
						ObjectAnimator animator = new ObjectAnimator();
						animator.setValues(alpha);
						animator.setStartDelay(action.getInt("startTime"));
						animator.setRepeatCount(action.optInt("repeatCount", 0));
						animator.setDuration(action.getInt("duration"));
						composition.animations.add(animator);
					} else if (ImageLayer.ACTION_TYPE_ROTATION.equals(action.getString("type"))) {
						JSONArray keyframeArray = action.getJSONArray("keyframes");
						Keyframe[] keyframes = new Keyframe[keyframeArray.length()];
						for (int k = 0; k < keyframeArray.length(); k++) {
							JSONObject tranKeyframe = keyframeArray.getJSONObject(k);
							Keyframe frame = Keyframe.ofFloat((float) tranKeyframe.getDouble("fraction"), tranKeyframe.getInt("value"));
							keyframes[k] = frame;
						}

						PropertyValuesHolder rotation = PropertyValuesHolder.ofKeyframe("rotation", keyframes);

						ObjectAnimator animator = new ObjectAnimator();
						animator.setValues(rotation);
						animator.setStartDelay(action.getInt("startTime"));
						animator.setRepeatCount(action.optInt("repeatCount", 0));
						animator.setDuration(action.getInt("duration"));
						composition.animations.add(animator);
					}
				}
			}


			JSONArray jsonLayers = json.getJSONArray("layers");
			for (int i = 0; i < jsonLayers.length(); i++) {
				Layer layer = null;
				if (Layer.LAYER_TYPE_IMAGE.equals(jsonLayers.getJSONObject(i).getString("type"))) {
					layer = new ImageLayer(context);
					layer.fromJson(jsonLayers.getJSONObject(i));
				} else if (Layer.LAYER_TYPE_PARTICLE.equals(jsonLayers.getJSONObject(i).getString("type"))) {
					layer = new ParticleLayer(context);
					layer.fromJson(jsonLayers.getJSONObject(i));
				} else if (Layer.LAYER_TYPE_GIF.equals(jsonLayers.getJSONObject(i).getString("type"))) {
					layer = new GifLayer(context);
					layer.fromJson(jsonLayers.getJSONObject(i));
				} else if (Layer.LAYER_TYPE_SVG.equals(jsonLayers.getJSONObject(i).getString("type"))) {
					layer = new LottieLayer(context);
					layer.fromJson(jsonLayers.getJSONObject(i));
				}
				addLayer(composition, layer);
			}
		} catch (JSONException e) {
			throw new IllegalStateException("EffectComposition Json format error.", e);
		}
		return composition;
	}

	static String getUnZipRootFile(String zipFilePath) {
		File file = new File(zipFilePath);
		String fileName = file.getName();
		String suffixFileName = fileName.substring(0, fileName.lastIndexOf("."));
		File unRootFile = new File(file.getParent() + File.separator + suffixFileName);

		if (!unRootFile.exists()) {
			unRootFile.mkdirs();
		}
		return unRootFile.getAbsolutePath();
	}

	private static void addLayer(EffectComposition composition, Layer layer) {
		composition.mLayers.add(layer);
	}

	/**
	 * 大额礼物配置数据转化成，配置文件的数据时1080P
	 *
	 * @param context
	 * @param pxvalue
	 * @return
	 */
	public static int effectPx2Px(Context context, float pxvalue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxvalue / 3 * scale + 0.5f);
	}

	public interface OnCompositionLoadedListener {
		void onCompositionLoaded(EffectComposition composition);
	}
}
