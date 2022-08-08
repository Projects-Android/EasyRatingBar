# EasyRatingBar
仿系统RatingBar，更易用，更简洁。自定义形状、大小、间距...

![image](/screenshot/demo.gif)

# How to use++
**1、Add it in your root build.gradle at the end of repositories:**
```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

**2、Add the dependency**
```groovy
dependencies {
    implementation 'com.github.Projects-Android:EasyRatingBar:0.2'
	}
```

**3、use in xml**
```xml
<com.ev.easyratingbar.EasyRatingBar
    android:id="@+id/erb_star"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:padding="4dp"
    app:drawableMargin="4dp"
    app:drawableWidth="50dp"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintTop_toTopOf="parent"
    app:maxCount="5"
    app:progressColor="@color/purple_700"
    app:rate="1"
    app:ratingDrawable="@mipmap/ic_star"
    app:step="0.1"
    app:tintColor="@color/purple_200"
    app:slidable="true"/>
```

**4、observe in java**
```java
EasyRatingBar starRating = findViewById(R.id.erb_star);
starRating.setOnRatingSeekListener(newRate -> {
    Log.d("", newRate + "");
});
```