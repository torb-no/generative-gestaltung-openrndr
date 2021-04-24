import org.openrndr.application
import org.openrndr.color.ColorHSVa
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.olive.oliveProgram
import utils.QuitOnEsc
import utils.d
import kotlin.math.max

fun main() = application {
    configure {
        width = 800
        height = 400
    }

    oliveProgram {
        extend(QuitOnEsc())
        extend(Screenshots())

        extend {
            val stepX = max(mouse.position.x, 2.0).toInt()
            val stepY = max(mouse.position.y, 2.0).toInt()

            for (x in 0..width step stepX) {
                for (y in 0..height step stepY) {
                    val hue = x / width.d * 360.0
                    val saturation = 1.0 - y / height.d
                    drawer.stroke = null
                    drawer.fill = ColorHSVa(hue, saturation, 1.0).toRGBa()
                    drawer.rectangle(x.d, y.d, stepX.d, stepY.d)
                }
            }
        }
    }
}