package com.kebinr.karmaaplication.ui.content.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kebinr.karmaaplication.R
import com.kebinr.karmaaplication.model.Favor
import com.kebinr.karmaaplication.ui.content.adapters.FavoresOtrosAdapter
import com.kebinr.karmaaplication.viewmodel.FirebaseAuthViewModel
import com.kebinr.karmaaplication.viewmodel.FirebaseFavorRTViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_favor.view.*
import kotlinx.android.synthetic.main.fragment_messages.*
import kotlinx.android.synthetic.main.fragment_tomar_favor.*
import kotlinx.android.synthetic.main.nav_header_main.*

@AndroidEntryPoint
class TomarFavorFragment : Fragment(R.layout.fragment_tomar_favor),
    FavoresOtrosAdapter.onListIteration {

    val firebaseAuthViewModel: FirebaseAuthViewModel by activityViewModels()
    val firebasefavorRTVM : FirebaseFavorRTViewModel by activityViewModels()
    private val adapter =
        FavoresOtrosAdapter(
            ArrayList(),
            this
        )
    var userUid : String = "_"
    var name : String = "_"
    var karma : Int =0
    var favores: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tomar_favor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireView().favores_recycler.adapter = adapter
        requireView().favores_recycler.layoutManager = LinearLayoutManager(requireContext())

        firebaseAuthViewModel.logged().observe(getViewLifecycleOwner(), Observer {
            userUid = it
            adapter.uid = it

            firebasefavorRTVM.getuserInfo(userUid)
            firebasefavorRTVM.user.observe(getViewLifecycleOwner(), Observer{
                name = it.nombre!!
                karma = it.karma!!
                favores = it.favores!!

            })
            firebasefavorRTVM.getValues(userUid,"")
            //filtrar los favores
            button5.setOnClickListener {
                if (radioButton6.isChecked()){
                    firebasefavorRTVM.getValues(userUid,"Sacar fotocopias")
                }else{
                    if (radioButton7.isChecked()){
                        firebasefavorRTVM.getValues(userUid,"Comprar km5")
                    }else{
                        if (radioButton8.isChecked()){
                            firebasefavorRTVM.getValues(userUid,"Buscar domicilio puerta 7")
                        }else{
                            MaterialAlertDialogBuilder(requireContext())
                                .setTitle("Filtros")
                                .setMessage("Debe seleccionar un filtro primero para que sea valido")
                                .setPositiveButton("Aceptar") { dialog, which ->
                                    // Respond to positive button press
                                }
                                .show()
                        }
                    }
                }

            }
            //limpiar filtros
            button4.setOnClickListener {
                radioButton6.isChecked = false
                radioButton7.isChecked = false
                radioButton8.isChecked = false
                firebasefavorRTVM.getValues(userUid,"")
            }
            firebasefavorRTVM.ldfavoresotroslist.observe(getViewLifecycleOwner(), Observer {
                Log.d("MyOut","Número de favores "+it.size)
                adapter.posts.clear()
                adapter.posts.addAll(it)
                adapter.notifyDataSetChanged()
            })
        })

    }

    override fun onListButtonInteraction(favor: Favor, uid: String) {
        //implementar evento de asignacion de favor
        firebasefavorRTVM.updateFavorStatus(favor.user_askingid!!,name,uid)

    }
}