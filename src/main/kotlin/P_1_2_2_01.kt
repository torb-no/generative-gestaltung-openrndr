import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.*
import org.openrndr.extensions.Screenshots
import org.openrndr.math.Vector2
import utils.QuitOnEsc
import java.lang.Double.max

// FIXME: Add swatch export

fun main() = application {
    configure {
        width = 800
        height = 800
    }

    program {
        extend(QuitOnEsc())
        extend(Screenshots())

        fun sizedPixelData(image: ColorBuffer): ColorBufferShadow {
            val rtImage = renderTarget(width, height) { colorBuffer() }

            drawer.withTarget(rtImage) {
                drawer.image(image, Vector2.ZERO, drawer.bounds.width, drawer.bounds.height)
            }

            val shadow = rtImage.colorBuffer(0).shadow
            shadow.download()

            return shadow
        }

        // Used to access pixel data
        var image = loadImage("data/images/cheeta.jpg")
        var imageData = sizedPixelData(image)

        window.drop.listen {
            it.files.firstOrNull()?.let { file ->
                image = loadImage(file)
                imageData = sizedPixelData(image)
            }
        }

        var colorComparator: Comparator<ColorRGBa>? = null

        keyboard.keyUp.listen {
            colorComparator = when (it.name) {
                "1" -> null
                "2" -> Comparator { a, b ->
                    // Hue
                    b.toHSLa().h.compareTo(a.toHSLa().h)
                }
                "3" -> Comparator { a, b ->
                    // Saturation
                    b.toHSLa().s.compareTo(a.toHSLa().s)
                }
                "4" -> Comparator { a, b ->
                    // Luminance
                    b.luminance.compareTo(a.luminance)
                }
                else -> colorComparator
            }
        }

        extend {
            val colors = mutableListOf<ColorRGBa>()
            val rectSize = max(mouse.position.x, 10.0)
            val gridSize = rectSize.toInt()

            for (y in 0 until height step gridSize) {
                for (x in 0 until width step gridSize) {
                    colors.add(imageData[x, y])
                }
            }

            colorComparator?.let {
                colors.sortWith(it)
            }

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
                drawer.rectangle(x, y, rectSize, rectSize)
            }
        }
    }
}
