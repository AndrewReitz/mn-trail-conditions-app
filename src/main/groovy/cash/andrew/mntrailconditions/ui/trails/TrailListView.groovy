package cash.andrew.mntrailconditions.ui.trails

import android.content.Context
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.widget.LinearLayout
import cash.andrew.mntrailconditions.R
import cash.andrew.mntrailconditions.data.Funcs
import cash.andrew.mntrailconditions.data.Injector
import cash.andrew.mntrailconditions.data.api.Results
import cash.andrew.mntrailconditions.data.api.TrailConditionsService
import cash.andrew.mntrailconditions.data.model.TrailInfo
import cash.andrew.mntrailconditions.data.model.TrailRegion
import cash.andrew.mntrailconditions.ui.misc.BetterViewAnimator
import com.arasthel.swissknife.SwissKnife
import com.arasthel.swissknife.annotations.InjectView
import groovy.transform.CompileStatic
import org.threeten.bp.LocalDateTime
import rx.subscriptions.CompositeSubscription
import timber.log.Timber

import javax.inject.Inject
import retrofit2.adapter.rxjava.Result
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

@CompileStatic
class TrailListView extends LinearLayout {

  private static final LocalDateTime THREE_MONTHS_BEFORE_TODAY = LocalDateTime.now().minusMonths(3)

  @InjectView(R.id.trail_list_toolbar) Toolbar toolbarView
  @InjectView(R.id.trail_list_animator) BetterViewAnimator animator
  @InjectView(R.id.trail_list_content) SwipeRefreshLayout refreshLayout
  @InjectView(R.id.trail_list_recycler_view) RecyclerView recyclerView

  @Inject TrailConditionsService trailConditionsService
  @Inject TrailListAdapter trailListAdapter

  private static final CompositeSubscription subscriptions = new CompositeSubscription()

  TrailListView(Context context, AttributeSet attrs) {
    super(context, attrs)
    if (!isInEditMode()) {
      Injector.obtain(context).inject(this)
    }
  }

  @Override protected void onFinishInflate() {
    super.onFinishInflate()
    SwissKnife.inject(this)

    recyclerView.layoutManager = new LinearLayoutManager(context)
    recyclerView.adapter = trailListAdapter

    refreshLayout.onRefreshListener = {
      Timber.d("onRefresh() called")
      loadData()
    }
  }

  @Override protected void onAttachedToWindow() {
    super.onAttachedToWindow()
    Timber.d("onAttachedToWindow() called")
    loadData()
  }

  @Override
  protected void onDetachedFromWindow() {
    Timber.d("onDetachedFromWindow() called")
    subscriptions.clear()
    super.onDetachedFromWindow()
  }

  private void loadData() {
    Timber.d("loadData() called");
    Observable<Result<List<TrailRegion>>> result = trailConditionsService.trailRegions
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .share()

    def subscription = result.filter(Results.isSuccessful())
            .map(Results.resultToBodyData())
            .flatMap { List<TrailRegion> trailRegions -> Observable.from(trailRegions) }
            .map { TrailRegion trailRegion -> trailRegion.trails }
            .flatMap { List<TrailInfo> trailInfoList -> Observable.from(trailInfoList) }
            .filter { TrailInfo ti -> ti.lastUpdated.isAfter(THREE_MONTHS_BEFORE_TODAY) }
            .toList()
            .subscribe { List<TrailInfo> trailInfo ->
              trailListAdapter.trails = trailInfo
              animator.displayedChildId = R.id.trail_list_content
            }
    subscriptions.add(subscription)

    subscription = result.filter(Funcs.not(Results.isSuccessful()))
            .doOnNext(Results.logError())
            .subscribe {
              animator.displayedChildId = R.id.trail_list_error
            }
    subscriptions.add(subscription)

    refreshLayout.refreshing = false
  }
}
