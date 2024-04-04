package com.raf.catalist.cats.domain

// Compiler will automatically generate all getters and setters for data class.
// We're val for variables whose value never changes, var for variables whose value can chage.
data class BreedData(
    val weight: Weight,
    val id: String,
    val name: String,
    val cfaUrl: String,
    val vetstreetUrl: String,
    val vcahospitalsUrl: String,
    val temperament: String,
    val origin: String,
    val countryCodes: String,
    val countryCode: String,
    val description: String,
    val lifeSpan: String,
    val indoor: Int,
    val lap: Int,
    val altNames: String,
    val adaptability: Int,
    val affectionLevel: Int,
    val childFriendly: Int,
    val dogFriendly: Int,
    val energyLevel: Int,
    val grooming: Int,
    val healthIssues: Int,
    val intelligence: Int,
    val sheddingLevel: Int,
    val socialNeeds: Int,
    val strangerFriendly: Int,
    val vocalisation: Int,
    val experimental: Int,
    val hairless: Int,
    val natural: Int,
    val rare: Int,
    val rex: Int,
    val suppressedTail: Int,
    val shortLegs: Int,
    val wikipediaUrl: String,
    val hypoallergenic: Int,
    val referenceImageId: String
)

data class Weight(
    val imperial: String,
    val metric: String
)
