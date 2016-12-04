package com.example.yao.demotest;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private VideoView videoView;
    private MyAdapter adapter;
    private RecyclerView recyclerView;
    private final String url = "http://newapi.meipai.com/output/channels_topics_timeline.json?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //控件初始化
        //videoview初始化，关联Mediacontroller（进度条）
        videoView = (VideoView) findViewById(R.id.videoview);
        final MediaController me = new MediaController(this);
        videoView.setMediaController(new MediaController(this));
        me.setMediaPlayer(videoView);
        //recycleview初始化，设置布局方式
        recyclerView = (RecyclerView) findViewById(R.id.recycleview);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));//瀑布流布局
        recyclerView.setItemAnimator(new DefaultItemAnimator());//动画效果

        //网络解析数据，获得数据内容
        startData();


    }

    /**
     * item的监听事件,在舰艇时间里，获取所需要展示的视频
     */
    private void setlistener() {
        adapter.setmOnItemClickListener(new MyAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, DataBean dataBean) {
                String url = dataBean.getUrl();//得到url
                getMp4Path(url);//解析得到的url，从而获得播放地址
            }
        });
    }

    /**
     * 网络解析数据，获得数据内容
     */
    private void startData() {
        RequestParams params = new RequestParams(); //网络访问必须要的参数
        params.put("id", "16");
        AsyncHttpClient as = new AsyncHttpClient();
        as.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, org.apache.http.Header[] headers, byte[] bytes) {
                String data = new String(bytes);//必须将byte类型转换为String类型，不然的话无法解析，因为没办法识别byte
                Gson gson = new Gson();//谷歌解析，新建个对象
                List<DataBean> dataBeen = gson.fromJson(data,new TypeToken<List<DataBean>>(){}.getType());//解析得到所需要的数据，用List<DataBean>接收
                Message message = handler.obtainMessage();
                message.what = 1;
                message.obj = dataBeen;
                handler.sendMessage(message);
            }

            @Override
            public void onFailure(int i, org.apache.http.Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    List<DataBean> dataBeen = (List<DataBean>) msg.obj;
                    adapter = new MyAdapter(MainActivity.this,dataBeen);//adapter建好后传入recycleview展示
                   recyclerView.setAdapter(adapter);//recycleview展示效果

                    //item的监听事件
                    setlistener();
                    break;
                case 3:
                    String mp4 = (String) msg.obj; //接收到播放地址的string类型
                    Uri uri = Uri.parse(mp4); //将string类型转化为uri类型，从而让videoview识别
                    //开始播放
                    videoView.setVideoURI(uri);
                    videoView.requestFocus();
                    videoView.start();
                    break;
            }
        }
    };

    /**
     * 传入网页的url，通过jsoup解析网页，获得播放视频的地址
     * @param s
     */
    private void getMp4Path(final String s) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Document doc = null;
                try {
                    doc = Jsoup.connect(s).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Element mp4path = doc.head();
                String content = mp4path.select("meta[property=\"og:video:url\"]").attr("content");

                Message message = handler.obtainMessage();
                message.obj = content;
                message.what = 3;
                handler.sendMessage(message);

            }
        }).start();
    }
}
