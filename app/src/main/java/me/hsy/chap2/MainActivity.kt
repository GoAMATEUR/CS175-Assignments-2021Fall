package me.hsy.chap2


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Data Preparation
        var json : String? = null
        val items = arrayListOf<ItemTemplate>()

        try {
            val inputStream:InputStream = assets.open("pet_data.json")
            json = inputStream.bufferedReader().use{it.readText()}

            var jsonarr = JSONObject(json).getJSONArray("pets")
            for (i in 0..jsonarr.length()-1){
                var jsonobj = jsonarr.getJSONObject(i)
                var id: Int = jsonobj.getInt("id")
                items.add(ItemTemplate(jsonobj.getInt("id"), jsonobj.getString("title"), jsonobj.getString("description"), jsonobj.getString("gender"), jsonobj.getString("location"),jsonobj.getString("breed"), jsonobj.getString("age")))
            }
        }
        catch(e : IOException){
            Log.e("IO", "Json Read Failure")
        }


        val list = findViewById<RecyclerView>(R.id.list)
        val adapter = SearchAdapter()

        list.adapter = adapter
        list.layoutManager = LinearLayoutManager(this)
        adapter.updateItems(items)

        var thisContext = this
        adapter.setItemClickListener(object:SearchAdapter.ItemClickListener{
            override fun onItemClick(position: Int) {
                var no: Int = position + 1
                Log.d("@=>","onClick: ITEM $no")

                val intent = Intent(thisContext, DetailActivity::class.java)
                intent.putExtra("No", no)
                startActivity(intent)
            }
        })

        val cancel = findViewById<TextView>(R.id.cancel)
        cancel.setOnClickListener{
            findViewById<EditText>(R.id.edit).setText("")
        }

        findViewById<SearchEditText>(R.id.search_edit).setTextChangedListener(object :
            SearchEditText.Listener {
            override fun onChanged(text: String) {
                Log.d("@=>","onTextChanged: $text")
                val filters = items.filter{
                    it.getTitle().contains(text)
                }
                adapter.updateItems(filters)
            }
        })
    }
}