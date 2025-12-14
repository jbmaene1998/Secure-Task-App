package com.securetaskapp.service

import com.securetaskapp.common.NotFoundException
import com.securetaskapp.dto.TaskCreateRequest
import com.securetaskapp.dto.TaskResponse
import com.securetaskapp.model.Task
import com.securetaskapp.model.User
import com.securetaskapp.repository.TaskRepository
import com.securetaskapp.repository.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class TaskServiceImplTest {

    @Mock
    lateinit var taskRepository: TaskRepository

    @Mock
    lateinit var userRepository: UserRepository

    private lateinit var taskService: TaskServiceImpl

    @BeforeEach
    fun setUp() {
        taskService = TaskServiceImpl(taskRepository, userRepository)
    }

    @Test
    fun `createTask should save task for existing user`() {
        val userId = 1L

        val user = User().apply {
            email = "user@example.com"
            name = "User"
            roles = "USER"
        }

        val request = TaskCreateRequest().apply {
            title = "My Task"
            description = "Description"
        }

        given(userRepository.findById(userId)).willReturn(Optional.of(user))

        val savedTask = Task().apply {
            this.user = user
            title = request.title
            description = request.description
            isDone = false
        }

        given(taskRepository.save(any(Task::class.java))).willReturn(savedTask)

        val response: TaskResponse = taskService.createTask(userId, request)

        assertNull(response.id)
        assertEquals("My Task", response.title)
        assertEquals("Description", response.description)

        assertFalse(response.isDone)

        assertNull(response.userId)

        verify(taskRepository).save(any(Task::class.java))
    }

    @Test
    fun `getTask with unknown task should throw NotFoundException`() {
        val userId = 1L
        val taskId = 99L

        given(taskRepository.findByIdAndUserId(taskId, userId)).willReturn(Optional.empty())

        assertThrows(NotFoundException::class.java) {
            taskService.getTask(userId, taskId)
        }
    }
}
