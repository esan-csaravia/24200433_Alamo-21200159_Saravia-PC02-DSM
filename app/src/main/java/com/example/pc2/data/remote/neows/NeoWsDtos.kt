package com.example.pc2.data.remote.neows

import com.google.gson.annotations.SerializedName

data class NeoWsFeedResponse(
    @SerializedName("near_earth_objects")
    val nearEarthObjects: Map<String, List<AsteroidDto>>?
)

data class AsteroidDto(
    @SerializedName("id")
    val id: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("is_potentially_hazardous_asteroid")
    val isPotentiallyHazardous: Boolean?,
    @SerializedName("estimated_diameter")
    val estimatedDiameter: EstimatedDiameterDto?,
    @SerializedName("close_approach_data")
    val closeApproachData: List<CloseApproachDataDto>?
)

data class EstimatedDiameterDto(
    @SerializedName("meters")
    val meters: DiameterUnitDto?
)

data class DiameterUnitDto(
    @SerializedName("estimated_diameter_min")
    val estimatedDiameterMin: Double?,
    @SerializedName("estimated_diameter_max")
    val estimatedDiameterMax: Double?
)

data class CloseApproachDataDto(
    @SerializedName("close_approach_date")
    val closeApproachDate: String?,
    @SerializedName("relative_velocity")
    val relativeVelocity: RelativeVelocityDto?,
    @SerializedName("miss_distance")
    val missDistance: MissDistanceDto?
)

data class RelativeVelocityDto(
    @SerializedName("kilometers_per_hour")
    val kilometersPerHour: String?
)

data class MissDistanceDto(
    @SerializedName("kilometers")
    val kilometers: String?
)
