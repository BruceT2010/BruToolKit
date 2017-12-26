package com.bru.toolkit.views.viewpager;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bru.toolkit.R;
import com.bru.toolkit.mgr.image.impl.AnimateFirstDisplayListener;
import com.bru.toolkit.views.imageview.ResizableImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * 图片滚动展示控件 可实现在viewpager嵌套后依然可响应手指滑动事件
 *
 * @author 林燕军
 */
public class BannerView extends FrameLayout implements Runnable {

    private int default_img_resId = -1;
    private String TAG = getClass().getSimpleName();
    // 点击的监听事件
    private BannerViewPager.OnSimpleClickListener mSimpleListener = null;
    private Context context;
    // 滚动展示的控件
    private BannerViewPager viewPager = null;
    // 滚动展示控件的adapter
    private PageAdapter pageAdapter = null;
    // 要展示的界面
    private List<View> views = null;
    // 展示图片的页码（一排点标志展示哪张图）指示器
    private List<ImageView> indicatorImgs = null;
    // 滚动时的事件监听
    private BannerPageChangeListener pageChangeListener = null;
    // “当前是哪页”的标志点布局
    private LinearLayout bannerIndicator_Linear = null;
    // 是否显示页码标志点
    private boolean showIndicator = true;
    // 滚动线程休眠时间【秒】注意，在方法 poster.setData(imgPoster,this,true,5);会被覆盖
    private int sleepTime = 5;
    private int curPosition = 0;// 当前页码
    private int maxPage = 0;// 最大页码
    // 自动滚动的线程
    private Thread thread = null;
    // 用于线程的运行
    private boolean isRun = true;
    // 是否自己滚动
    private boolean needAutoRoll = true;

    // 处理 SlidingMenu 事件拦截的问题
    // private SlidingMenu sm;

    private ViewPager parentViewPager;
    // 是否延迟加载网络图片标识
    private boolean asyncShow = false;
    private String[] imgUrls;

    // 用于延迟加载图片的类
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions bannerOptions;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    // 用于锁定用户滑动请求
    private boolean mLock = false;

    // banner手势部分
    private float xDistance, yDistance;
    /**
     * 记录按下的X坐标
     **/
    private float mLastMotionX, mLastMotionY;
    /**
     * 是否是左右滑动
     **/
    private boolean mIsBeingDragged = true;

    /**
     * View两个参数的构造函数，在加载布局文件时被调用
     **/
    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        // 初始化控件
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.banner_layout, this, true);
        viewPager = (BannerViewPager) findViewById(R.id.viewPager);
        bannerIndicator_Linear = (LinearLayout) findViewById(R.id.bannerIndicator_Linear);
        // 实现一个PagerAdapter,设置数据
        views = new ArrayList<View>();
        pageAdapter = new PageAdapter(views);
        viewPager.setAdapter(pageAdapter);
        // 页卡切换监听
        pageChangeListener = new BannerPageChangeListener();
        viewPager.setOnPageChangeListener(pageChangeListener);
        // 设置控件相关数据
        ((RelativeLayout.LayoutParams) bannerIndicator_Linear.getLayoutParams()).setMargins(0, 0, 0, 10);
        this.setBackgroundColor(0xffeef1f3);

        // 设置最小滑动距离
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        // 设置UIL选项
        bannerOptions = new DisplayImageOptions.Builder().showImageOnLoading(default_img_resId == -1 ? R.color.white : default_img_resId)// 正在加载
                .showImageForEmptyUri(default_img_resId == -1 ? R.color.white : default_img_resId)// 图片为空
                .showImageOnFail(default_img_resId == -1 ? R.color.white : default_img_resId)// 加载出错
                .cacheInMemory(true)// 内存缓存
                .cacheOnDisk(true)// 磁盘缓存
                .considerExifParams(true)// 考虑像素参数
                // .displayer(new RoundedBitmapDisplayer(20))// BitMap圆角
                .build();// 创建实例

        viewPager.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                viewPager.getGestureDetector().onTouchEvent(event);
                final float x = event.getRawX();
                final float y = event.getRawY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        xDistance = yDistance = 0f;
                        mLastMotionX = x;
                        mLastMotionY = y;
                    case MotionEvent.ACTION_MOVE:
                        final float xDiff = Math.abs(x - mLastMotionX);
                        final float yDiff = Math.abs(y - mLastMotionY);
                        xDistance += xDiff;
                        yDistance += yDiff;

                        float dx = xDistance - yDistance;
                        /** 左右滑动避免和下拉刷新冲突 **/
                        if (xDistance > yDistance || Math.abs(xDistance - yDistance) < 0.00001f) {
                            mIsBeingDragged = true;
                            mLastMotionX = x;
                            mLastMotionY = y;
                            v.getParent().getParent().requestDisallowInterceptTouchEvent(true);
                        } else {
                            mIsBeingDragged = false;
                            v.getParent().getParent().requestDisallowInterceptTouchEvent(false);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        if (mIsBeingDragged) {
                            v.getParent().getParent().requestDisallowInterceptTouchEvent(false);
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

    }

    public void setViews(List<View> views) {
        if (null == views)
            return;
        if (showIndicator)
            indicatorImgs = new ArrayList<ImageView>();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(5, 0, 5, 0);
        maxPage = views.size();
        bannerIndicator_Linear.removeAllViews();
        ImageView img = null;
        for (int i = 0; i < views.size(); i++) {
            if (showIndicator) {
                img = new ImageView(context);
                img.setBackgroundResource((i == 0) ? R.drawable.dot_selected : R.drawable.dot_none);
                img.setLayoutParams(params);
                indicatorImgs.add(img);
                bannerIndicator_Linear.addView(img);
            }
        }
        this.views.clear();
        this.views.addAll(views);
        pageAdapter.notifyDataSetChanged();
        viewPager.setCurrentItem(0);
    }

    /**
     * 设置banner数据
     */
    @SuppressLint("NewApi")
    public void setData(String[] imgUrls) {
        viewPager.setOnSimpleClickListener(mSimpleListener);
        /** 初始化指示器 **/
        if (showIndicator)
            indicatorImgs = new ArrayList<ImageView>();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(5, 0, 5, 0);
        maxPage = imgUrls.length;
        // 清除数据
        views.clear();
        bannerIndicator_Linear.removeAllViews();
        // 根据传入图片的数量，确定指示器的点的数量
        ImageView img = null;
        for (int i = 0; i < imgUrls.length; i++) {
            if (showIndicator) {
                img = new ImageView(context);
                img.setBackgroundResource((i == 0) ? R.drawable.dot_selected : R.drawable.dot_none);
                img.setLayoutParams(params);
                indicatorImgs.add(img);
                bannerIndicator_Linear.addView(img);
            }
            /** 初始化内容图片 **/
            // 设置ViewPager中图片的参数
            final ResizableImageView imgView = new ResizableImageView(context);
            imgView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            // 根据图片的来源加载
            imageLoader.displayImage(imgUrls[i], imgView, bannerOptions, animateFirstListener);
            views.add(imgView);
        }
        pageAdapter.notifyDataSetChanged();
        viewPager.setCurrentItem(0);
    }

    /**
     * 设置数据
     *
     * @param _imgDrawable
     */
    public void setData(String[] imageUrls, BannerViewPager.OnSimpleClickListener _Listener, boolean showIndicator) {
        setMyOnClickListener(_Listener);
        this.showIndicator = showIndicator;
        setData(imageUrls);
        if (!showIndicator && bannerIndicator_Linear != null) {
            bannerIndicator_Linear.setVisibility(LinearLayout.GONE);
        }
        if (needAutoRoll) {
            thread = new Thread(this);
            thread.start();
        }
    }

    /**
     * 设置点击监听
     */
    public void setMyOnClickListener(BannerViewPager.OnSimpleClickListener _Listener) {
        this.mSimpleListener = _Listener;
    }

    /**
     * 向左滚动，当前页面已是第0页时，显示最后一页
     */
    public void leftScroll() {
        if (viewPager != null)
            viewPager.setCurrentItem((--curPosition < 0) ? maxPage : curPosition);

    }

    /**
     * 向右滚动，当前页面已是最后一页时，显示第0页
     */
    public void rightScroll() {
        if (viewPager != null)
            viewPager.setCurrentItem((++curPosition > maxPage) ? 0 : curPosition);

    }

    /**
     * 轮播控制器，使得图片向右滑动
     **/
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            rightScroll();
        }
    };

    @Override
    public void run() {
        Thread thisThread = Thread.currentThread();
        // while (blinker == thisThread) {
        while (isRun && !Thread.interrupted() && thisThread == thread) {
            // System.out.println("======线程========"+sleepTime);
            try {
                Thread.sleep(1000 * sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
                if (thread != null)
                    thread.interrupt();
            }
            if (handler != null)
                handler.sendMessage(Message.obtain(handler));
        }
        thisThread = null;
    }

    /**
     * 清空数据，内存回收
     */
    public void clearMemory() {
        if (thread != null) {
            // System.out.println("====>>>"+sleepTime);
            isRun = false;
            thread.interrupt();
            thread = null;
        }
        mSimpleListener = null;
        context = null;
        // 滚动展示的控件
        viewPager = null;
        // 滚动展示控件的adapter
        pageAdapter = null;
        // 要展示的界面
        if (views != null) {
            views.clear();
            views = null;
        }
        // 展示图片的页码（一排点标志展示哪张图）
        if (indicatorImgs != null) {
            indicatorImgs.clear();
            indicatorImgs = null;
        }
        // 滚动时的事件监听
        pageChangeListener = null;
        // “当前是哪页”的标志点布局
        bannerIndicator_Linear = null;
        // 这个控件内的图片（drawable）集合
        handler = null;
    }

    /**
     * 内部类===>banner页面切换监听 此处主要用于当页面切换时更新指示器
     **/
    class BannerPageChangeListener implements OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int state) {
        }

        @Override
        public void onPageScrolled(int position, float offSetPercent, int offSetPixel) {
        }

        /**
         * 当选中页面发生改变时，重新设置指示器图片
         **/
        @Override
        public void onPageSelected(int position) {
            curPosition = position;
            if (showIndicator && indicatorImgs != null) {
                for (int i = 0; i < indicatorImgs.size(); i++) {
                    // 不是当前选中的page，其小圆点设置为未选中的状态
                    indicatorImgs.get(i).setBackgroundResource((position % indicatorImgs.size() != i) ? R.drawable.dot_none : R.drawable.dot_selected);
                }
            }
        }
    }

    class PageAdapter extends PagerAdapter {
        List<View> views;

        public PageAdapter(List<View> views) {
            this.views = views;
        }

        /**
         * 要显示的页面的个数
         */
        @Override
        public int getCount() {
            // 设置成最大值以便循环滑动
            // int cont=((views == null)? 0 : Integer.MAX_VALUE);
            // maxPage=cont;
            // return cont;
            return maxPage;
        }

        /**
         * 获取一个指定页面的title描述 如果返回null意味着这个页面没有标题，默认的实现就是返回null
         * 如果要显示页面上的title则此方法必须实现
         */
        @Override
        public CharSequence getPageTitle(int position) {
            // System.out.println("==标题==>"+titles[position]);
            // return titles[position];
            return null;
        }

        /**
         * 创建指定position的页面。这个适配器会将页面加到容器container中。
         *
         * @param container 创建出的实例放到container中，这里的container就是viewPager
         * @return 返回一个能表示该页面的对象，不一定要是view，可以其他容器或者页面。
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            try {
                ((ViewPager) container).addView(views.get(position % views.size()), 0);
            } catch (Exception e) {
            }
            return (views.size() > 0) ? views.get(position % views.size()) : null;
            // ((ViewPager) container).addView(views.get(position), 0);
            // viewPager.setObjectForPosition(views.get(position), position);
            // return views.get(position);
        }

        /**
         * 此方法会将容器中指定页面给移除 该方法中的参数container和position跟instantiateItem方法中的内容一致
         *
         * @param object 这个object 就是 instantiateItem方法中返回的那个Object
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // 由于需要它循环滚动，所以不能将其清除掉。
            // if(position<views.size())
            // {
            container.removeView(views.get(position));
            // }
            // ((ViewPager)
            // container).removeView(viewPager.findViewFromObject(position));
        }

        /**
         * 这个方法就是比较一下容器中页面和instantiateItem方法返回的Object是不是同一个
         *
         * @param arg0 ViewPager中的一个页面
         * @param arg1 instantiateItem方法返回的对象
         */
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

    }

    /**
     * 临时存储X坐标
     */
    private int tempX;
    /**
     * 按下点的X坐标
     */
    private int downX;
    /**
     * 按下点的Y坐标
     */
    private int downY;
    private boolean isSilding;
    private int mTouchSlop;

    /**
     * 用于处理事件并改变事件的传递方向，它的返回值是一个布尔值，决定了Touch事件是否要向它包含的子View继续传递，
     * 这个方法是从父View向子View传递
     **/

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    // @Override
    // public boolean onInterceptTouchEvent(MotionEvent e) {
    // if (mLock) {
    // return false;
    // }
    // int y = (int) e.getRawY();
    // switch (e.getAction()) {
    // case MotionEvent.ACTION_DOWN:
    // // if (this.sm!=null) {
    // // this.sm.requestDisallowInterceptTouchEvent(true);
    // // }
    // if (this.parentViewPager != null) {
    // this.parentViewPager.requestDisallowInterceptTouchEvent(true);
    // }
    // // 首先拦截down事件,记录y坐标
    // downX = tempX = (int) e.getRawX();
    // downY = (int) e.getRawY();
    // return false;
    // case MotionEvent.ACTION_MOVE:
    // Logs.e(TAG, "this-->ACTION_MOVE");
    // int moveX = (int) e.getRawX();
    // // 满足此条件屏蔽banner_layout里面子类的touch事件
    // if ((Math.abs(moveX - downX) > mTouchSlop && Math.abs((int) e.getRawY() -
    // downY) < mTouchSlop)) {
    //
    // return true;
    // }
    // break;
    // case MotionEvent.ACTION_UP:
    // // if (this.sm!=null) {
    // // this.sm.requestDisallowInterceptTouchEvent(false);
    // // }
    // if (this.parentViewPager != null) {
    // this.parentViewPager.requestDisallowInterceptTouchEvent(false);
    // }
    // return false;
    // case MotionEvent.ACTION_CANCEL:
    // // if (this.sm!=null) {
    // // this.sm.requestDisallowInterceptTouchEvent(false);
    // // }
    // if (this.parentViewPager != null) {
    // this.parentViewPager.requestDisallowInterceptTouchEvent(false);
    // }
    // return true;
    // }
    // return super.onInterceptTouchEvent(e);
    // }

    boolean sildingToRight = false;
    boolean sildingToLeft = false;

    /**
     * 用于接收事件并处理，它的返回值也是一个布尔值，决定了事件及后续事件是否继续向上传递，这个方法是从子View向父View传递。
     **/
    // @Override
    // public boolean onTouchEvent(MotionEvent event) {
    // if (mLock) {
    // return false;
    // }
    // switch (event.getAction()) {
    // case MotionEvent.ACTION_DOWN:
    // return false;
    // case MotionEvent.ACTION_MOVE:
    // break;
    // case MotionEvent.ACTION_UP:
    // checkSildingLeftorRight(event);
    // if (sildingToRight) {
    // leftScroll();
    // }
    // if (sildingToLeft) {
    // rightScroll();
    // }
    // return false;
    // /**
    // * ACTION_CANCEL 当用户保持按下操作，并从你的控件转移到外层控件时，会触发ACTION_CANCEL，建议进行处理～
    // * 当前的手势被中断，不会再接收到关于它的记录。 推荐将这个事件作为 ACTION_UP 来看待，但是要区别于普通的ACTION_UP
    // **/
    // case MotionEvent.ACTION_CANCEL:
    // checkSildingLeftorRight(event);
    // if (sildingToRight) {
    // leftScroll();
    // }
    // if (sildingToLeft) {
    // rightScroll();
    // }
    // return false;
    // }
    // return true;
    // }
    private void checkSildingLeftorRight(MotionEvent event) {
        int moveX = (int) event.getRawX();
        int deltaX = tempX - moveX;
        if (Math.abs(moveX - downX) > mTouchSlop && Math.abs((int) event.getRawY() - downY) < mTouchSlop) {
            isSilding = true;
        }

        if (moveX - downX >= 0 && isSilding) {
            sildingToRight = true;
            sildingToLeft = false;
        }

        if (moveX - downX <= 0 && isSilding) {
            sildingToRight = false;
            sildingToLeft = true;
        }
    }

    // @Override
    // public void onClick(View v)
    // {
    // switch(v.getId())
    // {
    // case R.id.showNextPageBtn:
    // Intent intent = new Intent();
    // intent.setClass(this, HomeActivity.class);
    // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    // startActivity(intent);
    // finish();
    // intent=null;
    // clearMemory();
    // break;
    // }
    // }

    // @Override
    // public void clearMemory() {
    //
    // }

    // public void setSm(SlidingMenu sm) {
    // this.sm = sm;
    // }

    // 设置外层ViewPager
    public void setParentViewPager(ViewPager viewPager) {
        this.parentViewPager = viewPager;
    }

    public String[] getImgUrls() {
        return imgUrls;
    }

    // 设置banner图片的url
    public void setImgUrls(String[] imgUrls) {
        asyncShow = true;// 用于控制显示网络图片还是本地图片
        this.imgUrls = imgUrls;
    }

    public boolean ismLock() {
        return mLock;
    }

    public void setmLock(boolean mLock) {
        this.mLock = mLock;
    }

    public int getDefault_img_resId() {
        return default_img_resId;
    }

    public void setDefault_img_resId(int default_img_resId) {
        this.default_img_resId = default_img_resId;
    }
}
