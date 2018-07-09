# TableLayout
配合ViewPager滑动的简单Tab标签页
### 描述
    一个配合ViewPager滑动的简单Tab标签,Tab标签较少时可以使用.
### Demo
   ![](https://github.com/MyylxWF/TableLayout/blob/master/tab_img.gif)
### 使用
#### 直接复制TableLayout.java到你的自己定义View目录下，按照如下步骤使用

   * XML
```Xml   
        <控件目录.TableLayout  
               android:id="@+id/tab_layout"  
               android:layout_width="match_parent"  
               android:layout_height="wrap_content"/>  
```            
   * JAVA 
   
          关联标签和ViewPager,这一步必须有
```Java   
        String[] tabs = new String[]{"发布的话题", "参与的话题", "收藏的帖子"};
        
        tabLayout.setViewAndTab(ViewPager vp,String[]tabs);
```
   * 下划线的宽度类型  
```Java    
          setLineType（int lineType） 
            
          //字体宽度
          TableLayout.LINE_TYPE_TEXTLENGTH
          //屏幕平分宽度
          TableLayout.LINE_TYPE_EQUAL
 ```   
   * 字体颜色和下划线颜色
 ```Java        
          //修改下划线颜色  
          setLineColor(0xFFD0AD61);  
          //修改选中字体颜色
          setTextSelectColor(0xFFD0AD61);
 ```           
              
