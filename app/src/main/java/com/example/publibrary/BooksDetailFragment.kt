package com.example.publibrary

import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.example.publibrary.data.DataBook
import com.example.publibrary.databinding.BooksDetailFragmentBinding
import java.text.SimpleDateFormat
import java.util.Calendar


class BooksDetailFragment private constructor(): Fragment() {
    private val MENU_RES_ID = R.menu.cat_edit_menu
    var bookId: Int = 0

    private object SingeltonInstance {
        val INSTANCE = BooksDetailFragment()
    }

    companion object {
        val argumentKey = "bookID"
        val instance:BooksDetailFragment get() = SingeltonInstance.INSTANCE
    }


    private lateinit var binding: BooksDetailFragmentBinding
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
        binding = BooksDetailFragmentBinding.inflate(inflater)

        bookId = requireArguments().getInt(argumentKey)

        viewModel = ViewModelProvider(this).get(BookViewModel::class.java)

        binding.lifecycleOwner = viewLifecycleOwner

        subscribeToModel(viewModel)

        actionbarSetup()

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getBooksData()
    }

    public fun getBooksData(): Boolean {
        try {
            binding.progress.visibility = View.VISIBLE
            return requireView().postDelayed({ viewModel.getBookData(bookId) }, 100L * 2)
        }catch (e:Exception){
            e.printStackTrace()
        }
       return false
    }

    private fun subscribeToModel(model: BookViewModel) {
        model.uiState!!.observe(viewLifecycleOwner) { uiState ->
            if (uiState != null) {

                val book = uiState.dataItem
                if (book.bookTitle.isNotEmpty()) {
                    binding.progress.visibility = View.GONE
                    val sdf = SimpleDateFormat("yyyy-MM-dd")
                    val date = sdf.parse(book.publicationDate)
                    val calendar = Calendar.getInstance()
                    calendar.time = date
                    val year = calendar.get(Calendar.YEAR)

                    binding.title.text = "title: ${book.bookTitle}"
                    binding.author.text = "author: ${book.author}"
                    binding.publicyear.text = "public year: ${year}"
                    binding.isbn.text = "ISBN: ${book.isbn}"
                    binding.bookId.text = "book id: ${book.bookId}"
                }

                if (uiState.deleteBookStatus > 0) {
                    binding.progress.visibility = View.GONE

                    val ac = requireActivity() as MainActivity
                    ac.handler.sendMessage(Message())

                    view?.postDelayed({
                        closeFragment()
                    }, 100L * 3)

                }

            } else {

            }
        }
    }

    fun actionbarSetup() {
        val toolBar = binding.myToolbar
        toolBar.title = getString(R.string.module_name_2)

        setHasOptionsMenu(true)

        toolBar.inflateMenu(MENU_RES_ID)

        toolBar.apply {
            setNavigationOnClickListener {
                closeFragment()
            }
        }

        toolBar.setOnMenuItemClickListener { onOptionsItemSelectedx(it) }
    }

    fun onOptionsItemSelectedx(item: MenuItem): Boolean {

        if (item.itemId == R.id.action_edit_book) {
            Toast.makeText(context, "action_settings", Toast.LENGTH_SHORT).show()

            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.container, BooksModifyFragment.instance.apply {
                    val bundleNew = Bundle()
                    bundleNew.putInt(argumentKey, viewModel.uiState!!.value!!.dataItem.bookId)
                    arguments = bundleNew

                    Log.d("load_data", "BooksDetailFragment ${arguments.toString()}")
                })
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commitNow()
        }
        if (item.itemId == R.id.action_delete_book) {
            Toast.makeText(context, "action_delete_book", Toast.LENGTH_SHORT).show()
            binding.progress.visibility = View.VISIBLE
            var book = DataBook(bookId = bookId)
            viewModel.deleteBookData(book)
        }


        return super.onOptionsItemSelected(item)
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
