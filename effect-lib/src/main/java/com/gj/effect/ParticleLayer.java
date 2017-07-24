package com.gj.effect;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;

import com.plattysoft.leonids.ParticleSystem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/2/17.
 */

public class ParticleLayer extends Layer {

	public static final int REF_LAYER_ID_NULL = -1;
	//setSpeedModuleAndAngleRange 参数，最终决定speedX,speedY
	private float speedMin, speedMax;
	private int minAngle = -1, maxAngle = -1;
	//SpeeddByComponentsInitializer 参数，最终决定speedX,speedY
	private float speedMinX, speedMaxX, speedMinY, speedMaxY;
	// 旋转角度
	private int minRotationAngle, maxRotationAngle;
	// 旋转速度
	private float minRotationSpeed, maxRotationSpeed;

	// 缩放参数
	private float minScale, maxScale;

	//粒子加速度\粒子加速度角度，最终确定mAccelerationX，mAccelerationY
	private float minAcceleration, maxAcceleration;
	private int minAccelerateAngle, maxAccelerateAngle;

	//粒子淡出时长
	private long fadeOutTime;


	//粒子的存活时间
	private int timeToLive;
	//最大的粒子数
	private int maxParticles;
	//每秒发射的例子数
	private int particlesPerSecond;
	// 粒子的发射时间
	private int emitingTime;
	//number of particles launched on the one shot,如果参数不为0，则是一次性发射
	private int numParticles;

	private ArrayList<String> particleBtimapValues = new ArrayList<>();

	//礼物的emit的位置，有两种方式，1、关联refId，2、设置emit位置
	private int refId = REF_LAYER_ID_NULL;
	private View refView = null;
	private int emitStartMinX;
	private int emitStartMaxX;
	private int emitStartMinY;
	private int emitStartMaxY;

	public void setRefView(View refView) {
		this.refView = refView;
	}

	public float getSpeedMin() {
		return speedMin;
	}

	public float getSpeedMax() {
		return speedMax;
	}

	public int getMinAngle() {
		return minAngle;
	}

	public int getMaxAngle() {
		return maxAngle;
	}

	public float getSpeedMinX() {
		return speedMinX;
	}

	public float getSpeedMaxX() {
		return speedMaxX;
	}

	public float getSpeedMinY() {
		return speedMinY;
	}

	public float getSpeedMaxY() {
		return speedMaxY;
	}

	public int getMinRotationAngle() {
		return minRotationAngle;
	}

	public int getMaxRotationAngle() {
		return maxRotationAngle;
	}

	public float getMinRotationSpeed() {
		return minRotationSpeed;
	}

	public float getMaxRotationSpeed() {
		return maxRotationSpeed;
	}

	public float getMinScale() {
		return minScale;
	}

	public float getMaxScale() {
		return maxScale;
	}

	public float getMinAcceleration() {
		return minAcceleration;
	}

	public float getMaxAcceleration() {
		return maxAcceleration;
	}

	public int getMinAccelerateAngle() {
		return minAccelerateAngle;
	}

	public int getMaxAccelerateAngle() {
		return maxAccelerateAngle;
	}

	public long getFadeOutTime() {
		return fadeOutTime;
	}

	public int getTimeToLive() {
		return timeToLive;
	}

	public int getMaxParticles() {
		return maxParticles;
	}

	public int getParticlesPerSecond() {
		return particlesPerSecond;
	}

	public int getEmitingTime() {
		return emitingTime;
	}

	public int getNumParticles() {
		return numParticles;
	}

	public ArrayList<String> getParticleBtimapValues() {
		return particleBtimapValues;
	}

	public int getRefId() {
		return refId;
	}

	public int getEmitStartMinX() {
		return emitStartMinX;
	}

	public int getEmitStartMaxX() {
		return emitStartMaxX;
	}

	public int getEmitStartMinY() {
		return emitStartMinY;
	}

	public int getEmitStartMaxY() {
		return emitStartMaxY;
	}

	public ParticleLayer(Context context) {
		super(context);
	}

	@Override
	public void fromJson(JSONObject json) {
		super.fromJson(json);
		try {
			this.speedMin = (float) json.optDouble("speedMin", -1);
			this.speedMax = (float) json.optDouble("speedMax", -1);
			this.minAngle = json.optInt("minAngle");
			this.maxAngle = json.optInt("maxAngle");

			this.speedMinX = (float) json.optDouble("speedMinX");
			this.speedMaxX = (float) json.optDouble("speedMaxX");
			this.speedMinY = (float) json.optDouble("speedMinY");
			this.speedMaxY = (float) json.optDouble("speedMaxY");

			this.minRotationAngle = json.optInt("minRotationAngle");
			this.maxRotationAngle = json.optInt("maxRotationAngle");
			this.minRotationSpeed = json.optInt("minRotationSpeed");
			this.maxRotationSpeed = json.optInt("maxRotationSpeed");

			this.minScale = (float) json.optDouble("minScale");
			this.maxScale = (float) json.optDouble("maxScale");

			this.minAcceleration = (float) json.optDouble("minAcceleration");
			this.maxAcceleration = (float) json.optDouble("maxAcceleration");
			this.minAccelerateAngle = json.optInt("minAccelerateAngle");
			this.maxAccelerateAngle = json.optInt("maxAccelerateAngle");

			this.fadeOutTime = json.optInt("fadeOutTime");
			this.timeToLive = json.optInt("timeToLive");
			this.maxParticles = json.optInt("maxParticles");
			this.particlesPerSecond = json.optInt("particlesPerSecond");
			this.emitingTime = json.optInt("emitingTime");

			this.numParticles = json.optInt("numParticles");

			this.refId = json.optInt("refId");
			this.emitStartMinX = json.optInt("emitStartMinX");
			this.emitStartMaxX = json.optInt("emitStartMaxX");
			this.emitStartMinY = json.optInt("emitStartMinY");
			this.emitStartMaxY = json.optInt("emitStartMaxY");

			JSONArray arrays = json.getJSONArray("particleBtimapValue");
			for (int i = 0; i < arrays.length(); i++) {
				this.particleBtimapValues.add(arrays.getString(i));
			}

		} catch (JSONException e) {
			throw new IllegalArgumentException("Unable to parse Particle json.", e);
		}
	}

	@Override
	public void startAnimator() {
		valueAnimator.setDuration(this.duration);
		valueAnimator.setStartDelay(this.getStartShowTime());
		valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				if (ParticleLayer.this.target instanceof ParticleSystem) {
					if (getRefId() == REF_LAYER_ID_NULL) {
						int emitX = getEmit(getEmitStartMinX(), getEmitStartMaxX(), (float) animation.getAnimatedValue());
						int emitY = getEmit(getEmitStartMinY(), getEmitStartMaxY(), (float) animation.getAnimatedValue());
						((ParticleSystem) ParticleLayer.this.target).updateEmitPoint(emitX, emitY);
					} else {
						((ParticleSystem) ParticleLayer.this.target).updateEmitPoint(ParticleLayer.this.refView);
					}
				}
			}
		});
		valueAnimator.addListener(new AnimatorListenerAdapter() {

			@Override
			public void onAnimationStart(Animator animation) {
				super.onAnimationStart(animation);
				if (ParticleLayer.this.target instanceof ParticleSystem) {
					if (getRefId() == REF_LAYER_ID_NULL) {
						((ParticleSystem) ParticleLayer.this.target).emit(getEmitStartMinX(), getEmitStartMaxY(), getParticlesPerSecond());
					} else {
						((ParticleSystem) ParticleLayer.this.target).emit(ParticleLayer.this.refView, getParticlesPerSecond());
					}
				}
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				super.onAnimationEnd(animation);
				if (ParticleLayer.this.target instanceof ParticleSystem) {
					((ParticleSystem) ParticleLayer.this.target).cancel();
				}
			}
		});
		valueAnimator.start();
	}

	private int getEmit(int start, int end, float present) {
		return (int) (start + (end - start) * present);
	}
}
