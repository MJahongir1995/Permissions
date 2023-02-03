package uz.jahongir.permissions.models

import android.graphics.Bitmap

class MyContacts :java.io.Serializable{
    var name:String = ""
    var number:String = ""
    var image:Bitmap? = null

    constructor(name: String, number: String, image: Bitmap?) {
        this.name = name
        this.number = number
        this.image = image
    }

    constructor(name: String, number: String) {
        this.name = name
        this.number = number
    }

}
