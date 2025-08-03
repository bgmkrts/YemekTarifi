package com.begumkaratas.yemekkitab.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.room.Room
import com.begumkaratas.yemekkitab.databinding.FragmentTarifBinding
import com.begumkaratas.yemekkitab.model.Tarif
import com.begumkaratas.yemekkitab.roomdb.TarifDAO
import com.begumkaratas.yemekkitab.roomdb.TarifDatabase
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.ByteArrayOutputStream
import java.io.IOException

class TarifFragment : Fragment() {

    private var _binding: FragmentTarifBinding? = null
    private val binding get() = _binding!!

    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    private var secilenGorsel: Uri? = null
    private var secilenBitMap: Bitmap? = null


    private  val mDisposable=CompositeDisposable()
    private var tarifFromListe : Tarif? = null

    private lateinit var db: TarifDatabase
    private lateinit var tarifDao:TarifDAO


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerLauncher()

        db=Room.databaseBuilder(requireContext(),TarifDatabase::class.java,"Tarifler")
           // .allowMainThreadQueries()
            .build()
        tarifDao=db. TarifDAO()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTarifBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageView.setOnClickListener { gorselSec(it) }
        binding.KaydetButton.setOnClickListener { kaydet() }
        binding.silButton.setOnClickListener { sil(it) }

        arguments?.let {
            val bilgi = TarifFragmentArgs.fromBundle(it).bilgi
            if (bilgi == "yeni") {
                binding.silButton.isEnabled = false
                binding.KaydetButton.isEnabled = true
                binding.editText.setText("")
            } else {
                binding.silButton.isEnabled = true
                binding.KaydetButton.isEnabled = false
                val id=TarifFragmentArgs.fromBundle(it).id

                tarifDao.findById(id)
            }
        }
    }

    private fun kaydet() {
        val isim=binding.editText.text.toString()
        val malzeme=binding.malzemeText.text.toString()

        //val kucukBitmapOlustur=kucukBitmapOlustur(secilenBitMap,300)

        if(secilenBitMap!=null){
            val kucukBitmap=kucukBitmapOlustur(secilenBitMap!!,300)
            val outputStream=ByteArrayOutputStream()
            kucukBitmap.compress(Bitmap.CompressFormat.PNG,50, outputStream)
            val byteDizisi=outputStream.toByteArray()

            val tarif = Tarif(isim = isim, malzeme = malzeme, gorsel = byteDizisi)

//rxjava
            mDisposable.add(
            tarifDao.insert(tarif)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponseForInsert)

            )

        }
    }
private fun handleResponseForInsert(){
//bir önceki fragmente dön
    val action=TarifFragmentDirections.actionTarifFragmentToListeFragment()
    Navigation.findNavController(requireView()).navigate(action)


}private fun handleResponseForInsertionAndDeletion() {
        val action = TarifFragmentDirections.actionTarifFragmentToListeFragment()
        Navigation.findNavController(requireView()).navigate(action)

    }

     fun sil(view: View) {
        tarifFromListe?.let {
            mDisposable.add(
                tarifDao.delete(tarifFromListe!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handleResponseForInsertionAndDeletion)
            )
        }
    }

    private fun gorselSec(view: View) {
        val izin = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(requireContext(), izin) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), izin)) {
                Snackbar.make(view, "Galeriye ulaşıp görsel seçmemiz lazım", Snackbar.LENGTH_INDEFINITE)
                    .setAction("İzin ver") {
                        permissionLauncher.launch(izin)
                    }.show()
            } else {
                permissionLauncher.launch(izin)
            }
        } else {
            galeriyeGit()
        }
    }

    private fun galeriyeGit() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activityResultLauncher.launch(intent)
    }

    private fun registerLauncher() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == AppCompatActivity.RESULT_OK) {
                    val intentData = result.data
                    secilenGorsel = intentData?.data

                    try {
                        secilenGorsel?.let { uri ->
                            secilenBitMap = if (Build.VERSION.SDK_INT >= 28) {
                                val source = ImageDecoder.createSource(requireActivity().contentResolver, uri)
                                ImageDecoder.decodeBitmap(source)
                            } else {
                                MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
                            }
                            binding.imageView.setImageBitmap(secilenBitMap)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    galeriyeGit()
                } else {
                    Toast.makeText(requireContext(), "İzin verilmedi", Toast.LENGTH_LONG).show()
                }
            }
    }
private fun kucukBitmapOlustur(KullanicininSectigiBitmap:Bitmap,maximumBoyut:Int):Bitmap{
    var width=KullanicininSectigiBitmap.width
    var height=KullanicininSectigiBitmap.height

    val bitmapOranı:Double=width.toDouble()/height.toDouble()

    if(bitmapOranı>1){
        //gorsel yatay
        width=maximumBoyut
        val kisaltilmisYukseklik=width/bitmapOranı
        height=kisaltilmisYukseklik.toInt()
    }else{
        //gorsel dikey
        height=maximumBoyut
        val kisaltilmisYukseklik=height*bitmapOranı
        width=kisaltilmisYukseklik.toInt()



    }

    return Bitmap.createScaledBitmap(KullanicininSectigiBitmap,100,100,true)

}
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mDisposable.clear()
    }
}


