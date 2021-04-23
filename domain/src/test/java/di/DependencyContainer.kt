package di

import com.foobarust.domain.models.auth.AuthProfile
import com.foobarust.domain.models.user.UserDetail
import java.util.*

/**
 * Created by kevin on 4/21/21
 */
 
class DependencyContainer {

    val fakeIdToken = UUID.randomUUID().toString()

    val fakeAuthProfile = AuthProfile(
        id = USER_ID,
        email = EMAIL,
        username = USERNAME
    )

    val fakeUserDetail = UserDetail(
        id = USER_ID,
        username = USERNAME,
        email = EMAIL,
        name = "Hello World",
        phoneNum = "12345678",
        photoUrl = "about:blank",
        updatedAt = Date()
    )

    companion object {
        private val USER_ID = UUID.randomUUID().toString()
        private const val EMAIL = "testuser@foobarpp.com"
        private const val USERNAME = "testuser"
    }
}