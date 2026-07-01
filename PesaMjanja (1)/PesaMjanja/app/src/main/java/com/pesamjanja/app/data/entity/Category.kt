package com.pesamjanja.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    /** Emoji or icon-font key, kept as a simple string for now. */
    val icon: String,
    val isDefault: Boolean = false
)

/** Seed list used on first DB creation. Easy to extend without touching parsing logic. */
object DefaultCategories {
    val seed = listOf(
        Category(name = "Food", icon = "\uD83C\uDF5B", isDefault = true),
        Category(name = "Boda/Uber", icon = "\uD83D\uDEFA", isDefault = true),
        Category(name = "Bundles/Data", icon = "\uD83D\uDCF6", isDefault = true),
        Category(name = "Airtime", icon = "\uD83D\uDCDE", isDefault = true),
        Category(name = "Bae Budget", icon = "\u2764\uFE0F", isDefault = true),
        Category(name = "Gaming & Entertainment", icon = "\uD83C\uDFAE", isDefault = true),
        Category(name = "Black Tax/Home", icon = "\uD83C\uDFE0", isDefault = true),
        Category(name = "Notes/Printing", icon = "\uD83D\uDDA8\uFE0F", isDefault = true),
        Category(name = "M-Pesa Charges", icon = "\uD83D\uDCB8", isDefault = true),
        Category(name = "Fun/Hangouts", icon = "\uD83C\uDF89", isDefault = true),
    )
}
