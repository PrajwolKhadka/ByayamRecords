package com.example.byayamrecords

import com.example.byayamrecords.model.BMIRecord
import com.example.byayamrecords.repository.BMIRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.OnCompleteListener
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.*
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*

@RunWith(MockitoJUnitRunner::class)
class BMIRepositoryImplTest {

    private lateinit var bmiRepository: BMIRepositoryImpl

    @Mock
    private lateinit var mockDatabase: FirebaseDatabase

    @Mock
    private lateinit var mockUsersRef: DatabaseReference

    @Mock
    private lateinit var mockAuth: FirebaseAuth

    @Mock
    private lateinit var mockUser: FirebaseUser

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        bmiRepository = BMIRepositoryImpl()

        whenever(mockDatabase.reference).thenReturn(mockUsersRef)
        whenever(mockUsersRef.child("users")).thenReturn(mockUsersRef)
        whenever(mockUsersRef.child("testUserId")).thenReturn(mockUsersRef)
        whenever(mockUsersRef.child("bmiRecords")).thenReturn(mockUsersRef)
        whenever(mockAuth.currentUser).thenReturn(mockUser)
        whenever(mockUser.uid).thenReturn("testUserId")
    }

    private fun<T> mockTask(success: Boolean, exception: Exception? = null): Task<T> {
        val task: Task<T> = mock()
        whenever(task.isSuccessful).thenReturn(success)
        whenever(task.exception).thenReturn(exception)

        doAnswer { invocation ->
            val listener = invocation.getArgument<OnCompleteListener<T>>(0)
            listener.onComplete(task)
            null
        }.whenever(task).addOnCompleteListener(any())

        return task
    }

    @Test
    fun testsaveBMIsuccess() {
        val record = BMIRecord("testRecordId", "24.5", 70.0, 1.75, "Normal", "2025-03-02")

        doReturn(mockTask<Void>(true)).whenever(mockUsersRef).setValue(any())

        bmiRepository.saveBMI(record)

        verify(mockUsersRef).child("users")
        verify(mockUsersRef).child("testUserId")
        verify(mockUsersRef).child("bmiRecords")
        verify(mockUsersRef).child("testRecordId")
        verify(mockUsersRef).setValue(any())
    }

    @Test
    fun testsaveBMIfailure() {
        val record = BMIRecord("testRecordId", "24.5", 70.0, 1.75, "Normal", "2025-03-02")

        val exception = Exception("Failed to save data")
        doReturn(mockTask<Void>(false, exception)).whenever(mockUsersRef).setValue(any())

        bmiRepository.saveBMI(record)

        verify(mockUsersRef).child("users")
        verify(mockUsersRef).child("testUserId")
        verify(mockUsersRef).child("bmiRecords")
        verify(mockUsersRef).child("testRecordId")
        verify(mockUsersRef).setValue(any())
    }

    @Test
    fun testdeleteBMIsuccess() {
        val recordId = "testRecordId"

        doReturn(mockTask<Void>(true)).whenever(mockUsersRef).removeValue()

        bmiRepository.deleteBMI(recordId)

        verify(mockUsersRef).child("users")
        verify(mockUsersRef).child("testUserId")
        verify(mockUsersRef).child("bmiRecords")
        verify(mockUsersRef).child(recordId)
        verify(mockUsersRef).removeValue()
    }

    @Test
    fun testdeleteBMIfailure() {
        val recordId = "testRecordId"

        val exception = Exception("Failed to delete data")
        doReturn(mockTask<Void>(false, exception)).whenever(mockUsersRef).removeValue()

        bmiRepository.deleteBMI(recordId)

        verify(mockUsersRef).child("users")
        verify(mockUsersRef).child("testUserId")
        verify(mockUsersRef).child("bmiRecords")
        verify(mockUsersRef).child(recordId)
        verify(mockUsersRef).removeValue()
    }

    @Test
    fun testgetAllBMIsuccess() {
        val mockSnapshot: DataSnapshot = mock()
        val bmiRecords = listOf(
            BMIRecord("id1", "24.5", 70.0, 1.75, "Normal", "2025-03-02"),
            BMIRecord("id2", "22.3", 65.0, 1.80, "Underweight", "2025-03-01")
        )

        whenever(mockSnapshot.children).thenReturn(bmiRecords.map { mockDataSnapshot(it) })

        doAnswer { invocation ->
            val listener = invocation.getArgument<ValueEventListener>(0)
            listener.onDataChange(mockSnapshot)
            null
        }.whenever(mockUsersRef).addValueEventListener(any())

        bmiRepository.getAllBMIs { result ->
            assert(result.size == 2)
        }

        verify(mockUsersRef).child("users")
        verify(mockUsersRef).child("testUserId")
        verify(mockUsersRef).child("bmiRecords")
        verify(mockUsersRef).addValueEventListener(any())
    }

    private fun mockDataSnapshot(record: BMIRecord): DataSnapshot {
        val snapshot: DataSnapshot = mock()
        whenever(snapshot.getValue(BMIRecord::class.java)).thenReturn(record)
        whenever(snapshot.child("bmi").getValue(String::class.java)).thenReturn(record.bmi)
        whenever(snapshot.child("weight").getValue(Double::class.java)).thenReturn(record.weight)
        whenever(snapshot.child("height").getValue(Double::class.java)).thenReturn(record.height)
        return snapshot
    }
}
