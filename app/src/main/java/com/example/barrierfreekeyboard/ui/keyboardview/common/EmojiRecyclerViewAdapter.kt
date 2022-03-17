package com.example.barrierfreekeyboard.ui.keyboardview.common

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputConnection
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.barrierfreekeyboard.R

class EmojiRecyclerViewAdapter(val context: Context, val emojiList: ArrayList<String>, val inputConnection: InputConnection): RecyclerView.Adapter<EmojiRecyclerViewAdapter.EmojiRecyclerViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmojiRecyclerViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.emoji_item, parent, false)
        return EmojiRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmojiRecyclerViewHolder, position: Int) {
        holder.bind(emojiList[position], context)
    }

    override fun getItemCount(): Int {
        return emojiList.size
    }

    inner class EmojiRecyclerViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        private val textView = itemView?.findViewById<TextView>(R.id.emoji_text)

        fun bind(emoji: String, context: Context) {
            textView?.text = emoji
            textView?.setOnClickListener {
                inputConnection.commitText((it as TextView).text.toString(), 1)
            }
        }
    }
}