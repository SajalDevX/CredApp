package me.mrsajal.credapp


import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse(
    val items: List<Item>
)

@Serializable
data class Item(
    val open_state: OpenState? = null,
    val closed_state: ClosedState? = null,
    val cta_text: String? = ""
)

@Serializable
data class OpenState(
    val body: OpenBody? = null
)

@Serializable
data class OpenBody(
    val title: String? = null,
    val subtitle: String? = null,
    val card: Card? = null,
    val items: List<EmiItem> = emptyList(),
    val footer: String? = null
)

@Serializable
data class Card(
    val header: String?,
    val description: String? = null,
    val max_range: Int? = null,
    val min_range: Int? = null
)

@Serializable
data class EmiItem(
    val emi: String? = null,
    val duration: String? = null,
    val title: String? = null,
    val subtitle: String? = null,
    val icon: String? = null,
    val tag: String? = null
)

@Serializable
data class ClosedState(
    val body: ClosedBody?
)

@Serializable
data class ClosedBody(
    val key1: String? = null,
    val key2: String? = null
)
