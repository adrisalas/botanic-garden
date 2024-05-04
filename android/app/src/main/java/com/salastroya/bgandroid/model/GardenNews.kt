package com.salastroya.bgandroid.model

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


data class GardenNews(
    val id: Int,
    val title: String,
    val subtitle: String,
    val description: String,
    val date: LocalDateTime,
    val image: String? = null
) {
    fun getFormattedDate(): String {
        val formatter = DateTimeFormatter.ofPattern("EEE dd MMM")
        return date.format(formatter)
    }
}

val listOfNews: List<GardenNews> = listOf(
    GardenNews(
        id = 1,
        title = "Cine nocturno",
        subtitle = "Cine nocturno al aire libre en el pabellón 2",
        description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent ut auctor ex. " +
                "Sed convallis ligula massa, vitae rhoncus nulla gravida vitae. Suspendisse nunc ante, varius a molestie quis, malesuada at massa. In euismod," +
                " diam id molestie laoreet, lacus lectus pharetra mi, eget consectetur nisl ipsum nec erat. Proin laoreet vitae tellus nec pretium. Nulla semper" +
                " est ac diam posuere blandit. Vestibulum sit amet ex mi. Nulla turpis est, imperdiet non ipsum in, tincidunt pretium magna. Vestibulum ante ipsum " +
                "primis in faucibus orci luctus et ultrices posuere cubilia curae; Vivamus nec ultrices nisi. Vestibulum tincidunt nibh eu sem maximus aliquet. Duis sollicitudin," +
                " eros at aliquet egestas, ante nibh euismod lectus, at rutrum enim sem quis quam.",
        date = LocalDateTime.of(2024, 6, 23, 18, 20)
    ),
    GardenNews(
        id = 2,
        title = "Presentación libro botánica",
        subtitle = "Clara Luna presenta su nuevo libro de botánica",
        description = "Fusce hendrerit fringilla nibh, id egestas nulla lobortis et. " +
                "Phasellus fermentum erat eros, vitae euismod elit porta non. Praesent dolor tellus," +
                " ultricies nec imperdiet eu, ultrices in tellus. Curabitur euismod blandit nibh sit amet hendrerit." +
                " Quisque mollis ornare leo, a suscipit nunc pulvinar non. Morbi a neque in erat egestas convallis. " +
                "Praesent gravida a turpis id varius. Aliquam sed turpis odio. Nunc rutrum, lectus vestibulum posuere" +
                " pulvinar, justo mauris auctor nibh, quis malesuada elit magna pharetra ante.",
        date = LocalDateTime.of(2024, 7, 29, 10, 30)
    ),
    GardenNews(
        id = 3,
        title = "Espectáculo de luces",
        subtitle = "Vuelve el artista Paco a decorar el jardín un año más",
        description = "Fusce hendrerit fringilla nibh, id egestas nulla lobortis et. " +
                "Phasellus fermentum erat eros, vitae euismod elit porta non. Praesent dolor tellus," +
                " ultricies nec imperdiet eu, ultrices in tellus. Curabitur euismod blandit nibh sit amet hendrerit." +
                " Quisque mollis ornare leo, a suscipit nunc pulvinar non. Morbi a neque in erat egestas convallis. " +
                "Praesent gravida a turpis id varius. Aliquam sed turpis odio. Nunc rutrum, lectus vestibulum posuere" +
                " pulvinar, justo mauris auctor nibh, quis malesuada elit magna pharetra ante.",
        date = LocalDateTime.of(2024, 7, 12, 10, 0)
    )
)
