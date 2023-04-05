package dev.abdujabbor.chatappsimple.models
 class MyPerson {
     var uid: String? = null
     var photoUrl: String? = null
     var displayName: String?= null
     var tick:String? = null


     constructor(uid: String?, photoUrl: String?, displayName: String?,tick:String?) {
         this.uid = uid
         this.tick = tick
         this.photoUrl = photoUrl
         this.displayName = displayName
     }
     constructor()
 }
