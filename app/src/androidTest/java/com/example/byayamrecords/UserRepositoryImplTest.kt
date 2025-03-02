package com.example.byayamrecords

import com.example.byayamrecords.repository.UserRepositoryImpl
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*

@RunWith(MockitoJUnitRunner::class)
class UserRepositoryImplTest {

    private lateinit var userRepository: UserRepositoryImpl

    @Mock
    private lateinit var mockAuth: FirebaseAuth

    @Mock
    private lateinit var mockUser: FirebaseUser

    @Mock
    private lateinit var mockAuthResult: AuthResult

    @Before
    fun setUp() {
        userRepository = UserRepositoryImpl(mockAuth)
    }

    @Test
    fun `testloginsuccess`() {
        val email = "test@example.com"
        val password = "password"

        // Mock AuthResult to return a user
        whenever(mockAuthResult.user).thenReturn(mockUser)

        // Mock signInWithEmailAndPassword to return a successful Task<AuthResult>
        val successTask = mockSuccessTask(mockAuthResult)
        whenever(mockAuth.signInWithEmailAndPassword(email, password)).thenReturn(successTask)

        userRepository.login(email, password) { success, message ->
            assert(success)
            assert(message == "Login Successful")
        }
    }

    @Test
    fun `testloginfailure`() {
        val email = "test@example.com"
        val password = "wrongpassword"
        val exception = Exception("Authentication failed")

        // Mock signInWithEmailAndPassword to return a failed Task<AuthResult>
        val failedTask = mockFailedTask<AuthResult>(exception)
        whenever(mockAuth.signInWithEmailAndPassword(email, password)).thenReturn(failedTask)

        userRepository.login(email, password) { success, message ->
            assert(!success)
            assert(message == "Authentication failed")
        }
    }

    // Helper function to mock a successful Firebase Authentication task
    private fun <T> mockSuccessTask(result: T): Task<T> {
        val task: Task<T> = mock()
        whenever(task.isSuccessful).thenReturn(true)
        whenever(task.result).thenReturn(result)

        doAnswer {
            val listener = it.getArgument<OnCompleteListener<T>>(0)
            listener.onComplete(task)
            task
        }.whenever(task).addOnCompleteListener(any())

        return task
    }

    // Helper function to mock a failed Firebase Authentication task
    private fun <T> mockFailedTask(exception: Exception): Task<T> {
        val task: Task<T> = mock()
        whenever(task.isSuccessful).thenReturn(false)
        whenever(task.exception).thenReturn(exception)

        doAnswer {
            val listener = it.getArgument<OnCompleteListener<T>>(0)
            listener.onComplete(task)
            task
        }.whenever(task).addOnCompleteListener(any())

        return task
    }
}
