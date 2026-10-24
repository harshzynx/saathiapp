package harshzynx.saathi.data.model

data class ParentalTask(
    val id: Int,
    val title: String,
    val description: String,
    val isDone: Boolean = false
)
