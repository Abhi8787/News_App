package com.example.news_app.auth


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.news_app.R
import com.google.firebase.auth.FirebaseAuth

class RegisterViewModel : ViewModel() {


    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String>
        get() = _toastMessage

    private val _updateIntent = MutableLiveData<String>()
    val updateIntent : LiveData<String>
        get() = _updateIntent


    private val mAuth = FirebaseAuth.getInstance()
    private val currentUser = mAuth.currentUser
    val email = currentUser?.email

    val emails = MutableLiveData<String>(email)
    val name = MutableLiveData(currentUser?.displayName)
    val phone = MutableLiveData<String>("")
    val city = MutableLiveData<String>("")
    val state = MutableLiveData<String>("")
    val pinCode = MutableLiveData<String>("")


    val selectedGender = MutableLiveData<String>()

    fun onGenderSelected(buttonId: Int) {
        val gender = when (buttonId) {
            R.id.maleRadioButton -> "Male"
            R.id.femaleRadioButton -> "Female"
            R.id.otherRadioButton -> "Other"
            else -> {}
        }
        selectedGender.value = gender.toString()
    }

//    val selectedType = MutableLiveData<String>()

//    fun onTypeSelected(buttonId: Int) {
//        val type = when (buttonId) {
//            R.id.collegeStudent -> "College_Student"
//            R.id.individual -> "Individual"
//            else -> {}
//        }
//        selectedType.value = type.toString()
//    }



     fun  getUser() : User?
     {
          val user = currentUser?.let {
              User(
                  it.uid ,
                  currentUser.photoUrl.toString(),
                  name.value.toString().trim(),
                  emails.value.toString().trim(),
                  selectedGender.value.toString(),
                  phone.value.toString().trim(),
                  city.value.toString().trim(),
                  pinCode.value.toString().trim(),
                  state.value.toString().trim())
          }

         return user
     }


    fun registerUser()
     {
         val user = getUser()

         if (user != null) {
             if (user.phone.isEmpty() || user.gender.isEmpty() || user.city.isEmpty() || user.pinCode.isEmpty()
                 || user.state.isEmpty()) {
                _toastMessage.value = "Please fill all the fields"
             }

             else if(user.phone.length < 10){
                 _toastMessage.value = "Enter 10 Digit Mobile Number"
             }

             else if(user.pinCode.length < 6){
                 _toastMessage.value = "Enter 6 Digit Pin Code"
             }

             else
             {
                 val usersDao = UserDao()
                 usersDao.addUser(user)
                 _updateIntent.value = "Abhishek"
             }
         }
     }
}