package model

import kotlinx.serialization.Serializable

@Serializable
data class People(
    val id:Long,
    val name:String,
    val age: Int,
    val isActive: Boolean,
    val role: Role
)

enum class Role{
    ADMIN,
    USER
}