package dev.macrohq.swiftslayer.feature

import dev.macrohq.swiftslayer.feature.implementation.AutoRotation

class FeatureManager {
  private val features = mutableListOf<IFeature>()

  companion object {
    private var instance: FeatureManager? = null
    fun getInstance(): FeatureManager {
      if (instance == null) {
        instance = FeatureManager()
      }
      return instance!!
    }
  }

  fun loadFeatures(): List<IFeature> {
    val features = listOf(
      AutoRotation,
    )
    this.features.addAll(features)
    return this.features
  }

  fun disableFeatures(disablePassiveFeatures: Boolean) {
    this.features.forEach { feature ->
      if(!feature.isPassiveFeature || (feature.isPassiveFeature && disablePassiveFeatures)){
        feature.disable()
      }
    }
  }
}