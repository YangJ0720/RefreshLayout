# RefreshLayout

#### 450行代码实现一个超级简单的RefreshLayout控件

项目特点
* 支持下拉刷新、上拉加载，并且是可配置的（仅下拉刷新 or 仅上拉加载）
* 支持NestedScrolling特性
* Header和Footer支持自定义，目前本工程仅实现两种效果，分别为默认效果和缩放效果

##  APK下载
[Download](https://github.com/YangJ0720/RefreshLayout/blob/master/apk/app-debug.apk)

## XML配置
```xml
<yangj.refreshlayout.RefreshLayout
        android:id="@+id/refreshLayout"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="@android:color/darker_gray"
        app:reset_duration="800"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent">

    <android.support.v7.widget.RecyclerView
            android:id="@+id/recyclerView"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:scrollbars="vertical"
            android:background="@android:color/white"/>

</yangj.refreshlayout.RefreshLayout>
```
## 属性说明
|布局属性|说明|
|----|-----
|app:reset_duration|重置为初始状态时长

|方法属性|说明|
|----|-----
|addHeaderView()|添加header
|addFooterView()|添加footer
|refreshComplete()|下拉刷新完成，调用该方法使RefreshLayout恢复正常状态
|loaderComplete()|上拉刷新完成，调用该方法使RefreshLayout恢复正常状态

## 效果图
如下图所示：

![image](https://github.com/YangJ0720/RefreshLayout/blob/master/gif/preview.gif)
