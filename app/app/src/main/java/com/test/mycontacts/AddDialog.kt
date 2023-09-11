package com.test.mycontacts

import android.app.Activity
import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.test.mycontacts.databinding.DialogBinding
import android.Manifest
import android.net.Uri
import android.util.Log


class AddDialog(private val activity: Activity, private val binding: DialogBinding) :
    Dialog(activity) {
    var onDataAdded: (() -> Unit)? =
        null // 람다함수를 자료형으로 가지는 변수. 그런데 ?이 붙어 null도 가능하고 초기값을 null로 주었다. 나중에 다이어로그 화면 갱신때 사용.
    private lateinit var onClickedListener: ButtonClickListener
    private var notificationTime = 0  // 알림시간을 추가할 전역 변수

    companion object { // companion object로 정의하면 AddDialog.REQUEST_GALLERY_DIALOG 이런식으로 다른 클래스에서도 쓸 수 있다.
        const val REQUEST_GALLERY_DIALOG = 3
    }

    var selectedImageUri: Uri? = null // 갤러리에서 선택할 사진을 담을 변수 초기값은 null로 설정
    fun openGalleryForDialog() { // 갤러리를 여는 함수.
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_EXTERNAL_STORAGE // READ_EXTERNAL_STORAGE 권한이 있는지 확인. 앱이 사용자의 저장소에서 파일을 읽을수 있도록 허용하는 권한. android.Manifest를 import 해와야한다.
            ) != PackageManager.PERMISSION_GRANTED // PackageManager.PERMISSION_GRANTED는 권한 승인상태. 승인상태가 아니라면, 아래의 실행문을 실행하게 된다.
        ) {
            ActivityCompat.requestPermissions( // requestPermissions 메서드를 이용해 사용자에게 해당 권한을 요청함.
                activity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_GALLERY_DIALOG
            )
        } else { // 권한이 없는상태가 아니라면(= 권한이 있는 상태라면)
            val intent =
                Intent(Intent.ACTION_PICK) // 이미지를 선택할 수 있는 액션을 가진 인텐트 객체를 생성, 선택을 하면 intent에 데이터가 담김
            intent.type = "image/*" // 갤러리에서 이미지 파일만 표시하도록 지정
            activity.startActivityForResult(
                intent,
                REQUEST_GALLERY_DIALOG
            ) // REQUEST_GALLERY_DIALOG = 3이면, 갤러리 액티비티를 시작하면서 이 액티비티에서 결과를 반환받을 것임을 알린다.
        } // 이 함수의 결과로 데이터를 선택하면 intent에 이미지 데이터(자료형은 Uri)가 담김
    }

    fun dig() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)
        setCanceledOnTouchOutside(false) // 다이얼로그 외의 부분을 눌렀을 때 꺼지지 않게 설정
        window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        binding.profileimage.setOnClickListener {
            openGalleryForDialog()
        }
        val saveButton = binding.save
        val cancelButton = binding.cancelbtn


        binding.off.setOnClickListener { notificationTime = 0 }
        binding.fivemin.setOnClickListener { notificationTime = 5 }
        binding.tenmin.setOnClickListener { notificationTime = 10 }
        binding.thirtymin.setOnClickListener { notificationTime = 30 }

        saveButton.setOnClickListener {
            val name = binding.name.text.toString()
            val number = binding.number.text.toString()
            val mail = binding.mail.text.toString()
            val uri = selectedImageUri // 갤러리에서 선택한 사진을 uri라는 변수에 담음
            Log.d("imageUri1", "imageUri:${uri}") // 잘 추가됫는지 Log 확인용
            if (condition(uri, name, number, mail)) {
                onClickedListener.onClicked(
                    uri,
                    name,
                    number,
                    mail,
                    notificationTime
                ) // condition()을 만족하면 온클릭리스너를 사용 가능
                scheduleAlarm(context, notificationTime) // 알림 예약 코드
                onDataAdded?.let { it() } // null때문에 직접 호출하진 못하고, ?.let{ it() } 으로. ContactList에서 뷰페이저 어뎁터 갱신하는데에 사용, 어뎁터를 갱신하는 이유는 프래그먼트를 갱신해 데이터 추가후 화면을 갱신하기 위함.
                dismiss()
            }
        }

        cancelButton.setOnClickListener {
            dismiss()
        }
    }

    interface ButtonClickListener {
        fun onClicked(uri: Uri?, name: String, number: String, email: String, notificationTime: Int)
    }

    fun setOnButtonClickListener(listener: ButtonClickListener) {
        onClickedListener = listener
    }


    // 빈칸 조건
    private fun condition(
        uri: Uri?,
        name: String,
        number: String,
        mail: String
    ): Boolean { // condition 함수를 통해 빈칸이 있을 경우 false 반환
        if (name.isEmpty() || number.isEmpty() || mail.isEmpty()) {
            Toast.makeText(context, "빈칸을 채워주세요.", Toast.LENGTH_LONG).show()
            return false
        }

        if (!numberCondition(number)) {
            Toast.makeText(context, "올바른 형식의 핸드폰 번호를 입력하세요. (하이픈)", Toast.LENGTH_LONG).show()
            return false
        }

        if (!mailCondition(mail)) {
            Toast.makeText(context, "올바른 형식의 이메일을 입력하세요.", Toast.LENGTH_LONG).show()
            return false
        }
        if (uri == null) {
            Toast.makeText(context, "사진을 선택해 주세요.", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    private fun mailCondition(mail: String): Boolean {

        val mail_condition = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}".toRegex()
        return mail_condition.matches(mail)
    }

    private fun numberCondition(number: String): Boolean {

        val number_condition = "^\\d{3}-\\d{3,4}-\\d{4}\$".toRegex()
        return number_condition.matches(number)
    }

    private fun scheduleAlarm(context: Context, minutesFromNow: Int) {
        if (minutesFromNow != 0) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val alarmIntent = Intent(context, AlarmReceiver::class.java).let {
                PendingIntent.getBroadcast(context, 0, it, PendingIntent.FLAG_IMMUTABLE)
            }

            val triggerTime =
                System.currentTimeMillis() + minutesFromNow * 1000  // 현재 시간으로부터 'minutesFromNow'초 후

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, alarmIntent)
        }
    }

    fun setImageUri(imageUri: Uri?) { // 다이어로그 이미지 추가용
//        Log.d("imageUri2", "imageUri:${imageUri}") // 잘 추가됫는지 Log 확인용
        this.selectedImageUri = imageUri // 갤러리에서 선택한 사진 데이터를 담음
        binding.profileimage.setImageURI(imageUri) // 갤러리에서 선택한 사진을 다이어로그에 표기
    }
}