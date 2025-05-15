package com.example.vibematch

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vibematch.api.ApiClient
import com.example.vibematch.api.ApiService
import com.example.vibematch.databinding.ActivityMainScreenBinding
import com.example.vibematch.models.Event
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainScreenActivity : AppCompatActivity() {
    private lateinit var apiService: ApiService
    private lateinit var binding: ActivityMainScreenBinding
    private var events: List<Event> = emptyList()
    private var currentEventIndex = 0
    private var likedEvents: Set<Int> = emptySet()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("MainScreenActivity", "onCreate called")

        // Инициализация API сервиса
        apiService = ApiClient.getClient().create(ApiService::class.java)

        // Проверяем, что кнопка лайка существует
        if (binding.btnLike == null) {
            Log.e("MainScreenActivity", "Like button not found in layout!")
        } else {
            Log.d("MainScreenActivity", "Like button found in layout")
        }

        // Настройка кнопок навигации
        binding.btnPrevious.setOnClickListener {
            if (currentEventIndex > 0) {
                currentEventIndex--
                updateEventDisplay()
            }
        }

        binding.btnNext.setOnClickListener {
            if (currentEventIndex < events.size - 1) {
                currentEventIndex++
                updateEventDisplay()
            }
        }

        // Настройка кнопки профиля
        binding.btnProfile.setOnClickListener {
            val userId = getSharedPreferences("UserProfile", MODE_PRIVATE).getInt("user_id", 0)
            if (userId > 0) {
                val intent = Intent(this, UserProfileActivity::class.java).apply {
                    putExtra("user_id", userId)
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Ошибка: ID пользователя не найден", Toast.LENGTH_SHORT).show()
            }
        }

        // Загрузка данных
        loadEvents()
    }

    private fun loadEvents() {
        lifecycleScope.launch {
            try {
                Log.d("MainScreenActivity", "Starting to load events")
                // Показываем индикатор загрузки
                binding.progressBar.visibility = View.VISIBLE
                binding.btnLike.isEnabled = false

                // Загружаем события
                val eventsResponse = apiService.getEvents()
                Log.d("MainScreenActivity", "Events response - code: ${eventsResponse.code()}, isSuccessful: ${eventsResponse.isSuccessful}")
                
                if (eventsResponse.isSuccessful) {
                    events = eventsResponse.body() ?: emptyList()
                    Log.d("MainScreenActivity", "Loaded ${events.size} events")
                    
                    // Загружаем лайки текущего пользователя
                    val userId = getSharedPreferences("UserProfile", MODE_PRIVATE).getInt("user_id", 0)
                    if (userId != 0) {
                        try {
                            Log.d("MainScreenActivity", "Loading liked events for user $userId")
                            val likedEventsResponse = apiService.getUserLikedEvents(userId)
                            Log.d("MainScreenActivity", "Liked events response - code: ${likedEventsResponse.code()}, isSuccessful: ${likedEventsResponse.isSuccessful}")
                            
                            if (likedEventsResponse.isSuccessful) {
                                likedEvents = likedEventsResponse.body()?.mapNotNull { event -> 
                                    event.eventId.takeIf { it > 0 }
                                }?.toSet() ?: emptySet()
                                Log.d("MainScreenActivity", "Loaded ${likedEvents.size} liked events")
                            } else {
                                Log.e("MainScreenActivity", "Failed to load liked events: ${likedEventsResponse.errorBody()?.string()}")
                            }
                        } catch (e: Exception) {
                            Log.e("MainScreenActivity", "Error loading liked events", e)
                            likedEvents = emptySet()
                        }
                    } else {
                        Log.w("MainScreenActivity", "User ID not found, skipping liked events load")
                    }
                    
                    // Обновляем отображение
                    currentEventIndex = 0
                    updateEventDisplay()
                } else {
                    val errorMessage = when (eventsResponse.code()) {
                        401 -> "Требуется авторизация"
                        else -> "Ошибка при загрузке событий: ${eventsResponse.errorBody()?.string() ?: "Неизвестная ошибка"}"
                    }
                    Log.e("MainScreenActivity", "Failed to load events: $errorMessage")
                    Toast.makeText(this@MainScreenActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("MainScreenActivity", "Error loading events", e)
                Toast.makeText(this@MainScreenActivity, "Ошибка сети: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                // Скрываем индикатор загрузки
                binding.progressBar.visibility = View.GONE
                binding.btnLike.isEnabled = true
            }
        }
    }

    private fun updateEventDisplay() {
        if (events.isEmpty()) {
            binding.tvEventTitle.text = "Нет доступных событий"
            binding.tvEventLocation.text = ""
            binding.tvEventDateTime.text = ""
            binding.btnLike.isEnabled = false
            return
        }

        val event = events[currentEventIndex]
        Log.d("MainScreenActivity", "Updating display for event: ${event.eventId} - ${event.title}")
        
        binding.tvEventTitle.text = event.title
        binding.tvEventLocation.text = event.location
        
        // Форматируем дату и время
        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        binding.tvEventDateTime.text = event.startTime?.let { dateFormat.format(it) } ?: "Дата не указана"

        // Обновляем состояние кнопки лайка
        val isLiked = event.eventId?.let { likedEvents.contains(it) } ?: false
        Log.d("MainScreenActivity", "Event ${event.eventId} like state: $isLiked")
        
        binding.btnLike.apply {
            isEnabled = true
            text = if (isLiked) "Убрать лайк" else "Лайк"
            setIconResource(if (isLiked) android.R.drawable.btn_star_big_on else android.R.drawable.btn_star_big_off)
            
            // Удаляем предыдущий обработчик, если он есть
            setOnClickListener(null)
            
            // Добавляем новый обработчик с логированием
            setOnClickListener { view ->
                Log.d("MainScreenActivity", "Like button clicked for event: ${event.eventId}")
                view.isEnabled = false // Блокируем кнопку сразу при нажатии
                
                // Проверяем, что eventId существует и положительный
                if (event.eventId != null && event.eventId > 0) {
                    Log.d("MainScreenActivity", "Calling toggleLike for event: ${event.eventId}")
                    toggleLike(event.eventId)
                } else {
                    Log.e("MainScreenActivity", "Invalid event ID: ${event.eventId}")
                    Toast.makeText(this@MainScreenActivity, "Ошибка: некорректный ID события", Toast.LENGTH_SHORT).show()
                    view.isEnabled = true // Разблокируем кнопку в случае ошибки
                }
            }
        }

        // Обновляем состояние кнопок навигации
        binding.btnPrevious.isEnabled = currentEventIndex > 0
        binding.btnNext.isEnabled = currentEventIndex < events.size - 1

        // Обработка клика по карточке события
        binding.root.setOnClickListener {
            if (event.eventId != null && event.eventId > 0) {
                val intent = Intent(this, EventDetailsActivity::class.java).apply {
                    putExtra("event_id", event.eventId)
                }
                startActivity(intent)
            }
        }
    }

    private fun toggleLike(eventId: Int) {
        val userId = getSharedPreferences("UserProfile", MODE_PRIVATE).getInt("user_id", 0)
        if (userId == 0) {
            Log.e("MainScreenActivity", "User ID not found in SharedPreferences")
            Toast.makeText(this, "Ошибка: ID пользователя не найден", Toast.LENGTH_SHORT).show()
            return
        }

        if (eventId <= 0) {
            Log.e("MainScreenActivity", "Invalid event ID: $eventId")
            Toast.makeText(this, "Ошибка: некорректный ID события", Toast.LENGTH_SHORT).show()
            return
        }

        // Блокируем кнопку на время запроса
        binding.btnLike.isEnabled = false
        Log.d("MainScreenActivity", "Starting like toggle for eventId: $eventId, userId: $userId")

        lifecycleScope.launch {
            try {
                // Проверяем текущее состояние лайка
                val isCurrentlyLiked = likedEvents.contains(eventId)
                Log.d("MainScreenActivity", "Current like state: ${if (isCurrentlyLiked) "liked" else "not liked"}")
                
                // Отправляем запрос на сервер
                Log.d("MainScreenActivity", "Sending toggle like request...")
                val response = apiService.toggleEventLike(eventId, userId)
                Log.d("MainScreenActivity", "Response received - code: ${response.code()}, isSuccessful: ${response.isSuccessful}")
                
                if (response.isSuccessful) {
                    val eventLike = response.body()
                    Log.d("MainScreenActivity", "Response body: $eventLike")
                    
                    // Обновляем список лайков
                    val newLikedEvents = likedEvents.toMutableSet()
                    if (eventLike != null) {
                        Log.d("MainScreenActivity", "Adding like for event $eventId")
                        newLikedEvents.add(eventId)
                    } else {
                        Log.d("MainScreenActivity", "Removing like for event $eventId")
                        newLikedEvents.remove(eventId)
                    }
                    likedEvents = newLikedEvents
                    
                    // Обновляем UI
                    updateEventDisplay()
                    
                    // Показываем сообщение об успехе
                    val message = if (eventLike != null) "Событие добавлено в избранное" else "Событие удалено из избранного"
                    Toast.makeText(this@MainScreenActivity, message, Toast.LENGTH_SHORT).show()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("MainScreenActivity", "Error response: code=${response.code()}, body=$errorBody")
                    
                    val errorMessage = when (response.code()) {
                        404 -> "Событие не найдено"
                        400 -> "Неверный запрос: $errorBody"
                        401 -> "Требуется авторизация"
                        500 -> "Ошибка сервера: $errorBody"
                        else -> "Ошибка при обновлении лайка: ${errorBody ?: "Неизвестная ошибка"}"
                    }
                    Toast.makeText(this@MainScreenActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    
                    // Восстанавливаем предыдущее состояние
                    updateEventDisplay()
                }
            } catch (e: Exception) {
                Log.e("MainScreenActivity", "Exception during like toggle", e)
                Toast.makeText(this@MainScreenActivity, "Ошибка сети: ${e.message}", Toast.LENGTH_SHORT).show()
                // Восстанавливаем предыдущее состояние
                updateEventDisplay()
            } finally {
                // Разблокируем кнопку
                binding.btnLike.isEnabled = true
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Перезагружаем данные при возвращении на экран
        loadEvents()
    }
}

class EventAdapter(private val onEventClick: (Event) -> Unit) : RecyclerView.Adapter<EventAdapter.ViewHolder>() {
    private var events: List<Event> = emptyList()
    private var likedEvents: Set<Int> = emptySet()

    fun submitList(newEvents: List<Event>, newLikedEvents: Set<Int>) {
        events = newEvents
        likedEvents = newLikedEvents
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = events[position]
        holder.bind(event, likedEvents.contains(event.eventId))
    }

    override fun getItemCount() = events.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvTitle: TextView = view.findViewById(R.id.tvEventTitle)
        private val tvLocation: TextView = view.findViewById(R.id.tvEventLocation)
        private val tvDateTime: TextView = view.findViewById(R.id.tvEventDateTime)
        private val btnLike: MaterialButton = view.findViewById(R.id.btnLike)

        fun bind(event: Event, isLiked: Boolean) {
            tvTitle.text = event.title
            tvLocation.text = event.location
            
            // Форматируем дату и время
            val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            tvDateTime.text = event.startTime?.let { dateFormat.format(it) } ?: "Дата не указана"

            // Обновляем состояние кнопки лайка
            btnLike.apply {
                text = if (isLiked) "Убрать лайк" else "Лайк"
                setIconResource(if (isLiked) android.R.drawable.btn_star_big_on else android.R.drawable.btn_star_big_off)
                setOnClickListener {
                    event.eventId?.let { eventId ->
                        if (eventId > 0) {
                            toggleLike(eventId)
                        } else {
                            Toast.makeText(itemView.context, "Ошибка: некорректный ID события", Toast.LENGTH_SHORT).show()
                        }
                    } ?: run {
                        Toast.makeText(itemView.context, "Ошибка: ID события не найден", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            // Обработка клика по карточке события
            itemView.setOnClickListener {
                onEventClick(event)
            }
        }

        private fun toggleLike(eventId: Int) {
            val userId = itemView.context.getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
                .getInt("user_id", 0)
            if (userId == 0) {
                Toast.makeText(itemView.context, "Ошибка: ID пользователя не найден", Toast.LENGTH_SHORT).show()
                return
            }

            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val apiService = ApiClient.getClient().create(ApiService::class.java)
                    val response = apiService.toggleEventLike(eventId, userId)
                    if (response.isSuccessful) {
                        // Обновляем список лайков
                        val newLikedEvents = likedEvents.toMutableSet()
                        if (likedEvents.contains(eventId)) {
                            newLikedEvents.remove(eventId)
                        } else {
                            newLikedEvents.add(eventId)
                        }
                        submitList(events, newLikedEvents)
                    } else {
                        Toast.makeText(itemView.context, "Ошибка при обновлении лайка", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(itemView.context, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}