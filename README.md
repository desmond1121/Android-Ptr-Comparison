#Ptr Comparizon
目前仅比对github上star数>1500的下拉刷新开源库，在比较完成之后可能会加入其它有代表性的库.

##Repo
|Repo|Owner|Star (up to2015.12.5)|Snap shot|
|:--:|:--:|:------:|:---:|:--:|
|[Android-PullToRefresh][3]<br/>(已停止维护)|[chrisbanes][4]|6014|![chrisbanes](/demo_gif/chrisbanes.gif)|
|[android-Ultra-Pull-To-Refresh][1]|[liaohuqiu][2]|3413|![liaohuqiu](/demo_gif/liaohuqiu.gif)|
|[android-pulltorefresh][5]<br/>(已停止维护)|[johannilsson][6]|2414|![johannilsson](/demo_gif/johan.gif)|
|[Phoenix][7]|[Yalantis][8]|1897|![yalantis](/demo_gif/yalantis.gif)|
|[FlyRefresh][9]|[race604][10]|1843|![flyrefresh](/demo_gif/flyrefresh.gif)|

##拓展性

|Repo|自定义顶部视图|支持的内容布局|
|:--:|:--:|:--:|:---:|:--:|:--:|
|[Android-PullToRefresh][3]|不支持，只能改代码。<br/>由于仅支持其中实现的`LoadingLayout`作为顶视图，改代码实现自定义工作量较大。|任意视图，内置:`GridView`<br/>`ListView`,`HorizontalScrollView`<br/> `ScrollView` ,`WebView`|
|[android-Ultra-Pull-To-Refresh][1]|任意视图。<br/> 通过继承`PtrUIHandler`并调用<br/>`PtrFrameLayout.addPtrUIHandler()`得到最大支持。|任意视图||
|[android-pulltorefresh][5]|不支持，只能改代码。<br/> 代码仅一个`ListView`，耦合度太高，改动工作量较大。|无法扩展，自身为`ListView`|
|[Phoenix][7]|不支持，此控件特点就是顶部视图及动画。|任意视图|
|[FlyRefresh][9]|不支持，此控件特点就是顶部视图及动画。|任意视图|

##易用性

|Repo|gradle配置|上拉加载|自动加载|滑动阻尼配置|
|:--:|:--:|:------:|:---:|:--:|:--:|:--:|
|[Android-PullToRefresh][3]|×|√|×|
|[android-Ultra-Pull-To-Refresh][1]|√|×|√|√|
|[android-pulltorefresh][5]|×|×|×|
|[Phoenix][7]|√|×|×|
|[FlyRefresh][9]|√|×|×|

##性能分析

通过捕捉如下图中的操作持续1秒钟的systrace进行性能分析：

![trace_operation](trace_operation.gif)

> 注：由于开源库Header大多无法直接放自定义视图，头部视图复杂程度不同，数据对比结果会有所偏差。

###1. Chris Banes's Ptr

滑动实现方式：`View.post(Runnable)` + `View.scrollTo()` 

trace snapshot:

![trace_chrisbanes](/traces/chrisbanes.PNG)

作为Github上星星数最多的Android下拉刷新控件，从性能上看（渲染时间构成）几乎没有什么明显的缺点。可惜的是作者已经不再维护，并且gradle中也无法使用。在本次demo这类层级比较简单的环境中，几乎都达到了60fps，可以与后面的trace对比。

###2. liaohuqiu's Ptr

滑动实现方式：`Scroller` + `View.post(Runnable)` + `View.offsetTopAndBottom()`

trace snapshot:

![trace_liaohuqiu](/traces/liaohuqiu.PNG)

这套开源库可以说是自定义功能最强的组件了，美中不足的就是在下拉状态变化的时候会有一阵measure时间。我查看了一下代码，发现是`PtrClassicFrameLayout`的顶部视图出了问题：

![liaohuqiu_header](/liaohuqiu_ptr_header.PNG)

看！都是wrap_content，那么当里面的内容变化的时候，是会触发requestLayout的。不要小看这一个子视图的小操作，一个requestLayout大概是这么一个流程：`View.requestLayout()`->`ViewParent.requestLayout()`->...->`ViewRootImpl.requestLayout()`->`ViewRootImpl.doTraversal()`=>MEASURE ViewGroup=>MEASURE View

在层级复杂的时候（大部分互联网产品由于复杂的产品需求嵌套都会比较多），它会层层向上调用，将measure时间放大至一个可观的层级。下拉刷新界面的卡顿由此而来。

我修改了一下，将其全部变为固定高度、宽度，之后的trace如下：

![trace_liaohuqiu_new](/traces/liaohuqiu_new.PNG)

measure时间神奇的没掉了吧:P

###3. johannilsson's Ptr

滑动实现方式：`View.setPadding()`

trace snapshot:

![trace_johan](/traces/johan.PNG)

分析：

通过顶视图调用`View.setPadding()`来实现的滑动，是会造成不断的`requestLayout()`!这就解释了为什么图中UI线程的蓝色块时间很明显。**当你在视图层级比较复杂的app中使用它时，下拉动作所造成的开销会非常明显，卡顿是必然结果。**

###4. Yalantis's Ptr

滑动实现方式：`Animation` + `View.topAndBottomOffset()`

trace snapshot:

![trace_yalantis](/traces/yalantis.PNG)

分析：此开源库动画效果非常柔和，且顶部视图全部是通过draw去更新，不会造成第三个开源库那样的大开销问题。可惜的是比较难以去自定义顶部视图，不好在大型线上产品中使用，不过这个开源库是一个好的练手与学习的对象。它的性能同样非常好。

###5. race604's Ptr

滑动实现方式：`NestedScrollingChildHelper.dispatchNestedScroll()`

trace snapshot:

![trace_flyrefresh](/traces/flyrefresh.PNG)

分析：待分析。

[1]: https://github.com/liaohuqiu/android-Ultra-Pull-To-Refresh
[2]: https://github.com/liaohuqiu
[3]: https://github.com/chrisbanes/Android-PullToRefresh
[4]: https://github.com/chrisbanes
[5]: https://github.com/johannilsson/android-pulltorefresh
[6]: https://github.com/johannilsson
[7]: https://github.com/Yalantis/Phoenix
[8]: https://github.com/Yalantis
[9]: https://github.com/race604/FlyRefresh
[10]: https://github.com/race604