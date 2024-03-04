package com.example.publibrary

import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.publibrary.databinding.SettingFragmentBinding
import com.example.publibrary.net.BookApiImplementation
import java.lang.StringBuilder


class SettingFragment private constructor() : Fragment() {


    private object SingeltonInstance {
        val INSTANCE = SettingFragment()
    }

    companion object {
        val instance: SettingFragment get() = SingeltonInstance.INSTANCE
    }

    private lateinit var binding: SettingFragmentBinding
    private lateinit var viewModel: BookViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SettingFragmentBinding.inflate(inflater)

        actionbarSetup()

        binding.buttonChangeUrl.setOnClickListener {
            val host = binding.hostEdtv.text.toString()

            if (host.isNotEmpty()) {
                LibraryApplication.libraryServiceClient?.url=host
                LibraryApplication.libraryServiceClient?.reConstructService()

                val ac = requireActivity() as MainActivity
                ac.handler.sendMessage(Message())

                Toast.makeText(requireContext(), "host setting successfully", Toast.LENGTH_SHORT)
                    .show()
//                Log.e("OKHttp-----", "url = ${BookApiImplementation.Singleton.instance()}")
                requireView().postDelayed({
                    closeFragment()
                }, 100L * 3)

            } else {
                Toast.makeText(requireContext(), "input filed can not be null", Toast.LENGTH_SHORT)
                    .show()
            }

        }

        return binding.root
    }


    fun actionbarSetup() {
        val toolBar = binding.myToolbar
        toolBar.title = getString(R.string.module_name_4)
        toolBar.apply {
            setNavigationOnClickListener {
                closeFragment()
            }
        }
    }

    private fun closeFragment() {
        val transaction: FragmentTransaction =
            requireActivity().supportFragmentManager.beginTransaction()
        requireActivity().supportFragmentManager.popBackStack()
        transaction.remove(this)
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
        transaction.commit()
    }

    override fun onResume() {
        super.onResume()

        captureBackPressEvent()
    }

    private fun captureBackPressEvent() {
        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener(View.OnKeyListener { view, i, keyEvent ->
            if (keyEvent.action === KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_BACK) {
                closeFragment()
                return@OnKeyListener true
            }
            false
        })
    }
}
