## 需求
一份源码，修改API地址，包名，替换桌面图标和一些其他资源文件，生成不同的APK卖给不同的人。

## PS 篇幅略长，但是绝对全是干货

## 解决方案
为解决这个问题，我经历了以下三个阶段:

	1. 需要几份APK就copy几份源码进行修改。
	2. 将整个项目作为module来引用。
	3. 使用Gradle的Product Flavors来构建变种。

#### 第一种方案:需要几份APK就copy几份源码进行修改
这是早期做Android开发，仍然是EC横行的时候，很痛苦，需要几份就复制几份，开始简单，但是后期维护
却是特别的痛苦。原因是当出现一个BUG的时候需要修改多次，虽然说修改完成一份，后面只需要复制黏贴，但是也是痛苦，可以想象一下，被繁琐的事情缠绕。

#### 第二种方案:将整个项目作为module来引用
这是我接触到了AS，当时考虑的就是将整个项目改造成为module，创建不同的主项目来引用同一份源码，最终改造到一半，不可行，因为要修改大量的代码,比如:
	1. ID不再是final类型的了，switch view.id 之类的方法不能再使用。


#### 第三种方案:使用Gradle的Product Flavors来构建变种
前面两种方案简直是让我生不如死，但是随着时间的积累，慢慢的发现出现一种叫做多渠道打包方法，于是我按照这个思路找下去，终于在Google的官方文档中找到了解决方案:

[配置构建变体](https://developer.android.com/studio/build/build-variants.html?hl=zh-cn)

有兴趣的小伙伴可以直接去看，里面描述了如何去使用。


## 实践
### 1.基础配置
#### 1.创建一个普通的空白项目
这一步就不截图了，创建就可以了。

#### 2.在manifest中进行占位符配置

	${APP_ICON}	占位启动图标
	${APP_NAME}	占位app名称
	
同理，其他第三方需要的相关配置也是可以使用占位符，例如极光相关和友盟相关
	
		<permission
        android:name="${JPUSH_MESSAGE_VALUE}"
        android:protectionLevel="signature" />
        
         <meta-data
            android:name="UMENG_APPKEY"
            android:value="${UMENG_APPKEY}" />
        <meta-data
            android:name="UMENG_CHANNEL"
            android:value="${UMENG_CHANNEL}" />
            
            
            
            
 以上的占位符，都可以在gradle中使用其它值来替换。
 
 ![清单文件配置](http://qiniu.fullscreendeveloper.cn/%E6%B8%85%E5%8D%95%E6%96%87%E4%BB%B61.png)
 
 
 
#### 在Gradle中对占位符等进行配置
1. 对defaultConfig节点做一些修改，增加manifestPlaceholders字段，来替换manifest中的占位符。为了快速演示，所以就没配置极光和友盟了，都是一样的。


		defaultConfig {
	        applicationId "com.aohanyao.product.flavorsdemos"
	        minSdkVersion 18
	        targetSdkVersion 25
	        versionCode 1
	        versionName "1.0"
	        manifestPlaceholders = [APP_ICON: "@mipmap/ic_launcher",
	                                APP_NAME: "这是默认的配置"]
	    }
	    
	上面的配置将图标改为了ic_launcher，app的名称改为了这是默认的配置，为了保险起见，先直接运行APP，看看有没有更改成功。
	
	![构建默认的配置](http://qiniu.fullscreendeveloper.cn/%E6%9E%84%E5%BB%BA%E5%8F%98%E7%A7%8D-1.png)
	
	从上图可以看到，已经直接运行安装成功了，可以证明我们替换占位符是正确的。
	
2.变种的配置，ProductFlavors配置
在android节点中配置productFlavors节点，将defaultConfig节点复制一份过来，修改一下需要修改的地方，具体如下配置:

	flavorDimensions "default"
	productFlavors {
        //变种1
        flavorsdemo1 {
            applicationId "com.aohanyao.product.flavorsdemos.demo1"//修改了包名
            minSdkVersion 18
            targetSdkVersion 25
            versionCode 1
            versionName "1.0"
            manifestPlaceholders = [APP_ICON: "@mipmap/ic_launcher1",
                                    APP_NAME: "变种1"]
        }
        //变种2
        flavorsdemo2 {
            applicationId "com.aohanyao.product.flavorsdemos.demo2"//修改了包名
            minSdkVersion 18
            targetSdkVersion 25
            versionCode 1
            versionName "1.0"
            manifestPlaceholders = [APP_ICON: "@mipmap/ic_launcher2",
                                    APP_NAME: "变种2"]
        }
    }
    
  为了方便区分，我增加了ic_launcher1和ic_launcher2两个图标。
  
  详细配置截图:
  
  ![构建变种](http://qiniu.fullscreendeveloper.cn/小书匠/1523873942783.jpg)
  
  在上面的配置中，分别修改了APP的包名，名称以及图标。接下来就是验证是否成功了。只要这这两个都能安装上就算是成功(同样的包名是不能安装在同一台手机上的，当然有种黑科技不算)。配置完成后同步一下。
  
 3.选择构建变体
 
 	菜单->Build->Select Build Variant
 这时候左下会出现一个窗口，如果Select Build Variant无法选中的话，请先选择app目录。
 
 ![Select Build Variant](http://qiniu.fullscreendeveloper.cn/%E6%9E%84%E5%BB%BA%E5%8F%98%E7%A7%8D-3.png)
 
 可以看到，在gradle中配置的flavorsdemo1和flavorsdemo2都出现在了了选项中，一个是debug版本，一个是release版本，不用管它，直接选择debug版本就好，选择完成后会重新创建，这里先选择flavorsdemo1Debug，等待build完成后直接运行。安装完成后再选择flavorsdemo2Debug版本，等待build，安装。见证奇迹的时候到了：
 
 ![构建变种成功安装](http://qiniu.fullscreendeveloper.cn/%E6%9E%84%E5%BB%BA%E5%8F%98%E7%A7%8D-4.png)
 
 可以看到，变种1和变种2都安装成功了，而且图标都不一样。这时候可能会有人问：包名呢？来来，使用ADM来看看正在运行中的程序：
 
 ![查看两个变种的包名](http://qiniu.fullscreendeveloper.cn/%E6%9E%84%E5%BB%BA%E5%8F%98%E7%A7%8D-5.png)
 
 看，demo1和demo2都在，而且和我们配置的一毛一样呀，看到这里，有没有朋友想到更广泛的用途呢？
 
 到这里，构建变种的基础就算完成，接下来就是配置不同的源码和资源文件。
 

### 2.源码配置
#### 业务场景
1. 构建多个APP，那么其中的API的地址肯定是不一样的对吧，我的做法是将API存放在一个全局的常亮类中，那么久可以将它抽取出来，为不同的变种配置不同的地址。
2. 权限控制，页面跳转控制。说的庸俗一点，同一个APP，有一个功能模块A是后来开发的，而前面有些客户没有给这个功能模块A的钱，那肯定是不能给他看的呀，所以可以在一个类中加个flag来控制显示与隐藏功能。
3. 反正呢，就是用来配置少量不同源码，大多数源码是应该写在main下，变种只用来做少量配置。

#### 实践
在这我只实现第一条，一法通百法通，其它都是一样的。
1. 首先要使用project视图，这样才能完全的看到整个项目结构，然后在src下(和main同级)创建和变种名字相同的目录.以下：

![构建变种，创建源码目录](http://qiniu.fullscreendeveloper.cn/%E6%9E%84%E5%BB%BA%E5%8F%98%E7%A7%8D-6.png)

2. 创建包
	
首先在flavorsdemo1和flavorsdemo2下创建java和res文件夹，让后再java目录下创建包，这里创建的包要和main下面的一样，而且flavorsdemo1和flavorsdemo2的目录也必须一致，还有变种中存在的类，main中是不允许存在的。

先看图，再解释：
![变种目录结构](http://qiniu.fullscreendeveloper.cn/%E6%9E%84%E5%BB%BA%E5%8F%98%E7%A7%8D-7.png)

三个源码下的包名都是一致的，在变种中增加了global包，并创建了一个全局类

flavorsdemo1 Constant

```java
	public class Constant {
    public static final String API_ADDRESS="这是变种1的API地址";
	}
```

flavorsdemo2 Constant

```java
	public class Constant {
    public static final String API_ADDRESS="这是变种2的API地址";
	}
```

两个类的名字，变量名称都是一样的，只是值不相同，接下来在MainActivity中引用

布局文件


``` xml

	<?xml version="1.0" encoding="utf-8"?>
	<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
	    xmlns:app="http://schemas.android.com/apk/res-auto"
	    xmlns:tools="http://schemas.android.com/tools"
	    android:layout_width="match_parent"
	    android:layout_height="match_parent"
	    android:orientation="vertical"
	    tools:context="com.aohanyao.product.flavorsdemos.MainActivity">
	
	    <TextView
	        android:id="@+id/tv_api"
	        android:layout_width="wrap_content"
	        android:layout_height="wrap_content"
	        android:text="Hello World!"
	        app:layout_constraintBottom_toBottomOf="parent"
	        app:layout_constraintLeft_toLeftOf="parent"
	        app:layout_constraintRight_toRightOf="parent"
	        app:layout_constraintTop_toTopOf="parent" />
	
	</LinearLayout>

``` 

MainActivity

```java	
public class MainActivity extends AppCompatActivity {

    private TextView tvApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvApi = (TextView) findViewById(R.id.tv_api);
        tvApi.setText(Constant.API_ADDRESS);
    }
}
```

直接对Constant.API_ADDRESS进行引用，来，直接启动两个变种吧。

![构建变种8-源码配置](http://qiniu.fullscreendeveloper.cn/%E6%9E%84%E5%BB%BA%E5%8F%98%E7%A7%8D8-%E6%BA%90%E7%A0%81%E9%85%8D%E7%BD%AE.png)

图中分别是flavorsdemo1和flavorsdemo1两个变种，可见，显示的都是对应的Constant.API_ADDRESS中的值，看到这里，配置不同源码部分就算是完成，所以应该发散一下思维，这可可以做到很多事情的。

### 3.资源配置
#### 业务场景
比如，变种一和变种二的某个功能模块相同，但是名字和图标不相同

	1. 大量文字变更
	2. 大量图标变更


#### 实践
替换字符串资源和图片资源

首先在两个变种的res目录下创建values目录，创建相同的资源文件fd_string.xml，各自创建相同的字符串资源名称。并在MainActivity进行了引用。

![构建变种9-配置不同的字符串资源](http://qiniu.fullscreendeveloper.cn/%E6%9E%84%E5%BB%BA%E5%8F%98%E7%A7%8D9-%E9%85%8D%E7%BD%AE%E4%B8%8D%E5%90%8C%E7%9A%84%E5%AD%97%E7%AC%A6%E4%B8%B2%E8%B5%84%E6%BA%90.png)

接下来分别启动两个变种，看看结果

![构建变种9-配置不同的字符串资源-结果](http://qiniu.fullscreendeveloper.cn/%E6%9E%84%E5%BB%BA%E5%8F%98%E7%A7%8D9-%E9%85%8D%E7%BD%AE%E4%B8%8D%E5%90%8C%E7%9A%84%E5%AD%97%E7%AC%A6%E4%B8%B2%E8%B5%84%E6%BA%90-%E7%BB%93%E6%9E%9C.png)

很明显，达到了想要的效果。

下一步就是图片资源的替换，和上面一样，创建相应的资源文件夹，放入名称一样，内容不一样的图片，并在MainActivity中引用。

![构建变种10-图片资源配置](http://qiniu.fullscreendeveloper.cn/%E6%9E%84%E5%BB%BA%E5%8F%98%E7%A7%8D10-%E5%9B%BE%E7%89%87%E8%B5%84%E6%BA%90%E9%85%8D%E7%BD%AE.png)

来看看结果

![构建变种10-图片资源配置-结果](http://qiniu.fullscreendeveloper.cn/%E6%9E%84%E5%BB%BA%E5%8F%98%E7%A7%8D10-%E5%9B%BE%E7%89%87%E8%B5%84%E6%BA%90%E9%85%8D%E7%BD%AE-%E7%BB%93%E6%9E%9C.png)

至此，利用Gradle构建变种就算是完全完成了。


### 最后


[源码地址](https://github.com/aohanyao/ProductFlavorsDemos) 
