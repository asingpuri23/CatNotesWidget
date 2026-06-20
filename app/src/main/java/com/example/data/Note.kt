package com.example.data

import androidx.room.*
import org.json.JSONArray
import org.json.JSONObject

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val themeId: String = "hello_kitty",
    val stickersJson: String = "[]" // JSON representation of List<NoteSticker>
) {
    // Helper to extract stickers dynamically
    fun getStickers(): List<NoteSticker> {
        return try {
            val list = mutableListOf<NoteSticker>()
            val arr = JSONArray(stickersJson)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    NoteSticker(
                        stickerId = obj.optString("stickerId", ""),
                        relativeX = obj.optDouble("relativeX", 0.5).toFloat(),
                        relativeY = obj.optDouble("relativeY", 0.5).toFloat(),
                        scale = obj.optDouble("scale", 1.0).toFloat(),
                        rotation = obj.optDouble("rotation", 0.0).toFloat()
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    companion object {
        fun createStickersJson(stickers: List<NoteSticker>): String {
            return try {
                val arr = JSONArray()
                for (s in stickers) {
                    val obj = JSONObject()
                    obj.put("stickerId", s.stickerId)
                    obj.put("relativeX", s.relativeX)
                    obj.put("relativeY", s.relativeY)
                    obj.put("scale", s.scale)
                    obj.put("rotation", s.rotation)
                    arr.put(obj)
                }
                arr.toString()
            } catch (e: Exception) {
                "[]"
            }
        }
    }
}

data class NoteSticker(
    val stickerId: String,
    val relativeX: Float, // 0.0 to 1.0 relative to notepad bounds
    val relativeY: Float, // 0.0 to 1.0 relative to notepad bounds
    val scale: Float = 1.0f,
    val rotation: Float = 0.0f
)
