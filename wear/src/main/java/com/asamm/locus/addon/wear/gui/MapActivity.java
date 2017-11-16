package com.asamm.locus.addon.wear.gui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.asamm.locus.addon.wear.R;
import com.asamm.locus.addon.wear.gui.custom.NavHelper;
import com.assam.locus.addon.wear.common.communication.Const;
import com.assam.locus.addon.wear.common.communication.DataPath;
import com.assam.locus.addon.wear.common.communication.containers.DataPayload;
import com.assam.locus.addon.wear.common.communication.containers.MapContainer;
import com.assam.locus.addon.wear.common.communication.containers.TimeStampStorable;
import com.assam.locus.addon.wear.common.communication.containers.commands.MapPeriodicParams;
import com.assam.locus.addon.wear.common.communication.containers.commands.PeriodicCommand;

import locus.api.android.features.periodicUpdates.UpdateContainer;
import locus.api.android.utils.UtilsFormat;
import locus.api.objects.enums.PointRteAction;

public class MapActivity extends LocusWearActivity {

    private static final int MAP_REFRESH_PERIOD_MS = 5000;
    // reference to map view
    private ImageView mMapView;

    // NAVIGATION PANEL

    // main container
    private LinearLayout mLlNavPanel;
    // top navigation command (next)
    private ImageView mIvNavPanelTop;
    // main navigation command (current)
    private ImageView mIvNavPanelMiddle;
    // distance to next command (value)
    private TextView mTvNavPanelDistValue;
    // distance to next command (units)
    private TextView mTvNavPanelDistUnits;

    private MapContainer mlastContainer;

    @Override
    protected DataPayload<PeriodicCommand> getInitialCommandType() {
        final MapPeriodicParams params =
                new MapPeriodicParams(0d, 0d, Const.ZOOM_UNKONWN,
                        getWindow().getDecorView().getWidth(),
                        getWindow().getDecorView().getHeight());
        return new DataPayload<>(DataPath.GET_PERIODIC_DATA,
                new PeriodicCommand(PeriodicCommand.IDX_PERIODIC_ACITIVITY_MAP,
                        MAP_REFRESH_PERIOD_MS, params));
    }

    @Override
    protected DataPath getInitialCommandResponseType() {
        return DataPath.PUT_MAP;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mMapView = findViewById(R.id.image_view_map);
        mLlNavPanel = findViewById(R.id.linear_layout_panel_navigation);
        mIvNavPanelTop = findViewById(R.id.image_view_next);
        mIvNavPanelMiddle = findViewById(R.id.image_view_main);
        mTvNavPanelDistValue = findViewById(R.id.text_view_dist_value);
        mTvNavPanelDistUnits = findViewById(R.id.text_view_dist_units);

        // Enables Always-on
        setAmbientEnabled();
    }

    public void refreshLayout(final MapContainer data) {
        // run in UI thread
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // refresh layout
                refreshMapView(data);
                refreshPanelNavigation(data);
            }
        });
    }

    /**
     * Refreshes map image view
     */
    private void refreshMapView(MapContainer data) {
        if (data != null && data.getLoadedMap() != null) {
            mMapView.setImageBitmap(data.getLoadedMap().getImage());
        }
    }

    /**
     * Refresh panel with navigation.
     */
    private void refreshPanelNavigation(MapContainer data) {
        if (data == null || data.getGuideType() != UpdateContainer.GUIDE_TYPE_TRACK_NAVIGATION) {
            mLlNavPanel.setVisibility(View.GONE);
            return;
        }
        mLlNavPanel.setVisibility(View.VISIBLE);

        // action for current point
        int img1 = NavHelper.getNavPointImageRes(data.getNavPointAction1Id());
        mIvNavPanelMiddle.setImageResource(img1 != PointRteAction.UNDEFINED.getId() ?
                img1 : R.drawable.ic_direction_unknown);

        // action for next point
        int img2 = NavHelper.getNavPointImageRes(data.getNavPointAction1Id());
        mIvNavPanelTop.setImageResource(img2 != PointRteAction.UNDEFINED.getId() ?
                img2 : R.drawable.ic_direction_unknown);

        // set time to target
        mTvNavPanelDistValue.setText(UtilsFormat.formatDistance(
                data.getUnitsFormatLength(), data.getNavPoint1Dist(), true));
        mTvNavPanelDistUnits.setText(UtilsFormat.formatDistanceUnits(
                data.getUnitsFormatLength(), data.getNavPoint1Dist()));
    }

    @Override
    public void consumeNewData(DataPath path, TimeStampStorable data) {
        super.consumeNewData(path, data);
        switch (path) {
            case PUT_MAP:
                mlastContainer = (MapContainer) data;
                refreshLayout(mlastContainer);
                break;
        }
    }
}