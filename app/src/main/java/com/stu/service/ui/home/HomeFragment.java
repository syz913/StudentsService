package com.stu.service.ui.home;

import android.Manifest;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.stu.service.R;
import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.just.agentweb.AgentWeb;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import static android.content.Context.LOCATION_SERVICE;

public class HomeFragment extends Fragment {

    private AgentWeb mAgentWeb;
    private String url = "https://zlapp.fudan.edu.cn/site/ncov/fudanDaily";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestLocation();
        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent((LinearLayout) view, new LinearLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()
                .createAgentWeb()
                .ready()
                .go(url);
    }
    private void requestLocation() {
        LocationManager lm = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        boolean ok = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (ok) {
            if (PermissionsUtil.hasPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                //有获取地理位置的权限
            } else {
                PermissionsUtil.requestPermission(getActivity(), new PermissionListener() {
                    @Override
                    public void permissionGranted(@NonNull String[] permissions) {
                        //用户授予权限
                    }

                    @Override
                    public void permissionDenied(@NonNull String[] permissions) {
                        //用户拒绝了权限申请
                    }
                }, new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
            }
        } else {
            Log.e("BRG", "系统检测到未开启GPS定位服务");
            Toast.makeText(getActivity(), "系统检测到未开启GPS定位服务", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 1315);
        }
    }
}
