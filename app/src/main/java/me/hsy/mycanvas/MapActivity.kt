package me.hsy.mycanvas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.baidu.mapapi.SDKInitializer
import com.baidu.mapapi.map.*
import com.baidu.mapapi.model.LatLng

class MapActivity : AppCompatActivity() {
    var mapView: MapView? = null
    var mBaiduMap: BaiduMap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SDKInitializer.initialize(applicationContext)
        setContentView(R.layout.activity_map)

        mapView = findViewById(R.id.map_view)
        mBaiduMap = mapView?.map

        val point: LatLng = LatLng(31.027947,121.444687)
        val bitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_mark)
        val options: OverlayOptions = MarkerOptions()
            .position(point)
            .icon(bitmap)
            .anchor(1f, 100f)
        mBaiduMap!!.addOverlay(options)
        var status = MapStatusUpdateFactory.newLatLng(point)

        mBaiduMap!!.setMapStatus(status)
        status = MapStatusUpdateFactory.zoomTo(18f)
        mBaiduMap!!.setMapStatus(status)

    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy();
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }
}