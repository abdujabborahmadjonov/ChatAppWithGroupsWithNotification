package dev.abdujabbor.chatappsimple.models
data class ChatMessage(
    var id:String="",
    val senderName:String="",
    val imageLink:String="",
    var senderId: String = "",
    val receiverId: String = "",
    val message: String = "",
    val timestamp: String = "",
    var imageforchat:String = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSi_juY1LxwpDuqtI15_j8dYPKKsm_ZUDkNQA&usqp=CAU"

)