    package com.app.contactlistapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.contactlistapplication.R
import com.app.contactlistapplication.model.Content
import com.app.contactlistapplication.utils.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class ContactAdapter(private val context: Context,
                     private val onCellClickListener: OnCellClickListener,
                     private val onItemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<ContactAdapter.MyViewHolder>() {

    var contactList : List<Content> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_row, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = contactList[position]
        val name = contactList.get(position).name
        val phone = contactList.get(position).phone
        holder.tvName.text = name
        holder.tvMobile.text = phone

        Glide.with(context).load(Utils.BASE_URL + contactList.get(position).thumbnail)
                .apply(RequestOptions().centerCrop())
                .circleCrop()
                .into(holder.image)

        if(contactList[position].isStarred == 1){
            holder.fav_image.setBackgroundResource(R.drawable.ic_baseline_favorite);
        }else{
            holder.fav_image.setBackgroundResource(R.drawable.ic_baseline_favorite_grey);
        }

        holder.fav_image.setOnClickListener{
            if (onItemClickListener != null) {
                val position: Int = holder.adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener.onItemClick(position,data)
                    if(data.isStarred == 1){
                        holder.fav_image.setBackgroundResource(R.drawable.ic_baseline_favorite);
                    }else if(data.isStarred == 0){
                        holder.fav_image.setBackgroundResource(R.drawable.ic_baseline_favorite_grey);
                    }
                }
            }
        }

        holder.itemView.setOnClickListener{
            val position: Int = holder.adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onCellClickListener.onCellClickListener(position,data)
            }
        }
    }

   class MyViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {

       val tvName: TextView = itemView!!.findViewById(R.id.tvName)
       val tvMobile: TextView = itemView!!.findViewById(R.id.tvMobile)
       val image: ImageView = itemView!!.findViewById(R.id.image)
       val fav_image: ImageView = itemView!!.findViewById(R.id.favourite)

   }

    fun setContactList(contactList: ArrayList<Content>){
        this.contactList = contactList;
        notifyDataSetChanged()
    }

    interface OnCellClickListener {
        fun onCellClickListener(position: Int,data: Content)
    }

    interface OnItemClickListener {
          fun onItemClick(position: Int,data: Content)
    }

}

