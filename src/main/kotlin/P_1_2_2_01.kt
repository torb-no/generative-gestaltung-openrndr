import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.*
import org.openrndr.extensions.Screenshots
import org.openrndr.math.Vector2
import utils.QuitOnEsc
import java.lang.Double.max

fun main() = application {
    configure {
        width = 800
        height = 800
    }

    program {
        extend(QuitOnEsc())
        extend(Screenshots())


        fun sizedPixelData(image: ColorBuffer): ColorBufferShadow {
            val rtImage = renderTarget(width, height) { colorBuffer(type = ColorType.UINT8) }

            drawer.withTarget(rtImage) {
                drawer.image(image, Vector2.ZERO, drawer.bounds.width, drawer.bounds.height)
            }

            val shadow = rtImage.colorBuffer(0).shadow
            shadow.download()

            return shadow
        }

//        var colorComparator: Comparator<ColorRGBa> = Comparator { a, b ->
//            a.luminance.compareTo(b.luminance)
//        }



        // Used to access pixel data
        var image = loadImage("data/images/cheeta.jpg")
        var imageData = sizedPixelData(image)

        // TODO: use buffer instead of shadow for increased performance?

        window.drop.listen {
            it.files.firstOrNull()?.let { file ->
                image = loadImage(file)
                imageData = sizedPixelData(image)
            }
        }

        extend {
            val colors = mutableListOf<ColorRGBa>()
            val rectSize = max(mouse.position.x, 10.0)
            val rectSizeI = rectSize.toInt()

            for (x in 0 until width step rectSizeI) {
                for (y in 0 until height step rectSizeI) {
                    colors.add(imageData[x, y])
                }
            }

            colors.sortWith { a, b ->
                a.luminance.compareTo(b.luminance)
            }
            // TODO: sort the colors

            drawer.stroke = null

            var x = 0.0
            var y = 0.0
            colors.forEach {
                x += rectSize
                if (x > width) {
                    x = 0.0
                    y += rectSize
                }

                drawer.fill = it
                drawer.rectangle(y, x, rectSize, rectSize)
            }
        }
    }
}
