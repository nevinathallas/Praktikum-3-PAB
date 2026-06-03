package com.example.praktikum3

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.widget.*
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import java.util.HashMap

class SecondActivity : Activity() {

    private lateinit var listView: ListView
    private lateinit var btnTambah: Button
    private lateinit var adapter: ArrayAdapter<String>
    private var mahasiswaList = ArrayList<Mahasiswa>()

    private val baseUrl = "http://10.0.2.2/praktikum3"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.second_activity)

        listView = findViewById(R.id.list_mhs)
        btnTambah = findViewById(R.id.btn_tambah)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ArrayList<String>())
        listView.adapter = adapter

        btnTambah.setOnClickListener {
            showInputDialog(null)
        }

        listView.setOnItemLongClickListener { _, _, position, _ ->
            val mhs = mahasiswaList[position]
            showOptionsDialog(mhs)
            true
        }

        loadData()
    }

    private fun loadData() {
        val queue = Volley.newRequestQueue(this)
        val request = StringRequest(Request.Method.GET, "$baseUrl/read.php",
            { response ->
                try {
                    val jsonArray = JSONArray(response)
                    mahasiswaList.clear()
                    val displayList = ArrayList<String>()

                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val mhs = Mahasiswa(
                            id = jsonObject.getInt("id"),
                            nama = jsonObject.getString("nama"),
                            nim = jsonObject.getString("nim"),
                            jurusan = jsonObject.getString("jurusan")
                        )
                        mahasiswaList.add(mhs)
                        displayList.add("${mhs.nama} - ${mhs.nim} - ${mhs.jurusan}")
                    }

                    adapter.clear()
                    adapter.addAll(displayList)
                    adapter.notifyDataSetChanged()

                } catch (e: Exception) {
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Gagal Memuat Data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )
        queue.add(request)
    }

    private fun showInputDialog(mhs: Mahasiswa?) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_mahasiswa, null)
        val etNama = dialogView.findViewById<EditText>(R.id.et_nama)
        val etNim = dialogView.findViewById<EditText>(R.id.et_nim)
        val etJurusan = dialogView.findViewById<EditText>(R.id.et_jurusan)

        if (mhs != null) {
            etNama.setText(mhs.nama)
            etNim.setText(mhs.nim)
            etJurusan.setText(mhs.jurusan)
        }

        AlertDialog.Builder(this)
            .setTitle(if (mhs == null) "Tambah Mahasiswa" else "Edit Mahasiswa")
            .setView(dialogView)
            .setPositiveButton("Simpan") { _, _ ->
                val nama = etNama.text.toString()
                val nim = etNim.text.toString()
                val jurusan = etJurusan.text.toString()

                if (mhs == null) {
                    createData(nama, nim, jurusan)
                } else {
                    updateData(mhs.id, nama, nim, jurusan)
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun showOptionsDialog(mhs: Mahasiswa) {
        val options = arrayOf("Edit", "Hapus")
        AlertDialog.Builder(this)
            .setTitle(mhs.nama)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showInputDialog(mhs)
                    1 -> deleteData(mhs.id)
                }
            }
            .show()
    }

    private fun createData(nama: String, nim: String, jurusan: String) {
        val queue = Volley.newRequestQueue(this)
        val request = object : StringRequest(Request.Method.POST, "$baseUrl/create.php",
            { response ->
                try {
                    val json = JSONObject(response)
                    Toast.makeText(this, json.getString("message"), Toast.LENGTH_SHORT).show()
                    loadData()
                } catch (e: Exception) {
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Gagal: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["nama"] = nama
                params["nim"] = nim
                params["jurusan"] = jurusan
                return params
            }
        }
        queue.add(request)
    }

    private fun updateData(id: Int, nama: String, nim: String, jurusan: String) {
        val queue = Volley.newRequestQueue(this)
        val request = object : StringRequest(Request.Method.POST, "$baseUrl/update.php",
            { response ->
                try {
                    val json = JSONObject(response)
                    Toast.makeText(this, json.getString("message"), Toast.LENGTH_SHORT).show()
                    loadData()
                } catch (e: Exception) {
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Gagal: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["id"] = id.toString()
                params["nama"] = nama
                params["nim"] = nim
                params["jurusan"] = jurusan
                return params
            }
        }
        queue.add(request)
    }

    private fun deleteData(id: Int) {
        val queue = Volley.newRequestQueue(this)
        val request = StringRequest(Request.Method.GET, "$baseUrl/delete.php?id=$id",
            { response ->
                try {
                    val json = JSONObject(response)
                    Toast.makeText(this, json.getString("message"), Toast.LENGTH_SHORT).show()
                    loadData()
                } catch (e: Exception) {
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Gagal: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )
        queue.add(request)
    }
}
