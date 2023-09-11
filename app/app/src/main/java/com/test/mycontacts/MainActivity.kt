package com.test.mycontacts


import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.test.mycontacts.databinding.ActivityMainBinding
import com.test.mycontacts.databinding.DialogBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dialogBinding: DialogBinding
    private lateinit var addDialog: AddDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.toolbar)

        val pagerAdapter = ViewPagerAdapter(supportFragmentManager)
        val pager = binding.viewPager
        pager.adapter = pagerAdapter
        val tab = binding.tab
        tab.setupWithViewPager(pager)

        binding.fbAdd.setOnClickListener {
            dialogBinding = DialogBinding.inflate(layoutInflater)
            addDialog = AddDialog(this, dialogBinding)

            addDialog.onDataAdded =
                { // 위에서 초기화한 AddDialog에 onDataAdded?.let { it() }에 실행문이 전달되고 실행된다.
                    pager.adapter = pagerAdapter // ViewPager 어댑터 설정
                    pager.setCurrentItem(0, true)
                }

            addDialog.setOnButtonClickListener(object :
                AddDialog.ButtonClickListener { // 다이어로그의 클릭 리스너를 정의함.
                override fun onClicked(
                    uri: Uri?,
                    name: String,
                    number: String,
                    mail: String,
                    notificationTime: Int
                ) {

//                    Log.d("imageUri3","imageUri:${uri}") // 잘 추가됫는지 Log 확인용
                    val contactListFragment =
                        pagerAdapter.getItem(0) as ContactList // ContactList 프래그먼트를 불러와서
                    contactListFragment.addContact(
                        uri,
                        name,
                        number,
                        mail,
                        notificationTime
                    ) // 데이터를 추가하는 함수를 실행함.
//                    Log.d("DataListCheck", "Size of dataList: ${defaultDataList.size}") // 데이터 추가됫는지 확인용 Logcat
                    Toast.makeText(
                        this@MainActivity,
                        "${name}님께 연락할 수 있도록 ${notificationTime}분 이후 알림",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })

            addDialog.dig()
            addDialog.show()
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) { // 액티비티나 프래그먼트에서 다른 액티비티로의 요청 결과를 받아오는 역할
        // 다이어로그에서 갤러리 선택으로 넘어가고, 이후 갤러리에서 다이어로그로 넘어올 때 갤러리의 Intent에 이미지 정보'만' 담기면서 아래의 실행
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AddDialog.REQUEST_GALLERY_DIALOG && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data // 갤러리에서 선택한 데이터정보만 Intent에 담겨있음
//            Log.d("ImageUri4", "$imageUri") 잘 추가됫는지 Log 확인용
            addDialog.setImageUri(imageUri) // 다이어로그 이미지 추가 & 변수에 이미지 담는 함수
        }
    }
}
