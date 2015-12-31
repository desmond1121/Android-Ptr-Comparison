#安卓下拉刷新开源库对比
目前仅比对github上star数>1500的下拉刷新开源库，在比较完成之后可能会加入其它有代表性的库.

##目录

- [对比的开源库列表及简介](#Repo)
- [拓展性](#拓展性)
- [易用性](#易用性)
- [触屏事件分发](#触屏事件分发)
- [性能分析](#性能分析)
- [总结](#总结)
- [附录-知识点参考](#附录-知识点参考)

##Repo
|Repo|Owner|Star<br/>(2015.12.5)|version|Snap shot|
|:--:|:--:|:------:|:---:|:--:|
|[Android-PullToRefresh][3]<br/>(作者已停止维护)|[chrisbanes][4]|6014|latest|![chrisbanes](/demo_gif/chrisbanes.gif)|
|[android-Ultra-Pull-To-Refresh][1]|[liaohuqiu][2]|3413|1.0.11|![liaohuqiu](/demo_gif/liaohuqiu.gif)|
|[android-pulltorefresh][5]<br/>(作者已停止维护)|[johannilsson][6]|2414|latest|![johannilsson](/demo_gif/johan.gif)|
|[Phoenix][7]|[Yalantis][8]|1897|1.2.3|![yalantis](/demo_gif/yalantis.gif)|
|[FlyRefresh][9]|[race604][10]|1843|2.0.0|![flyrefresh](/demo_gif/flyrefresh.gif)|
|[SwipeRefreshLayout][11]|Android <br/> Support v4 <br/> (19.1.0 ↑)|None|latest|![swipe_refresh](/demo_gif/swipe.gif)|

##拓展性

|Repo|自定义顶部视图|支持的内容布局|
|:--:|:--:|:--:|:---:|:--:|:--:|
|[Android-PullToRefresh][3]|不支持，只能改代码。<br/>由于仅支持其中实现的`LoadingLayout`作为顶视图，改代码实现自定义工作量较大。|任意视图，内置:`GridView`<br/>`ListView`,`HorizontalScrollView`<br/> `ScrollView` ,`WebView`|
|[android-Ultra-Pull-To-Refresh][1]|任意视图。<br/> 通过继承`PtrUIHandler`并调用<br/>`PtrFrameLayout.addPtrUIHandler()`得到最大支持。|任意视图|
|[android-pulltorefresh][5]|不支持，只能改代码。<br/> 代码仅一个`ListView`，耦合度太高，改动工作量较大。|无法扩展，自身为`ListView`|
|[Phoenix][7]|不支持，此控件特点就是顶部视图及动画。|任意视图，只显示最后一个嵌套的子视图。|
|[FlyRefresh][9]|不支持，此控件特点就是顶部视图及动画。|任意视图|
|[SwipeRefreshLayout][11]|不支持，固定为Material风格|任意视图|

##易用性

|Repo|可在gradle配置|上拉加载|自动加载|滑动阻尼配置|
|:--:|:--:|:------:|:---:|:--:|:--:|:--:|
|[Android-PullToRefresh][3]|×|√|×|移动比固定1/2|
|[android-Ultra-Pull-To-Refresh][1]|√|×|√|√|
|[android-pulltorefresh][5]|×|×|×|移动比固定1/1.7|
|[Phoenix][7]|√|×|×|移动比固定1/2|
|[FlyRefresh][9]|√|×|×|×|
|[SwipeRefreshLayout][11]|√|×|×|移动比固定1/2|

##触屏事件分发

本节分析控件对于**触屏事件的分发以及处理拖动的时机**，具体**拖动实现**将在下一节[性能分析](#性能分析)中介绍。

此处添加进一个可以横滑的组件，并将所有组件中的`ListView`替换为自己实现的`ClassicListView`，重写控件`dispatchTouchEvent`, `onTouchEvent`来观察事件的处理传递。举几个典型：

###1. Chris Banes' ptr

**触屏分发**：

- `dispatchTouchEvent` 没有处理。
- `onInterceptTouchEvent` 返回结果为`mIsBeingDragged`。
    + `DOWN` 不拦截。若可以拉动，更新`mIsBeingDragged`为`false`；
    + `MOVE` 正在更新时直接拦截，如果拉动模式方向（竖直or水平）上的移动更多则将`mIsBeingDragged`置为`true`（反之不会置为`false`）。
    + `UP/CANCEL` 不拦截，更新`mIsBeingDragged`为false。
- `onTouchEvent` （此阶段处理UI拖动逻辑）
    + `DOWN` 此时可以拉动刷新时消耗该event（返回`true`），否则返回`false`；
    + `MOVE` `mIsBeingDragged`为`true`时消耗该event（返回`true`），否则返回`false`；
    + `UP/CANCEL` `mIsBeingDragged`为`true`时，消耗该event（返回`true`），否则返回`false`。

**分析**：

在`onTouchEvent`阶段处理了UI移动逻辑，且dispatch阶段不处理分发逻辑。配合此处intercept的处理，有两种情况：

- 最开始横滑，则不拦截，并且``mIsBeingDragged`为`false`时，`onTouchEvent`没有消耗此次事件，则此次不会再交给自己处理，它现在只有dispatch的功能，无法进行下拉、上拉的拖动；于是可以这么说：横滑事件一旦进行，就**无法触发上拉、下拉刷新**。
- 最开始竖滑，则可以拉动时，事件将被截断，并`onTouchEvent`返回`true`消耗该事件，无法分发到下层View，始终交由自身处理。

**触屏事件示例**：

![chrisbanes_scroll](/demo_gif/chrisbanes_scroll.gif)

###2. SwipeRefreshLayout

**触屏分发**：

- `dispatchTouchEvent` 没有处理。
- `onInterceptTouchEvent` 如果此时无法触发刷新，直接返回`false`；其他情况返回结果为`mIsBeingDragged`:
    + `DOWN` 更新`mIsBeingDragged`为`false`，不拦截；
    + `MOVE` 只要竖向移动偏移量大于`TouchSlop`则拦截，更新`mIsBeingDragged`为`true`；
    + `UP/CANCEL` 不拦截，更新`mIsBeingDragged`为false。
- `onTouchEvent` （此阶段处理UI拖动逻辑）
    + `DOWN` 消耗event（返回`true`）；
    + `MOVE` 仅仅当移动回顶部后再移动时不消耗，其他情况均消耗event（返回`true`）；
    + `UP/CANCEL` 不消耗。

**分析**：

与Chris Banes一样，处理方式都是重写`onInterceptTouchEvent` + `onTouchEvent`，不过效果却完全不同。究其原因，主要是它没有对水平、竖直冲突时做判断，并且`onTouchEvent`中除个别情况外都返回`true`，即消耗了这个事件。所以**只要能够刷新时，无论事件是否被底层view消耗，刷新动作一定会截断事件分发**。

**触屏事件示例**：

![swipe_scroll](/demo_gif/swipe_scroll.gif)

###3. Liaohuqiu's ptr

**触屏分发**：

- `dispatchTouchEvent` （此阶段处理UI拖动逻辑）
    + `DOWN` 手动调用`super.dispatchTouchEvent()`将事件传递下去，但之后直接返回`true`，保证后续能够处理到move、up、cancel事件；
    + `MOVE` 被拉动时直接返回`true`，不向下传递事件；没有被拉动、无法触发拉动时不处理，传递给下层view。若设置了`disableWhenHorizontalMove`，则在没有被拉动时的横滑操作直接传递给下层view；
    + `UP/CANCEL` 如果被拖动了，则直接返回`true`，截断了此次事件，并手动向下层传递一个cancel事件；否则直接传递给下层view。
- `onInterceptTouchEvent` 没有处理
- `onTouchEvent` 没有处理

**分析**：

dispatch阶段直接处理了分发逻辑与UI移动逻辑。只要它自身或它的子view处理了事件，dispatch永远会被触发，且它down时永远返回`true`。那么可以说：只要满足能够下拉的情况（对于`ListView`，默认为第一项完全可见）时，**下拉刷新动作一定会被触发**。一旦拉动，会在`updatePos`里面向下层view传递一个cancel事件，下层将会不再处理此次事件序列(原因可见`View.dispatchTouchEvent()` -> `InputEventConsistencyVerifier.onTouchEvent()`)。

所以如果内部有冲突的滑动事件处理机制（典型就是嵌套横滑），那么只要一进行刷新拉动，内部的事件处理马上就会被截断。与Chris Banes的下拉刷新处理机制（内部消耗事件时外部无法拉动）不一样。

**触屏事件示例**：

![liaohuqiu_scroll](/demo_gif/liaohuqiu_scroll.gif)

###3.其他库

基本的做法就是如上两种。以下不再赘述，由于`ListView`一定会消耗事件，如果是**嵌套视图**的话必须重写`onInterceptTouchEvent`+`onTouchEvent`或者直接重写`dispatchTouchEvent`才能够保证正确接收并处理到触摸事件。两种写法各有利弊，我个人认为重写`onInterceptTouchEvent` + `onTouchEvent`更加灵活。下面简单列出余下库的做法：

- **Johannilsson's ptr** 没有嵌套，直接处理`onTouchEvent`；
- **Yalantis's ptr** 嵌套视图，处理类似Chris banes' ptr；
- **race604's ptr** 嵌套视图，处理类似Chris banes' ptr；

##性能分析

通过捕捉如下图中的操作持续1秒钟的systrace进行性能分析：

![trace_operation](trace_operation.gif)

> 注：由于开源库Header大多无法直接放自定义顶部视图，头部视图复杂程度不同，数据对比结果会有所偏差。

###1. Chris Banes's Ptr

滑动实现方式：触摸造成的下拉均是`View.scrollTo()`实现的；在松手之后，`View.post(Runnable)`触发`Runnable`执行回滚动画，在滑回原处之前不断`post`自己，并配合`Interpolator`执行`scrollTo()`进行滚动。 

trace snapshot:

![trace_chrisbanes](/traces/chrisbanes.PNG)

**分析**：

作为Github上星星数最多的Android下拉刷新控件，从性能上看（渲染时间构成）几乎没有什么明显的缺点。可惜的是作者已经不再维护，顶部视图的扩展性比较差，并且gradle中也无法使用。在本次demo这类层级比较简单的环境中，几乎都达到了60fps，可以与后面的trace对比。

###2. liaohuqiu's Ptr

滑动实现方式：触摸造成的下拉均是`View.offsetTopAndBottom()`实现的；在松手之后，触发`Scroller.startScroll()`计算回滚，使用`View.post(Runnable)`不停地监视`Scroller`的计算结果，从而实现视图变化(此处依然是`View.offsetTopAndBottom()`完成视图移动)。

trace snapshot:

![trace_liaohuqiu](/traces/liaohuqiu.PNG)

**分析**：

这套开源库可以说是自定义功能最强的组件了，你可以实现`PtrUIHandler`并将其add到`PtrFrameLayout`完美地与下拉刷新事件适配。美中不足的就是在下拉状态变化的时候会有一阵measure时间。我查看了一下代码，发现是`PtrClassicFrameLayout`（作者实现的集成默认下拉视图的layout）的顶部视图出了问题：

![liaohuqiu_header](/liaohuqiu_ptr_header.PNG)

看！都是`wrap_content`，那么当里面的内容变化的时候，是会触发`View.requestLayout()`的。不要小看这一个子视图的小操作，一个`requestLayout()`大概是这么一个流程：`View.requestLayout()`->`ViewParent.requestLayout()`->...->`ViewRootImpl.requestLayout()`->`ViewRootImpl.doTraversal()`=>**MEASURE**(ViewGroup)=>**MEASURE**(ChildView of ViewGroup)

在层级复杂的时候（大部分互联网产品由于复杂的产品需求嵌套都会比较多），它会层层向上调用，将measure时间放大至一个可观的层级。下拉刷新界面的卡顿由此而来。

我修改了一下，将其全部变为固定高度、宽度，之后的trace如下：

![trace_liaohuqiu_new](/traces/liaohuqiu_new.PNG)

measure时间神奇的没掉了吧:)

###3. johannilsson's Ptr

滑动实现方式：初始时`setSelection(1)`隐藏顶部视图（使用这个下拉刷新控件注意将滚动栏隐藏，否则会露馅）。在拉下来超过header view的measure高度之前，均是`ListView`自有的滚动；在下拉超过header measure高度之后，对header使用`View.setPadding()`让header继续下移。

trace snapshot:

![trace_johan](/traces/johan.PNG)

**分析**：

通过顶视图调用`View.setPadding()`来实现的滑动，在下拉距离超过header高度后，会造成不断的`requestLayout()`!这就解释了为什么图中UI线程的蓝色块时间(measure时间)很明显。**当你在视图层级比较复杂的app中使用它时，下拉动作所造成的开销会非常明显，卡顿是必然结果。**

###4. Yalantis's Ptr

滑动实现方式：通过`View.topAndBottomOffset()`移动视图，在松手之后启动一个`Animation`执行回滚动画，内容视图的移动也使用`View.offsetTopAndBottom()`实现。为了保证子内容视图的底部padding在移动之后与布局文件中的padding属性一致，它额外调用了`View.setPadding()`实时设置padding。

顶部动效实现方式：`Drawable`的`draw()`中，为`Canvas`中设置“太阳”偏移量及背景缩放。

trace snapshot:

![trace_yalantis](/traces/yalantis.PNG)

**分析**：

此开源库动画效果非常柔和，且顶部视图全部是通过draw去更新，不会造成第三个开源库那样的大开销问题。可惜的是比较难以去自定义顶部视图，不好在线上产品中使用，不过这个开源库是一个好的练手与学习的对象。由于顶部动效实现开销不大，它的性能同样非常好。

它在松手后回滚时调用的`View.setPadding()`可能会造成measure开销比较大，于是我特地测了一下松手回滚的trace，一看确实measure时间非常可观：

![trace_yalantis_scroll_back](/traces/yalantis_back.PNG)

确实它如果要保证展示内容视图的padding与布局文件中一致，是必须这么做的（调用`View.setPadding()`），因为通过`View.offsetTopAndBottom()`向下移动子视图时，子视图的内容整个移动下来，在视觉上会影响它设置好的底部padding。但是很有意思，它向下移动的时候没有这么设置，拉下来的时候底部padding就没了。回滚动画的时候才设了padding，就显得没那么必要了。我在demo中也进行了实践，确实是这样的：

![yalantis_padding](/demo_gif/yalantis_padding.gif)

我暂时也没想到什么方法可以更好地处理子视图padding问题。但实际上，由于这个库是一个嵌套视图，并且只会有一个内容视图显示出来，可以尝试放弃对子视图padding的处理。如果需要，可以使用父视图的padding来代替，这样是最完美的效果。子视图再怎么移动，也会被父视图已经设好的padding局限住。由此一来padding就不会被影响，同时提高了性能。不过这样一来牺牲了子视图padding的设置，在使用的时候可以根据需要各取所需。

我粗略的做了一点点改动，将它的`setPadding()`注释掉了。不过由于该库的一些其他实现逻辑，导致会有一些问题，此处仅看性能上的变化，改动后松手回滚trace，已经没有了measure时间：

![yalantis_back_trace_new](/traces/yalantis_back_new.PNG)

###5. race604's Ptr

滑动实现方式：`View.topAndBottomOffset()`

顶部动效实现方式：

- **飞机滑动** `ObjectAnimator`.
- **山体移动、树木弯曲** 通过移动距离计算山体偏移、树木轮廓，得出`Path`后进行draw.

trace snapshot:

![trace_flyrefresh](/traces/flyrefresh.PNG)

**分析**：每次拖动都会重新计算背景"山体"与"树木"的`Path`，造成了draw时间过长。效果不错，也是一个好的学习对象，相比`Yalantis`的下拉刷新性能上就差一些了，它的draw中的计算量太多。使用起来疑似有bug：拖动到顶部，无法再往上拖动，并且会出现拖动异常。

###6. SwipeRefreshLayout

滑动实现方式：内容固定，仅有顶部动效。

顶部动效实现方式：

- **上下移动** `View.bringToFront()` + `View.offsetTopAndBottom()`.
- **动效** 通过移动偏移量计算弧形曲线的角度、三角形的位置，使用`drawArc`, `drawTriangle`将他们画到`Canvas`上。

trace snapshot:

![trace_swipe](/traces/swipe.PNG)

**分析**：官方的下拉刷新组件，动画十分美观简洁，API构造清晰明了。但是为什么每次的移动都会有一段明显的measure时间呢？我研究了一下代码，发现罪魁祸首是`View.bringToFront()`，它在每一次滑动的时候都会对顶部动效视图调用这个函数。仔细追朔这个函数源码，它会走到下面这段代码中：

`ViewGroup.java`
```
    public void bringChildToFront(View child) {
        final int index = indexOfChild(child);
        if (index >= 0) {
            removeFromArray(index);
            addInArray(child, mChildrenCount);
            child.mParent = this;
            requestLayout();
            invalidate();
        }
    }
```

看，它是会触发`View.requestLayout()`的！这个函数会造成的后果我们在之前已经解释了，它会造成大量的UI线程开销。实际上我认为这个函数是没有调用的必要的，`SwipeRefreshLayout`明明在重写`onLayout()`的时候，header会被layout到child之上，没有必要再`bringToFront()`。

于是我copy了一份代码，将这一行注了(对应代码ptr-source-lib/src/main/java/com/android/support/SwipeRefreshLayout.java)，再次编译，measure时间确实没掉了，对功能毫无影响，性能却有了很大优化：

![trace_swipe](/traces/swipe_new.PNG)

这样一来就不会每一次拉动，都会触发measure。若有同学知道这个`bringToFront()`在其中有其他我未探测到的功效，请issue指点:) 

##总结

|Repo|性能|拓展性|综合建议|
|:--:|:--:|:--:|:--:|
|[Android-PullToRefresh][3]|★★★★★|★★★|由于作者不再维护，无法在gradle中配置，顶部视图难以拓展，不建议放入工程中使用|
|[android-Ultra-Pull-To-Refresh][1]|★★★★★|★★★★★|如之前分析，`PtrClassicFrameLayout`性能有缺陷；建议使用`PtrFrameLayout`，性能较好。这套库自定义能力很强，建议使用。|
|[android-pulltorefresh][5]|★|★|实现方式上有缺陷，拓展性也很差。优点就是代码非常简单，只能作为反面例子。|
|[Phoenix][7]|★★★★|★★|效果非常好，性能不错，可惜比较难拓展顶部视图，为了适配布局padding造成了性能损失，有优化空间。可以作为学习与练手的对象。|
|[FlyRefresh][9]|★★★★|★★|效果很新颖，可惜的是顶部视图计算动效上开销太大，优化空间较少，可以作为学习与练手的对象。|
|[SwipeRefreshLayout][11]|★★★|★★|官方出品，更新有保障，但是如上分析，其实性能上还是有点缺陷的，拓展性比较差，不建议放入工程中使用。|


##附录-知识点参考

1. [为你的应用加速 - 安卓优化指南](https://github.com/bboyfeiyu/android-tech-frontier/blob/master/issue-27/%E4%B8%BA%E4%BD%A0%E7%9A%84%E5%BA%94%E7%94%A8%E5%8A%A0%E9%80%9F%20-%20%E5%AE%89%E5%8D%93%E4%BC%98%E5%8C%96%E6%8C%87%E5%8D%97.md)
1. [使用Systrace分析UI性能](https://github.com/bboyfeiyu/android-tech-frontier/blob/b7e3f1715158fb9f2bbb0f349c4ec3da3db81342/issue-26/%E4%BD%BF%E7%94%A8Systrace%E5%88%86%E6%9E%90UI%E6%80%A7%E8%83%BD.md)
1. [Systrace-文档](developers.android.com/tools/help/systrace.html)
1. [事件分发-郭霖csdn](http://blog.csdn.net/guolin_blog/article/details/9153747)

[4]: https://github.com/chrisbanes
[3]: https://github.com/chrisbanes/Android-PullToRefresh
[2]: https://github.com/liaohuqiu
[1]: https://github.com/liaohuqiu/android-Ultra-Pull-To-Refresh
[5]: https://github.com/johannilsson/android-pulltorefresh
[6]: https://github.com/johannilsson
[7]: https://github.com/Yalantis/Phoenix
[8]: https://github.com/Yalantis
[9]: https://github.com/race604/FlyRefresh
[10]: https://github.com/race604
[11]: http://developer.android.com/reference/android/support/v4/widget/SwipeRefreshLayout.html