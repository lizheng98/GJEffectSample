package com.gj.effect;

import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by Administrator on 2017/2/15.
 */

public class CompositionLoader extends AsyncTask<String, Void, EffectComposition> {

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
