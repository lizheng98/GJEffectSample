#GJEffectLib

某大型直播平台特效礼物动效引擎项目<br/>

#Features：

  支持Json动画格式解析<br/>
  支持动画数据网络下发，动态显示动画<br/>
  支持图片的属性动画属性PropertyValuesHolder Keyframe<br/>
  支持Gif动画<br/>
  支持粒子重力动画<br/>
  
  三者结合实现复杂、绚丽的动画效果<br/><br/>
  
#Using：

  动画实现使用GJEffectView控件实现。<br/>
  
  ```
  GJEffectView mGJEffect = (GJEffectView) findViewById(R.id.live_gift_effect)
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
    ```
  GJEffectSample 项目有示例动画压缩包"yacht.zip"文件
  
  //加载网络动画数据
  ```
		EffectGiftLoader.getInstance(this).loadDataForComposition("http://",
				new EffectComposition.OnCompositionLoadedListener() {
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
