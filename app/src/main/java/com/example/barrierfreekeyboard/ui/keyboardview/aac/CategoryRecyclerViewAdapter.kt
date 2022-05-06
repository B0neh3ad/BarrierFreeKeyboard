package com.example.barrierfreekeyboard.ui.keyboardview.aac

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputConnection
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.barrierfreekeyboard.R
import com.example.barrierfreekeyboard.model.AACCategory
import com.example.barrierfreekeyboard.ui.KeyboardConstants
import java.io.File

class CategoryRecyclerViewAdapter(
    val context: Context,
    val aacCategoryList: ArrayList<AACCategory>,
    val inputConnection: InputConnection?,
    val height: Int,
    private val onClickListener: (String) -> (Unit)): RecyclerView.Adapter<CategoryRecyclerViewAdapter.CategoryRecyclerViewHolder>()  {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryRecyclerViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.aac_category_item, parent, false)
        return CategoryRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: CategoryRecyclerViewHolder,
        position: Int
    ) {
        holder.bind(aacCategoryList[position], position, context)
    }

    override fun getItemCount(): Int {
        return aacCategoryList.size
    }

    inner class CategoryRecyclerViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!){
        private val imageView = itemView?.findViewById<ImageView>(R.id.aac_category_img)

        fun bind(aacCategory: AACCategory, position: Int, context: Context){
            Glide.with(context)
                .load(File(aacCategory.imageURI))
                .into(imageView!!)
            imageView.setOnClickListener {
                onClickListener(aacCategory.title)
                // TODO: setLayoutComponent(categoryIdx) 호출
            }

            if(position <= aacCategoryList.size) {
                val endPosition = if (position + KeyboardConstants.PRELOAD_ITEM_COUNT > aacCategoryList.size) {
                    aacCategoryList.size
                } else {
                    position + KeyboardConstants.PRELOAD_ITEM_COUNT
                }
                aacCategoryList.subList(position, endPosition).map { it.imageURI }.forEach {
                    Glide.with(context).load(it)
                        .preload()
                }
            }
        }
    }
}