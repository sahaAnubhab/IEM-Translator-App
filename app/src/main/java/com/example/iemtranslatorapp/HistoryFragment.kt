package com.example.iemtranslatorapp
import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.iemtranslatorapp.databinding.FragmentHistoryBinding
import com.example.iemtranslatorapp.model.DatabaseHelper
import com.example.iemtranslatorapp.model.History

class HistoryFragment() : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    // TODO: Rename and change types of parameters
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HistoryAdapter
    private lateinit var databaseHelper: DatabaseHelper
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        databaseHelper = DatabaseHelper(requireContext())
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        recyclerView = binding.historyRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = HistoryAdapter(getHistory())
        recyclerView.adapter = adapter
        binding.clearbtn.setOnClickListener{
            databaseHelper.deleteAll()
        }
        return binding.root
    }

    private fun getHistory(): List<History> {
        val historyList = mutableListOf<History>()
        val db = databaseHelper.readableDatabase
        val cursor: Cursor? = db.rawQuery("SELECT translation, source, timestamp FROM history", null)
        cursor?.let {
            if (cursor.moveToFirst()) {
                do {
//                    Toast.makeText(context, "Hi", Toast.LENGTH_SHORT).show()

                    val index1 = cursor.getColumnIndex("translation")
                    val index2 = cursor.getColumnIndex("source")
                    val index3 = cursor.getColumnIndex("timestamp")

                    if(index1 != -1 && index2 != -1) {
                        val translation =
                            cursor.getString(index1)
                        val source = cursor.getString(index2)
                        val timestamp = cursor.getString(index3)
                        val history=History(translation,source,timestamp)
                        historyList.add(History(source, translation, timestamp))
                    }
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
        db.close()
        return historyList
    }
}