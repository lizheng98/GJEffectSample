package com.gj.effect;

import android.animation.ValueAnimator;
import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017/2/15.
 */

public abstract class Layer {
	public static final String LAYER_TYPE_IMAGE = "IMAGE";
	public static final String LAYER_TYPE_GIF = "GIF";
	public static final String LAYER_TYPE_PARTICLE = "PARTICLE";
	public static final String LAYER_TYPE_SVG = "SVG";
	protected Context context;
	protected int id;
	protected String type;
	protected String value;
	protected int width;
	protected int heigth;
	protected int[] startPosition = new int[2];
	protected int startShowTime = 0;
	protected int duration;
	protected boolean endIsVisible;
	protected Object target;
	protected final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 1f);

	public void setTarget(Object target) {
		this.target = target;
	}

	public Object getTarget() {
		return target;
	}

	public int getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	public String getValue() {
		return value;
	}

	public int getWidth() {
		return width;
	}

	public int getHeigth() {
		return heigth;
	}

	public int[] getStartPosition() {
		return startPosition;
	}

	public int getStartShowTime() {
		return startShowTime;
	}

	public int getDuration() {
		return duration;
	}

	public boolean isEndIsVisible() {
		return endIsVisible;
	}


	public Layer(Context context) {
		this.context = context.getApplicationContext();
	}

	public void fromJson(JSONObject json) {
		try {
			this.id = json.getInt("id");
			if (json.getInt("w") >= 0) {
				this.width = EffectComposition.effectPx2Px(context, json.getInt("w"));
			} else {
				this.width = json.getInt("w");
			}
			if (json.getInt("h") >= 0) {
				this.heigth = EffectComposition.effectPx2Px(context, json.getInt("h"));
			} else {
				this.heigth = json.getInt("h");
			}
			this.type = json.getString("type");
			this.value = json.optString("value");

			this.startShowTime = json.optInt("startShowTime");
			this.duration = json.optInt("duration");

			this.endIsVisible = json.optBoolean("endVisible", false);

			this.startPosition[0] = EffectComposition.effectPx2Px(context, json.optInt("startX"));
			this.startPosition[1] = EffectComposition.effectPx2Px(context, json.optInt("startY"));
		} catch (JSONException e) {
			throw new IllegalArgumentException("Unable to parse layer json.", e);
		}

	}

	public abstract void startAnimator();

}
