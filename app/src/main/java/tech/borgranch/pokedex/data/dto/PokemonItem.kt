package tech.borgranch.pokedex.data.dto

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

const val POKEMON_ITEM_TABLE_NAME = "pokemon_item"

@Entity(tableName = POKEMON_ITEM_TABLE_NAME)
data class PokemonItem(
    @PrimaryKey
    @NotNull val name: String,
    val url: String,
    val image: String,
    val artwork: String,
    val dreamworld: String,
    val page: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(url)
        parcel.writeString(image)
        parcel.writeString(artwork)
        parcel.writeString(dreamworld)
        parcel.writeInt(page)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PokemonItem> {
        override fun createFromParcel(parcel: Parcel): PokemonItem {
            return PokemonItem(parcel)
        }

        override fun newArray(size: Int): Array<PokemonItem?> {
            return arrayOfNulls(size)
        }
    }
}
