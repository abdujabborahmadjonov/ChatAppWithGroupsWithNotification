package dev.abdujabbor.chatappsimple.models.modelOfGroups

class GroupModel {
    var id:String? = null
    var groupName:String?=null
    var description:String?=null
    var phottorl:String?=null
    var creator:String?=null

    constructor(id: String?, groupName: String?, description: String?, phottorl: String?,creator:String?) {
        this.id = id
        this.groupName = groupName
        this.description = description
        this.phottorl = phottorl
        this.creator = creator
    }
    constructor()
}