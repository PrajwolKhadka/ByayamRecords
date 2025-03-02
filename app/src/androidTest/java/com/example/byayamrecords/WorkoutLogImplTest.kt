package com.example.byayamrecords.repository

import com.example.byayamrecords.model.WorkoutLog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import java.lang.reflect.Field

class WorkoutLogImplTest {

    private lateinit var workoutLogRepoImpl: WorkoutLofRepoImpl
    private lateinit var mockFirebaseAuth: FirebaseAuth
    private lateinit var mockFirebaseDatabase: FirebaseDatabase
    private lateinit var mockDatabaseReference: DatabaseReference
    private lateinit var mockUserReference: DatabaseReference
    private lateinit var mockProductReference: DatabaseReference

    @Before
    fun setUp() {
        mockFirebaseAuth = mock()
        mockFirebaseDatabase = mock()
        mockDatabaseReference = mock()
        mockUserReference = mock()
        mockProductReference = mock()

        // Mock FirebaseDatabase and DatabaseReference behavior
        `when`(mockFirebaseDatabase.reference).thenReturn(mockDatabaseReference)
        `when`(mockDatabaseReference.child("users")).thenReturn(mockUserReference)

        // Create an instance of WorkoutLofRepoImpl
        workoutLogRepoImpl = WorkoutLofRepoImpl()

        // Use reflection to inject mocked FirebaseAuth and FirebaseDatabase
        injectMockFirebaseAuth(workoutLogRepoImpl, mockFirebaseAuth)
        injectMockFirebaseDatabase(workoutLogRepoImpl, mockFirebaseDatabase)
    }

    @Test
    fun addLog_shouldReturnSuccess_whenUserIsAuthenticatedAndLogIsAddedSuccessfully() {
        // Arrange
        val userId = "user123"
        val workoutLog = WorkoutLog()
        val logId = "log123"

        `when`(mockFirebaseAuth.currentUser?.uid).thenReturn(userId)
        `when`(mockUserReference.child(userId).child("products").push().key).thenReturn(logId)
        `when`(mockUserReference.child(userId).child("products").child(logId).setValue(workoutLog))
            .thenAnswer {
                val callback = it.getArgument<DatabaseReference.CompletionListener>(0)
                callback.onComplete(null, mockDatabaseReference)
            }

        // Act
        var isSuccess = false
        var message = ""
        workoutLogRepoImpl.addLog(workoutLog) { success, msg ->
            isSuccess = success
            message = msg
        }

        // Assert
        assertTrue(isSuccess)
        assertEquals("Log Added successfully", message)
    }

    @Test
    fun addLog_shouldReturnFailure_whenUserIsNotAuthenticated() {
        // Arrange
        `when`(mockFirebaseAuth.currentUser?.uid).thenReturn(null)

        // Act
        var isSuccess = false
        var message = ""
        workoutLogRepoImpl.addLog(WorkoutLog()) { success, msg ->
            isSuccess = success
            message = msg
        }

        // Assert
        assertFalse(isSuccess)
        assertEquals("User not authenticated", message)
    }

    @Test
    fun updateLog_shouldReturnSuccess_whenUserIsAuthenticatedAndLogIsUpdatedSuccessfully() {
        // Arrange
        val userId = "user123"
        val productId = "product123"
        val data = mutableMapOf<String, Any>("key" to "value")

        `when`(mockFirebaseAuth.currentUser?.uid).thenReturn(userId)
        `when`(mockUserReference.child(userId).child("products").child(productId).updateChildren(data))
            .thenAnswer {
                val callback = it.getArgument<DatabaseReference.CompletionListener>(0)
                callback.onComplete(null, mockDatabaseReference)
            }

        // Act
        var isSuccess = false
        var message = ""
        workoutLogRepoImpl.updateLog(productId, data) { success, msg ->
            isSuccess = success
            message = msg
        }

        // Assert
        assertTrue(isSuccess)
        assertEquals("Log Updated successfully", message)
    }

    @Test
    fun deleteLog_shouldReturnSuccess_whenUserIsAuthenticatedAndLogIsDeletedSuccessfully() {
        // Arrange
        val userId = "user123"
        val productId = "product123"

        `when`(mockFirebaseAuth.currentUser?.uid).thenReturn(userId)
        `when`(mockUserReference.child(userId).child("products").child(productId).removeValue())
            .thenAnswer {
                val callback = it.getArgument<DatabaseReference.CompletionListener>(0)
                callback.onComplete(null, mockDatabaseReference)
            }

        // Act
        var isSuccess = false
        var message = ""
        workoutLogRepoImpl.deleteLog(productId) { success, msg ->
            isSuccess = success
            message = msg
        }

        // Assert
        assertTrue(isSuccess)
        assertEquals("Log Deleted successfully", message)
    }

    @Test
    fun getLogById_shouldReturnLog_whenUserIsAuthenticatedAndLogExists() {
        // Arrange
        val userId = "user123"
        val productId = "product123"
        val workoutLog = WorkoutLog()

        `when`(mockFirebaseAuth.currentUser?.uid).thenReturn(userId)
        `when`(mockUserReference.child(userId).child("products").child(productId))
            .thenReturn(mockProductReference)

        val mockDataSnapshot = mock<DataSnapshot>()
        `when`(mockDataSnapshot.exists()).thenReturn(true)
        `when`(mockDataSnapshot.getValue(WorkoutLog::class.java)).thenReturn(workoutLog)

        val valueEventListenerCaptor = argumentCaptor<ValueEventListener>()
        doAnswer {
            val listener = valueEventListenerCaptor.firstValue
            listener.onDataChange(mockDataSnapshot)
            null
        }.`when`(mockProductReference).addListenerForSingleValueEvent(valueEventListenerCaptor.capture())

        // Act
        var resultLog: WorkoutLog? = null
        var isSuccess = false
        var message = ""
        workoutLogRepoImpl.getLogById(productId) { log, success, msg ->
            resultLog = log
            isSuccess = success
            message = msg
        }

        // Assert
        assertNotNull(resultLog)
        assertTrue(isSuccess)
        assertEquals("Log fetched successfully", message)
    }

    @Test
    fun getAllLog_shouldReturnListOfLogs_whenUserIsAuthenticatedAndLogsExist() {
        // Arrange
        val userId = "user123"
        val workoutLog1 = WorkoutLog()
        val workoutLog2 = WorkoutLog()

        `when`(mockFirebaseAuth.currentUser?.uid).thenReturn(userId)
        `when`(mockUserReference.child(userId).child("products"))
            .thenReturn(mockProductReference)

        val mockDataSnapshot = mock<DataSnapshot>()
        `when`(mockDataSnapshot.exists()).thenReturn(true)
        `when`(mockDataSnapshot.children).thenReturn(listOf(
            mock<DataSnapshot>().apply {
                `when`(this.getValue(WorkoutLog::class.java)).thenReturn(workoutLog1)
            },
            mock<DataSnapshot>().apply {
                `when`(this.getValue(WorkoutLog::class.java)).thenReturn(workoutLog2)
            }
        ))

        val valueEventListenerCaptor = argumentCaptor<ValueEventListener>()
        doAnswer {
            val listener = valueEventListenerCaptor.firstValue
            listener.onDataChange(mockDataSnapshot)
            null
        }.`when`(mockProductReference).addListenerForSingleValueEvent(valueEventListenerCaptor.capture())

        // Act
        var resultLogs: List<WorkoutLog>? = null
        var isSuccess = false
        var message = ""
        workoutLogRepoImpl.getAllLog { logs, success, msg ->
            resultLogs = logs
            isSuccess = success
            message = msg
        }

        // Assert
        assertNotNull(resultLogs)
        assertEquals(2, resultLogs?.size)
        assertTrue(isSuccess)
        assertEquals("Logs fetched successfully", message)
    }

    // Helper function to inject mocked FirebaseAuth using reflection
    private fun injectMockFirebaseAuth(repo: WorkoutLofRepoImpl, mockAuth: FirebaseAuth) {
        val field: Field = WorkoutLofRepoImpl::class.java.getDeclaredField("firebaseAuth")
        field.isAccessible = true
        field.set(repo, mockAuth)
    }

    // Helper function to inject mocked FirebaseDatabase using reflection
    private fun injectMockFirebaseDatabase(repo: WorkoutLofRepoImpl, mockDatabase: FirebaseDatabase) {
        val field: Field = WorkoutLofRepoImpl::class.java.getDeclaredField("firebaseDatabase")
        field.isAccessible = true
        field.set(repo, mockDatabase)
    }
}