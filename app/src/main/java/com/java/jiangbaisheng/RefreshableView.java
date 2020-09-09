package com.java.jiangbaisheng;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.app.Activity;
import android.view.View.OnTouchListener;

public class RefreshableView extends LinearLayout implements OnTouchListener {

    public static final int STATUS_PULL_TO_REFRESH = 0;
    public static final int STATUS_RELEASE_TO_REFRESH = 1;
    public static final int STATUS_REFRESHING = 2;
    public static final int STATUS_REFRESH_FINISHED = 3;
    public static final int SCROLL_SPEED = -20; // scrolling back

    private PullToRefreshListener kocoPTRL; // pull-to-refresh listener

    private View header; // pull-to-refresh header
    private View foot; // no-more-content foot
    private ListView listView;
    private ProgressBar progressBar;
    private ImageView arrow;
    private TextView description;
    private MarginLayoutParams headerLayoutParams;
    private int mId = -1; // id of this view
    private int hideHeaderHeight;

    private int currentStatus = STATUS_REFRESH_FINISHED;
    private int lastStatus = currentStatus;

    private float yDown; // 手指按下时的屏幕纵坐标
    private int touchSlop; // 在被判定为滚动之前用户手指可以移动的最大值
    private boolean loadOnce; // whether layout is loaded already; initially false
    private boolean ableToPull;

    // this refreshable view is inserted dynamically.
    public RefreshableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        header = LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_head,
                null, true);
        foot = LayoutInflater.from(context).inflate(R.layout.no_more_content,
                null, true);
        progressBar = header.findViewById(R.id.progress_bar);
        arrow = header.findViewById(R.id.arrow);
        description = header.findViewById(R.id.description);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        setOrientation(VERTICAL);
        addView(header, 0);
    }

    // on layout initialized
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed && !loadOnce) {
            hideHeaderHeight = -header.getHeight();
            headerLayoutParams = (MarginLayoutParams)header.getLayoutParams();
            headerLayoutParams.topMargin = hideHeaderHeight;
            listView = (ListView)getChildAt(1);
            listView.setOnTouchListener(this);
            loadOnce = true;
        }
    }

    // on listview touched
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        setIsAbleToPull(event);
        if (ableToPull) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    yDown = event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float yMove = event.getRawY();
                    int distance = (int) (yMove - yDown);

                    if (distance <= 0 && headerLayoutParams.topMargin <= hideHeaderHeight) {
                        return false;
                    }
                    if (distance < touchSlop) {
                        return false;
                    }
                    if (currentStatus != STATUS_REFRESHING) {
                        if (headerLayoutParams.topMargin > 0) {
                            currentStatus = STATUS_RELEASE_TO_REFRESH;
                        } else {
                            currentStatus = STATUS_PULL_TO_REFRESH;
                        }

                        // animation
                        headerLayoutParams.topMargin = (distance / 2) + hideHeaderHeight;
                        header.setLayoutParams(headerLayoutParams);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                default:
                    if (currentStatus == STATUS_RELEASE_TO_REFRESH) {
                        new RefreshingTask().execute();
                    } else if (currentStatus == STATUS_PULL_TO_REFRESH) {
                        new HideHeaderTask().execute();
                    }
                    break;
            }

            if (currentStatus == STATUS_PULL_TO_REFRESH
                    || currentStatus == STATUS_RELEASE_TO_REFRESH) {
                updateHeaderView();
                // when dragging, make listview unfocused
                listView.setPressed(false);
                listView.setFocusable(false);
                listView.setFocusableInTouchMode(false);
                lastStatus = currentStatus;
                // override scrolling of listview
                return true;
            }
        }
        return false;
    }


    public void setOnRefreshListener(PullToRefreshListener listener, int id) {
        kocoPTRL = listener;
        mId = id;
    }


    public void finishRefreshing() {
        currentStatus = STATUS_REFRESH_FINISHED;
        new HideHeaderTask().execute();
    }


    private void setIsAbleToPull(MotionEvent event) {
        View firstChild = listView.getChildAt(0);
        if (firstChild != null) {
            int firstVisiblePos = listView.getFirstVisiblePosition();
            if (firstVisiblePos == 0 && firstChild.getTop() == 0) {
                if (!ableToPull) {
                    yDown = event.getRawY();
                }
                // first element's top is 0dp from parent: able to pull, else not
                ableToPull = true;
            } else {
                if (headerLayoutParams.topMargin != hideHeaderHeight) {
                    headerLayoutParams.topMargin = hideHeaderHeight;
                    header.setLayoutParams(headerLayoutParams);
                }
                ableToPull = false;
            }
        } else {
            // currently no elements in listview: able to pull
            ableToPull = true;
        }
    }


    private void updateHeaderView() {
        if (lastStatus != currentStatus) {
            if (currentStatus == STATUS_PULL_TO_REFRESH) {
                description.setText(getResources().getString(R.string.pull_to_refresh));
                arrow.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            } else if (currentStatus == STATUS_RELEASE_TO_REFRESH) {
                description.setText(getResources().getString(R.string.release_to_refresh));
                arrow.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
            } else if (currentStatus == STATUS_REFRESHING) {
                description.setText(getResources().getString(R.string.refreshing));
                progressBar.setVisibility(View.VISIBLE);
                arrow.clearAnimation();
                arrow.setVisibility(View.GONE);
            }
        }
    }


    class RefreshingTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            int topMargin = headerLayoutParams.topMargin;
            while (true) {
                topMargin = topMargin + SCROLL_SPEED;
                if (topMargin <= 0) {
                    topMargin = 0;
                    break;
                }
                publishProgress(topMargin);
                sleep(10);
            }
            currentStatus = STATUS_REFRESHING;
            publishProgress(0);
            if (kocoPTRL != null) {
                kocoPTRL.onRefresh();
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(Integer... topMargin) {
            updateHeaderView();
            headerLayoutParams.topMargin = topMargin[0];
            header.setLayoutParams(headerLayoutParams);
        }
    }

    // hide header after a pull-refresh
    class HideHeaderTask extends AsyncTask<Void, Integer, Integer> {
        @Override
        protected Integer doInBackground(Void... params) {
            int topMargin = headerLayoutParams.topMargin;
            while (true) {
                topMargin = topMargin + SCROLL_SPEED;
                if (topMargin <= hideHeaderHeight) {
                    topMargin = hideHeaderHeight;
                    break;
                }
                publishProgress(topMargin);
                sleep(10);
            }
            return topMargin;
        }
        @Override
        protected void onProgressUpdate(Integer... topMargin) {
            headerLayoutParams.topMargin = topMargin[0];
            header.setLayoutParams(headerLayoutParams);
        }
        @Override
        protected void onPostExecute(Integer topMargin) {
            headerLayoutParams.topMargin = topMargin;
            header.setLayoutParams(headerLayoutParams);
            currentStatus = STATUS_REFRESH_FINISHED;
        }
    }


    private void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public interface PullToRefreshListener {

        void onRefresh();
    }
}