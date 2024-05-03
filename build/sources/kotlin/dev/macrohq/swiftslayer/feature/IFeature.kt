package dev.macrohq.swiftslayer.feature

interface IFeature {
  var enabled: Boolean
  val featureName: String
  val isPassiveFeature: Boolean
  var forceEnable: Boolean
  var failed: Boolean
  var success: Boolean

  fun disable()
  fun canEnable(): Boolean
  fun setSucceeded(succeeded: Boolean = true)
  fun succeeded(): Boolean
  fun failed(): Boolean
}