package com.example.publibrary

import android.os.Bundle
import android.os.Message
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.example.publibrary.data.DataBook
import com.example.publibrary.databinding.BooksAddFragmentBinding
import com.example.publibrary.databinding.BooksDetailFragmentBinding
import java.text.SimpleDateFormat


class BooksAddFragment private constructor() : Fragment() {
    private object SingeltonInstance {
        val INSTANCE = BooksAddFragment()
    }

    companion object {
        val argumentKey = "bookID"
        val instance:BooksAddFragment get() = SingeltonInstance.INSTANCE
    }

    private lateinit var binding: BooksAddFragmentBinding
    private lateinit var viewModel: BookViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(BookViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BooksAddFragmentBinding.inflate(inflater)

//        bookId = requireArguments().getInt(argumentKey)!!

        viewModel = ViewModelProvider(this).get(BookViewModel::class.java)

        binding.lifecycleOwner = viewLifecycleOwner

//        binding.viewmodel = viewModel

        subscribeToModel(viewModel)

        actionbarSetup()

        binding.buttonAddBook.setOnClickListener {
            val title = binding.title.text.toString()
            val author = binding.author.text.toString()
            val publicYear = binding.publicyear.text.toString()
            val isbn = binding.isbn.text.toString()

            if (title.isNotEmpty() && author.isNotEmpty() && publicYear.isNotEmpty() && isbn.isNotEmpty()) {
                binding.progress.visibility = View.VISIBLE
                val book = DataBook(0, title, author, publicYear, isbn)
                viewModel.addBookData(book)
            } else {
                Toast.makeText(requireContext(), "input filed can not be null", Toast.LENGTH_SHORT)
                    .show()
            }

        }

        return binding.root
    }

    private fun subscribeToModel(model: BookViewModel) {
        model.uiState!!.observe(viewLifecycleOwner) { uiState ->
            if (uiState != null) {
                val status = uiState.addBookStatus
                if (status>0) {
                    val ac = requireActivity() as MainActivity
                    ac.handler.sendMessage(Message())

                    binding.progress.visibility = View.GONE
                    Toast.makeText(requireContext(), "book add success", Toast.LENGTH_SHORT)
                        .show()
                    closeFragment()
                }else{
                    if(binding.progress.isVisible) {
                        binding.progress.visibility = View.GONE
                        Toast.makeText(requireContext(), "book add failed", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

            } else {

            }
        }
    }

    fun actionbarSetup() {
        val toolBar = binding.myToolbar
        toolBar.title = getString(R.string.module_name_1)
//        val a = activity as AppCompatActivity
//        a.setSupportActionBar(toolBar)
//        setHasOptionsMenu(true)
//        a.supportActionBar?.title = getString(R.string.module_name_1)

//        toolBar.inflateMenu(MENU_RES_ID)

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
