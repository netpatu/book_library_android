package com.example.publibrary

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.publibrary.data.DataBook
import com.example.publibrary.databinding.SearchBooksFragmentBinding

class SearchBooksFragment private constructor() : Fragment() {
    private var firstInit = true

    private object SingeltonInstance {
        val INSTANCE = SearchBooksFragment()
    }

    companion object {
        val instance: SearchBooksFragment get() = SingeltonInstance.INSTANCE
    }

    private lateinit var binding: SearchBooksFragmentBinding
    private lateinit var viewModel: BookViewModel

    private var booksListAdapter: BookListAdapterx? = null

    var bookDetailFrgmnt: BooksDetailFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(BookViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SearchBooksFragmentBinding.inflate(inflater)

        viewModel = ViewModelProvider(this).get(BookViewModel::class.java)

        binding.lifecycleOwner = viewLifecycleOwner

        subscribeToModel(viewModel)

        actionbarSetup()

        loadBooksData()

        binding.buttonClickSearch.setOnClickListener {
            val content = binding.editTextSearch.text.toString()

            if (content.isNotEmpty()) {
                binding.progress.visibility = View.VISIBLE
                viewModel.getBookData(content.toInt())
            } else {
                Toast.makeText(context, "pls input book id", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        getBooksData()
//    }

//    fun getBooksData(): Boolean {
//        if (bookDetailFrgmnt != null) {
//            bookDetailFrgmnt?.getBooksData()
//        }
//        return requireView().postDelayed({ viewModel.initData() }, 100L * 2)
//    }

    private fun subscribeToModel(model: BookViewModel) {
        model.uiState!!.observe(viewLifecycleOwner) { uiState ->
            if (uiState != null) {
                if (uiState.dataItems.size>0) {
                    binding.progress.visibility = View.GONE
                    booksListAdapter?.notifyDataSetChanged()
                    if (!firstInit) {
                        firstInit = true
                    }
                }

            }
        }
    }

    private fun loadBooksData() {
        booksListAdapter = BookListAdapterx(requireContext(), viewModel.uiState!!.value!!.dataItems)
        val booksRecyclerView = binding.booksList
        val layoutManager = LinearLayoutManager(context)
        booksRecyclerView.layoutManager = layoutManager
        booksRecyclerView.adapter = booksListAdapter

        val myInterface = object : MyInterfacex {
            override fun toBookDetail(bookID: Int) {
                bookDetailFrgmnt = BooksDetailFragment.instance.apply {
                    val bundleNew = Bundle()
                    bundleNew.putInt(BooksDetailFragment.argumentKey, bookID)
                    arguments = bundleNew

                    Log.d("load_data", "BooksFragment ${arguments.toString()}")
                }
                activity!!.supportFragmentManager.beginTransaction()
                    .add(
                        R.id.container, bookDetailFrgmnt!!
                    )
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commitNow()
            }

        }

        booksListAdapter?.myInterface = myInterface
    }

    fun actionbarSetup() {
        val toolBar = binding.myToolbar
        toolBar.title =getString(R.string.module_name_5)

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
}

interface MyInterfacex {
    fun toBookDetail(bookID: Int)
}

class BookListAdapterx(
    context: Context,
    data: ArrayList<DataBook>,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var ctx: Context? = null
    var data: ArrayList<DataBook>? = null

    var myInterface: MyInterfacex? = null

    init {
        ctx = context
        this.data = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Log.d("load_data", "onCreateViewHolder")
        return BooksViewHolderx(
            LayoutInflater.from(ctx!!).inflate(R.layout.view_books_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        val count = data!!.size
        Log.d("load_data", "getItemCount ${count}")
        return count
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.d("load_data", "onBindViewHolder")
        val bookVHolder: BooksViewHolderx = holder as BooksViewHolderx
        val dict = data!![position]

        bookVHolder.itemView.setOnClickListener {
            myInterface?.toBookDetail(dict.bookId)
        }

        bookVHolder.loadData2View(
            bkId = dict.bookId,
            bookTitleStr = dict.bookTitle,
            authorStr = dict.author,
            publicYearStr = dict.publicationDate,
            isbnStr = dict.isbn
        )
    }
}

class BooksViewHolderx(itemView: View) :
    RecyclerView.ViewHolder(itemView) {

    var bookId: TextView
    var bookTitle: TextView
    var author: TextView
    var publicYear: TextView
    var isbn: TextView

    init {
        bookId = itemView.findViewById(R.id.book_id)
        bookTitle = itemView.findViewById(R.id.book_title)
        author = itemView.findViewById(R.id.author)
        publicYear = itemView.findViewById(R.id.publicyear)
        isbn = itemView.findViewById(R.id.isbn)
    }

    public fun loadData2View(
        bkId: Int = 321321321,
        bookTitleStr: String = "time shit",
        authorStr: String = "xxxJw",
        publicYearStr: String = "2000",
        isbnStr: String = "#53454353453"
    ) {
        bookId.setText("$bkId")
        bookTitle.setText(bookTitleStr)
        author.setText(authorStr)
        publicYear.setText(publicYearStr)
        isbn.setText(isbnStr)
    }
}

