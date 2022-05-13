package tech.borgranch.pokedex.data.dto

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

const val POKEMON_DETAIL_TABLE_NAME = "pokemon_detail"

@Entity(tableName = POKEMON_DETAIL_TABLE_NAME)
data class PokemonDetail(
    @PrimaryKey
    @NotNull
    val name: String,
    val height: Int = 0,
    val weight: Int = 0,
    val species: String? = null,
    val abilities: String? = null,
    val sprites: String? = null,
    val types: String? = null,
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    fun getHeightString(): String {
        return "${this.height.toFloat() / 10.0} m"
    }

    fun getWeightString(): String {
        return "${this.weight.toFloat() / 10.0} kg"
    }

    fun getAbilitiesString(): String {
        return this.abilities ?: ""
    }

    fun getTypesString(): String {
        return this.types ?: ""
    }

    fun getFrontSprite(): String {
        return this.sprites?.split(", ")?.get(0) ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(height)
        parcel.writeInt(weight)
        parcel.writeString(species)
        parcel.writeString(abilities)
        parcel.writeString(sprites)
        parcel.writeString(types)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PokemonDetail> {
        override fun createFromParcel(parcel: Parcel): PokemonDetail {
            return PokemonDetail(parcel)
        }

        override fun newArray(size: Int): Array<PokemonDetail?> {
            return arrayOfNulls(size)
        }
    }
}
