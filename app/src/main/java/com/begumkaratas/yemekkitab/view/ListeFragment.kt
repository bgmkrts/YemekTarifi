package com.begumkaratas.yemekkitab.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.begumkaratas.yemekkitab.adapter.TarifAdapter
import com.begumkaratas.yemekkitab.databinding.FragmentListeBinding
import com.begumkaratas.yemekkitab.model.Tarif
import com.begumkaratas.yemekkitab.roomdb.TarifDAO
import com.begumkaratas.yemekkitab.roomdb.TarifDatabase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers


class ListeFragment : Fragment() {
    private var _binding: FragmentListeBinding? = null
    private val binding get() = _binding!!

    private  val mDisposable= CompositeDisposable()

    private lateinit var db: TarifDatabase
    private lateinit var tarifDao: TarifDAO


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db= Room.databaseBuilder(requireContext(),TarifDatabase::class.java,"Tarifler")
            // .allowMainThreadQueries()
            .build()
        tarifDao = db.tarifDao()


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.floatingActionButton.setOnClickListener{yeniEkle(it)}
        binding.tarifRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        verileriAl()

    }
   private fun yeniEkle(view: View){
        val action=ListeFragmentDirections.actionListeFragmentToTarifFragment(bilgi="yeni",id=0)
        Navigation.findNavController(view).navigate(action)




    }
    private fun verileriAl(){
        mDisposable.add(
            tarifDao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn( AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse)


        )
    }
    private fun handleResponse(tarifler: List<Tarif>){
       /*tarifler.forEach{
            println(it.isim)
            println(it.malzeme)
        }*/
        val adapter=TarifAdapter(tarifler)
        binding.tarifRecyclerView.adapter=adapter


    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mDisposable.clear()

    }

}


