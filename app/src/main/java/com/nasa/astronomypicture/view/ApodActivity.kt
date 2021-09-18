package com.nasa.astronomypicture.view

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.CalendarView.OnDateChangeListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nasa.astronomypicture.model.ApodDataModel
import com.nasa.astronomypicture.model.NasaRepository
import com.nasa.astronomypicture.model.api.ApodRestService
import com.nasa.astronomypicture.model.api.RetrofitInstance
import com.nasa.astronomypicture.model.db.NasaDatabase
import com.nasa.astronomypicture.R
import com.nasa.astronomypicture.databinding.ActivityMainBinding
import com.nasa.astronomypicture.viewmodel.ApodViewModel
import com.nasa.astronomypicture.viewmodel.NasaViewModelFactory
import java.text.SimpleDateFormat
import java.util.*


class ApodActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var apodViewModel: ApodViewModel
    private lateinit var adapter: FavouriteApodAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val retService = RetrofitInstance
            .getInstance(this)
            .create(ApodRestService::class.java)

        val dao = NasaDatabase.getInstance(application).apodDao
        val repository = NasaRepository(dao, retService)
        val factory = NasaViewModelFactory(repository)
        apodViewModel = ViewModelProvider(this, factory).get(ApodViewModel::class.java)
        binding.apodViewModel = apodViewModel
        binding.lifecycleOwner = this

        apodViewModel.message.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        })
        binding.description.movementMethod = ScrollingMovementMethod()
        apodViewModel.isFavourite.observe(this, Observer {
            val res = if(it == null || !it) R.drawable.favorite_unmark else R.drawable.favorite_mark
            binding.favourite.setImageResource(res)
            hideAll()
        })
        binding.apodVideo.settings.javaScriptEnabled = true
        apodViewModel.isVideo.observe(this, {
            if(it){
                binding.apodImage.visibility = View.INVISIBLE
                binding.apodVideo.visibility = View.VISIBLE
                apodViewModel.currentApodDataModel.value?.url?.let {
                    binding.apodVideo.loadUrl(it)
                }
            }else{
                binding.apodImage.visibility = View.VISIBLE
                binding.apodVideo.visibility = View.INVISIBLE
            }
        })
        initRecyclerView()

        apodViewModel.getApod(getToday())
        binding.calendarView
            .setOnDateChangeListener { _, year, month, dayOfMonth ->
                val date = year.toString() + "-" + (month + 1) + "-" + dayOfMonth
                apodViewModel.getApod(date)
                hideAll()
            }
    }

    private fun initRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = FavouriteApodAdapter { selectedItem: ApodDataModel, action : ACTION ->
            if(action == ACTION.ITEM_CLICK){
                apodViewModel.getApodOnID(selectedItem.id)
            }else if(action == ACTION.ITEM_DELETE){
                apodViewModel.unCheckFavourite(selectedItem)
            }
        }
        binding.recyclerView.adapter = adapter
        displayFavouriteList()
    }

    private fun displayFavouriteList() {
        apodViewModel.getAllFavourites().observe(this, Observer {
            adapter.setList(it)
            adapter.notifyDataSetChanged()
        })
    }

    fun calender(view : View){
        binding.calenderParent.visibility = if(binding.calenderParent.visibility == View.VISIBLE) View.INVISIBLE else View.VISIBLE
        binding.listParent.visibility = View.INVISIBLE
    }

    fun favlist(view : View){
        binding.listParent.visibility = if(binding.listParent.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        binding.calenderParent.visibility = View.INVISIBLE
    }

    fun clearAll(view : View){
        apodViewModel.clearAll()
        hideAll()
    }

    private fun getToday() : String{
        return SimpleDateFormat("yyyy-MM-dd").format(Date())
    }

    private fun hideAll(){
        binding.listParent.visibility = View.INVISIBLE
        binding.calenderParent.visibility = View.INVISIBLE
    }
}