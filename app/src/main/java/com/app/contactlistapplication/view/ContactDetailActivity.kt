package com.app.contactlistapplication.view

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.app.contactlistapplication.R
import com.app.contactlistapplication.viewModel.MainViewModel
import com.app.contactlistapplication.utils.Status
import com.app.contactlistapplication.viewModel.ViewModelFactory
import com.app.contactlistapplication.model.StarMain
import com.app.contactlistapplication.utils.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.app.contactlistapplication.retrofit.ApiHelper
import com.app.contactlistapplication.retrofit.RetrofitBuilder
import kotlinx.android.synthetic.main.activity_contact_detail.*

var isStarred = -1

class ContactDetailActivity : AppCompatActivity(){

    lateinit var tvname: TextView
    lateinit var tvemail: TextView
    lateinit var tvphone: TextView
    lateinit var image: ImageView
    lateinit var star_img: ImageView
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_detail)

        setupViewModel()
        setupUI()

        val id = intent.getIntExtra("id", -1);
        val name =intent.getStringExtra("name")
        val email=intent.getStringExtra("email")
        val phone=intent.getStringExtra("phone")
        val url_img =intent.getStringExtra("url_img")
        isStarred =intent.getIntExtra("isStarred", -1)

        tvname.setText(name)
        tvemail.setText(email)
        tvphone.setText(phone)

        Glide.with(this@ContactDetailActivity).load(Utils.BASE_URL + url_img)
            .apply(RequestOptions().centerCrop())
            .circleCrop()
            .into(image)

        if(isStarred == 1){
            star_img.setBackgroundResource(R.drawable.ic_baseline_favorite);
        }else if(isStarred == 0){
            star_img.setBackgroundResource(R.drawable.ic_baseline_favorite_grey);
        }

        star_img.setOnClickListener(){
            when (isStarred) {
                0 -> {
                    viewModel.setStar(id).observe(this, Observer {
                        it?.let { resource ->
                            when (resource.status) {
                                Status.SUCCESS -> {
                                    isStarred = 1
                                    resource.data?.let { star -> setStar(star) }
                                }
                                Status.ERROR -> {
                                    Utils.showToast(this, " ${it.message} ")
                                }
                                Status.LOADING -> { }
                            }
                        }
                    })
                }

                1 -> {
                    viewModel.setUnStar(id).observe(this, Observer {
                        it?.let { resource ->
                            when (resource.status) {
                                Status.SUCCESS -> {
                                    isStarred = 0
                                    resource.data?.let { unStar -> setUnStar(unStar) }
                                }
                                Status.ERROR -> {
                                    Utils.showToast(this, " ${it.message} ")
                                }
                                Status.LOADING -> { }
                            }
                        }
                    })
                }

            }
        }

    }

    private fun setupUI() {
        tvname =  findViewById(R.id.tvName)
        tvemail = findViewById(R.id.tvEmail)
        tvphone = findViewById(R.id.tvMobile)
        image = findViewById(R.id.image)
        star_img = findViewById(R.id.star)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this, ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(MainViewModel::class.java)
    }

    private fun setStar(star: StarMain) {
        if (star.meta.success)
            star_img.setBackgroundResource(R.drawable.ic_baseline_favorite);
            Utils.showToast(this, " ${star.meta.message} ")
    }

    private fun setUnStar(unStar: StarMain) {
        if (unStar.meta.success)
            star_img.setBackgroundResource(R.drawable.ic_baseline_favorite_grey);
            Utils.showToast(this, " ${unStar.meta.message} ")
    }

}