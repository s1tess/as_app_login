package com.example.hello;

import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.core.view.GestureDetectorCompat;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Callback;
import java.io.IOException;

public class VideoPreviewFragment extends Fragment {

    private VideoView videoView;
    private int currentVideoIndex = 0;
    private GestureDetectorCompat gestureDetector;
    private Random random = new Random();
    private List<String> videoUrls = new ArrayList<>();
    private static final String API_URL = "http://112.124.22.157/video/get_videos.php";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.video, container, false);

        videoView = view.findViewById(R.id.videoView);

        // 初始化手势检测器
        gestureDetector = new GestureDetectorCompat(getActivity(), new GestureListener());

        // 设置触摸监听器
        videoView.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return true;
        });

        // 设置点击事件用于播放/暂停
        videoView.setOnClickListener(v -> {
            if (videoView.isPlaying()) {
                videoView.pause();
            } else {
                videoView.start();
            }
        });

        // 视频播放完成监听器
        videoView.setOnCompletionListener(mp -> {
            if (!videoUrls.isEmpty()) {
                currentVideoIndex = random.nextInt(videoUrls.size());
                setVideoSource(currentVideoIndex);
            }
        });

        fetchVideoUrls();
        return view;
    }

    private void fetchVideoUrls() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(API_URL)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                getActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "获取视频列表失败", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String jsonData = response.body().string();
                        JSONObject jsonObject = new JSONObject(jsonData);

                        if (jsonObject.getInt("code") == 200) {
                            JSONArray videos = jsonObject.getJSONArray("data");
                            videoUrls.clear();

                            for (int i = 0; i < videos.length(); i++) {
                                videoUrls.add(videos.getString(i));
                            }

                            getActivity().runOnUiThread(() -> {
                                if (!videoUrls.isEmpty()) {
                                    currentVideoIndex = random.nextInt(videoUrls.size());
                                    setVideoSource(currentVideoIndex);
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void setVideoSource(int index) {
        if (index >= 0 && index < videoUrls.size()) {
            Uri videoUri = Uri.parse(videoUrls.get(index));
            videoView.setVideoURI(videoUri);
            videoView.setOnPreparedListener(mp -> videoView.start());
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float diffY = e1.getY() - e2.getY();

            if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (!videoUrls.isEmpty()) {
                    currentVideoIndex = random.nextInt(videoUrls.size());
                    setVideoSource(currentVideoIndex);
                }
                return true;
            }
            return false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (videoView != null && videoView.isPlaying()) {
            videoView.pause();
        }
    }
}