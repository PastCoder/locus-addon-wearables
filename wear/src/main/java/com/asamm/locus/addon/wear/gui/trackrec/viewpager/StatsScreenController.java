package com.asamm.locus.addon.wear.gui.trackrec.viewpager;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.asamm.locus.addon.wear.R;
import com.asamm.locus.addon.wear.common.communication.containers.trackrecording.TrackRecordingValue;
import com.asamm.locus.addon.wear.gui.custom.DisableGuiHelper;
import com.asamm.locus.addon.wear.gui.trackrec.TrackRecActivityState;
import com.asamm.locus.addon.wear.gui.trackrec.TrackRecordActivityConfiguration;
import com.asamm.locus.addon.wear.gui.trackrec.TrackStatLayout;

import java.util.Arrays;

/**
 * Created by Milan Cejnar on 27.11.2017.
 * Asamm Software, s.r.o.
 */

public class StatsScreenController implements TrackRecordingUpdatable{
	private ViewGroup mLayout;

	// recording active screen fields
	private TrackStatLayout mTopLeft, mTopRight, mBottomLeft, mBottomRight;

	private final int mScreenIdx;
	StatsScreenController(ViewGroup ctx, int screenIdx) {
		this.mScreenIdx = screenIdx;
		LayoutInflater inflater = LayoutInflater.from(ctx.getContext());
		mLayout = (ViewGroup) inflater.inflate(R.layout.track_record_recording_screen_stats, ctx, false);
		mTopLeft = mLayout.findViewById(R.id.track_stats_top_left);
		mTopRight = mLayout.findViewById(R.id.track_stats_top_right);
		mBottomLeft = mLayout.findViewById(R.id.track_stats_bottom_left);
		mBottomRight = mLayout.findViewById(R.id.track_stats_bottom_right);
		loadAndInitStats(ctx.getContext(), mLayout);
	}

	@Override
	public void onTrackActivityStateChange(Activity context, TrackRecActivityState newState) {

	}

	@Override
	public void onNewTrackRecordingData(Activity context, TrackRecordingValue newData) {
		if (newData == null) return;
		mTopLeft.consumeNewStatistics(newData);
		mTopRight.consumeNewStatistics(newData);
		mBottomLeft.consumeNewStatistics(newData);
		mBottomRight.consumeNewStatistics(newData);
	}

	private void loadAndInitStats(Context context, ViewGroup view) {
		TrackRecordActivityConfiguration mConfig = new TrackRecordActivityConfiguration(); // TODO cejnar Load from preferences on
		int idxOffset = -2 + (mScreenIdx << 2);
		mTopRight.setType(mConfig.getTypeForIdx(idxOffset));
		mTopLeft.setType(mConfig.getTypeForIdx(idxOffset + 1));
		mBottomRight.setType(mConfig.getTypeForIdx(idxOffset + 2));
		mBottomLeft.setType(mConfig.getTypeForIdx(idxOffset + 3));
	}

	@Override
	public ViewGroup getRootView() {
		return mLayout;
	}

}
