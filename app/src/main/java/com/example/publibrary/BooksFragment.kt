package com.example.publibrary

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.publibrary.data.DataBook
import com.example.publibrary.databinding.BooksFragmentBinding

class BooksFragment : Fragment() {
    private val MENU_RES_ID = R.menu.cat_topappbar_menu
    private var firstInit = true

    private object SingeltonInstance {
        val INSTANCE = BooksFragment()
    }

    companion object {
        val instance: BooksFragment get() = SingeltonInstance.INSTANCE
    }

    private lateinit var binding: BooksFragmentBinding
    private lateinit var viewModel: BookViewModel

    private var booksListAdapter: BookListAdapter? = null

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
        binding = BooksFragmentBinding.inflate(inflater)

        viewModel = ViewModelProvider(this).get(BookViewModel::class.java)

        binding.lifecycleOwner = viewLifecycleOwner

        subscribeToModel(viewModel)

        actionbarSetup()

        binding.progress.visibility = View.VISIBLE

        loadBooksData()

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getBooksData()
    }

    fun getBooksData(): Boolean {
        if (bookDetailFrgmnt != null) {
            bookDetailFrgmnt?.getBooksData()
        }
        return requireView().postDelayed({ viewModel.initData() }, 100L * 2)
    }

    private fun subscribeToModel(model: BookViewModel) {
        model.uiState!!.observe(viewLifecycleOwner) { uiState ->
            if (uiState != null) {
                if (uiState.dataItems.size != 0) {
                    binding.progress.visibility = View.GONE
                    booksListAdapter!!.notifyDataSetChanged()
                    if (!firstInit) {
                        firstInit = true
                    }
                }

            } else {

            }
        }
    }

    private fun loadBooksData() {
        booksListAdapter = BookListAdapter(requireContext(), viewModel.uiState!!.value!!.dataItems)
        val booksRecyclerView = binding.booksList
        val layoutManager = LinearLayoutManager(context)
        booksRecyclerView.layoutManager = layoutManager
        booksRecyclerView.adapter = booksListAdapter

        val myInterface = object : MyInterface {
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

        val a = activity as AppCompatActivity
        a.setSupportActionBar(toolBar)
        setHasOptionsMenu(true)
        a.supportActionBar?.title = getString(R.string.app_name)

        toolBar.apply {
            setNavigationOnClickListener {
                Toast.makeText(context, "go back", Toast.LENGTH_SHORT).show()
                requireActivity().finish()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(MENU_RES_ID, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_add_book) {
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.container, BooksAddFragment.instance)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commitNow()
        } else if (item.itemId == R.id.action_setting) {
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.container, SettingFragment.instance)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commitNow()
        } else if (item.itemId == R.id.action_refresh) {
            getBooksData()
        } else if (item.itemId == R.id.action_search) {
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.container, SearchBooksFragment.instance)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commitNow()
        }

        return super.onOptionsItemSelected(item)
    }
}

interface MyInterface {
    fun toBookDetail(bookID: Int)
}

class BookListAdapter(
    context: Context,
    data: ArrayList<DataBook>,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var ctx: Context? = null
    var data: ArrayList<DataBook>? = null

    var myInterface: MyInterface? = null

    init {
        ctx = context
        this.data = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Log.d("load_data", "onCreateViewHolder")
        return BooksViewHolder(
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
        val bookVHolder: BooksViewHolder = holder as BooksViewHolder
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

class BooksViewHolder(itemView: View) :
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

