package com.app.contactlistapplication.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.contactlistapplication.R
import com.app.contactlistapplication.adapter.ContactAdapter
import com.app.contactlistapplication.viewModel.MainViewModel
import com.app.contactlistapplication.utils.Status
import com.app.contactlistapplication.viewModel.ViewModelFactory
import com.app.contactlistapplication.model.ContactMain
import com.app.contactlistapplication.model.Content
import com.app.contactlistapplication.model.StarMain
import com.app.contactlistapplication.utils.Utils
import com.app.contactlistapplication.utils.Utils.Companion.isInternetAvailable
import com.app.contactlistapplication.utils.Utils.Companion.retrieveOfflineList
import com.app.contactlistapplication.utils.Utils.Companion.showToast
import com.app.contactlistapplication.retrofit.ApiHelper
import com.app.contactlistapplication.retrofit.RetrofitBuilder
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ContactAdapter.OnCellClickListener,
        ContactAdapter.OnItemClickListener {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mViewModel: MainViewModel
    private lateinit var mAdapter: ContactAdapter
    private val mContactList: ArrayList<Content> = ArrayList()
    private var mPageNumber: Int = 1
    private var mIsScrolling = false
    private var mStopScrollWhenNoContent = false
    private var mCurrentItems = 0
    private var mTotalItems: Int = 0
    private var mScrollOutItems: Int = 0
    private lateinit var mLayoutManager: LinearLayoutManager
    private var mLastClickTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupViewModel()
        setupUI()
    }

    private fun setupViewModel() {
        mViewModel = ViewModelProviders.of(this, ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(MainViewModel::class.java)
    }

    private fun setupUI() {
        mRecyclerView = findViewById(R.id.recyclerView)
        mAdapter = ContactAdapter(this, this, this)
        mRecyclerView.adapter = mAdapter
        mLayoutManager = LinearLayoutManager(this)
        mRecyclerView.layoutManager = mLayoutManager

        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    mIsScrolling = true
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                mCurrentItems = mLayoutManager.getChildCount()
                mTotalItems = mLayoutManager.getItemCount()
                mScrollOutItems = mLayoutManager.findFirstVisibleItemPosition()
                if (mIsScrolling && mCurrentItems + mScrollOutItems == mTotalItems) {
                    mIsScrolling = false

                    if (!mStopScrollWhenNoContent) {
                        mPageNumber++
                        contactListObserver(mPageNumber)
                    }
                }
            }
        })

        if (isInternetAvailable(this)) {
            contactListObserver(mPageNumber)
        } else {
            fetchContactListOffline()
        }
    }

    private fun fetchContactListOffline() {
        progressBar.visibility = View.GONE
        mRecyclerView.visibility = View.VISIBLE
        mContactList.clear()
        mContactList.addAll(retrieveOfflineList(this@MainActivity))
        mAdapter.setContactList(mContactList)
        mAdapter.notifyDataSetChanged()
        showToast(this, R.string.offline_msg)
    }

    private fun contactListObserver(pageNumber: Int) {
        mViewModel.getContactList1(pageNumber).observe(this, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        mRecyclerView.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                        resource.data?.let { users -> fetchContactListOnline(users) }
                    }
                    Status.ERROR -> {
                        mRecyclerView.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                        Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    }
                    Status.LOADING -> {
                        progressBar.visibility = View.VISIBLE
                        mRecyclerView.visibility = View.GONE
                    }
                }
            }
        })
    }

    private fun fetchContactListOnline(contactList: ContactMain) {
        if (contactList.content.isEmpty()) {
            mStopScrollWhenNoContent = true
            progressBar.visibility = View.GONE
            showToast(this, "Page No. ${contactList.meta.pageNumber} - No content available")
        } else {
            for (i in contactList.content) {
                if (!mContactList.contains(i)) mContactList.add(i)
            }
            mAdapter.setContactList(mContactList)
            Utils.saveData(mContactList, this@MainActivity)
            progressBar.visibility = View.GONE
        }
    }

    override fun onCellClickListener(position: Int, data: Content) {
        val intent = Intent(this@MainActivity, ContactDetailActivity::class.java)
        intent.putExtra("id", data.id)
        intent.putExtra("name", data.name)
        intent.putExtra("email", data.email)
        intent.putExtra("phone", data.phone)
        intent.putExtra("url_img", data.thumbnail)
        intent.putExtra("isStarred", data.isStarred)
        startActivity(intent)
    }

    override fun onItemClick(position: Int, data: Content) {
        if (isInternetAvailable(this)) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 3000) {
                showToast(this, R.string.multi_click_msg)
                return;
            } else {
                mLastClickTime = SystemClock.elapsedRealtime();
                if (data.isStarred == 0) {
                    data.apply { isStarred = 1 }
                    mViewModel.setStar(data.id).observe(this, Observer {
                        it?.let { resource ->
                            when (resource.status) {
                                Status.SUCCESS -> {
                                    resource.data?.let { star -> setStar(star) }
                                }
                                Status.ERROR -> {
                                    showToast(this, " ${it.message} ")
                                }
                                Status.LOADING -> {
                                }
                            }
                        }
                    })
                } else if (data.isStarred == 1) {
                    data.apply { isStarred = 0 }
                    mViewModel.setUnStar(data.id).observe(this, Observer {
                        it?.let { resource ->
                            when (resource.status) {
                                Status.SUCCESS -> {
                                    resource.data?.let { unStar -> setUnStar(unStar) }
                                }
                                Status.ERROR -> {
                                    showToast(this, " ${it.message} ")
                                }
                                Status.LOADING -> {
                                }
                            }
                        }
                    })
                }
            }

        } else {
            showToast(this, R.string.no_internet)
        }
    }

    private fun setStar(star: StarMain) {
        if (star.meta.success) showToast(this, " ${star.meta.message} ")
    }

    private fun setUnStar(unStar: StarMain) {
        if (unStar.meta.success) showToast(this, " ${unStar.meta.message} ")
    }

}