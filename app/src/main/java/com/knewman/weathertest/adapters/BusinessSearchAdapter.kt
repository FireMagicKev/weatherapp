//package com.knewman.weathertest.adapters
//
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.knewman.weathertest.databinding.ItemBusinessSearchBinding
//import com.knewman.weathertest.models.Business
//import com.knewman.weathertest.ui.list.Listener
//
//class BusinessSearchAdapter(val listener: Listener): RecyclerView.Adapter<BusinessSearchAdapter.ViewHolder>() {
//    var businesses: List<Business> = emptyList()
//        set(value) {
//            field = value
//            notifyDataSetChanged()
//        }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val layoutInflater = LayoutInflater.from(parent.context)
//        val binding = ItemBusinessSearchBinding.inflate(layoutInflater, parent, false)
//        return ViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
//        holder.bind(businesses[position])
//
//    override fun getItemCount() = businesses.size
//
//    inner class ViewHolder(private val binding: ItemBusinessSearchBinding): RecyclerView.ViewHolder(binding.root) {
//        fun bind(business: Business) {
//            binding.business = business
//            binding.root.setOnClickListener {
//                listener.onBusinessSelected(business)
//            }
//        }
//    }
//}