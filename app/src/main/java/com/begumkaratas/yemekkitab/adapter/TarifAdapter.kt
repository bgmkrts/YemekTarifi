package com.begumkaratas.yemekkitab.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.begumkaratas.yemekkitab.databinding.RecyclerRowBinding
import com.begumkaratas.yemekkitab.model.Tarif
import com.begumkaratas.yemekkitab.view.ListeFragmentDirections

class TarifAdapter(val tarifListesi: List<Tarif>) : RecyclerView.Adapter<TarifAdapter.tarifHolder>() {
    class tarifHolder(val recyclerRowBinding: RecyclerRowBinding) : RecyclerView.ViewHolder(recyclerRowBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): tarifHolder {
        val recyclerRowBinding: RecyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return tarifHolder(recyclerRowBinding)
    }

    override fun getItemCount(): Int {
        return tarifListesi.size
    }

    override fun onBindViewHolder(holder: tarifHolder, position: Int) {
holder.recyclerRowBinding.tarifRecyclerView.text=tarifListesi[position].isim
        holder.itemView.setOnClickListener{
            val action=ListeFragmentDirections.actionListeFragmentToTarifFragment(bilgi="eski",id=tarifListesi[position].id)
            Navigation.findNavController(it).navigate(action)
    }

}
}