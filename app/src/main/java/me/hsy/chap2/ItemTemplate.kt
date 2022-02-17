package me.hsy.chap2

class ItemTemplate constructor(imageId: Int, title: String, description: String, gender: String, location:  String, breed: String, age: String){
    private var imageId: Int = imageId
    private var title: String = title
    private var description: String = description
    private var gender: String = gender
    private var breed: String = breed
    private var location: String = location
    private var age: String = age

    fun getImageId(): Int {
        return this.imageId
    }
    fun getDescription(): String {
        return this.description
    }
    fun getTitle(): String{
        return this.title
    }
    fun getGender(): String{
        return this.gender
    }
    fun getBreed(): String{
        return this.breed
    }
    fun getAge(): String{
        return this.age
    }
    fun getLocation(): String{
        return this.location
    }
}