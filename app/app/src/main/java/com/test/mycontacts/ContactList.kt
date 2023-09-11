package com.test.mycontacts

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.ex6_simplelistview.MyAdapter
import com.test.mycontacts.MyItem.Companion.defaultDataList
import com.test.mycontacts.databinding.FragmentRecyclerviewBinding

class ContactList : Fragment() {

    private lateinit var binding: FragmentRecyclerviewBinding // 멤버 변수 선언
    private val adapter = MyAdapter(defaultDataList) // 리사이클러뷰 어댑터

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecyclerviewBinding.inflate(inflater, container, false)

        // 어댑터 생성 및 연결
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter.itemClick = object : MyAdapter.ItemClick { // 아이템뷰 클릭시 실행문
            override fun onClick(view: View, position: Int) {

                val item = defaultDataList[position]

                val name: String
                val image: Uri?
                val phonenumber: String
                val email: String
                val notifi: Int

                name = item.aName
                image = item.aIcon
                phonenumber = item.aPhonenumber
                email = item.aEmail
                notifi = item.notifi
                // 변수를 선언하고 현재 item의 값을 대입

                Toast.makeText(requireContext(), " $name 선택!", Toast.LENGTH_SHORT).show()
                // 데이터를 Bundle로 패킹
                val bundle = Bundle()
                bundle.putString("name", name)
//                bundle.putInt("image", image)
                bundle.putParcelable("image", image) // URI 데이터는 Paracelable로 전달 가능
                bundle.putString("phonenumber", phonenumber)
                bundle.putString("email", email)
                bundle.putInt("notifi", notifi)
                // ... 다른 데이터도 Bundle에 추가

                // MyPageFragment 인스턴스 생성 및 Bundle 설정
                val detailFragment = DetailPageFragment()
                detailFragment.arguments = bundle

                // 프래그먼트 트랜잭션 시작
                parentFragmentManager.beginTransaction()
                    .replace(
                        android.R.id.content,
                        detailFragment
                    )
                    .addToBackStack(null) // 뒤로 가기 버튼 처리를 위해 스택에 추가
                    .commit()
            }
        }
        return binding.root


    }

    fun addContact(uri: Uri?, name: String, number: String, mail: String, notificationTime: Int) {
        val addContact = MyItem(uri, name, number, mail, R.drawable.img_like3, notificationTime, 0)
        defaultDataList.add(addContact) // 데이터 클래스의 데이터에 다이어로그에서 입력한 데이터를 추가함.
//        Log.d("ContactList", "dataList: $defaultDataList") // 데이터 잘 추가됫는지 확인용
    }
}
