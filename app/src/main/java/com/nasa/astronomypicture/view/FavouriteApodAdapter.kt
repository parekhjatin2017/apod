package com.nasa.astronomypicture.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.nasa.astronomypicture.model.ApodDataModel
import com.nasa.astronomypicture.R
import com.nasa.astronomypicture.databinding.ListItemBinding
import com.nasa.astronomypicture.viewmodel.ApodViewModel

class FavouriteApodAdapter(private val clickListener: (ApodDataModel) -> Unit) :
        RecyclerView.Adapter<MyViewHolder>() {

    private val apodDataList = ArrayList<ApodDataModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ListItemBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.list_item, parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return apodDataList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(apodDataList[position], clickListener)
    }

    fun setList(apodList: List<ApodDataModel>) {
        apodDataList.clear()
        apodDataList.addAll(apodList)

    }

}

class MyViewHolder(val binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(model: ApodDataModel, clickListener: (ApodDataModel) -> Unit) {
        ApodViewModel.loadImage(binding.image, model.url)
        binding.title.text = model.title
        binding.date.text = model.date
        binding.description.text = model.explanation
        binding.listItemLayout.setOnClickListener {
            clickListener(model)
        }
    }
}