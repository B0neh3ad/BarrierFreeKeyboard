package com.example.barrierfreekeyboard.ui.keyboardview.aac

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputConnection
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.barrierfreekeyboard.R
import com.example.barrierfreekeyboard.model.AACCategory

class CategoryRecyclerViewAdapter(val context: Context, val aacCategoryList: ArrayList<AACCategory>, val inputConnection: InputConnection, val height: Int): RecyclerView.Adapter<CategoryRecyclerViewAdapter.CategoryRecyclerViewHolder>()  {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryRecyclerViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.aac_symbol_item, parent, false)
        return CategoryRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: CategoryRecyclerViewHolder,
        position: Int
    ) {
        holder.bind(aacCategoryList[position], context)
    }

    override fun getItemCount(): Int {
        return aacCategoryList.size
    }

    inner class CategoryRecyclerViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!){
        private val imageView = itemView?.findViewById<ImageView>(R.id.aac_category_img)

        fun bind(aacCategory: AACCategory, context: Context){
            imageView?.setImageURI(aacCategory.imageURI)
            imageView?.setOnClickListener {
                // TODO: setLayoutComponent(categoryIdx) 호출
            }
        }
    }
}