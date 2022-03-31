package com.example.barrierfreekeyboard.ui.keyboardview.aac

import android.content.Context
import android.hardware.display.DisplayManager
import android.util.DisplayMetrics
import android.view.*
import android.view.inputmethod.InputConnection
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.barrierfreekeyboard.R
import com.example.barrierfreekeyboard.model.AACSymbol
import timber.log.Timber
import java.io.File

class SymbolRecyclerViewAdapter(val context: Context, val aacList: ArrayList<AACSymbol>, val inputConnection: InputConnection): RecyclerView.Adapter<SymbolRecyclerViewAdapter.SymbolRecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SymbolRecyclerViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.aac_symbol_item, parent, false)
        return SymbolRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: SymbolRecyclerViewHolder, position: Int) {
        holder.bind(aacList[position], context)
    }

    override fun getItemCount(): Int {
        return aacList.size
    }

    inner class SymbolRecyclerViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        private val imageView = itemView?.findViewById<ImageView>(R.id.aac_symbol_img)

        fun bind(aacSymbol: AACSymbol, context: Context){
            Glide.with(context)
                .load(File(aacSymbol.imageURI))
                .into(imageView!!)
            imageView.setOnClickListener{
                inputConnection.commitText(aacSymbol.text, 1)
            }
        }
    }
}