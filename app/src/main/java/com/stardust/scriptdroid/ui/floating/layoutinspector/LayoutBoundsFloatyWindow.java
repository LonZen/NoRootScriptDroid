package com.stardust.scriptdroid.ui.floating.layoutinspector;

import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.stardust.enhancedfloaty.FloatyService;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.ui.floating.FullScreenFloatyWindow;
import com.stardust.view.accessibility.NodeInfo;
import com.stardust.widget.BubblePopupMenu;

import java.util.Arrays;

/**
 * Created by Stardust on 2017/3/12.
 */

public class LayoutBoundsFloatyWindow extends FullScreenFloatyWindow {

    private LayoutBoundsView mLayoutBoundsView;
    private MaterialDialog mNodeInfoDialog;
    private BubblePopupMenu mBubblePopMenu;
    private NodeInfoView mNodeInfoView;
    private NodeInfo mSelectedNode;
    private Context mContext;
    private NodeInfo mRootNode;

    public LayoutBoundsFloatyWindow(NodeInfo rootNode) {
        mRootNode = rootNode;
    }

    @Override
    protected View inflateView(FloatyService service) {
        mContext = new ContextThemeWrapper(service, R.style.AppTheme);
        mLayoutBoundsView = new LayoutBoundsView(mContext) {
            @Override
            public boolean dispatchKeyEvent(KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    close();
                    return true;
                }
                return super.dispatchKeyEvent(event);
            }
        };
        setupView();
        return mLayoutBoundsView;
    }

    private void setupView() {
        mLayoutBoundsView.setOnNodeInfoSelectListener(new OnNodeInfoSelectListener() {
            @Override
            public void onNodeSelect(NodeInfo info) {
                mSelectedNode = info;
                ensureOperationPopMenu();
                if (mBubblePopMenu.getContentView().getMeasuredWidth() <= 0)
                    mBubblePopMenu.preMeasure();
                mBubblePopMenu.showAsDropDownAtLocation(mLayoutBoundsView, info.getBoundsInScreen().height(), info.getBoundsInScreen().centerX() - mBubblePopMenu.getContentView().getMeasuredWidth() / 2, info.getBoundsInScreen().bottom - mLayoutBoundsView.getStatusBarHeight());
            }
        });
        mLayoutBoundsView.getBoundsPaint().setStrokeWidth(2f);
        mLayoutBoundsView.setRootNode(mRootNode);
        if (mSelectedNode != null)
            mLayoutBoundsView.setSelectedNode(mSelectedNode);
    }


    private void showNodeInfo() {
        ensureDialog();
        mNodeInfoView.setNodeInfo(mSelectedNode);
        mNodeInfoDialog.show();
    }

    private void ensureOperationPopMenu() {
        if (mBubblePopMenu != null)
            return;
        mBubblePopMenu = new BubblePopupMenu(mContext, Arrays.asList(
                mContext.getString(R.string.text_show_widget_infomation),
                mContext.getString(R.string.text_show_layout_hierarchy)));
        mBubblePopMenu.setOnItemClickListener(new BubblePopupMenu.OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                mBubblePopMenu.dismiss();
                if (position == 0) {
                    showNodeInfo();
                } else {
                    showLayoutHierarchy();
                }
            }
        });
        mBubblePopMenu.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mBubblePopMenu.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void showLayoutHierarchy() {
        close();
        LayoutHierarchyFloatyWindow window = new LayoutHierarchyFloatyWindow(mRootNode);
        window.setSelectedNode(mSelectedNode);
        FloatyService.addWindow(window);
    }

    private void ensureDialog() {
        if (mNodeInfoDialog == null) {
            mNodeInfoView = new NodeInfoView(mContext);
            mNodeInfoDialog = new MaterialDialog.Builder(mContext)
                    .customView(mNodeInfoView, false)
                    .theme(Theme.LIGHT)
                    .build();
            mNodeInfoDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
        }
    }

    public void setSelectedNode(NodeInfo selectedNode) {
        mSelectedNode = selectedNode;
    }
}
