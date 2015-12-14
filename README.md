#安卓下拉刷新开源库对比
目前仅比对github上star数>1500的下拉刷新开源库，在比较完成之后可能会加入其它有代表性的库.

##Repo
|Repo|Owner|Star<br/>(2015.12.5)|version|Snap shot|
|:--:|:--:|:------:|:---:|:--:|
|[Android-PullToRefresh][3]<br/>(作者已停止维护)|[chrisbanes][4]|6014|latest|![chrisbanes](/demo_gif/chrisbanes.gif)|
|[android-Ultra-Pull-To-Refresh][1]|[liaohuqiu][2]|3413|1.0.11|![liaohuqiu](/demo_gif/liaohuqiu.gif)|
|[android-pulltorefresh][5]<br/>(作者已停止维护)|[johannilsson][6]|2414|latest|![johannilsson](/demo_gif/johan.gif)|
|[Phoenix][7]|[Yalantis][8]|1897|1.2.3|![yalantis](/demo_gif/yalantis.gif)|
|[FlyRefresh][9]|[race604][10]|1843|2.0.0|![flyrefresh](/demo_gif/flyrefresh.gif)|
|[SwipeRefreshLayout][11]|Android <br/> Support v4|None|19.1.0|![swipe_refresh](/demo_gif/swipe.gif)|

##拓展性

|Repo|自定义顶部视图|支持的内容布局|
|:--:|:--:|:--:|:---:|:--:|:--:|
|[Android-PullToRefresh][3]|不支持，只能改代码。<br/>由于仅支持其中实现的`LoadingLayout`作为顶视图，改代码实现自定义工作量较大。|任意视图，内置:`GridView`<br/>`ListView`,`HorizontalScrollView`<br/> `ScrollView` ,`WebView`|
|[android-Ultra-Pull-To-Refresh][1]|任意视图。<br/> 通过继承`PtrUIHandler`并调用<br/>`PtrFrameLayout.addPtrUIHandler()`得到最大支持。|任意视图|
|[android-pulltorefresh][5]|不支持，只能改代码。<br/> 代码仅一个`ListView`，耦合度太高，改动工作量较大。|无法扩展，自身为`ListView`|
|[Phoenix][7]|不支持，此控件特点就是顶部视图及动画。|任意视图|
|[FlyRefresh][9]|不支持，此控件特点就是顶部视图及动画。|任意视图|
|[SwipeRefreshLayout][11]|不支持，固定为Material风格|任意视图|

##易用性

|Repo|可在gradle配置|上拉加载|自动加载|滑动阻尼配置|
|:--:|:--:|:------:|:---:|:--:|:--:|:--:|
|[Android-PullToRefresh][3]|×|√|×|移动比固定1/2|
|[android-Ultra-Pull-To-Refresh][1]|√|×|√|√|
|[android-pulltorefresh][5]|×|×|×|移动比固定1/1.7|
|[Phoenix][7]|√|×|×|×|
|[FlyRefresh][9]|√|×|×|×|
|[SwipeRefreshLayout][11]|√|×|×|移动比固定1/2|

##性能分析

通过捕捉如下图中的操作持续1秒钟的systrace进行性能分析：

![trace_operation](trace_operation.gif)

> 注：由于开源库Header大多无法直接放自定义顶部视图，头部视图复杂程度不同，数据对比结果会有所偏差。

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

看！都是`wrap_content`，那么当里面的内容变化的时候，是会触发`View.requestLayout()`的。不要小看这一个子视图的小操作，一个`requestLayout()`大概是这么一个流程：`View.requestLayout()`->`ViewParent.requestLayout()`->...->`ViewRootImpl.requestLayout()`->`ViewRootImpl.doTraversal()`=>**MEASURE**(ViewGroup)=>**MEASURE**(ChildView of ViewGroup)

在层级复杂的时候（大部分互联网产品由于复杂的产品需求嵌套都会比较多），它会层层向上调用，将measure时间放大至一个可观的层级。下拉刷新界面的卡顿由此而来。

我修改了一下，将其全部变为固定高度、宽度，之后的trace如下：

![trace_liaohuqiu_new](/traces/liaohuqiu_new.PNG)

measure时间神奇的没掉了吧:)

###3. johannilsson's Ptr

滑动实现方式：`View.setPadding()`

trace snapshot:

![trace_johan](/traces/johan.PNG)

分析：

通过顶视图调用`View.setPadding()`来实现的滑动，是会造成不断的`requestLayout()`!这就解释了为什么图中UI线程的蓝色块时间(measure时间)很明显。**当你在视图层级比较复杂的app中使用它时，下拉动作所造成的开销会非常明显，卡顿是必然结果。**

###4. Yalantis's Ptr

滑动实现方式：`Animation` + `View.topAndBottomOffset()`

顶部动效实现方式：`Drawable`的`draw()`中，为`Canvas`中设置偏移量及缩放。

trace snapshot:

![trace_yalantis](/traces/yalantis.PNG)

分析：此开源库动画效果非常柔和，且顶部视图全部是通过draw去更新，不会造成第三个开源库那样的大开销问题。可惜的是比较难以去自定义顶部视图，不好在大型线上产品中使用，不过这个开源库是一个好的练手与学习的对象。由于顶部动效实现开销不大，它的性能同样非常好。

###5. race604's Ptr

滑动实现方式：`View.topAndBottomOffset()`

顶部动效实现方式：

- **飞机滑动** `ObjectAnimator`.
- **山体移动、树木弯曲** 通过移动距离计算山体偏移、树木轮廓，得出`Path`后进行draw.

trace snapshot:

![trace_flyrefresh](/traces/flyrefresh.PNG)

分析：每次拖动都会重新计算背景"山体"与"树木"的`Path`，造成了draw时间过长。效果不错，也是一个好的学习对象，相比`Yalantis`的下拉刷新性能上就差一些了，它的draw中的计算量太多。使用起来疑似有bug：拖动到顶部，无法再往上拖动，并且会出现拖动异常。

###6. SwipeRefreshLayout

滑动实现方式：内容固定，仅有顶部动效。

顶部动效实现方式：

- **上下移动** `View.bringToFront` + `View.offsetTopAndBottom()`.
- **动效** 通过移动偏移量计算弧形曲线的角度、三角形的位置，使用`drawArc`, `drawTriangle`将他们画到`Canvas`上。

trace snapshot:

![trace_swipe](/traces/swipe.PNG)

分析：官方的下拉刷新组件，动画十分美观简洁，API构造清晰明了。但是为什么每次的移动都会有一段明显的measure时间呢？我研究了一下代码，发现罪魁祸首是`View.bringToFront`，仔细看这个源码，它会走到下面这段代码中：

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

看，它是会触发`View.requestLayout()`的！这个函数会造成的后果我们在之前已经解释了，它会造成大量的UI线程开销。实际上我认为这个函数是没有调用的必要的，`SwipeRefreshLayout`明明在重写`layout()`的时候，header会被layout到child之上，没有必要再`bringToFront()`。

于是我copy了一份代码，将这一行注了(对应代码ptr-source-lib/src/main/java/com/android/support/SwipeRefreshLayout.java)，再次编译，measure时间确实没掉了，对功能毫无影响，性能却有了很大优化：

![trace_swipe](/traces/swipe_new.PNG)

这样一来就不会每一次拉动，都会触发measure。若有同学知道这个`bringToFront()`在其中有其他我未探测到的功效，请issue指点:) 

##总结

|Repo|性能|拓展性|综合建议|
|:--:|:--:|:--:|:--:|
|[Android-PullToRefresh][3]|★★★★★|★★★|由于作者不再维护，无法在gradle中配置，顶部视图难以拓展，不建议放入工程中使用|
|[android-Ultra-Pull-To-Refresh][1]|★★★★★|★★★★★|如之前分析，`PtrClassicFrameLayout`性能有缺陷；建议使用`PtrFrameLayout`，性能较好。这套库自定义能力很强，建议使用。|
|[android-pulltorefresh][5]|★|★|实现方式上有缺陷，拓展性也很差。优点就是代码非常简单，只能作为反面例子。|
|[Phoenix][7]|★★★★★|★★|效果非常好，性能不错，可惜比较难拓展顶部视图，可以作为学习与练手的对象。|
|[FlyRefresh][9]|★★★★|★★|效果很新颖，可惜的是顶部视图计算动效上开销太大，可以作为学习与练手的对象。|
|[SwipeRefreshLayout][11]|★★★|★★|官方出品，更新有保障，但是如上分析，其实性能上还是有点缺陷的，拓展性比较差，不建议放入工程中使用。|


##附录-知识点参考

1. [为你的应用加速 - 安卓优化指南](https://github.com/bboyfeiyu/android-tech-frontier/blob/master/issue-27/%E4%B8%BA%E4%BD%A0%E7%9A%84%E5%BA%94%E7%94%A8%E5%8A%A0%E9%80%9F%20-%20%E5%AE%89%E5%8D%93%E4%BC%98%E5%8C%96%E6%8C%87%E5%8D%97.md)
2. [使用Systrace分析UI性能](https://github.com/bboyfeiyu/android-tech-frontier/blob/b7e3f1715158fb9f2bbb0f349c4ec3da3db81342/issue-26/%E4%BD%BF%E7%94%A8Systrace%E5%88%86%E6%9E%90UI%E6%80%A7%E8%83%BD.md)

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