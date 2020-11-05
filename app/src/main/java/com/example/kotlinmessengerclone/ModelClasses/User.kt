package com.example.kotlinmessengerclone.ModelClasses

class User {
    private var uid: String = ""
    private var username: String = ""
    private var profile: String = ""
    private var cover: String = ""
    private var status: String = ""
    private var search: String = ""
    private var facebook: String = ""
    private var instagram: String = ""
    private var website: String = ""

    constructor()
    constructor(
        uid: String,
        username: String,
        profile: String,
        cover: String,
        status: String,
        search: String,
        facebook: String,
        instagram: String,
        website: String
    ) {
        this.uid = uid
        this.username = username
        this.profile = profile
        this.cover = cover
        this.status = status
        this.search = search
        this.facebook = facebook
        this.instagram = instagram
        this.website = website
    }

    fun getUid():String{
        return uid
    }
    fun getUsername():String{
        return username
    }
    fun getProfile():String{
        return profile
    }
    fun getCover():String{
        return cover
    }
    fun getStatus():String{
        return status
    }
    fun getSearch():String{
        return search
    }
    fun getFacebook():String{
        return facebook
    }
    fun getInstagram():String{
        return instagram
    }
    fun getWebsite():String{
        return website
    }
    fun setUid(uid: String){
        this.uid = uid
    }
    fun setUsername(username: String){
        this.username = username
    }
    fun setProfile(profile: String){
        this.profile = profile
    }
    fun setCover(cover: String){
        this.cover = cover
    }
    fun setStatus(status: String){
        this.status = status
    }
    fun setSearch(search: String){
        this.search = search
    }
    fun setFacebook(facebook: String){
        this.facebook = facebook
    }
    fun setInstagram(instagram: String){
        this.instagram = instagram
    }
    fun setWebsite(website: String){
        this.website = website
    }


}