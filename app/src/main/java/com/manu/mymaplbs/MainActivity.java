package com.manu.mymaplbs;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

import java.util.List;

/**
 * 百度地图的基本使用
 */
public class MainActivity extends AppCompatActivity{
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private EditText editText;
    private PoiSearch  poiSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.editText);

        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        //开启实时交通图
        mBaiduMap.setTrafficEnabled(true);
        //开启百度城市热力图
//        mBaiduMap.setBaiduHeatMapEnabled(true);
        //标注覆盖物
        markLatLng();
    }

    /**
     * 标注覆盖物及设置地图缩放级别
     */
    private void markLatLng() {
        //定义Marker坐标点
        LatLng point = new LatLng(40.1077760000,116.3894120000);
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions options = new MarkerOptions()
                .position(point)
                .icon(bitmap);
        //在地图上添加Marker并显示
        mBaiduMap.addOverlay(options);

        //获取地图缩放级别
        System.out.println(mBaiduMap.getMaxZoomLevel());
        System.out.println(mBaiduMap.getMinZoomLevel());
        //设置地图的缩放级别,以point点为中心进行缩放
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(point,15);
        mBaiduMap.setMapStatus(mapStatusUpdate);
    }
    /**
     * 普通地图
     * @param view
     */
    public void normal(View view){
        //设置地图类型为普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
    }
     /**
     * 卫星地图
     * @param view
     */
    public void satellite(View view){
        //设置地图类型为卫星地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
    }
     /**
     * 空白地图:基础地图瓦片将不会被渲染。在地图类型中设置为NONE，将不会使用流量下载基础地图瓦片图层。
      * 使用场景：与瓦片图层一起使用，节省流量，提升自定义瓦片图下载速度。
     * @param view
     */
    public void back(View view){
        //设置地图类型为空白地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NONE);
    }
    /**
     * POI城市检索
     * @param view
     */
    public void poiSearch(View view){
        //创建POI检索实例
        poiSearch = PoiSearch.newInstance();
        //创建POI检索监听者
        OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {
                //获取POI检索结果
                List<PoiInfo> info = poiResult.getAllPoi();
                for (PoiInfo poi : info){
                    System.out.println(poi.city+"-"+poi.name+"-"+poi.phoneNum+"-"+poi.address);
                    //在地图上标注检索结果
                    BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.icon_marker);
                    //创建MarkerOption,用于在地图上添加Marker
                    OverlayOptions options = new MarkerOptions()
                            .icon(bitmap)
                            .position(poi.location)
                            .title(poi.name);
                    //将标注图层显示在地图上
                    mBaiduMap.addOverlay(options);
                }
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
                //获取place详情页检索结果
            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
                //POI室内检索结果
            }
        };
        //设置POI检索的监听者
        poiSearch.setOnGetPoiSearchResultListener(poiListener);
        //发起检索请求
        poiSearch.searchInCity(new PoiCitySearchOption()
                .city("北京")//检索城市
                .keyword("网吧")//检索关键字
                .pageCapacity(10)//设置每页容量
                .pageNum(1));//检索结果的分页编号
        //释放POI检索实例
        poiSearch.destroy();
    }

    /**
     * POI周边搜索
     * @param view
     */
    public void poiNearSearch(View view){
        //创建POI检索实例
        poiSearch = PoiSearch.newInstance();
        //创建POI检索监听者
        OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {
                //获取POI检索结果
                List<PoiInfo> info = poiResult.getAllPoi();
                for (int i=0; i<info.size(); i++){
                    System.out.println(info.get(i).city+"-"+info.get(i).name+"-"+info.get(i).phoneNum+"-"+info.get(i).address);
                    //在地图上标注检索结果
                    BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.icon_marker);
                    //创建MarkerOption,用于在地图上添加Marker
                    OverlayOptions options = new MarkerOptions()
                            .icon(bitmap)
                            .position(info.get(i).location)
                            .title(info.get(i).name);
                    //将标注图层显示在地图上
                    mBaiduMap.addOverlay(options);
                }
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
                //获取place详情页检索结果
            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
                //POI室内检索结果
            }
        };
        //设置POI检索的监听者
        poiSearch.setOnGetPoiSearchResultListener(poiListener);
        //发起检索请求
//        LatLng point = new LatLng(40.1077760000,116.3894120000);
//        poiSearch.searchNearby(new PoiNearbySearchOption()
//                .location(point)
//                .keyword("网吧")
////                .pageNum(1)
//                .radius(2000));
        //使用地理编码发起检索请求

        //第一步：创建地址编码检索实例
        final GeoCoder geoCoder = GeoCoder.newInstance();
        //第二步：创建地理编码检索监听者
        OnGetGeoCoderResultListener resultListener = new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                if (geoCodeResult == null || geoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    //没有检索到结果
                    Toast.makeText(MainActivity.this, "没有检索到结果", Toast.LENGTH_SHORT).show();
                }
                //获取地理编码结果
                LatLng latLng = geoCodeResult.getLocation();//获得地址对应的编码（经纬度）
                System.out.println(geoCodeResult.getAddress());
                //周边检索
                if (TextUtils.isEmpty(editText.getText().toString())){
                    Toast.makeText(MainActivity.this, "请输入搜索半径", Toast.LENGTH_SHORT).show();
                    return;
                }
                int radius = Integer.parseInt(editText.getText().toString());
                poiSearch.searchNearby(new PoiNearbySearchOption()
                        .location(latLng)//具体位置
                        .keyword("网吧")//关键字
                        .radius(radius));//搜索半径
            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    //没有找到检索结果
                }
                //获取反向地理编码结果

            }
        };
        //第三步:设置地理编码检索监听者
        geoCoder.setOnGetGeoCodeResultListener(resultListener);
        //第四步：发起地理编码检索
        geoCoder.geocode(new GeoCodeOption()
                .city("北京")
                .address("顺玮阁大酒店"));
        //释放POI检索实例
        poiSearch.destroy();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }
}
