package org.angigaram.models

data class User(val guid: String,
                val name: String,
                val timeZone: String,
                val photoUrl: String)