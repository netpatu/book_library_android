package com.example.publibrary

import android.os.Bundle
import android.os.Message
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.example.publibrary.data.DataBook
import com.example.publibrary.databinding.BooksModifyFragmentBinding
import java.text.SimpleDateFormat
import java.util.Calendar

class BooksModifyFragment private constructor(): Fragment() {
    var bookId: Int = 0

    private object SingeltonInstance {
        val INSTANCE = BooksModifyFragment()
    }

    companion object {
        val instance:BooksModifyFragment get() = SingeltonInstance.INSTANCE
    }

//    companion object {
//        fun newInstance() = BooksModifyFragment()
//    }

    private lateinit var binding: BooksModifyFragmentBinding
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
        binding = BooksModifyFragmentBinding.inflate(inflater)

        bookId = requireArguments().getInt(BooksDetailFragment.argumentKey)

        viewModel = ViewModelProvider(this).get(BookViewModel::class.java)

        binding.lifecycleOwner = viewLifecycleOwner

        subscribeToModel(viewModel)

        actionbarSetup()

        binding.buttonModifyBook.setOnClickListener {
            val title = binding.title.text.toString()
            val author = binding.author.text.toString()
            val publicYear = binding.publicyear.text.toString()
            val isbn = binding.isbn.text.toString()
            if (title.isNotEmpty() && author.isNotEmpty() && publicYear.isNotEmpty() && isbn.isNotEmpty()) {
                binding.progress.visibility = View.VISIBLE
                val book = DataBook(bookId, title, author, publicYear, isbn)
                viewModel.modifyBookData(book)
            } else {
                Toast.makeText(requireContext(), "input filed can not be null", Toast.LENGTH_SHORT)
                    .show()
            }

        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.progress.visibility = View.VISIBLE
        requireView().postDelayed({ viewModel.getBookData(bookId) }, 100L * 2)
    }

    private fun subscribeToModel(model: BookViewModel) {
        model.uiState!!.observe(viewLifecycleOwner) { uiState ->
            if (uiState != null) {
                val book = uiState.dataItem
                if (book.bookTitle.isNotEmpty()) {
                    binding.progress.visibility = View.GONE

                    binding.title.setText(book.bookTitle)
                    binding.author.setText(book.author)
                    binding.publicyear.setText("${book.publicationDate}")
                    binding.isbn.setText(book.isbn)
                }

                val status = uiState.modifyBookStatus
                if (status>0) {
                    val ac = requireActivity() as MainActivity
                    ac.handler.sendMessage(Message())

                    closeFragment()
                }
            } else {

            }
        }
    }

    fun actionbarSetup() {
        val toolBar = binding.myToolbar
        toolBar.title = getString(R.string.module_name_3)

//        val a = activity as AppCompatActivity
//        a.setSupportActionBar(toolBar)
//        setHasOptionsMenu(true)
//        a.supportActionBar?.title = getString(R.string.app_name)

//        toolBar.inflateMenu(MENU_RES_ID)

        toolBar.apply {
            setNavigationOnClickListener {

//                Toast.makeText(context, "go back", Toast.LENGTH_SHORT).show()
//                requireActivity().finish()

                closeFragment()
            }
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(MENU_RES_ID, menu)
//        super.onCreateOptionsMenu(menu, inflater)
//    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//
//        if (item.itemId == R.id.action_settings) {
//            Toast.makeText(context, "action_settings", Toast.LENGTH_SHORT).show()
//
//            requireActivity().supportFragmentManager.beginTransaction()
//                .add(R.id.container, SettingPocketFragment.newInstance())
//                .commitNow()
//        }
//
//        return super.onOptionsItemSelected(item)
//    }

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
