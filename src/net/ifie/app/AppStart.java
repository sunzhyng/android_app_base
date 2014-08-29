package net.ifie.app;

import net.ifie.app.ui.DemoPullToRefresh;
import net.ifie.app.ui.DemoScrollLayout;
import net.ifie.app.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

public class AppStart extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final View view = View.inflate(this, R.layout.start, null);
		setContentView(view);

		AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
		aa.setDuration(100);
		view.startAnimation(aa);
		aa.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation arg0) {
				redirectTo();
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}

		});

	}

	private void redirectTo() {
		Intent intent = new Intent(this, DemoScrollLayout.class);
		startActivity(intent);
		finish();
	}
}