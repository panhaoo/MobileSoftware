# 期中实验_NotePad笔记本应用

学号：116052017012
姓名：潘浩

------

## 1、成品展示：

若图片不可见，请访问https://blog.csdn.net/panhaoo/article/details/106464798

首先看成品，主界面可以看到增加了时间戳。

<img src=" http://qa81lgzxx.bkt.clouddn.com/m0.png " />

点击搜索按钮，就能展开搜索框并弹出键盘。

<img src=" http://qa81lgzxx.bkt.clouddn.com/m2.png " />

在输入内容时，会匹配可能的搜索结果，点击相关条目就能进入编辑界面。

<img src=" http://qa81lgzxx.bkt.clouddn.com/m3.png " />

点击提交，便会显示和输入内容完全匹配的内容，并关闭键盘。如果没有相关内容，则提示“未搜索到相关内容！”。

<img src=" http://qa81lgzxx.bkt.clouddn.com/m4.png " />

<img src=" http://qa81lgzxx.bkt.clouddn.com/m5.png " />

## 2、笔记时间戳功能：

​		通过对以下NotePad这段源代码的分析，不难发现NotePad的数据表里已经包括条目的创建时间和最晚更新时间。这样我们只需要修改条目的样式，并把最晚更新时间显示到对应的位置即可。

```java
@Override
       public void onCreate(SQLiteDatabase db) {
           db.execSQL("CREATE TABLE " + NotePad.Notes.TABLE_NAME + " ("
                   + NotePad.Notes._ID + " INTEGER PRIMARY KEY,"
                   + NotePad.Notes.COLUMN_NAME_TITLE + " TEXT,"
                   + NotePad.Notes.COLUMN_NAME_NOTE + " TEXT,"
                   + NotePad.Notes.COLUMN_NAME_CREATE_DATE + " INTEGER,"
                   + NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE + " INTEGER"
                   + ");");
       }
```

​		既然方向已经确定，我们先开始从修改样式开始。NotePad里的条目是用SimpleCursorAdapter产生的，SimpleCursorAdapter的样式存放在noteslist_item.xml。打开noteslist_item.xml，在原来的TextView下方新增一个用来显示日期的TextView。然后再微调一下样式，让它看起来更加赏心悦目。

```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <TextView
        android:id="@android:id/text1"
        android:layout_width="match_parent"
        android:layout_height="40dp"
        android:gravity="center_vertical"
        android:paddingLeft="5dp"
        android:text="你好"
        android:textAppearance="?android:attr/textAppearanceLarge" />

    <TextView
        android:id="@android:id/text2"
        android:layout_width="320dp"
        android:layout_height="25dp"
        android:paddingLeft="5dp"
        android:text="2020/5/24"
        android:textSize="15dp" />
 
</LinearLayout>
```

​		接下来就是去NotesList.java把最晚更新时间显示到对应的位置。先分析SimpleCursorAdapter。

```java
SimpleCursorAdapter adapter
            = new SimpleCursorAdapter(
                      this,                             // The Context for the ListView
                      R.layout.noteslist_item,          // Points to the XML for a list item
                      cursor,                           // The cursor to get items from
                      dataColumns,
                      viewIDs
              );
```

​		R.layout.noteslist_item我们已经在上面修改完成，接下来是cursor、dataColumns、viewIDs这3个部分。但是cursor如下所示：

```java
Cursor cursor = managedQuery(
            getIntent().getData(),            // Use the default content URI for the provider.
            PROJECTION,                       // Return the note ID and title for each note.
            null,                             // No where clause, return all records.
            null,                             // No where clause, therefore no where column values.
            NotePad.Notes.DEFAULT_SORT_ORDER  // Use the default sort order.
        );
```

​		所以又得分析PROJECTION，PROJECTION如下所示：

```java
private static final String[] PROJECTION = new String[] {
            NotePad.Notes._ID, // 0
            NotePad.Notes.COLUMN_NAME_TITLE, // 1
            NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE, // 2
    };
```

​		NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE是我新增的内容，包含了最晚更新时间的列名。后面注释// 2表明该列是第3列。完成这一项，cursor的内容就解决了。接下来完成dataColumns、viewIDs的内容。

```java
String[] dataColumns = { NotePad.Notes.COLUMN_NAME_TITLE, NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE };
```

​		dataColumns存放的是我们要显示在条目上的内容在cursor里对应的列名，果断加上NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE。

```java
int[] viewIDs = { android.R.id.text1, android.R.id.text2 };
```

​		viewIDs存放的是条目内容对应的样式，显示日期的TextView的id是text2，所以我们也把它加上。

​		这么一波下来，我们好像完成了？跑一下试试。

<img src=" http://qa81lgzxx.bkt.clouddn.com/m6.png " />

​		哦，该死的上帝，为什么是一串数字？赶紧去NotePad.java看源码。

```java
/**
         * Column name for the modification timestamp
         * <P>Type: INTEGER (long from System.curentTimeMillis())</P>
         */
        public static final String COLUMN_NAME_MODIFICATION_DATE = "modified";
```

​		可以看出数据库里存放的是用System.curentTimeMillis()方法获取的当前时间的秒数，因此我们还需要把这个数字转换成我们看得懂的日期。

​		注意！不要被这个可恶的INTEGER所迷惑。这里的INTEGER是数据库里的对应数据类型，而不是JAVA里的int类型。简而言之，我们要用JAVA里的long类型来接受数据库里INTEGER类型。不然你就会因为溢出穿越到1975年。

​		回归正题。在通过大量资料查阅后，终于发现我们可以修改SimpleCursorAdapter中指定列的显示内容。具体方法如下：

```Java
adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int column) {
                if( column == 2 ){
                    TextView time = (TextView) view;
                    Calendar c = Calendar.getInstance();
                    long millions = cursor.getLong(cursor.getColumnIndex(NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE));
                    c.setTimeInMillis(millions);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    time.setText(sdf.format(c.getTime()));
                    return true;
                }
                return false;
            }
        });
```

​		将日期秒数转换为对应日期的方式较为简单，不再赘述。这里唯一要注意的是column是从0开始计数，所以虽然NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE是第3列，但column为2。在完成这一系列操作以后，时间终于恢复正常。

<img src=" http://qa81lgzxx.bkt.clouddn.com/m7.png " />

## 3、笔记查询功能：

​		首先，我们需要一个搜索按钮，那么，这个按钮放哪里比较好呢？NotesPad里用的是ListActivity，因此整个界面就是一个默认的ListView，不需要再添加布局文件。但没有布局文件也就算了，还没有办法直接添加布局。所以我可爱的同学大都把搜索图标放在最上面的TitleBar 上，或者集成在菜单里。但我就不一样了，稍稍有点强迫症的我毅然决然地给NotesList加上了布局文件noteslist.xml。

<img src=" http://qa81lgzxx.bkt.clouddn.com/m8.png " />

​		但是直接写的布局，很不出意外地报错了，因为ListActivity有默认的ListView，而布局文件里的ListView会与默认的ListView发生冲突，导致程序崩溃。要避免冲突，就必须让布局文件里的ListView和ListActivity默认的ListView是同一个ListView。通过查阅资料知道ListActivity默认的ListView的id是android:list。这样我们把布局文件里的ListView的id指定为android:list就不会出错了。当然，我们还得在这个ListView上面加入作为查询功能的SearchView。写完就是下面的样子：

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">
    <SearchView
        android:id="@+id/sv"
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />
    <ListView
        android:id="@android:id/list"
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />
</LinearLayout>

```

​		接下来就是去NotesList绑定一下布局和触发事件。

```java
final SearchView searchView = findViewById(R.id.sv);
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
        });
```

​		我们逐一分析一下我用到的方法，当然大部分看英文单词就知道是干什么的了。

​		setSubmitButtonEnabled即是否显示提交按钮。我们设置为true，就会在搜索框最右边生成一个箭头图标的提交按钮。点击这个按钮就会触发onQueryTextSubmit方法里的事件。

​		setQueryHint即搜索框的默认显示内容。

​		onQueryTextSubmit即内容提交时触发的事件，点击提交按钮就会触发onQueryTextSubmit方法里的事件。

​		onQueryTextChange即内容变更时触发的事件，与onQueryTextSubmit的不同之处在于，我们只要改变搜索框的内容，onQueryTextChange方法里的事件就会被触发。用来判断用户可能的搜索条目再合适不过了。

​		接下来就是筛选条目的内容，我们先完成onQueryTextChange方法。通过之前的cursor，我们很轻松地知道了对应的用法，我们在这里只要稍加改进即可。

```java
			@Override
            public boolean onQueryTextChange(String newText) {
                String selection = NotePad.Notes.COLUMN_NAME_TITLE + " Like ? ";
                String[] selectionArgs = { "%" + newText + "%" };
                Cursor cursor = managedQuery(
                        getIntent().getData(),
                        PROJECTION,
                        selection,
                        selectionArgs,
                        NotePad.Notes.DEFAULT_SORT_ORDER
                );
                String[] dataColumns = { NotePad.Notes.COLUMN_NAME_TITLE, NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE };
                int[] viewIDs = { android.R.id.text1 , android.R.id.text2 };
                SimpleCursorAdapter adapter
                 = new SimpleCursorAdapter(
                        getApplicationContext(),
                        R.layout.noteslist_item,
                        cursor,
                        dataColumns,
                        viewIDs
                );
                setListAdapter(adapter);
                return true;
            }
```

​		简要说明一下这里的想法，当我们在搜索框中输入内容时，在onQueryTextChange方法内获取输入内容的值，然后用"like %输入内容%"对条目进行模糊查询。再把查询到的内容放到ListView，就大功告成啦。同理，把onQueryTextSubmit也完成。

```java
			@Override
            public boolean onQueryTextSubmit(String query) {
                String selection = NotePad.Notes.COLUMN_NAME_TITLE + " Like ? ";
                String[] selectionArgs = { query };
                Cursor cursor = managedQuery(
                        getIntent().getData(),
                        PROJECTION,
                        selection,
                        selectionArgs,
                        NotePad.Notes.DEFAULT_SORT_ORDER
                );
                if(cursor.getCount() == 0){
                    Toast.makeText(getApplicationContext(), "未搜索到相关内容！", Toast.LENGTH_SHORT).show();
                }
                String[] dataColumns = { NotePad.Notes.COLUMN_NAME_TITLE, NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE };
                int[] viewIDs = { android.R.id.text1, android.R.id.text2 };
                SimpleCursorAdapter adapter
                        = new SimpleCursorAdapter(
                        getApplicationContext(),
                        R.layout.noteslist_item,
                        cursor,
                        dataColumns,
                        viewIDs
                );
                setListAdapter(adapter);
                searchView.clearFocus();
                return true;
            }
```

​		当然，和onQueryTextChange方法还是有些细微的不同。首先对于提交的内容，我们就没必要再模糊查询了，不用加上2个“%”。其次，考虑到有时候没有一个搜索结果，所以我通过cursor.getCount()方法来获取查询到的条目数量，当该值为0时，便触发Toast提示未搜索到相关内容。最后，搜索完键盘还在，有点碍眼，加上clearFocus()，在提交后收起键盘。

​		好像完成了？跑跑试试。

<img src=" http://qa81lgzxx.bkt.clouddn.com/m9.png " />

​		噢，我的上帝，又是这该死的数字！不过好在之前已经解决了这个问题，现在只需要在2个方法里都粘贴一下就行了。

```java
					adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
                    @Override
                    public boolean setViewValue(View view, Cursor cursor, int column) {
                        if( column == 2 ){
                            TextView time = (TextView) view;
                            Calendar c = Calendar.getInstance();
                            long millions = cursor.getLong(cursor.getColumnIndex(NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE));
                            c.setTimeInMillis(millions);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            time.setText(sdf.format(c.getTime()));
                            return true;
                        }
                        return false;
                    }
                });
```

​		再试一下？

<img src=" http://qa81lgzxx.bkt.clouddn.com/m10.png " />

​		大功告成！