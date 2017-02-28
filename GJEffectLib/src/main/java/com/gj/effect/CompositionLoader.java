package com.gj.effect;

import android.content.Context;

import com.gj.file.load.AlxMultiTask;

/**
 * Created by Administrator on 2017/2/15.
 */

public class CompositionLoader extends AlxMultiTask<String, Void, EffectComposition> {

	private EffectComposition.OnCompositionLoadedListener mLoadedListener;
	private Context mContext;

	public CompositionLoader(Context context, EffectComposition.OnCompositionLoadedListener loadedListener) {
		this.mContext = context;
		this.mLoadedListener = loadedListener;
	}

	@Override
	protected EffectComposition doInBackground(String... params) {
		try {

			return EffectComposition.fromFileSync(mContext, params[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(EffectComposition effectComposition) {
		mLoadedListener.onCompositionLoaded(effectComposition);
	}


}
